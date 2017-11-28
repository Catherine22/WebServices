package com.catherine.webservices.interfaces;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;

/**
 * Created by Catherine on 2017/11/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface ActivityResultListener {
    void addValueCallback(ValueCallback<Uri> filePathCallback);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void addValueCallbackL(ValueCallback<Uri[]> filePathCallback);
}
