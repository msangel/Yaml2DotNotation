package ua.co.k.yaml2dotnotation;

import org.repackage.v2_8_5.com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.function.BiConsumer;

class Yaml2Props {


    public static <T> T load(Map<String, Object> data, Class<T> clazz) {
        Type type = new TypeToken<String>() {
        }.getType();
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Properties loadAsProperty(Path configFilePath) {

        try (final InputStream stream = Files.newInputStream(configFilePath)) {
            return ((Map<String, Object>) new Yaml().load(new InputStreamReader(stream, Charset.defaultCharset()))).entrySet()
                    .stream().collect(Properties::new,
                            new BiConsumer<Properties, Map.Entry<String, Object>>() {
                                private Stack<String> prefix = new Stack<>();

                                @SuppressWarnings({"unchecked", "Duplicates"})
                                @Override
                                public void accept(Properties properties, Map.Entry<String, Object> entry) {
                                    if (prefix.empty()) {
                                        prefix.push("");
                                    }
                                    if (isBasicType(entry.getValue())) {
                                        properties.put(prefix.peek() + entry.getKey(), String.valueOf(entry.getValue()));
                                    } else if (isArrayType(entry.getValue())) {
                                        if (prefix.empty()) {
                                            prefix.push(entry.getKey() + ".");
                                        } else {
                                            prefix.push(prefix.peek() + entry.getKey() + ".");
                                        }
                                        List list = (List) entry.getValue();
                                        for (int i = 0; i < list.size(); i++) {
                                            this.accept(properties, new AbstractMap.SimpleEntry<>(String.valueOf(i), list.get(i)));
                                        }
                                        prefix.pop();
                                    } else {
                                        if (prefix.empty()) {
                                            prefix.push(entry.getKey() + ".");
                                        } else {
                                            prefix.push(prefix.peek() + entry.getKey() + ".");
                                        }
                                        for (Map.Entry e : ((Map<String, Object>) entry.getValue()).entrySet()) {
                                            this.accept(properties, e);
                                        }
                                        prefix.pop();
                                    }


                                }

                                private boolean isArrayType(Object value) {
                                    return List.class.isInstance(value);
                                }

                                private boolean isBasicType(Object o) {
                                    if (o == null) {
                                        return true;
                                    }
                                    Class<?> clazz = o.getClass();
                                    return clazz.equals(Boolean.class) ||
                                            clazz.equals(Integer.class) ||
                                            clazz.equals(Character.class) ||
                                            clazz.equals(Byte.class) ||
                                            clazz.equals(Short.class) ||
                                            clazz.equals(Double.class) ||
                                            clazz.equals(Long.class) ||
                                            clazz.equals(Float.class) ||
                                            clazz.isPrimitive() ||
                                            o instanceof String;
                                }

                            }
                            , Hashtable::putAll);
        } catch (IOException e) {
            throw new RuntimeException("problem reading config file", e);
        }
    }
}
