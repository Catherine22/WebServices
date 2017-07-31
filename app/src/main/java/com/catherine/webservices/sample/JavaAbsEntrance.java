package com.catherine.webservices.sample;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Catherine on 2017/7/31.
 */

public abstract class JavaAbsEntrance {
    public abstract String formatDate(Date date);

    public @NonNull String formatTime(@NonNull Date date) {
        return new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(date);
    }
}
