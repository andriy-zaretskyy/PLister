package com.azar.plister.service;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.Selection;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.util.List;

/**
 * Created by azar on 4/26/14.
 */
class DefaultBucketService implements BucketService {
    private final SelectionService selectionService;

    public DefaultBucketService(SelectionService selectionService) {
        this.selectionService = selectionService;
    }

    @Override
    public void removeNearest(Bucket bucket, Selection selection) {
        Selection toBeRemoved = null;
        List<Selection> selections = bucket.getSelections();
        double nearestDistance = Double.MAX_VALUE;
        for (Selection s : selections) {
            double distance = selectionService.getDistance(selection, s);
            if (nearestDistance >= distance) {
                toBeRemoved = s;
                nearestDistance = distance;
            }
        }

        if (toBeRemoved != null) {
            bucket.removeSelection(toBeRemoved);
        }
    }

    @Override
    public Bitmap getBackground(Bucket bucket, ContentResolver resolver) {
        try {
            Matrix mat = new Matrix();
            // Rotate the bitmap
            Bitmap result = BitmapFactory.decodeStream(resolver.openInputStream(bucket.getImageUri()));
            if (result.getWidth() > result.getHeight()) {
                mat.postRotate(90);
            }

            return Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), mat, true);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

        double newHeight = height;
        double newWidth = ((double) height) * ratio;
        if (newWidth > width) {
            newHeight = ((double) width) / ratio;
            newWidth = width;
        }

        return Bitmap.createScaledBitmap(bitmap, (int) newWidth, (int) newHeight, true);
    }
}
