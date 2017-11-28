package com.catherine.webservices.toolkits;

import android.content.Context;
import android.os.Environment;

import com.catherine.webservices.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Catherine on 2017/11/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ApplicationConfig {
    private Context ctx;
    private final String FILE_NAME = "config.dat";
    public final static String LOG_FILE_NAME = "web_logs.dat";

    public ApplicationConfig(Context ctx) {
        this.ctx = ctx;
    }

    public synchronized void writeWebViewLog(String log) {
        try {
            File dir = MyApplication.INSTANCE.getLogDir();
            File file = new File(dir, LOG_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            String content = String.format("%s%s:\n%s\n\n", readWebViewLog(), getToday(), log);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readWebViewLog() {
        String log = "";
        try {
            File file = new File(MyApplication.INSTANCE.getLogDir(), LOG_FILE_NAME);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] result = bos.toByteArray();
            log = new String(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }

    public synchronized void deleteConfig() {
        try {
            File dir = MyApplication.INSTANCE.getLogDir();
            if (dir.exists()) {
                File file = new File(dir, FILE_NAME);
                file.delete();

                File file2 = new File(dir, LOG_FILE_NAME);
                file2.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getToday() {
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(presentTime_Date);
    }
}
