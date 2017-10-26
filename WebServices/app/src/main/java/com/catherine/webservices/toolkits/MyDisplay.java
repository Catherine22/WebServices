package com.catherine.webservices.toolkits;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
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

    /**
     * Draw an image in circular shape
     *
     * @param images
     * @param showShadow
     * @return
     */
    public static Bitmap drawCircularImage(Bitmap images, boolean showShadow) {
        Bitmap circleBitmap = Bitmap.createBitmap(images.getWidth(), images.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas c = new Canvas(circleBitmap);
        //圆的外圈
        int strokeWidth = images.getWidth() / 20;
        //半径
        int radius = images.getWidth() / 2 - strokeWidth;

        if (showShadow) {
            //第一层外圈,阴影
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);//抗锯齿
            paint.setStrokeWidth(strokeWidth);
            c.drawCircle(images.getWidth() / 2 + strokeWidth / 2, images.getHeight() / 2 + strokeWidth / 2, radius + strokeWidth / 2, paint);
        }
        //第二层外圈,圆的外围线
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);//抗锯齿
        paint.setStrokeWidth(strokeWidth / 2);
        c.drawCircle(images.getWidth() / 2, images.getHeight() / 2, radius + strokeWidth / 2, paint);

        //最内层的图片
        BitmapShader shader = new BitmapShader(images, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        //This will draw the image.
        c.drawCircle(images.getWidth() / 2, images.getHeight() / 2, radius, paint);
        return circleBitmap;
    }
}
