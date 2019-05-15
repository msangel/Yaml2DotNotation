package ua.co.k.yaml2dotnotation;

import org.junit.Test;
import ua.co.k.yaml2dotnotation.annot.Prop;

import static org.junit.Assert.*;

/**
 * Created by vasyl.khrystiuk on 05/07/2019.
 */
public class DottedPropertiesInjectorTest {

    public static class TestObjectFine {
        @Prop("a")
        public String as;

        @Prop("b")
        private Integer bas;

        @Prop("c")
        private Boolean das;

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
    }

    @Test
    public void testInjectFine() {
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3, c: true}");
        TestObjectFine to = new TestObjectFine();
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        assertEquals("1", to.as);
        assertEquals(3, (int)to.bas);
        assertTrue(to.isDas());
    }

    public static class TestObjectInvalidPrivatFieldUsed {
        @Prop("a")
        public String as;

        @Prop("b")
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
        @Prop("a")
        public String as;

        @Prop("b")
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
        @Prop("d")
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
}
