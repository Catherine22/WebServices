package com.catherine.webservices.toolkits;

import android.content.res.AssetManager;
import android.os.Environment;

import com.catherine.webservices.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Catherine on 2017/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class FileUtils {
    private final static String TAG = "FileUtils";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void copyAssets() {
        AssetManager assetManager = MyApplication.INSTANCE.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            CLog.Companion.e(TAG, e.getMessage() + "\nFailed to get asset file list.");
        }
        if (files != null) {
            for (String filename : files) {
                if ("邓俊辉_数据结构.pdf".equals(filename) || "big_o_cheat_sheet_poster.jpg".equals(filename)) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        File outFile = new File(MyApplication.INSTANCE.getDataCacheDir(), filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch (IOException e) {
                        CLog.Companion.e(TAG, e.getMessage() + "\nFailed to copy asset file: " + filename);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                }
            }
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
