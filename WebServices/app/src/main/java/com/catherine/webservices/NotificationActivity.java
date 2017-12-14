package com.catherine.webservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.catherine.webservices.network.MimeTypeList;
import com.catherine.webservices.toolkits.CLog;

import java.io.File;

/**
 * Created by Catherine on 2017/12/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NotificationActivity  extends BaseFragmentActivity {
    private final static String TAG = NotificationActivity.class.getSimpleName();
    private String name;
    private String path;

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_navigation, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    protected void onPermissionGranted() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            NotificationManager notificationManager = (NotificationManager) MyApplication.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.cancel(bundle.getInt("notificationID"));

            name = bundle.getString("name");
            path = bundle.getString("path");
            CLog.d(TAG, "name:" + name);
            CLog.d(TAG, "path:" + path);
            try {
                File file = new File(path);
                if (file.exists()) {
                    Uri data;
                    if (getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.N)
                        data = Uri.fromFile(file);
                    else
                        data = FileProvider.getUriForFile(NotificationActivity.this, getPackageName() + ".provider", file);
                    String type = getMimeType(data);
                    if (TextUtils.isEmpty(type))
                        type = "*/*";

                    CLog.d(TAG, "type:" + type);
                    if (MimeTypeList.MIME_TYPE_LIST.get("APK").equals(type)) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setClassName("com.android.packageinstaller",
                                "com.android.packageinstaller.PackageInstallerActivity");
                        intent.setDataAndType(data, type);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(data, type);
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    private String getMimeType(Uri uri) {
        if (uri == null)
            return null;

        String extension;
        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        if (!TextUtils.isEmpty(extension))
            return MimeTypeList.MIME_TYPE_LIST.get(extension.toUpperCase());
        else
            return extension;
    }
}
