package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.type.TypeReference;

class Cascade extends DottedProperties {
    private final DottedProperties defaults;
    private final DottedProperties explisit;

    public Cascade(DottedProperties defaults, DottedProperties explisit) {
        this.defaults = defaults;
        this.explisit = explisit;
    }

    @Override
    public boolean hasProperty(String path) {
        return this.defaults.hasProperty(path) || this.explisit.hasProperty(path);
    }

    @Override
    public <T> T getProperty(String path, TypeReference<T> ref) {
        if (this.defaults.hasProperty(path)) {
            return this.defaults.getProperty(path, ref);
        }
        return this.explisit.getProperty(path, ref);
    }
}
