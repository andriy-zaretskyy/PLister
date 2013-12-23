package com.azar.plister.model;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Created by azar on 12/9/13.
 */
public final class BucketIntsanceCreator implements InstanceCreator<Bucket> {
    @Override
    public Bucket createInstance(Type type) {
        return new SimpleBucket();
    }
}
