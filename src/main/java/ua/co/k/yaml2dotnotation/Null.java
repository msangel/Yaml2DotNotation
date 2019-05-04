package ua.co.k.yaml2dotnotation;

import com.jayway.jsonpath.TypeRef;

class Null extends DottedProperties {

    @Override
    public boolean hasProperty(String path) {
        return false;
    }

    @Override
    public <T> T getProperty(String path, TypeRef<T> ref) {
        return null;
    }
}
