package com.azar.plister.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by azar on 12/7/13.
 */
public final class SimpleBucket implements Bucket {
    private String name;
    private Uri imageUri;
    private List<Selection> selections = new ArrayList<Selection>();
    private transient Bitmap background = null;
    private String id = UUID.randomUUID().toString();

    @Override
    public String getUid() {
        return id;
    }

    public void setUid(String id){
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;

    }

    @Override
    public Bitmap getBackground(ContentResolver resolver) throws StorageException{
        if(this.background == null){
          try{
              this.background = BitmapFactory.decodeStream(resolver.openInputStream(getImageUri()));
          }catch (FileNotFoundException e){
              throw new StorageException("Error loading bitmap", e);
          }
        }

        return this.background;
    }

    @Override
    public List<Selection> getSelections() {
        return new ArrayList<Selection>(selections);
    }

    public void setSelections(List<Selection> selections) {
        this.selections = new ArrayList<Selection>(selections);
    }

    @Override
    public void AddSelection(Selection s) {
        this.selections.add(s);
    }

    @Override
    public void RemoveNearest(Selection sln) {
        Selection toBeRemoved = null;
        double nearestDistance = Double.MAX_VALUE;
        for(Selection s: selections)
        {
            double distance = s.getDistance(sln);
            if (nearestDistance >= distance)
            {
                toBeRemoved = s;
                nearestDistance = distance;
            }
        }

        if (toBeRemoved != null)
        {
            this.selections.remove(toBeRemoved);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof SimpleBucket)){
            return false;
        }

        return ((SimpleBucket)o).getUid().equals(this.getUid());
    }

    @Override
    public int hashCode() {
        return this.getUid().hashCode();
    }
}
