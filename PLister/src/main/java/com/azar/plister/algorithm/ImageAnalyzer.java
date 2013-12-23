package com.azar.plister.algorithm;

import android.graphics.Bitmap;

import com.azar.plister.model.Selection;

/**
 * Created by azar on 11/29/13.
 */
public interface ImageAnalyzer {
    void initModel(Bitmap src);

    Bitmap getModel();

    Selection getSelection(SelectionParams params);
}
