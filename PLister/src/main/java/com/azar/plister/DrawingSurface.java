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

import com.azar.plister.algorithm.BasicImageAnalyzer;
import com.azar.plister.algorithm.BasicImageProvider;
import com.azar.plister.algorithm.ImageAnalyzer;
import com.azar.plister.algorithm.ImageProvider;
import com.azar.plister.algorithm.SelectionParams;
import com.azar.plister.model.Bucket;
import com.azar.plister.model.SimpleSelection;
import com.azar.plister.model.StorageException;

/**
 * Created by Azar on 10/22/13.
 */
public final class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Boolean _run;
    protected DrawThread thread;
    private Bucket bucket = null;
    private ImageAnalyzer analyzer = new BasicImageAnalyzer();
    private ImageProvider provider = null;
    private  ContentResolver resolver;

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureListener());

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

    private void setRun(boolean run)
    {
        _run = run;
    }

    public void setBucket(Bucket src, ContentResolver resolver) throws StorageException {
        this.bucket = src;
        this.resolver = resolver;
    }


    private Point currentPointStart = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            currentPointStart = new Point((int)event.getX(), (int)event.getY());
        }
        else if(event.getAction() == MotionEvent.ACTION_UP && this.analyzer != null){
            if(currentPointStart == null)
            {
                currentPointStart = new Point((int)event.getX(), (int)event.getY());
            }
            Point currentPointEnd = new Point((int)event.getX(), (int)event.getY());

            if(currentPointEnd.x >= currentPointStart.x)
            {
                addSelection(currentPointStart, currentPointEnd);
            }
            else
            {
                removeSelection(currentPointStart, currentPointEnd);
            }
        }
        return true;
    }

    private void removeSelection(Point currentPointStart, Point currentPointEnd) {
        this.bucket.RemoveNearest(new SimpleSelection(currentPointStart, currentPointEnd));
    }

    private void addSelection(Point currentPointStart, Point currentPointEnd) {
        SelectionParams params = new SelectionParams(currentPointStart, currentPointEnd);
        this.bucket.AddSelection(this.analyzer.getSelection(params));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Bitmap backgroundNotScaled = this.bucket.getBackground(resolver);
        double ratio = ((double)backgroundNotScaled.getWidth()) / backgroundNotScaled.getHeight();

        double newHeight = this.getHeight();
        double newWidth = ((double)this.getHeight()) * ratio;
        if(newWidth > this.getWidth())
        {
            newHeight = ((double)this.getWidth()) / ratio;
            newWidth = this.getWidth();
        }

        Bitmap background = Bitmap.createScaledBitmap(backgroundNotScaled, (int)newWidth, (int)newHeight, true);
        this.analyzer.initModel(background);
        //this.provider = new BasicImageProvider(this.analyzer.getModel(), this.bucket);
        this.provider = new BasicImageProvider(background, this.bucket);

        this.setRun(true);
        thread = new DrawThread(holder, provider);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(this.thread != null)
        {
            try
            {
                    this.setRun(false);
                    thread.join();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    class DrawThread extends  Thread{
        private ImageProvider provider;
        private SurfaceHolder surfaceHolder;

        public DrawThread(SurfaceHolder surfaceHolder, ImageProvider provider){
            if (surfaceHolder == null){
                throw new NullPointerException("surfaceHolder can not be null");
            }

            if (provider == null){
                throw new NullPointerException("provider can not be null");
            }

            this.surfaceHolder = surfaceHolder;
            this.provider = provider;
        }

        @Override
        public void run() {
            synchronized (surfaceHolder)
            {
                Canvas canvas = null;
                while (_run){
                    try{
                        canvas = surfaceHolder.lockCanvas(null);
                        if(canvas != null)
                        {
                            canvas.drawBitmap(provider.getResultPicture(), 0, 0, null);
                        }
                    }
                    finally {
                        if (_run)
                        {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }
}
