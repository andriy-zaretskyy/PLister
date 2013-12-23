package com.azar.plister.model;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azar on 12/7/13.
 */
public final class SimpleStorage implements Storage {

    private static final String FILENAME = "storage.1.0.json";
    private List<Bucket> buckets = new ArrayList<Bucket>();
    private final File filename;
    private final Gson gson;

    public SimpleStorage(File dataLocation){
        this.filename = new File(dataLocation, FILENAME);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Bucket.class, new BucketJsonDeserializer());
        builder.registerTypeAdapter(Uri.class, new UriJsonDeserializer());
        builder.registerTypeAdapter(Uri.class, new UriJsonSerializer());
        builder.registerTypeAdapter(Selection.class, new SelectionDeserializer());
        builder.registerTypeAdapter(Selection.class, new SelectionSerializer());
        gson = builder.create();
    }


    @Override
    public List<Bucket> getBuckets(){
        if(this.buckets.isEmpty() && this.filename.exists()){

            StringBuffer buffer = new StringBuffer();
            try {
                BufferedReader in = new BufferedReader(new FileReader(this.filename));
                try{
                    String line;
                    while((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                }finally {
                    in.close();
                }
            } catch (IOException e) {
                throw new StorageException("Failed to save.", e);
            }

            Type collectionType = new TypeToken<ArrayList<Bucket>>(){}.getType();
            this.buckets = (ArrayList<Bucket>)gson.fromJson(buffer.toString(), collectionType);
        }

        return this.buckets;
    }

    @Override
    public void save(){

        String json = gson.toJson(this.buckets);
        try {
            Writer out = new OutputStreamWriter(new FileOutputStream(this.filename), "UTF-8");
            try{
                out.write(json);
            }finally {
                out.close();
            }

        } catch (IOException e) {
            throw new StorageException("Failed to save.", e);
        }
    }
}
