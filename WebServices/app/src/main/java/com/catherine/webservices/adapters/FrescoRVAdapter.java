package com.catherine.webservices.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;
import java.util.List;

/**
 * Created by Catherine on 2017/9/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * Cache images with DiskLruCache,
 * and hide images while there're exceptions.
 */
public class FrescoRVAdapter extends RecyclerView.Adapter<FrescoRVAdapter.MainRvHolder> {
    private final static String TAG = "FrescoRVAdapter";
    private Context ctx;
    private boolean cacheable;
    private List<ImageCard> entities;
    private OnItemClickListener listener;

    public FrescoRVAdapter(Context ctx, List<ImageCard> entities, boolean cacheable, OnItemClickListener listener) {
        this.ctx = ctx;
        this.cacheable = cacheable;
        this.entities = entities;
        this.listener = listener;
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_fresco_card, viewGroup, false));
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
            String url = entities.get(position).image;
            if (url != null) {
                if (cacheable) {
                    Bitmap b = getBitmapFromCache(url);
                    if (b != null) {
                        entities.get(position).subtitle = "cache";
                        mainRvHolder.sdv_main.setImageBitmap(b);
                    } else {
                        entities.get(position).subtitle = "fresh";
                        mainRvHolder.sdv_main.setImageURI(url);
                    }
                } else {
                    entities.get(position).subtitle = "fresh";
                    mainRvHolder.sdv_main.setImageURI(url);
                }
            }
            if (entities.get(position).title != null) {
                mainRvHolder.tv_title.setVisibility(View.VISIBLE);
                mainRvHolder.tv_title.setText(entities.get(position).title);
            }
            if (entities.get(position).subtitle != null) {
                mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
                mainRvHolder.tv_subtitle.setText(entities.get(position).subtitle);
            }
        }
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public void updateData(List<ImageCard> entities) {
        this.entities = entities;
    }

    public void cacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap = null;
        try {
            ImageRequest imageRequest = ImageRequest.fromUri(url);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_subtitle;
        SimpleDraweeView sdv_main;
        CardView cv;

        MainRvHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            sdv_main = itemView.findViewById(R.id.sdv_main);
            sdv_main.setAspectRatio(2.5f);
            cv = itemView.findViewById(R.id.cv);
        }
    }
}
