package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import ua.co.k.yaml2dotnotation.annot.Value;

import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by vasyl.khrystiuk on 05/07/2019.
 */
public class DottedPropertiesInjectorTest {

    public static class InnerObject {
        @JsonProperty("innerField")
        public String a;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InnerObject) {
                return Objects.equals(this.a, ((InnerObject) obj).a);
            }
            return false;
        }
    }

    public static class TestObjectFine {
        @Value("a")
        public String as;

        @Value("b")
        private Integer bas;

        @Value("c")
        private Boolean das;

        @Value("d")
        private InnerObject inner;

        public void setBas(Integer bas) {
            this.bas = bas;
        }

        public Integer getBas() {
            return bas;
        }

        public Boolean isDas() {
            return das;
        }

        public void setDas(Boolean das) {
            this.das = das;
        }

        public InnerObject getInner() {
            return inner;
        }

        public void setInner(InnerObject inner) {
            this.inner = inner;
        }
    }

    @Test
    public void testInjectFine() {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3, c: true, d: { innerField : 'inner object'}}");
        TestObjectFine to = new TestObjectFine();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        assertEquals("1", to.as);
        assertEquals(3, (int)to.bas);
        assertTrue(to.isDas());
        // none that top-level properties use @Value annotation as this object is utilazed by our library
        // but for nested we have to use JsonProperty as problems of writing proper type for value is Jackson responsibility
        assertEquals("inner object", to.inner.a);
    }

    public static class TestObjectInvalidPrivatFieldUsed {
        @Value("a")
        public String as;

        @Value("b")
        private Integer bas;
    }

    @Test(expected = InjectException.class)
    public void testInjectPrivateError() {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3}");
        TestObjectInvalidPrivatFieldUsed to = new TestObjectInvalidPrivatFieldUsed();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        fail();
    }

    public static class TestObjectInvalidFinalFielsUsed {
        @Value("a")
        public String as;

        @Value("b")
        private final Integer bas;

        public TestObjectInvalidFinalFielsUsed() {
            this.bas = null;
        }

        public Integer getBas() {
            return bas;
        }
    }

    @Test(expected = InjectException.class)
    public void testInjectFinalError() {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3}");
        TestObjectInvalidFinalFielsUsed to = new TestObjectInvalidFinalFielsUsed();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        fail();
    }


    public static class MoreLevels extends TestObjectFine {
        @Value("d")
        public String cas;
    }

    @Test
    public void testInjectFineWithInheritance() {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3, c: true, d: 42}");
        MoreLevels to = new MoreLevels();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        assertEquals("1", to.as);
        assertEquals(3, (int)to.getBas());
        assertTrue(to.isDas());
        assertEquals("42", to.cas);
    }

    @Test
    public void testGettingFine() {
        // yaml is superset of json, so we can use either of them
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3, c: true, d: { innerField : 'inner object'}}");
        String a = properties.getProperty("a").asString();
        assertEquals("1", a);

        Integer b = properties.getProperty("b").asInteger();
        assertEquals(3L, b.longValue());

        InnerObject inner = properties.getProperty("d", new TypeReference<InnerObject>() {}); // can be generics
        InnerObject expectd = new InnerObject();
        expectd.a = "inner object";
        assertEquals(expectd, inner);

        // all of them are valid and equal selectors:
        // d.innerField
        // d['innerField']
        // ['d'].innerField
        // ['d']['innerField']
        String nestedValue = properties.getProperty("d['innerField']", String.class); // value will be converted to needed type, if conversion is possible

        assertEquals("inner object", nestedValue);

    }
}
