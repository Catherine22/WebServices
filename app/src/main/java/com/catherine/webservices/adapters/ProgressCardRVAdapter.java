package com.catherine.webservices.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.ProgressBarInfo;
import com.catherine.webservices.interfaces.OnItemClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Catherine on 2017/9/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ProgressCardRVAdapter extends RecyclerView.Adapter<ProgressCardRVAdapter.MainRvHolder> {
    private final static String TAG = "ProgressCardRVAdapter";
    private Context ctx;
    private List<String> images, titles, subtitles, infos;
    private Handler handler;
    private OnItemClickListener onClickListener;
    private ProgressBarInfo[] progressList;

    public ProgressCardRVAdapter(Context ctx, List<String> images, List<String> titles, List<String> subtitles, List<String> infos) {
        this.ctx = ctx;
        this.images = images;
        this.titles = titles;
        this.subtitles = subtitles;
        this.infos = infos;
        progressList = new ProgressBarInfo[getItemCount()];
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_progress_card, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        if (titles != null && titles.size() > position) {
            mainRvHolder.tv_title.setVisibility(View.VISIBLE);
            mainRvHolder.tv_title.setText(titles.get(position));
        } else
            mainRvHolder.tv_title.setVisibility(View.GONE);

        if (subtitles != null && subtitles.size() > position) {
            mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
            mainRvHolder.tv_subtitle.setText(subtitles.get(position));
        } else
            mainRvHolder.tv_subtitle.setVisibility(View.GONE);

        if (infos != null && infos.size() > position) {
            mainRvHolder.tv_info.setVisibility(View.VISIBLE);
            mainRvHolder.tv_info.setText(infos.get(position));
        } else
            mainRvHolder.tv_info.setVisibility(View.GONE);

        if (images != null && images.size() > position) {
            mainRvHolder.iv_main.setVisibility(View.VISIBLE);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(images.get(position));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        final Bitmap b = BitmapFactory.decodeStream(input);
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainRvHolder.iv_main.setImageBitmap(b);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            mainRvHolder.iv_main.setVisibility(View.GONE);
        }

        if (progressList == null || progressList.length <= position || progressList[position] == null || progressList[position].MAX_PROGRESS < 0 || progressList[position].cur_progress < 0) {
            mainRvHolder.pb.setProgress(0);
            mainRvHolder.pb.setVisibility(View.INVISIBLE);
        } else {
            mainRvHolder.pb.setVisibility(View.VISIBLE);
            mainRvHolder.pb.setMax(progressList[position].MAX_PROGRESS);
            mainRvHolder.pb.setProgress(progressList[position].cur_progress);

            if (progressList[position].cur_progress == progressList[position].MAX_PROGRESS)
                mainRvHolder.pb.setVisibility(View.INVISIBLE);
        }
        mainRvHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClick(view, position);

            }
        });
        mainRvHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onClickListener.onItemLongClick(view, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void updateProgress(int pos, int MAX, int cur) {
        progressList[pos] = new ProgressBarInfo(MAX, cur);
    }

    public void updateInfo(List<String> infos) {
        this.infos = infos;
    }


    class MainRvHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_subtitle;
        TextView tv_info;
        ImageView iv_main;
        ProgressBar pb;
        CardView cv;

        MainRvHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            iv_main = itemView.findViewById(R.id.iv_main);
            tv_info = itemView.findViewById(R.id.tv_info);
            pb = itemView.findViewById(R.id.pb);
            cv = itemView.findViewById(R.id.cv);
        }
    }
}
