package com.azar.plister.model;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by azar on 12/7/13.
 */
public final class SimpleSelection implements Selection, Serializable {

    private final Point start;
    private final Point end;

    public SimpleSelection(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Point getStart() {
        return start;
    }

    @Override
    public Point getEnd() {
        return end;
    }
}
