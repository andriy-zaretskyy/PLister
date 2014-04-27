package com.azar.plister.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;

import com.azar.plister.model.Selection;
import com.azar.plister.model.SimpleSelection;

/**
 * Created by azar on 11/29/13.
 */
class DefaultImageAnalyzer implements ImageAnalyzer {
    BackgroundType backgroundType;
    private Bitmap modelBitmap;

    public DefaultImageAnalyzer() {
    }

    private static int getBrightness(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Math.min(255, (int) Math.sqrt(red * red * .241 + green * green * .691 + blue * blue * 0.68));
    }

    @Override
    public void initModel(Bitmap src) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(src, 0, 0, paint);

        int[] pixels = new int[bmpMonochrome.getWidth() * bmpMonochrome.getHeight()];
        bmpMonochrome.getPixels(pixels, 0, bmpMonochrome.getWidth(), 0, 0, bmpMonochrome.getWidth(), bmpMonochrome.getHeight());

        double average = 0;
        long count = 1;
        int max = Color.red(pixels[0]);
        int min = Color.red(pixels[0]);

        for (int pixel : pixels) {
            int color = Color.red(pixel);
            average = (average * (count - 1) + color) / count;
            ++count;
            if (color > max) {
                max = color;
            }
            if (color < min) {
                min = color;
            }
        }

        long blackCount = 0;
        long whiteCount = 0;
        final double blackThreshold = average * 0.6;
        final double whiteThreshold = average * 1.6;

        for (int pixel : pixels) {
            if (Color.red(pixel) < blackThreshold) {
                blackCount++;
            }
            if (Color.red(pixel) > whiteThreshold) {
                whiteCount++;
            }
        }

        boolean isWhiteBackground = whiteCount > blackCount;
        if (whiteCount < pixels.length * 0.01 && blackCount > pixels.length * 0.01) {
            isWhiteBackground = true;
        }

        backgroundType = isWhiteBackground ? BackgroundType.White : BackgroundType.Black;

        for (int i = 0; i < pixels.length; ++i) {
            if (isWhiteBackground) {
                pixels[i] = Color.red(pixels[i]) < average * 0.8 ? Color.BLACK : Color.WHITE;
            } else {
                pixels[i] = Color.red(pixels[i]) < whiteThreshold ? Color.WHITE : Color.BLACK;
            }
        }
        bmpMonochrome.setPixels(pixels, 0, bmpMonochrome.getWidth(), 0, 0, bmpMonochrome.getWidth(), bmpMonochrome.getHeight());
        modelBitmap = bmpMonochrome;
    }

    @Override
    public Bitmap getModel() {
        return this.modelBitmap;
    }

    @Override
    public Selection getSelection(SelectionParams params) {
        if (this.modelBitmap == null) {
            throw new IllegalStateException("Model is not initialized. Call initModel to initialize it.");
        }

        Point[] points = new Point[2];
        int whitespace = 0;
        int fluctuation = 0;
        if (backgroundType == BackgroundType.Black) {
            whitespace = (int) ((double) this.modelBitmap.getWidth()) / 35;
            fluctuation = (int) ((double) this.modelBitmap.getHeight()) / 45;
        } else {
            whitespace = (int) ((double) this.modelBitmap.getWidth()) / 20;
            fluctuation = (int) ((double) this.modelBitmap.getHeight()) / 40;
        }

        int threshold = (int) ((double) whitespace / 10);
        int xresultstart = Math.min(params.getTouchPointFirst().x, this.modelBitmap.getWidth());
        int yresultstart = Math.min(params.getTouchPointFirst().y, this.modelBitmap.getHeight());


        int lystart = Math.max(params.getTouchPointFirst().y - fluctuation, 0);
        int lyend = Math.min(params.getTouchPointFirst().y + fluctuation, this.modelBitmap.getHeight() - 1);

        for (int ly = lystart; ly <= lyend; ++ly) {
            int whitePixels = 0;
            int xstart = Math.min(params.getTouchPointFirst().x, this.modelBitmap.getWidth() - 1);

            for (; xstart > 0; --xstart) {
                if (Color.red(this.modelBitmap.getPixel(xstart, ly)) > 250) {
                    whitePixels++;
                } else {
                    whitePixels = 0;
                }

                if (whitePixels > whitespace) {
                    break;
                }
            }

            if (xresultstart > xstart && whitePixels > threshold) {
                xresultstart = xstart;
                yresultstart = ly;
            }
        }


        int xresultend = Math.min(params.getTouchPointLast().x, this.modelBitmap.getWidth() - 1);
        int yresultend = Math.min(params.getTouchPointLast().y, this.modelBitmap.getHeight() - 1);

        lystart = Math.max(params.getTouchPointLast().y - fluctuation, 0);
        lyend = Math.min(params.getTouchPointLast().y + fluctuation, this.modelBitmap.getHeight() - 1);

        for (int ly = lystart; ly <= lyend; ++ly) {
            int whitePixels = 0;
            int xend = Math.min(params.getTouchPointLast().x, this.modelBitmap.getWidth() - 1);

            for (; xend < this.modelBitmap.getWidth() - 1; ++xend) {
                if (Color.red(this.modelBitmap.getPixel(xend, ly)) > 250) {
                    whitePixels++;
                } else {
                    whitePixels = 0;
                }

                if (whitePixels > whitespace) {
                    break;
                }
            }

            if (xresultend < xend && whitePixels > threshold) {
                xresultend = xend;
                yresultend = ly;
            }

        }

        return new SimpleSelection(new Point(xresultstart, yresultstart), new Point(xresultend, yresultend));
    }


    private enum BackgroundType {
        Black,
        White
    }
}
