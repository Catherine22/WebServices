package com.catherine.webservices.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TextCardRVAdapter extends RecyclerView.Adapter<TextCardRVAdapter.MainRvHolder> {
    private Context ctx;
    private List<String> contents, titles, subtitles;
    private OnItemClickListener listener;
    private boolean fromHtml = false;

    public TextCardRVAdapter(Context ctx, List<String> contents, List<String> titles, List<String> subtitles, OnItemClickListener listener) {
        this.ctx = ctx;
        this.contents = contents;
        this.titles = titles;
        this.subtitles = subtitles;
        this.listener = listener;
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_text_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        mainRvHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        mainRvHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(view, position);
                return false;
            }
        });
        if (titles != null && titles.get(position) != null) {
            mainRvHolder.tv_title.setVisibility(View.VISIBLE);
            mainRvHolder.tv_title.setText(titles.get(position));
        }
        if (subtitles != null && subtitles.get(position) != null) {
            mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
            mainRvHolder.tv_subtitle.setText(subtitles.get(position));
        }
        if (contents != null && contents.get(position) != null) {
            mainRvHolder.tv_main.setVisibility(View.VISIBLE);
            //Enable TextView open url links
            mainRvHolder.tv_main.setMovementMethod(LinkMovementMethod.getInstance());
            if (fromHtml) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mainRvHolder.tv_main.setText(Html.fromHtml(contents.get(position),
                            Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mainRvHolder.tv_main.setText(Html.fromHtml(contents.get(position)));
                }
            }
            mainRvHolder.tv_main.setText(subtitles.get(position));
        } else {
            mainRvHolder.tv_main.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void setFromHtml(boolean fromHtml) {
        this.fromHtml = fromHtml;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public void setSubtitles(List<String> subtitles) {
        this.subtitles = subtitles;
    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_subtitle;
        TextView tv_main;

        MainRvHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            tv_main = itemView.findViewById(R.id.tv_main);
        }
    }
}
