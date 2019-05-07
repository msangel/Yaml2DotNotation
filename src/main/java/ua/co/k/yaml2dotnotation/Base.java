package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Base extends DottedProperties {

    private final TreeNode treeNode;
    private final ObjectMapper codec;

    Base(TreeNode treeNode, ObjectMapper codec) {
        this.treeNode = treeNode;
        this.codec = codec;
    }

    @Override
    public boolean hasProperty(String path) {
        TreeNode node = this.treeNode.at(path);
        return !node.isMissingNode();
    }

    @Override
    public <T> T getProperty(String path, TypeReference<T> ref) {
        path = dotted2pointer(path);
        TreeNode node = this.treeNode.at(path);
        if (node.isMissingNode()) {
            return null;
        }
        JsonParser parser = node.traverse(this.codec);
        try {
            Object value = parser.readValueAs(Object.class);
            return codec.convertValue(value, ref);
        } catch (IOException e) {
            throw new RuntimeException("problem getting property", e);
        }
    }

    // todo: https://tools.ietf.org/html/rfc6901
    private static String dotted2pointer(String in) {
        return "/"+String.join("/", in.split("\\."));
    }
}
