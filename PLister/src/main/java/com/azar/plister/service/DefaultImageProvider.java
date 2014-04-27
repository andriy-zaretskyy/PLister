package com.azar.plister.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.Selection;

/**
 * Created by azar on 12/7/13.
 */
class DefaultImageProvider implements ImageProvider {
    private final Bucket bucket;
    private final Bitmap background;
    private final SelectionService selectionService = ApplicationServices.INSTANCE.get(SelectionService.class);

    public DefaultImageProvider(Bitmap background, Bucket bucket) {
        this.bucket = bucket;
        this.background = background;
    }

    @Override
    public Bitmap getResultPicture() {
        Bitmap result = background.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        for (Selection s : bucket.getSelections()) {
            selectionService.draw(canvas, s);
        }

        return result;
    }
}
