package com.azar.plister.service;

import android.graphics.Canvas;

import com.azar.plister.model.Selection;

/**
 * Created by azar on 4/26/14.
 */
public interface SelectionService extends Service {
    void draw(Canvas canvas, Selection selection);

    double getDistance(Selection first, Selection second);
}
