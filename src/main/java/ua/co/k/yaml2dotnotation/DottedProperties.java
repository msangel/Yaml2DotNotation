package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

public abstract class DottedProperties {
    public abstract boolean hasProperty(String path);

    public <T> T getProperty(String path, Class<T> ref) {
        return getProperty(path, new TypeReference<T>(){
            @Override
            public Type getType() {
                return ref;
            }
        });
    }

    public abstract <T> T getProperty(String path, TypeReference<T> ref);

    public CommonTypes getProperty(String path) {
        return new CommonTypes(path);
    }

    public class CommonTypes {
        private final String path;
        CommonTypes(String path){
            this.path = path;
        }

        public String asString() {
            return DottedProperties.this.getProperty(path, String.class);
        }

        public BigDecimal asBigDecimal() {
            return DottedProperties.this.getProperty(path, BigDecimal.class);
        }

        public Boolean asBoolean() {
            return DottedProperties.this.getProperty(path, Boolean.class);
        }

        public Integer asInteger() {
            return DottedProperties.this.getProperty(path, Integer.class);
        }
    }
}
