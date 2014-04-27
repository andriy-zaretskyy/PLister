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
    private final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureListener());
    protected DrawThread thread;
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
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                currentPointStart = new Point((int) event.getX(), (int) event.getY());
            } else if (event.getAction() == MotionEvent.ACTION_UP && this.analyzer != null) {
                if (currentPointStart == null) {
                    currentPointStart = new Point((int) event.getX(), (int) event.getY());
                }
                Point currentPointEnd = new Point((int) event.getX(), (int) event.getY());

                if (currentPointEnd.x >= currentPointStart.x) {
                    addSelection(currentPointStart, currentPointEnd);
                } else {
                    removeSelection(currentPointStart, currentPointEnd);
                }
            }
            return true;
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
            return false;
        }
    }

    private void removeSelection(Point currentPointStart, Point currentPointEnd) {
        try {
            this.bucketService.removeNearest(this.bucket, new SimpleSelection(currentPointEnd, currentPointStart));
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
        }
    }

    private void addSelection(Point currentPointStart, Point currentPointEnd) {
        try {
            SelectionParams params = new SelectionParams(currentPointStart, currentPointEnd);
            this.bucket.addSelection(this.analyzer.getSelection(params));
        } catch (Exception e) {
            ExeptionHandler.handle(e, getContext());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Bitmap backgroundNotScaled = this.bucketService.getBackground(this.bucket, resolver);
            Bitmap background = this.bucketService.getScaledBitmap(backgroundNotScaled,this.getWidth(), this.getHeight());

            this.analyzer.initModel(background);
            this.provider = ApplicationServices.INSTANCE.createImageProvider(background, this.bucket);

            this.setRun(true);
            thread = new DrawThread(holder, provider);
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
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private class DrawThread extends Thread {
        private ImageProvider provider;
        private SurfaceHolder surfaceHolder;

        public DrawThread(SurfaceHolder surfaceHolder, ImageProvider provider) {
            if (surfaceHolder == null) {
                throw new NullPointerException("surfaceHolder can not be null");
            }

            if (provider == null) {
                throw new NullPointerException("provider can not be null");
            }

            this.surfaceHolder = surfaceHolder;
            this.provider = provider;
        }

        @Override
        public synchronized void run() {
            try {
                Canvas canvas = null;
                while (_run) {
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas != null) {
                            canvas.drawBitmap(provider.getResultPicture(), 0, 0, null);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (_run) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            } catch (Exception e) {
                ExeptionHandler.handle(e, getContext());
            }
        }
    }
}
