package com.azar.plister;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.SimpleBucket;
import com.azar.plister.model.SimpleStorage;
import com.azar.plister.model.Storage;
import com.azar.plister.model.StorageException;

public final class ViewListActivity extends Activity {

    // TextView textTargetUri;
    private DrawingSurface targetImage;
    private Storage storage;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        targetImage = (DrawingSurface) findViewById(R.id.targetimage);
        storage = new SimpleStorage(getFilesDir());
        String bucketId = (String) getIntent().getExtras().getString("bucket_uid");
        SimpleBucket sample = new SimpleBucket();
        sample.setUid(bucketId);


        try {
            Bucket current = storage.getBuckets().get(storage.getBuckets().indexOf(sample));
            targetImage.setBucket(current, getContentResolver());
        } catch (StorageException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.storage.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }
}
