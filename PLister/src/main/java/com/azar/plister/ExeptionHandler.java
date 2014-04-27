package com.azar.plister;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by azar on 4/27/14.
 */
public class ExeptionHandler {
    public static void handle(Exception e, Context context) {
        e.printStackTrace();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(e.getMessage());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
