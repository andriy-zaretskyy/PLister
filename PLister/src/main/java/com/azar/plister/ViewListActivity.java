package com.azar.plister;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.SimpleBucket;
import com.azar.plister.service.ApplicationServices;
import com.azar.plister.service.StorageService;

public final class ViewListActivity extends Activity {

    private DrawingSurface targetImage;
    private StorageService storageService;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.drawing_surface);

            targetImage = (DrawingSurface) findViewById(R.id.targetimage);
            storageService = ApplicationServices.INSTANCE.createStorage(getFilesDir());
            String bucketId = (String) getIntent().getExtras().getString("bucket_uid");
            SimpleBucket sample = new SimpleBucket();
            sample.setUid(bucketId);

            Bucket current = storageService.getBuckets().get(storageService.getBuckets().indexOf(sample));
            targetImage.setBucket(current, getContentResolver());
        } catch (Exception e) {
            ExeptionHandler.handle(e, ViewListActivity.this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            ExeptionHandler.handle(e, ViewListActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            this.storageService.save();
        } catch (Exception e) {
            ExeptionHandler.handle(e, ViewListActivity.this);
        }
    }
}
