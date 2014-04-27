package com.azar.plister.service;

import android.graphics.Bitmap;

import com.azar.plister.model.Selection;

/**
 * Created by azar on 11/29/13.
 */
public interface ImageAnalyzer extends Service {
    void initModel(Bitmap src);

    Bitmap getModel();

    Selection getSelection(SelectionParams params);
}
