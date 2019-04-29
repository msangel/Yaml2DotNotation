package ua.co.k.yaml2dotnotation;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class Yaml2PropsTest {

    @Test
    public void test() {
        URL resource = BaseTest.class.getResource("/test.yml");
        DottedProperties obj = Yaml2Props.create(resource);

    }

}
