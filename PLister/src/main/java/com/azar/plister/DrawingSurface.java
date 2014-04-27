package com.azar.plister;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.SimpleSelection;
import com.azar.plister.service.ApplicationServices;
import com.azar.plister.service.BucketService;
import com.azar.plister.service.ImageAnalyzer;
import com.azar.plister.service.ImageProvider;
import com.azar.plister.service.SelectionParams;


/**
 * Created by Azar on 10/22/13.
 */
public final class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    protected DrawThread thread;
    private GestureDetector gestureDetector;
    private Boolean _run;
    private Bucket bucket = null;
    private BucketService bucketService = ApplicationServices.INSTANCE.get(BucketService.class);
    private ImageAnalyzer analyzer = ApplicationServices.INSTANCE.get(ImageAnalyzer.class);
    private ImageProvider provider = null;
    private ContentResolver resolver;
    private Point currentPointStart = null;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DrawingSurface(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.getHolder().addCallback(this);
    }

    private void setRun(boolean run) {
        _run = run;
    }

    public void setBucket(Bucket src, ContentResolver resolver) {
        this.bucket = src;
        this.resolver = resolver;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return gestureDetector.onTouchEvent(event);
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
        }

        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            gestureDetector = new GestureDetector(this.getContext(), new GestureListener());
            Bitmap backgroundNotScaled = this.bucketService.getBackground(this.bucket, resolver);
            Bitmap background = this.bucketService.getScaledBitmap(backgroundNotScaled, this.getWidth(), this.getHeight());

            this.analyzer.initModel(background);
            this.provider = ApplicationServices.INSTANCE.createImageProvider(background, this.bucket);

            this.setRun(true);
            thread = new DrawThread();
            thread.start();
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (this.thread != null) {
                try {
                    this.setRun(false);
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
        }
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                Point currentPointStart = new Point((int) e1.getX(), (int) e1.getY());
                Point currentPointEnd = new Point((int) e2.getX(), (int) e2.getY());

                if (currentPointEnd.x >= currentPointStart.x) {
                    addSelection(currentPointStart, currentPointEnd);
                } else {
                    removeSelection(currentPointStart, currentPointEnd);
                }

            } catch (Exception e) {
                ExeptionHandler.handle(e, getContext());
            }

            return true;
        }

        private void removeSelection(Point currentPointStart, Point currentPointEnd) {
            bucketService.removeNearest(bucket, new SimpleSelection(currentPointEnd, currentPointStart));
        }

        private void addSelection(Point currentPointStart, Point currentPointEnd) {
            SelectionParams params = new SelectionParams(currentPointStart, currentPointEnd);
            bucket.addSelection(analyzer.getSelection(params));
        }
    }

    private class DrawThread extends Thread {

        @Override
        public synchronized void run() {
            Canvas canvas = null;
            while (_run) {
                try {
                    canvas = DrawingSurface.this.getHolder().lockCanvas(null);
                    if (canvas != null) {
                        canvas.drawBitmap(provider.getResultPicture(), 0, 0, null);
                    }
                } catch (Exception e) {
                    ExeptionHandler.handle(e, getContext());
                } finally {
                    if (_run) {
                        DrawingSurface.this.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
