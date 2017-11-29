package com.catherine.webservices.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.Encryption;
import com.catherine.webservices.toolkits.CLog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by Catherine on 2017/9/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * Cache images with DiskLruCache,
 * and hide images while there're exceptions.
 */
public class ImageCardRVAdapter extends RecyclerView.Adapter<ImageCardRVAdapter.MainRvHolder> {
    private final static String TAG = "ImageCardRVAdapter";
    private Context ctx;
    private boolean offlineMode;
    private List<ImageCard> entities;
    private OnItemClickListener listener;
    private DiskLruCache diskLruCache;
    private NetworkHelper helper;
    private Handler handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());

    public ImageCardRVAdapter(Context ctx, List<ImageCard> entities, boolean offlineMode, OnItemClickListener listener) {
        this.ctx = ctx;
        this.entities = entities;
        this.offlineMode = offlineMode;
        this.listener = listener;
        openDiskLruCache();
        helper = new NetworkHelper();
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_short_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        mainRvHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cv:
                        listener.onItemClick(view, position);
                        break;
                }
            }
        });
        mainRvHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                switch (view.getId()) {
                    case R.id.cv:
                        listener.onItemLongClick(view, position);
                        break;
                }
                return false;
            }
        });

        if (entities != null && entities.size() > position) {

            if (entities.get(position).title != null) {
                mainRvHolder.tv_title.setVisibility(View.VISIBLE);
                mainRvHolder.tv_title.setText(entities.get(position).title);
            }
            if (entities.get(position).subtitle != null) {
                mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
                mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
            }
            if (entities.get(position).image != null) {
                mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mainRvHolder.iv_main.setImageDrawable(ctx.getResources().getDrawable(R.drawable.default_pic));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Stop the process when showed the image and tried to cache it.

                        final String key = Encryption.doMd5Safely(new ByteArrayInputStream(entities.get(position).image.getBytes()));
                        try {
                            //1. Show caches when there're caches in the storage.
                            if (diskLruCache.isClosed())
                                openDiskLruCache();

                            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                            if (snapshot != null) {
                                final Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                                ((Activity) ctx).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CLog.d(TAG, "show cache " + position);
                                        entities.get(position).subtitle = "cache";
                                        mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
                                        mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                                        mainRvHolder.iv_main.setImageBitmap(bitmap);

                                    }
                                });

                                //Done
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            CLog.e(TAG, "DiskLruCache error");
                        }

                        //2. You will go on once there're no caches or caught exceptions.
                        try {
                            URL url = new URL(entities.get(position).image);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");//default
                            conn.setDoInput(true);//default
                            conn.setUseCaches(false);
                            conn.setConnectTimeout(MyHttpURLConnection.CONNECT_TIMEOUT);

                            //Show images at first in case the external/internal storage not works.
                            final Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                            conn.disconnect();

                            if (bitmap != null) {
                                ((Activity) ctx).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CLog.d(TAG, "fresh");
                                        entities.get(position).subtitle = "fresh";
                                        mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
                                        mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                                        mainRvHolder.iv_main.setImageBitmap(bitmap);

                                    }
                                });
                                //3. Cache the bitmap
                                try {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(getCompressFormat(entities.get(position).image), 100, stream);
                                    InputStream is = new ByteArrayInputStream(stream.toByteArray());
                                    DiskLruCache.Editor editor = diskLruCache.edit(key);
                                    if (editor != null) {
                                        BufferedInputStream bis = new BufferedInputStream(is, MyHttpURLConnection.MAX_CACHE_SIZE);
                                        BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(0), MyHttpURLConnection.MAX_CACHE_SIZE);

                                        byte[] buffer = new byte[1024]; // or other buffer size
                                        int read = -1;
                                        while ((read = bis.read(buffer)) != -1) {
                                            bos.write(buffer, 0, read);
                                        }
                                        is.close();
                                        bis.close();
                                        bos.flush();
                                        bos.close();
                                        editor.commit();
                                        CLog.d(TAG, "Cached the image successfully.");
                                    } else {
                                        editor.abort();
                                    }
                                    diskLruCache.flush();
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    CLog.e(TAG, "Failed to cache the image");
                                }
                            } else {
                                handleErrorPics(position, mainRvHolder);
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                            CLog.e(TAG, "HttpURLConnection error");
                            handleErrorPics(position, mainRvHolder);
                        }
                    }
                });
            }
        }
    }

    private void handleErrorPics(final int position, final MainRvHolder mainRvHolder) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (offlineMode) {
                    if (helper.isNetworkHealthy()) {
                        entities.get(position).subtitle = "error";
                        mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
                        mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                        //Show default picture
                        mainRvHolder.iv_main.setImageDrawable(ctx.getResources().getDrawable(R.drawable.default_pic));
                    } else {
                        //Scenario1: Remove the item instead of showing the default picture in offline mode.
//                        if (entities.size() > position) {
//                            entities.remove(position);
//                            notifyDataSetChanged();
//                        }
                        //Scenario2:
                        entities.get(position).subtitle = "failed to cache";
                        mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
                        mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                        //Show default picture
                        mainRvHolder.iv_main.setImageDrawable(ctx.getResources().getDrawable(R.drawable.default_pic));
                    }
                } else {
                    entities.get(position).subtitle = "error";
                    mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
                    mainRvHolder.iv_main.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mainRvHolder.iv_main.setVisibility(View.VISIBLE);
                    //Show default picture
                    mainRvHolder.iv_main.setImageDrawable(ctx.getResources().getDrawable(R.drawable.default_pic));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    /**
     * @param entities
     * @param shrinkList Remove data which hasn't been cached.
     */
    public void setImageCards(List<ImageCard> entities, boolean shrinkList) {
        CLog.d(TAG, "total:" + entities.size());
        int count = 0;
        if (shrinkList) {
            if (diskLruCache.isClosed())
                openDiskLruCache();
            for (int i = 0; i < entities.size(); i++) {
                final String key = Encryption.doMd5Safely(new ByteArrayInputStream(entities.get(i).image.getBytes()));
                try {
                    DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                    if (snapshot == null) {
                        entities.remove(i);
                        count++;
                    }
                } catch (Exception e) {
                    entities.remove(i);
                    count++;
                }
            }
        }
        this.entities = entities;
        CLog.d(TAG, "removed:" + count);
        CLog.d(TAG, "entities size:" + this.entities.size() + " " + shrinkList);
    }

    private void openDiskLruCache() {
        CLog.i(TAG, "open DiskLruCache");
        try {
            int version = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
            diskLruCache = DiskLruCache.open(MyApplication.INSTANCE.getDiskCacheDir("image"), version, 1, (long) MyHttpURLConnection.MAX_CACHE_SIZE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCache() {
        try {
            openDiskLruCache();
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap.CompressFormat getCompressFormat(String url) {
        String lowerUrl = url.toLowerCase(Locale.ENGLISH);
        if (lowerUrl.endsWith(".jpg")) {
            return Bitmap.CompressFormat.JPEG;
        } else if (lowerUrl.endsWith(".png")) {
            return Bitmap.CompressFormat.PNG;
        } else if (lowerUrl.endsWith(".webp")) {
            return Bitmap.CompressFormat.WEBP;
        }
        return Bitmap.CompressFormat.JPEG;
    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_subtitle;
        ImageView iv_main;
        CardView cv;

        MainRvHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            iv_main = itemView.findViewById(R.id.iv_main);
            cv = itemView.findViewById(R.id.cv);
        }
    }
}
