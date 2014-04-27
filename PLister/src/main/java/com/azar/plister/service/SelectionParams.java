package com.azar.plister.service;

import android.graphics.Point;

/**
 * Created by azar on 11/29/13.
 */
public class SelectionParams {
    private final Point touchPointFirst;
    private final Point touchPointLast;

    public SelectionParams(Point touchFirst, Point touchLast) {
        touchPointFirst = touchFirst;
        touchPointLast = touchLast;
    }

    public Point getTouchPointFirst() {
        return touchPointFirst;
    }

    public Point getTouchPointLast() {
        return touchPointLast;
    }
}
