package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public class Base extends DottedProperties {

    private final TreeNode treeNode;
    private final ObjectCodec codec;

    Base(TreeNode treeNode, ObjectCodec codec) {
        this.treeNode = treeNode;
        this.codec = codec;
    }

    @Override
    public boolean hasProperty(String path) {
        throw new RuntimeException("I need to go to sleep, will implent later");
    }

    @Override
    public <T> T getProperty(String path, TypeReference<T> ref) {
        path = dotted2pointer(path);
        JsonParser parser = this.treeNode.at(path).traverse(this.codec);
        try {
            return parser.readValueAs(ref);
        } catch (IOException e) {
            throw new RuntimeException("problem getting property", e);
        }
    }

    // todo: https://tools.ietf.org/html/rfc6901
    private static String dotted2pointer(String in) {
        return "/"+String.join("/", in.split("\\."));
    }
}
