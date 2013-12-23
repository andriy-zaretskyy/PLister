package com.azar.plister.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by azar on 11/29/13.
 */
public interface Selection {
    void draw(Canvas canvas);
    double getDistance(Selection other);
}
