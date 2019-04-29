package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import static org.junit.Assert.*;

public class NullTest {
    @Test
    public void test() {
        DottedProperties empty = new Null();
        assertNull(empty.getProperty("", new TypeReference<NullTest>(){}));
        assertFalse(empty.hasProperty(""));
    }

}
