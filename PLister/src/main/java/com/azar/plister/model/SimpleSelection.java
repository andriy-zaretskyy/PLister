package com.azar.plister.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by azar on 12/7/13.
 */
public final class SimpleSelection implements Selection, Serializable {

    private Point start;
    private Point end;
    transient private final Paint paint;

    public SimpleSelection(Point start, Point end) {
        this.start = start;
        this.end = end;
        Paint template = new Paint();
        template.setDither(true);
        template.setColor(Color.RED);
        template.setStyle(Paint.Style.FILL_AND_STROKE);
        template.setStrokeJoin(Paint.Join.ROUND);
        template.setStrokeCap(Paint.Cap.ROUND);
        template.setStrokeWidth(10);
        paint = template;
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.drawLine(start.x, start.y, end.x, end.y, paint);

    }

    @Override
    public double getDistance(Selection other) {
        if (other instanceof SimpleSelection) {
            return getSimpleDistance((SimpleSelection) other);
        } else {
            throw new IllegalStateException("distance can be applied only to Selection");
        }
    }

    private double dotProduct(double ux, double uy, double vx, double vy) {
        return ux * vx + uy * vy;
    }

    private double norm(double x, double y) {
        return Math.sqrt(dotProduct(x, y, x, y));
    }

    private double distance(double ux, double uy, double vx, double vy) {
        return norm(vx - ux, vy - uy);
    }

    private double distanceToSegment(double x, double y, double sx1, double sy1, double sx2, double sy2) {
        double vx = sx2 - sx1;
        double vy = sy2 - sy1;

        double wx = x - sx1;
        double wy = y - sy1;

        double c1 = dotProduct(wx, wy, vx, wy);
        if (c1 <= 0) {
            return distance(x, y, sx1, sx2);
        }

        double c2 = dotProduct(vx, vy, vx, vy);

        if (c2 <= c1) {
            return distance(x, y, sx1, sy1);
        }

        double b = c1 / c2;
        double bx = sx1 + b * vx;
        double by = sy1 + b * vy;

        return distance(x, y, bx, by);
    }

    private double getSimpleDistance(SimpleSelection other) {
        if (hasIntersection(this.start.x, this.start.y, this.end.x, this.end.y, other.start.x, other.start.y, other.end.x, other.end.x)) {
            return 0;
        } else {
            double d1 = distanceToSegment(this.start.x, this.start.y, other.start.x, other.start.y, other.end.x, other.end.x);
            double d2 = distanceToSegment(this.end.x, this.end.y, other.start.x, other.start.y, other.end.x, other.end.x);
            double d3 = distanceToSegment(other.start.x, other.start.y, this.start.x, this.start.y, this.end.x, this.end.x);
            double d4 = distanceToSegment(other.end.x, other.end.y, this.start.x, this.start.y, this.end.x, this.end.x);

            return Math.min(Math.min(d1, d2), Math.min(d3, d4));
        }
    }

    private boolean hasIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        //(x1, y1), (x2, y2) - first segment
        //(x3, y3), (x4, y4) - second segment

        if (!(Math.max(x1, x2) >= Math.min(x3, x4) && Math.max(x3, x4) >= Math.min(x1, x2) &&
                Math.max(y1, y2) >= Math.min(y3, y4) && Math.max(y3, y4) >= Math.min(y1, y2)))
            return false;


        double vector_prod1 = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
        //Vector product of vectors 1_2 and 1_4
        double vector_prod2 = (x2 - x1) * (y4 - y1) - (x4 - x1) * (y2 - y1);
        if (vector_prod1 * vector_prod2 > 0)
            return false;

        //Vector product of vectors 3_4 and 3_1
        double vector_prod3 = (x4 - x3) * (y1 - y3) - (x1 - x3) * (y4 - y3);
        //Vector product of vectors 3_4 and 3_2
        double vector_prod4 = (x4 - x3) * (y2 - y3) - (x2 - x3) * (y4 - y3);
        if (vector_prod3 * vector_prod4 > 0)
            return false;

        return true;
    }
}
