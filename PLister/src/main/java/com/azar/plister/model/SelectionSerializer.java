package com.azar.plister.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by azar on 12/10/13.
 */
public final class SelectionSerializer implements JsonSerializer<Selection> {
    @Override
    public JsonElement serialize(Selection src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
