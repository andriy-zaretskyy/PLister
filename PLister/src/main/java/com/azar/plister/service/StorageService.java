package com.azar.plister.service;

import com.azar.plister.model.Bucket;

import java.util.List;

/**
 * Created by azar on 11/29/13.
 */
public interface StorageService {
    List<Bucket> getBuckets();

    void save();
}
