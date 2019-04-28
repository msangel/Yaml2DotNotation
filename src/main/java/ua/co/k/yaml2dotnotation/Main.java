package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Main {
    public static class TestEntry {
        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        String a;
        String b;

        @Override
        public String toString() {
            return "test[a="+a+", b="+b+"]";
        }
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        URL resource = Main.class.getResource("/test.yml");
        DottedProperties obj = yamlReader.readValue(resource, DottedProperties.class);
        List<String> a = obj.getProperty("a", new TypeReference<List<String>>() {
        });

        System.out.println("a is: " + a);

        System.out.println("b is: " + obj.getProperty("b", new TypeReference<String>() {}));


        System.out.println("c is: " + obj.getProperty("c.1", new TypeReference<TestEntry>() {}));

        System.out.println(obj.toString());
    }
}
