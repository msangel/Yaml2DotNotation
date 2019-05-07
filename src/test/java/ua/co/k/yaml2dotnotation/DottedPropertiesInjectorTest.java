package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import ua.co.k.yaml2dotnotation.annot.Prop;

import static org.junit.Assert.*;

/**
 * Created by vasyl.khrystiuk on 05/07/2019.
 */
public class DottedPropertiesInjectorTest {

    public static class TestObject {
        @Prop("a")
        public String as;

        @Prop("b")
        public Integer bas;
    }

    public static void main(String[] args) {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3}");
        String aFromProps = properties.getProperty("a").asString();
        System.out.println("aFromProps: " + aFromProps);
        TestObject to = new TestObject();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        System.out.println("as: " + to.as);
        System.out.println("bas: " + to.bas);

        String aPropertyNew = properties.getProperty("a", String.class);
        System.out.println("aFromProps 2: " + aPropertyNew);
    }

}
