package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = DottedPropertiesDeserializer.class)
public class DottedProperties {

    public class CommonTypes {
        private final String path;
        CommonTypes(String path){
            this.path = path;
        }

        public String asString() {
            return DottedProperties.this.getProperty(path, String.class);
        }
    }

    private final TreeNode treeNode;
    private final ObjectCodec codec;

    DottedProperties(TreeNode treeNode, ObjectCodec codec) {
        this.treeNode = treeNode;
        this.codec = codec;
    }

    public CommonTypes getProperty(String path) {
        return new CommonTypes(path);
    }
    public <T> T getProperty(String path, Class<T> ref) {
        path = dotted2pointer(path);
        JsonParser parser = this.treeNode.at(path).traverse(this.codec);
        try {
            return parser.readValueAs(ref);
        } catch (IOException e) {
            throw new RuntimeException("problem getting property", e);
        }
    }


    public <T> T getProperty(String path, TypeReference<T> ref) {
        path = dotted2pointer(path);
        JsonParser parser = this.treeNode.at(path).traverse(this.codec);
        try {
            return parser.readValueAs(ref);
        } catch (IOException e) {
            throw new RuntimeException("problem getting property", e);
        }
    }

    @Override
    public synchronized String toString() {
        return super.toString();
    }

    private static String dotted2pointer(String in) {
        return "/"+String.join("/", in.split("\\."));
    }
}
