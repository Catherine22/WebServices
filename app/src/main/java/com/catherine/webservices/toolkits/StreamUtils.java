package com.catherine.webservices.toolkits;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class StreamUtils {
    public static String loadAssets(Context ctx, String fileName) {
        String content = null;
        try {
            InputStream is = ctx.getAssets().open(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] result = bos.toByteArray();
            content = new String(result);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
