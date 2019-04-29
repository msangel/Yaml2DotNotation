package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;

class Null extends DottedProperties {

    @Override
    public boolean hasProperty(String path) {
        return false;
    }

    @Override
    public <T> T getProperty(String path, TypeReference<T> ref) {
        return null;
    }
}
