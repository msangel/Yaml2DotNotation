# YamlToDottedProperty
Library for converting yaml to plain Java property file with full path in dotted format. Just like Spring Boot do, but without Spring Boot.


Draft for creating classical property file: https://gist.github.com/msangel/116730b2f64b8eeacb80a0a4fffa73ff

## Install
Follow instructions here: https://jitpack.io/#msangel/Yaml2DotNotation

## Usage
The library can work with both POJOs and primitives. 
This is sample usages from tests:
```java
public class DottedPropertiesInjectorTest {
    public static class InnerObject {
        @JsonProperty("innerField")
        public String a;
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
        // yaml is superset of json, so we can use either of them
        DottedProperties properties = Yaml2Props.create("{ a: 1, b: 3, c: true, d: { innerField : 'inner object'}}");
        TestObjectFine to = new TestObjectFine();
        
        // this method expect existed object, the marked for setting fields must be accessible for that(not final and public/have setter )  
        DottedPropertiesInjector.injectAnnotatedFields(to, properties);
        assertEquals("1", to.as);
        assertEquals(3, (int)to.bas);
        assertTrue(to.isDas());
        
        // none that top-level properties use @Value annotation as this object is utilazed by our library
        // but for nested we have to use JsonProperty as problems of writing proper type for value is Jackson responsibility
        assertEquals("inner object", to.inner.a);
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
```

## Alternatives
Here are two similar projects with answers about why they did not fit my needs:
* [Spring Boot](https://github.com/spring-projects/spring-boot) has a [module for this](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Configuration-Binding). Actually, my project inspirited by spring. But unfortunately, I was needed the lightweight implementation that respects "S" from "SOLID". And also I already had the DI container there, and that was not a spring. 
* [Governator](https://github.com/Netflix/governator) is another "multitool" with such functionality. And it has exactly the same problems as a project above. Governator is a meta-library for google guice with a [similar module](https://github.com/Netflix/governator/wiki/Configuration-Mapping). And even if my project was built based on the guice too, I still reject the option of taking a lot of unknown stuff for solving simple task like, which I can solve by homebrew solution with minimal dependencies and with nothing extra.

This project is not taking any DI container with it. 


## Things to do
Check this out: https://stackoverflow.com/questions/53196603/move-nested-map-values-of-a-mapstring-object-to-the-top-level
