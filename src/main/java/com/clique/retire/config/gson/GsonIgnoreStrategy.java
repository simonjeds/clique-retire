package com.clique.retire.config.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Objects;

public class GsonIgnoreStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        return Objects.nonNull(field.getAnnotation(GsonIgnore.class));
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}