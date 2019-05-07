package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.co.k.yaml2dotnotation.annot.Prop;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
public class DottedPropertiesInjector {

    @FunctionalInterface
    public interface ThrowingConsumer<T> extends Consumer<T> {

        @Override
        default void accept(final T elem) {
            try {
                acceptThrows(elem);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        void acceptThrows(T elem) throws Exception;

    }

    public static class FieldInformation {
        String path;
        TypeReference<?> valueTypeRef;
        ThrowingConsumer<DottedProperties> setterInvoker;
    }

    public static void injectAnnotatedFields(Object target) {
        Objects.requireNonNull(target, "injecting properties into NULL is not possible");

        Function<List<Field>, List<FieldInformation>> mapper = null;
        List<FieldInformation> fieldsToInject = getFields(target.getClass(), mapper);

        DottedProperties allProps = null;


    }

    static Function<List<Field>, List<FieldInformation>> isCandidateField(Object target, Field field) {
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

                FieldInformation res = new FieldInformation();

                res.path = annotation.value();

                res.valueTypeRef = new TypeReference<Object>() {
                    @Override
                    public Type getType() {
                        return field.getType();
                    }
                };


                Consumer<DottedProperties> setterInvoker;
                if (field.isAccessible()) {
                    // field is public, we can write it
                    // but need check if is writable (public and is not a final)
                    res.setterInvoker = new ThrowingConsumer<DottedProperties>() {

                        @Override
                        public void acceptThrows(DottedProperties elem) throws Exception {

                        }
                    };
                    return res;
                } else {
                    // https://stackoverflow.com/questions/10009052/invoking-setter-method-using-java-reflection
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), target.getClass());
                        Method writeMethod = pd.getWriteMethod();
                        if (writeMethod == null) {
                            return null;
                        }
                        res.setterInvoker = new ThrowingConsumer<DottedProperties>() {

                            @Override
                            public void acceptThrows(DottedProperties elem) throws Exception {
                                writeMethod.invoke(target, "");
                            }
                        };
                        return res;
                    } catch (IntrospectionException e) {
                        return null;
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
