package com.catherine.webservices.components;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;

/**
 * Created by Catherine on 2017/11/2.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DialogManager {
    private static int alertIcon = R.drawable.ic_warning_black_24dp;
    private static int errorIcon = R.drawable.ic_error_black_24dp;
    private static MyAlertDialog alertDialog, errorDialog;

    private static String getAlertTitle(Context ctx) {
        return ctx.getString(R.string.dialog_title_hint);
    }

    private static String getErrorTitle(Context ctx) {
        return ctx.getString(R.string.dialog_title_error);
    }


    public static void showPermissionDialog(final Context ctx, String message, DialogInterface.OnClickListener negativeButtonListener) {
        if (ctx == null)
            return;
        MyAlertDialog permissionDialog = new MyAlertDialog(ctx);
        permissionDialog.setIcon(alertIcon)
                .setCancelable(false)
                .setTitle(getAlertTitle(ctx))
                .setMessage(message)
                .setNegativeButton(ctx.getResources().getString(R.string.no_n_close), negativeButtonListener)
                .setPositiveButton(ctx.getResources().getString(R.string.yes_n_open), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", ctx.getPackageName(), null));
                        ((Activity) ctx).startActivityForResult(intent, Constants.OPEN_SETTINGS);
                    }
                });
        permissionDialog.showDialog();
    }

    /**
     * For special permission like write_settings
     *
     * @param ctx
     * @param message
     * @param positiveButtonListener
     * @param negativeButtonListener
     */
    public static void showSpecPermissionDialog(final Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        if (ctx == null)
            return;
        MyAlertDialog specPermissionDialog = new MyAlertDialog(ctx);
        specPermissionDialog.setIcon(alertIcon)
                .setCancelable(false)
                .setTitle(getAlertTitle(ctx))
                .setMessage(message)
                .setNegativeButton(ctx.getResources().getString(R.string.no_n_close), negativeButtonListener)
                .setPositiveButton(ctx.getResources().getString(R.string.yes_n_open), positiveButtonListener);
        specPermissionDialog.showDialog();
    }

    public static void showAlertDialog(Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener) {
        showAlertDialog(ctx, message, positiveButtonListener, null);
    }

    public static void showAlertDialog(Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        if (ctx == null)
            return;
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        alertDialog = new MyAlertDialog(ctx);
        if (negativeButtonListener == null) {
            alertDialog.setIcon(alertIcon)
                    .setTitle(getAlertTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.confirm_button), positiveButtonListener)
                    .setCancelable(false);
        } else {
            alertDialog.setIcon(alertIcon)
                    .setTitle(getAlertTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.confirm_button), positiveButtonListener)
                    .setNegativeButton(ctx.getString(R.string.cancel_button), negativeButtonListener)
                    .setCancelable(false);
        }
        alertDialog.showDialog();
    }


    public static void showErrorDialog(Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener) {
        showErrorDialog(ctx, message, positiveButtonListener, null);
    }

    public static void showErrorDialog(Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        if (ctx == null)
            return;
        if (errorDialog != null && errorDialog.isShowing())
            errorDialog.dismiss();
        errorDialog = new MyAlertDialog(ctx);
        if (negativeButtonListener == null) {
            errorDialog.setIcon(errorIcon)
                    .setTitle(getErrorTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.confirm_button), positiveButtonListener)
                    .setCancelable(false);
        } else {
            errorDialog.setIcon(errorIcon)
                    .setTitle(getAlertTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.confirm_button), positiveButtonListener)
                    .setNegativeButton(ctx.getString(R.string.cancel_button), negativeButtonListener)
                    .setCancelable(false);
        }
        errorDialog.showDialog();
    }

    public static void showRetryDialog(Context ctx, String message, DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        if (ctx == null)
            return;
        MyAlertDialog retryDialog = new MyAlertDialog(ctx);
        if (negativeButtonListener == null) {
            retryDialog.setIcon(errorIcon)
                    .setTitle(getErrorTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.retry_button), positiveButtonListener)
                    .setCancelable(false);
        } else {
            retryDialog.setIcon(errorIcon)
                    .setTitle(getAlertTitle(ctx))
                    .setMessage(message)
                    .setPositiveButton(ctx.getString(R.string.retry_button), positiveButtonListener)
                    .setNegativeButton(ctx.getString(R.string.cancel_button), negativeButtonListener)
                    .setCancelable(false);
        }
        retryDialog.showDialog();
    }
    
}
