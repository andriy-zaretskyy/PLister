package com.azar.plister.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by azar on 11/29/13.
 */
public interface Bucket {
    String getUid();

    String getName();

    Bitmap getBackground(ContentResolver resolver) throws StorageException;

    List<Selection> getSelections();

    void AddSelection(Selection s);

    void RemoveNearest(Selection s);
}
