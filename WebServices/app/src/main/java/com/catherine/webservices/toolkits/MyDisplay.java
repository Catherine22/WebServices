package com.catherine.webservices.toolkits;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Catherine on 2017/10/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyDisplay {
    private static final String TAG = "MyDisplay";

    public static Point getScreenSize() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();

        float density = dm.density;

        int screenWidthDip = dm.widthPixels;
        int screenHeightDip = dm.heightPixels;

//        float screenWidthPx = (int) (dm.widthPixels * density + 0.5f);
//        float screenHeightPx = (int) (dm.heightPixels * density + 0.5f);

        Point size;
        size = new Point(screenWidthDip, screenHeightDip);
        return size;
    }

    /**
     * Covert dp to px
     *
     * @param dp      dip
     * @param context Context
     * @return px pixel
     */
    public static int convertDpToPixel(int dp, Context context) {
        int px;
        px = Math.round(dp * getDensity(context));
        return px;
    }

    /**
     * Covert px to dp
     *
     * @param px      pixel
     * @param context Context
     * @return dp dip
     */
    public static int convertPixelToDp(int px, Context context) {
        int dp;
        dp = Math.round(px / getDensity(context));
        return dp;
    }

    /**
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     *
     * @param context Context
     * @return density
     */
    private static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}
