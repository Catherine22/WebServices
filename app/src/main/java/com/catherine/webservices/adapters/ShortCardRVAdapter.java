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
import com.catherine.webservices.network.MyHttpURLConnection;
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
 * Cache images with DiskLruCache
 */
public class ShortCardRVAdapter extends RecyclerView.Adapter<ShortCardRVAdapter.MainRvHolder> {
    private final static String TAG = "ShortCardRVAdapter";
    private Context ctx;
    private List<String> images, titles, subtitles;
    private OnItemClickListener listener;
    private DiskLruCache diskLruCache;
    private Handler handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());

    public ShortCardRVAdapter(Context ctx, List<String> images, List<String> titles, List<String> subtitles, OnItemClickListener listener) {
        this.ctx = ctx;
        this.images = images;
        this.titles = titles;
        this.subtitles = subtitles;
        this.listener = listener;
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        openDiskLruCache();
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_short_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        if (titles != null && titles.size() > position)
            mainRvHolder.tv_title.setText(titles.get(position));
        if (subtitles != null && subtitles.size() > position)
            mainRvHolder.tv_subtitle.setText(subtitles.get(position));
        if (images != null && images.size() > position) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (diskLruCache.isClosed())
                            openDiskLruCache();

                        final String key = Encryption.doMd5Safely(new ByteArrayInputStream(images.get(position).getBytes()));
                        DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                        if (snapshot != null) {
                            if (subtitles != null) {
                                final Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                                ((Activity) ctx).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CLog.Companion.d(TAG, "cache");
                                        subtitles.set(position, "cache");
                                        mainRvHolder.tv_subtitle.setText(subtitles.get(position));
                                        mainRvHolder.iv_main.setImageBitmap(bitmap);

                                    }
                                });
                            }
                        } else {
                            CLog.Companion.d(TAG, "fresh");
                            URL url = new URL(images.get(position));
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            //Show images at first in case the external/internal storage not works.
                            final Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                            conn.disconnect();
                            ((Activity) ctx).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CLog.Companion.d(TAG, "fresh");
                                    subtitles.set(position, "fresh");
                                    mainRvHolder.tv_subtitle.setText(subtitles.get(position));
                                    mainRvHolder.iv_main.setImageBitmap(bitmap);

                                }
                            });

                            //Cache bitmap
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(getCompressFormat(images.get(position)), 100, stream);
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
                                CLog.Companion.d(TAG, "Cached image successfully.");
                            } else {
                                editor.abort();
                            }
                            diskLruCache.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (subtitles != null) {
                                    subtitles.set(position, "IOException");
                                    mainRvHolder.tv_subtitle.setText(subtitles.get(position));
                                }
                            }
                        });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (subtitles != null) {
                                    subtitles.set(position, "NullPointerException");
                                    mainRvHolder.tv_subtitle.setText(subtitles.get(position));
                                }
                            }
                        });
                    }
                }
            });
        }
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
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public void setSubtitles(List<String> subtitles) {
        this.subtitles = subtitles;
    }


    private void openDiskLruCache() {
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
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
