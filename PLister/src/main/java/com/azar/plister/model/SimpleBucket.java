package com.azar.plister.model;

import android.net.Uri;

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
    private String id = UUID.randomUUID().toString();

    @Override
    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    @Override
    public List<Selection> getSelections() {
        return new ArrayList<Selection>(selections);
    }

    public void setSelections(List<Selection> selections) {
        this.selections = new ArrayList<Selection>(selections);
    }

    @Override
    public void addSelection(Selection s) {
        this.selections.add(s);
    }

    @Override
    public void removeSelection(Selection selection) {
        this.selections.remove(selection);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleBucket)) {
            return false;
        }

        return ((SimpleBucket) o).getUid().equals(this.getUid());
    }

    @Override
    public int hashCode() {
        return this.getUid().hashCode();
    }
}
