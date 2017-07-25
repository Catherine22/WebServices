package com.catherine.webservices.toolkits;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class StreamUtils {
    public static String inputStreamToString(InputStream is) {
        String content = null;
        try {
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
