package com.azar.plister.model;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by azar on 12/9/13.
 */
public final class BucketJsonDeserializer implements JsonDeserializer<Bucket> {

    @Override
    public Bucket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SimpleBucket result = new SimpleBucket();
        JsonObject obj = json.getAsJsonObject();
        result.setName(obj.getAsJsonPrimitive("name").getAsString());
        result.setUid(obj.getAsJsonPrimitive("id").getAsString());
        Uri uri = context.deserialize(obj.getAsJsonPrimitive("imageUri"), Uri.class);
        result.setImageUri(uri);
        Type collectionType = new TypeToken<ArrayList<Selection>>() {
        }.getType();
        ArrayList<Selection> selections = context.deserialize(obj.getAsJsonArray("selections"), collectionType);
        result.setSelections(selections);
        return result;
    }
}
