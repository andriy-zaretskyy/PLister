package com.azar.plister.service;

import android.graphics.Bitmap;

import com.azar.plister.model.Bucket;
import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

import java.io.File;

/**
 * Created by azar on 4/26/14.
 */
public enum ApplicationServices {
    INSTANCE;

    private final ClassToInstanceMap<Service> instances;

    ApplicationServices() {
        SelectionService selectionService = new DefaultSelectionService();
        instances = ImmutableClassToInstanceMap.<Service>builder().
                put(BucketService.class, new DefaultBucketService(selectionService)).
                put(ImageAnalyzer.class, new DefaultImageAnalyzer()).
                put(SelectionService.class, selectionService).
                build();
    }

    public StorageService createStorage(File file) {
        return new DefaultStorage(file);
    }

    public ImageProvider createImageProvider(Bitmap background, Bucket bucket) {
        return new DefaultImageProvider(background, bucket);
    }

    public <T extends Service> T get(Class<T> clazz) {
        Preconditions.checkArgument(instances.containsKey(clazz), "Instance does not contain class" + clazz);
        return (T) instances.get(clazz);
    }

}
