package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class Yaml2PropsTest {

    @Test
    public void test() {
        URL resource = Yaml2PropsTest.class.getResource("/test.yml");
        DottedProperties obj = Yaml2Props.create(resource);
        Integer node = obj.getProperty("c[0].a").asInteger();

        System.err.println(node);

        String node2 = obj.getProperty("c[1].b").asString();

        System.err.println(node2);
    }

}
