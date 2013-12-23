package com.azar.plister.model;

import java.util.List;

/**
 * Created by azar on 11/29/13.
 */
public interface Storage {
    List<Bucket> getBuckets();

    void save();
}
