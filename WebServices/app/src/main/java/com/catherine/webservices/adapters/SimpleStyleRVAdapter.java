package com.catherine.webservices.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.ImageCardEx;
import com.catherine.webservices.entities.MultiStyleItem;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemSelectListener;
import com.catherine.webservices.toolkits.CLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SimpleStyleRVAdapter extends RecyclerView.Adapter<SimpleStyleRVAdapter.MainRvHolder> {
    private final static String TAG = "SimpleStyleRVAdapter";
    private Context ctx;
    private int titles;
    private List<ImageCardEx> items;
    private OnMultiItemClickListener listener;
    private Handler handler;

    //Hexadecimal - online decimal to hex converter tool: http://www.binaryhexconverter.com/decimal-to-hex-converter

    //background
    private final int TOP = 0x0010000;
    private final int BOTTOM = 0x0100000;

    //style
    private final int PLAIN_TEXT = 0x1000000;

    public SimpleStyleRVAdapter(Context ctx, String title, List<ImageCardEx> items, OnMultiItemClickListener listener) {
        this.items = new ArrayList<>();
        this.ctx = ctx;
        this.listener = listener;
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        mergeList(title, items);
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_item_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        mainRvHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, getTitle(position), getPosInSet(position));
            }
        });
        mainRvHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(view, getTitle(position), getPosInSet(position));
                return false;
            }
        });

        String title = items.get(position).getTitle();
        String subtitle = items.get(position).getSubtitle();
        final String icon = items.get(position).getImage();
        int style = items.get(position).getStyle();
        CLog.Companion.i(TAG,position+":"+items.toString());

        //This is a title not an item
        if ((style & PLAIN_TEXT) == PLAIN_TEXT) {
            mainRvHolder.ll_background.setBackgroundResource(R.color.checker_board_light);
            mainRvHolder.tv_title.setText(title);
            mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.colorAccentDark));
            mainRvHolder.tv_subtitle.setVisibility(View.GONE);
            mainRvHolder.iv_icon.setVisibility(View.GONE);
        } else {
            //Item
            if ((style & (TOP | BOTTOM)) == (TOP | BOTTOM))
                mainRvHolder.ll_background.setBackgroundResource(R.drawable.round_rectangle);
            else if ((style & TOP) == TOP) {
                mainRvHolder.ll_background.setBackgroundResource(R.drawable.top_round_rectangle);
            } else if ((style & BOTTOM) == BOTTOM) {
                mainRvHolder.ll_background.setBackgroundResource(R.drawable.bottom_round_rectangle);
            } else {
                mainRvHolder.ll_background.setBackgroundResource(R.drawable.rectangle);
            }

            mainRvHolder.tv_title.setVisibility(View.VISIBLE);
            mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.grey700));
            if (!TextUtils.isEmpty(title))
                mainRvHolder.tv_title.setText(title);
            else {
                mainRvHolder.tv_title.setText("NULL");
            }

            mainRvHolder.tv_subtitle.setVisibility(View.VISIBLE);
            mainRvHolder.tv_subtitle.setTextColor(ctx.getResources().getColor(R.color.checker_board_dark));
            if (!TextUtils.isEmpty(subtitle))
                mainRvHolder.tv_subtitle.setText(subtitle);
            else {
                mainRvHolder.tv_subtitle.setText("NULL");
            }

            mainRvHolder.iv_icon.setVisibility(View.VISIBLE);
            mainRvHolder.iv_icon.setImageResource(R.drawable.ic_panorama_black_24dp);
            if (!TextUtils.isEmpty(icon)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File f = new File(icon);
                            final Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                            ((Activity) ctx).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (bitmap != null)
                                        mainRvHolder.iv_icon.setImageBitmap(bitmap);
                                    else
                                        mainRvHolder.iv_icon.setImageResource(R.drawable.ic_panorama_black_24dp);
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                mainRvHolder.iv_icon.setImageResource(R.drawable.ic_panorama_black_24dp);
            }

        }

    }

    /**
     * @param title Get all of the items between this title and next title
     * @return all of the items when the title has not found
     */
    public List<ImageCardEx> getItems(String title) {
        List<ImageCardEx> temp = new ArrayList<>();
        int from = 0;
        for (int i = 0; i < items.size(); i++) {
            if (title.equals(items.get(i).getTitle()) && (items.get(i).getStyle() & PLAIN_TEXT) == PLAIN_TEXT) {
                from = i + 1;
            }
        }

        //the last item is a title
        if (from == items.size())
            return null;

        for (int i = from; i < items.size(); i++) {
            if ((items.get(i).getStyle() & PLAIN_TEXT) != PLAIN_TEXT) {
                temp.add(items.get(i));
            } else {
                break;
            }
        }

        return temp;
    }

    private int getPosInSet(int position) {
        int pos = -1;
        for (int i = position; i >= 0; i--) {
            if (items.get(i).getStyle() != PLAIN_TEXT)
                pos++;
            else
                break;
        }
        return pos;
    }

    public int getRealItemCount() {
        return items.size() - titles;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getTitle(int position) {
        String title = null;
        for (int i = position; i >= 0; i--) {
            if ((items.get(i).getStyle() & PLAIN_TEXT) == PLAIN_TEXT) {
                title = items.get(i).getTitle();
                break;
            }
        }
        return title;
    }

    public void removeAll() {
        items.clear();
    }

    public void mergeList(String title, List<ImageCardEx> list) {
//        CLog.Companion.w(TAG, "CHECK_BOX:" + CHECK_BOX);
//        CLog.Companion.w(TAG, "SWITCH:" + SWITCH);
//        CLog.Companion.w(TAG, "TOP:" + TOP);
//        CLog.Companion.w(TAG, "BOTTOM:" + BOTTOM);
//        CLog.Companion.w(TAG, "PLAIN_TEXT:" + PLAIN_TEXT);
        if (list != null && list.size() > 0) {
            if (!TextUtils.isEmpty(title)) {
                titles++;
                ImageCardEx item = new ImageCardEx();
                item.setStyle(PLAIN_TEXT);
                item.setTitle(title);
                items.add(item);
            }
            items.addAll(list);
            optimizeStyle();
        }
    }

    public void updateItem(String title, int position, ImageCardEx item) {
        if (!TextUtils.isEmpty(title)) {
            int tag = -1;
            for (int i = 0; i < items.size(); i++) {
                ImageCardEx temp = items.get(i);
                if (title.equals(temp.getTitle())) {
                    tag = i;
                    break;
                }
            }

            if (tag == -1)
                return;

            items.set((tag + position + 1), item);
            optimizeStyle();
        }
    }

    /**
     * adjust background
     */
    private void optimizeStyle() {
        ImageCardEx temp = items.get(0);
        if (temp.getStyle() != PLAIN_TEXT)
            temp.setStyle(temp.getStyle() | TOP);

        temp = items.get(items.size() - 1);
        if (temp.getStyle() != PLAIN_TEXT)
            temp.setStyle(temp.getStyle() | BOTTOM);

        for (int i = 0; i < items.size(); i++) {
            ImageCardEx item = items.get(i);
            if (item.getStyle() == PLAIN_TEXT) {
                //skip
            } else {
                if (i < (items.size() - 1) && items.get(i + 1).getStyle() == PLAIN_TEXT) {
                    item.setStyle(item.getStyle() | BOTTOM);
                }

                if (i > 0 && items.get(i - 1).getStyle() == PLAIN_TEXT) {
                    item.setStyle(item.getStyle() | TOP);
                }
            }
        }

        //Debug
        for (int i = 0; i < items.size(); i++) {
            ImageCardEx item = items.get(i);
            CLog.Companion.i(TAG, item.getTitle() + ":" + item.getStyle());
        }

    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_background;
        TextView tv_title;
        TextView tv_subtitle;
        ImageView iv_icon;

        MainRvHolder(View itemView) {
            super(itemView);
            ll_background = itemView.findViewById(R.id.ll_background);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
