package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class IntegrationTest {


    public static class TestEntry {
        public String a;
        public String b;
    }

    private DottedProperties props;

    ObjectNode objectNode;
    ObjectMapper codec;

    @Before
    public void init() {
        codec = new ObjectMapper();
        objectNode = codec.createObjectNode();
        ArrayNode arrayNode = codec.createArrayNode();
        arrayNode.add("1").add("2").add("3");
        objectNode.set("a", arrayNode);
        objectNode.set("b", new TextNode("lol"));
        ObjectNode cObj = codec.createObjectNode();
        objectNode.set("c", cObj);
        ObjectNode entryObj = codec.createObjectNode();
        entryObj.set("a", new TextNode("3"));
        entryObj.set("b", new IntNode(4));
        cObj.set("1", entryObj);
        props = new Base(objectNode, codec);
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
