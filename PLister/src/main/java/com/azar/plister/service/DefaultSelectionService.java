package com.azar.plister.service;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.azar.plister.model.Selection;

/**
 * Created by azar on 4/26/14.
 */
class DefaultSelectionService implements SelectionService {
    private final Paint paint;

    public DefaultSelectionService() {
        Paint template = new Paint();
        template.setDither(true);
        template.setColor(Color.RED);
        template.setStyle(Paint.Style.FILL_AND_STROKE);
        template.setStrokeJoin(Paint.Join.ROUND);
        template.setStrokeCap(Paint.Cap.ROUND);
        template.setStrokeWidth(10);
        paint = template;
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
            return distance(x, y, sx1, sy1);
        }

        double c2 = dotProduct(vx, vy, vx, vy);

        if (c2 <= c1) {
            return distance(x, y, sx2, sy2);
        }

        double b = c1 / c2;
        double bx = sx1 + b * vx;
        double by = sy1 + b * vy;

        return distance(x, y, bx, by);
    }

    private boolean hasIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        //(x1, y1), (x2, y2) - first segment
        //(x3, y3), (x4, y4) - second segment

        double vector_prod1 = (x3 - x1) * (y2 - y1) - (x2 - x1) * (y3 - y1);
        //Vector product of vectors 1_2 and 1_4
        double vector_prod2 = (x2 - x1) * (y4 - y1) - (x4 - x1) * (y2 - y1);
        //Vector product of vectors 3_4 and 3_1
        double vector_prod3 = (x4 - x3) * (y1 - y3) - (x1 - x3) * (y4 - y3);
        //Vector product of vectors 3_4 and 3_2
        double vector_prod4 = (x2 - x3) * (y4 - y3) - (x4 - x3) * (y2 - y3);

        return vector_prod3 * vector_prod4 > 0 && vector_prod1 * vector_prod2 > 0;
    }

    @Override
    public void draw(Canvas canvas, Selection selection) {
        canvas.drawLine(selection.getStart().x, selection.getStart().y, selection.getEnd().x, selection.getEnd().y, paint);
    }

    @Override
    public double getDistance(Selection first, Selection second) {
        if (hasIntersection(first.getStart().x, first.getStart().y, first.getEnd().x, first.getEnd().y, second.getStart().x, second.getStart().y, second.getEnd().x, second.getEnd().y)) {
            return 0;
        } else {
            double d1 = distanceToSegment(first.getStart().x, first.getStart().y, second.getStart().x, second.getStart().y, second.getEnd().x, second.getEnd().y);
            double d2 = distanceToSegment(first.getEnd().x, first.getEnd().y, second.getStart().x, second.getStart().y, second.getEnd().x, second.getEnd().y);
            double d3 = distanceToSegment(second.getStart().x, second.getStart().y, first.getStart().x, first.getStart().y, first.getEnd().x, first.getEnd().y);
            double d4 = distanceToSegment(second.getEnd().x, second.getEnd().y, first.getStart().x, first.getStart().y, first.getEnd().x, first.getEnd().y);

            return Math.min(Math.min(d1, d2), Math.min(d3, d4));
        }
    }
}
