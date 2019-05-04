package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Yaml2Props {

    static ParseContext parseContext = getParseContext();

    private static ParseContext getParseContext() {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Configuration configuration = Configuration.defaultConfiguration()
                .mappingProvider(new JacksonMappingProvider(yamlReader))
                .jsonProvider(new JacksonJsonProvider(yamlReader));
        return JsonPath.using(configuration);
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

    private static DottedProperties doInScope(CheckedFunction<ParseContext, DocumentContext> arg) {
        try {
            DocumentContext context = arg.apply(parseContext);
            return new Base(context);
        } catch (IOException e) {
            throw new RuntimeException("problem parsing document", e);
        }

    }

    public static DottedProperties create(InputStream in) {
        return doInScope(parseContext -> parseContext.parse(in));
    }

    public static DottedProperties create(URL resource) {
        return doInScope(parseContext -> parseContext.parse(resource));
    }

    public static DottedProperties create(File resource) {
        return doInScope(parseContext -> parseContext.parse(resource));
    }

    public static DottedProperties create(String resource) {
        return doInScope(parseContext -> parseContext.parse(resource));
    }


    public static DottedProperties createEmpty() {
        return new Null();
    }

    public static DottedProperties cascade(DottedProperties defaults, DottedProperties explisit) {
        return new Cascade(defaults, explisit);
    }

}
