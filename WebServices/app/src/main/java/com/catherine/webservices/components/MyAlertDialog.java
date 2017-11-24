package com.catherine.webservices.components;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Catherine on 2017/10/31.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 * <p>
 * Singleton??
 */

class MyAlertDialog extends AlertDialog.Builder {
    private AlertDialog dialog;

    MyAlertDialog(Context ctx) {
        super(ctx);
    }

    void showDialog() {
        try {
            dialog = create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dismiss() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isShowing(){
        return dialog.isShowing();
    }
}