package com.azar.plister.service;

import android.content.ContentResolver;
import android.graphics.Bitmap;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.Selection;

/**
 * Created by azar on 4/26/14.
 */
public interface BucketService extends Service {
    void removeNearest(Bucket bucket, Selection selection);
    Bitmap getBackground(Bucket bucket, ContentResolver resolver);
    Bitmap getScaledBitmap(Bitmap bitmap, int width, int height);
}
