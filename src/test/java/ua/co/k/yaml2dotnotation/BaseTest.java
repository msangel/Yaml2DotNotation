package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BaseTest {

    public static class TestEntry {
        public String a;
        public String b;
    }

    private DottedProperties props;

    TreeNode treeNode;
    ObjectMapper codec;

    @Before
    public void init() {
        treeNode = mock(TreeNode.class);
        codec = mock(ObjectMapper.class);;
        props = new Base(treeNode, codec);
    }

    @Test
    public void test() {

        List<String> a = props.getProperty("a", new TypeReference<List<String>>() {});

        assertArrayEquals(new String[]{"1", "2", "3"}, a.toArray());
        assertEquals("lol", props.getProperty("b", new TypeReference<String>() {}));
        assertEquals("lol", props.getProperty("b", String.class));
        assertEquals("lol", props.getProperty("b").asString());
        TestEntry entry = props.getProperty("c.1", new TypeReference<TestEntry>() {});
        assertEquals("3", entry.a);
        assertEquals("4", entry.b);
    }

}
