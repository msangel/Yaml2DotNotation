package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.net.URL;

public class Yaml2Props {

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

    private static DottedProperties doInScope(CheckedFunction<ObjectMapper, DottedProperties> arg) {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        try {
            return arg.apply(yamlReader);
        } catch (IOException e) {
            throw new RuntimeException("problem reading properties", e);
        }
    }

    public static DottedProperties create(InputStream in) {
        return doInScope(yamlReader -> yamlReader.readValue(in, DottedProperties.class));
    }

    public static DottedProperties create(URL resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(byte[] resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(DataInput resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(File resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(JsonParser resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(Reader resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }

    public static DottedProperties create(String resource) {
        return doInScope(yamlReader -> yamlReader.readValue(resource, DottedProperties.class));
    }


    public static DottedProperties createEmpty() {
        return new Null();
    }

    public static DottedProperties cascade(DottedProperties defaults, DottedProperties explisit) {
        return new Cascade(defaults, explisit);
    }

}
