package ua.co.k.yaml2dotnotation;

import ua.co.k.yaml2dotnotation.annot.Prop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by vasyl.khrystiuk on 05/06/2019.
 */
public class DottedPropertiesInjector {

    public static void injectAnnotatedFields(Object target) {
        Objects.requireNonNull(target, "injecting properties into NULL is not possible");
        List<Field> fieldsToInject = getFields(target.getClass(), field -> isCandidateField(target, field));

    }

    static boolean isCandidateField(Object target, Field field) {
        if (field.getAnnotation(Prop.class) == null) {
            return false;
        }
        if (field.isAccessible()) {
            // field is public, we can write it
            return true;
        } else {
            // https://stackoverflow.com/questions/10009052/invoking-setter-method-using-java-reflection
        }
        return false;
    }

    public static List<Field> getFields(Class<?> startClass, Predicate<Field> predicate) {

        List<Field> currentClassFields = new ArrayList(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null) {
            List<Field> parentClassFields = getFields(parentClass, predicate);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }
}
