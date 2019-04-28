package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = DottedPropertiesDeserializer.class)
public class DottedProperties {

    private final TreeNode treeNode;
    private final ObjectCodec codec;

    DottedProperties(TreeNode treeNode, ObjectCodec codec) {
        this.treeNode = treeNode;
        this.codec = codec;
    }

    public <T> T getProperty(TypeReference<T> ref) {
        JsonParser parser = this.treeNode.traverse(this.codec);
        try {
            return parser.readValueAs(ref);
        } catch (IOException e) {
            throw new RuntimeException("problem getting property", e);
        }
    }


    public <T> T getProperty(String path, TypeReference<T> ref) {
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
}
