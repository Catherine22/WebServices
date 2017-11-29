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
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TextCardRVAdapter extends RecyclerView.Adapter<TextCardRVAdapter.MainRvHolder> {
    private Context ctx;
    private List<TextCard> entities;
    private OnItemClickListener listener;
    private boolean fromHtml = false;

    public TextCardRVAdapter(Context ctx, List<TextCard> entities, OnItemClickListener listener) {
        this.ctx = ctx;
        this.entities = entities;
        this.listener = listener;
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_text_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        if (listener != null) {
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
        }
        if (entities == null || entities.size() == 0)
            return;

        TextCard tc = entities.get(position);
        if (tc.title != null) {
            mainRvHolder.tv_title.setVisibility(View.VISIBLE);
            mainRvHolder.tv_title.setText(tc.title);
        }
        if (tc.subtitle != null) {
            mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
            mainRvHolder.tv_subtitle.setText(tc.subtitle);
        }
        if (tc.contents != null) {
            mainRvHolder.tv_main.setVisibility(View.VISIBLE);
            //Enable TextView open url links
            mainRvHolder.tv_main.setMovementMethod(LinkMovementMethod.getInstance());
            if (fromHtml) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mainRvHolder.tv_main.setText(Html.fromHtml(tc.contents,
                            Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mainRvHolder.tv_main.setText(Html.fromHtml(tc.contents));
                }
            }
            mainRvHolder.tv_main.setText(tc.contents);
        } else {
            mainRvHolder.tv_main.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public void setFromHtml(boolean fromHtml) {
        this.fromHtml = fromHtml;
    }

    public void setEntities(List<TextCard> entities) {
        this.entities = entities;
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
