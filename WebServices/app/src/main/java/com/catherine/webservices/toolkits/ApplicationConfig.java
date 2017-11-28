package com.catherine.webservices.toolkits;

import com.catherine.webservices.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private final String FILE_NAME = "config.dat";
    public final static String LOG_FILE_NAME = "web_logs.dat";

    public synchronized void writeWebViewLog(String log) {
        try {
            File dir = MyApplication.INSTANCE.getLogDir();
            File file = new File(dir, LOG_FILE_NAME);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] result = bos.toByteArray();
            String history = new String(result);

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            String content = String.format("%s%s:\n%s\n\n", history, getToday(), log);
            outputStreamWriter.write(content);
            outputStreamWriter.close();
            fos.flush();
            fos.close();

            bos.flush();
            bos.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean deleteConfig() {
        boolean finished = true;
        try {
            File dir = MyApplication.INSTANCE.getLogDir();
            if (dir.exists()) {
                File file = new File(dir, FILE_NAME);
                File file2 = new File(dir, LOG_FILE_NAME);
                finished = (file.delete() && file2.delete());
            }
        } catch (Exception e) {
            finished = false;
            e.printStackTrace();
        }
        return finished;
    }

    private String getToday() {
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(presentTime_Date);
    }
}
