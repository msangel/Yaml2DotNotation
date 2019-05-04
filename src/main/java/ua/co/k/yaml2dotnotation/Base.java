package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import net.minidev.json.writer.JsonReader;

import java.io.IOException;

public class Base extends DottedProperties {

    private TreeNode treeNode;
    private ParseContext parseContext;
    private DocumentContext context;


    public Base(DocumentContext context) {
        this.context = context;
    }

    @Override
    public boolean hasProperty(String path) {
        throw new RuntimeException("I need to go to sleep, will implent later");
    }

    @Override
    public <T> T getProperty(String path, TypeRef<T> ref) {
        return this.context.read(path, ref);
    }
}
