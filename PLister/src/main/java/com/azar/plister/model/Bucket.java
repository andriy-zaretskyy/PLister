package com.azar.plister.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;

/**
 * Created by azar on 11/29/13.
 */
public interface Bucket {
    String getUid();

    String getName();

    Uri getImageUri();

    List<Selection> getSelections();

    void addSelection(Selection s);

    void removeSelection(Selection s);
}
