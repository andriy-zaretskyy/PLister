package com.azar.plister.model.serialize;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by azar on 12/9/13.
 */
public final class UriJsonDeserializer implements JsonDeserializer<Uri> {
    @Override
    public Uri deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(json.getAsJsonPrimitive().getAsString());
    }
}
