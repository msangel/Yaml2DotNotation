package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.co.k.yaml2dotnotation.annot.Prop;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by vasyl.khrystiuk on 05/06/2019.
 *
 * Should be compliant with java 9, so the limitations of java 9 will not work with java 8.
 * Sorry for this but why this ever should set private fields without setters?
 * And regarding setting final fields... No.
 * Regarding annotated constructor params: this can be done, but as far as this
 * library is not rely on any DI, I can either set all constructor fields, either none.
 * Having this limitation in mind I decide even not implement this feature.
 *
 */
class DottedPropertiesInjector {

    public static abstract class FieldInformation implements Consumer<DottedProperties>{

        final String path;
        final TypeReference<?> valueTypeRef;

        public FieldInformation(String path, TypeReference<?> valueTypeRef){
            this.path = path;
            this.valueTypeRef = valueTypeRef;
        }

        @Override
        public void accept(final DottedProperties elem) {
            try {
                acceptThrows(elem);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        abstract void acceptThrows(DottedProperties elem) throws Exception;
    }

    public static void injectAnnotatedFields(Object target, DottedProperties props) {
        Objects.requireNonNull(target, "injecting properties into NULL is not possible");
        List<FieldInformation> fieldsToInject = getFields(target.getClass(), getCandidateFieldMapper(target));
        fieldsToInject.forEach(el -> el.accept(props));
    }

    static Function<List<Field>, List<FieldInformation>> getCandidateFieldMapper(Object target) {
        return new Function<List<Field>, List<FieldInformation>>() {
            @Override
            public List<FieldInformation> apply(List<Field> fields) {
                List<FieldInformation> res = new ArrayList<>();
                for (Field f: fields) {
                    FieldInformation fi = getFieldInformation(f);
                    if (fi != null) {
                        res.add(fi);
                    }
                }
                return res;
            }

            private FieldInformation getFieldInformation(Field field) {

                Prop annotation = field.getAnnotation(Prop.class);
                if (annotation == null) {
                    return null;
                }

                TypeReference<Object> valueTypeRef = new TypeReference<Object>() {
                    @Override
                    public Type getType() {
                        return field.getType();
                    }
                };

                int modifiers = field.getModifiers();

                if (Modifier.isFinal(modifiers)) {
                    throw new InjectException("cannot set final filed " + field.getName());
                }

                if (Modifier.isPublic(modifiers)) {
                    // field is public, we can write it
                    // but need check if is writable (public and is not a final)
                    return new FieldInformation(annotation.value(), valueTypeRef) {
                        @Override
                        void acceptThrows(DottedProperties elem) throws Exception {
                            Object value = elem.getProperty(this.path, this.valueTypeRef);
                            field.set(target, value);
                        }
                    };
                } else {
                    // https://stackoverflow.com/questions/10009052/invoking-setter-method-using-java-reflection
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), target.getClass());
                        Method writeMethod = pd.getWriteMethod();
                        if (writeMethod == null) {
                            throw new InjectException("field is not public and dont have setter method: " +  field.getName());
                        }

                        return new FieldInformation(annotation.value(), valueTypeRef) {
                            @Override
                            void acceptThrows(DottedProperties elem) throws Exception {
                                Object value = elem.getProperty(this.path, this.valueTypeRef);
                                writeMethod.invoke(target, value);
                            }
                        };
                    } catch (IntrospectionException e) {
                        throw new InjectException("problem with introspect field named " + field.getName() + ", is that field present/accessible/have setters?");
                    }

                }
            }
        };
    }

    public static List<FieldInformation> getFields(Class<?> startClass, Function<List<Field>, List<FieldInformation>> mapperToSetters) {

        List<FieldInformation> currentClassFields = mapperToSetters.apply(Arrays.asList(startClass.getDeclaredFields()));

        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null) {
            List<FieldInformation> parentClassFields = getFields(parentClass, mapperToSetters);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }
}
