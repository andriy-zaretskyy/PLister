package com.azar.plister.model;

import android.graphics.Point;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by azar on 12/9/13.
 */
public final class SelectionDeserializer implements JsonDeserializer<Selection> {
    @Override
    public Selection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Point start = context.deserialize(obj.getAsJsonObject("start"), Point.class);
        Point end = context.deserialize(obj.getAsJsonObject("end"), Point.class);
        return new SimpleSelection(start, end);
    }
}
