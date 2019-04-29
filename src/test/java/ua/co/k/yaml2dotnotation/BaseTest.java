package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BaseTest {

    public static class TestEntry {
        public String a;
        public String b;
    }

    @Test
    public void test() {
        URL resource = BaseTest.class.getResource("/test.yml");
        DottedProperties obj = Yaml2Props.create(resource);
        List<String> a = obj.getProperty("a", new TypeReference<List<String>>() {});

        assertArrayEquals(new String[]{"1", "2", "3"}, a.toArray());
        assertEquals("lol", obj.getProperty("b", new TypeReference<String>() {}));
        assertEquals("lol", obj.getProperty("b", String.class));
        assertEquals("lol", obj.getProperty("b").asString());
        TestEntry entry = obj.getProperty("c.1", new TypeReference<TestEntry>() {});
        assertEquals("3", entry.a);
        assertEquals("4", entry.b);
    }

}
