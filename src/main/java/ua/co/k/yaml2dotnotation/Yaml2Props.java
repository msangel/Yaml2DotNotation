package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.net.URL;

public class Yaml2Props {

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

    private static DottedProperties doInScope(CheckedFunction<JsonFactory, JsonParser> arg) {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        try {
            JsonFactory factory = yamlReader.getFactory();
            JsonParser p = arg.apply(factory);
            TreeNode treeNode = yamlReader.readTree(p);
            return new Base(treeNode, yamlReader);
        } catch (IOException e) {
            throw new RuntimeException("problem reading properties", e);
        }
    }

    public static DottedProperties create(InputStream in) {
        return doInScope(factory -> factory.createParser(in));
    }

    public static DottedProperties create(URL resource) {
        return doInScope(factory -> factory.createParser(resource));
    }

    public static DottedProperties create(byte[] resource) {
        return doInScope(factory -> factory.createParser(resource));
    }

    public static DottedProperties create(DataInput resource) {
        return doInScope(factory -> factory.createParser(resource));
    }

    public static DottedProperties create(File resource) {
        return doInScope(factory -> factory.createParser(resource));
    }

    public static DottedProperties create(Reader resource) {
        return doInScope(factory -> factory.createParser(resource));
    }

    public static DottedProperties create(String resource) {
        return doInScope(factory -> factory.createParser(resource));
    }


    public static DottedProperties createEmpty() {
        return new Null();
    }

    public static DottedProperties cascade(DottedProperties defaults, DottedProperties explisit) {
        return new Cascade(defaults, explisit);
    }

}
