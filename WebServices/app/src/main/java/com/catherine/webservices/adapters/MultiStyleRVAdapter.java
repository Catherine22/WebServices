package com.catherine.webservices.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.entities.MultiStyleItem;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemSelectListener;
import com.catherine.webservices.toolkits.CLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MultiStyleRVAdapter extends RecyclerView.Adapter<MultiStyleRVAdapter.MainRvHolder> {
    private final static String TAG = "MultiStyleRVAdapter";
    private Context ctx;
    private int titles;
    private List<MultiStyleItem> items;
    private OnMultiItemClickListener listener;
    private OnMultiItemSelectListener selector;

    //selector
    public final static int CHECK_BOX = 0x00001;
    public final static int SWITCH = 0x00010;

    //background
    private final int TOP = 0x00100;
    private final int BOTTOM = 0x01000;

    //style
    private final int PLAIN_TEXT = 0x10000;

    public MultiStyleRVAdapter(Context ctx, String title, List<MultiStyleItem> items, OnMultiItemClickListener listener, OnMultiItemSelectListener selector) {
        this.items = new ArrayList<>();
        this.ctx = ctx;
        this.listener = listener;
        this.selector = selector;
        mergeList(title, items);
    }

    @Override
    public MainRvHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainRvHolder(LayoutInflater.from(ctx).inflate(R.layout.rv_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MainRvHolder mainRvHolder, final int position) {
        mainRvHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, getTitle(position), position);
            }
        });
        mainRvHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(view, getTitle(position), position);
                return false;
            }
        });

        String title = items.get(position).getTitle();
        String subtitle = items.get(position).getSubtitle();
        int style = items.get(position).getStyle();
        boolean isSelect = items.get(position).isSelect();

        CLog.Companion.d(TAG, "style:" + style);

        //This is a title not an item
        if ((style & PLAIN_TEXT) == PLAIN_TEXT) {
            mainRvHolder.rl_background.setBackgroundResource(R.color.checker_board_light);
            mainRvHolder.tv_title.setText(title);
            mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.colorAccentDark));
            mainRvHolder.tv_subtitle.setVisibility(View.GONE);
            mainRvHolder.cb.setVisibility(View.GONE);
            mainRvHolder.s.setVisibility(View.GONE);
        } else {
            //Item
            if ((style & TOP & BOTTOM) == (TOP & BOTTOM))
                mainRvHolder.rl_background.setBackgroundResource(R.drawable.round_rectangle);
            else if ((style & TOP) == TOP) {
                mainRvHolder.rl_background.setBackgroundResource(R.drawable.top_round_rectangle);
            } else if ((style & BOTTOM) == BOTTOM) {
                mainRvHolder.rl_background.setBackgroundResource(R.drawable.bottom_round_rectangle);
            } else {
                mainRvHolder.rl_background.setBackgroundResource(R.drawable.rectangle);
            }

            mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.grey700));
            if (!TextUtils.isEmpty(title))
                mainRvHolder.tv_title.setText(title);
            else {
                mainRvHolder.tv_title.setText("NULL");
            }

            mainRvHolder.tv_subtitle.setTextColor(ctx.getResources().getColor(R.color.checker_board_dark));
            if (!TextUtils.isEmpty(subtitle))
                mainRvHolder.tv_subtitle.setText(subtitle);
            else {
                mainRvHolder.tv_title.setText("NULL");
            }

            if ((style & CHECK_BOX) == CHECK_BOX) {
                mainRvHolder.cb.setVisibility(View.VISIBLE);
                mainRvHolder.cb.setChecked(isSelect);
                mainRvHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        selector.onItemSelect(getTitle(position), position, isCheck);
                    }
                });
            } else {
                mainRvHolder.cb.setVisibility(View.GONE);
            }

            if ((style & SWITCH) == SWITCH) {
                mainRvHolder.s.setVisibility(View.VISIBLE);
                mainRvHolder.s.setChecked(isSelect);
                mainRvHolder.s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        selector.onItemSelect(getTitle(position), position, isCheck);
                    }
                });
                //Disable checkBox
                mainRvHolder.cb.setVisibility(View.GONE);
            } else {
                mainRvHolder.s.setVisibility(View.GONE);
            }
        }

    }

    /**
     * @param title Get all of the items between this title and next title
     * @return all of the items when the title has not found
     */
    public List<MultiStyleItem> getItems(String title) {
        List<MultiStyleItem> temp = new ArrayList<>();
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

    @Override
    public int getItemCount() {
        return items.size() - titles;
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

    public void mergeList(String title, List<MultiStyleItem> list) {
        if (list != null && list.size() > 0) {
            if (!TextUtils.isEmpty(title)) {
                titles++;
                MultiStyleItem item = new MultiStyleItem();
                item.setStyle(PLAIN_TEXT);
                item.setTitle(title);
                items.add(item);
            }
            items.addAll(list);

            //adjust background

            for (int i = 1; i < items.size() - 1; i++) {
                MultiStyleItem n = items.get(i);
                n.setStyle(n.getStyle() ^ TOP ^ BOTTOM);

                if ((items.get(i).getStyle() & PLAIN_TEXT) == PLAIN_TEXT) {
                    MultiStyleItem b = items.get(i - 1);
                    b.setStyle(b.getStyle() | BOTTOM);

                    MultiStyleItem t = items.get(i + 1);
                    t.setStyle(t.getStyle() | TOP);
                }
            }

            if ((items.get(0).getStyle() & PLAIN_TEXT) == PLAIN_TEXT) {
                MultiStyleItem itemS = items.get(0);
                itemS.setStyle(itemS.getStyle() ^ TOP ^ BOTTOM);

                MultiStyleItem item1 = items.get(1);
                item1.setStyle(item1.getStyle() | TOP);
            } else {
                MultiStyleItem itemS = items.get(0);
                itemS.setStyle(itemS.getStyle() | TOP);
            }

            if ((items.get(items.size() - 1).getStyle() & PLAIN_TEXT) != PLAIN_TEXT) {
                MultiStyleItem itemE = items.get(items.size() - 1);
                itemE.setStyle(itemE.getStyle() | BOTTOM);
            } else {
                MultiStyleItem itemE = items.get(items.size() - 1);
                itemE.setStyle(itemE.getStyle() ^ TOP ^ BOTTOM);
            }
        }
    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_background;
        TextView tv_title;
        TextView tv_subtitle;
        CheckBox cb;
        Switch s;

        MainRvHolder(View itemView) {
            super(itemView);
            rl_background = itemView.findViewById(R.id.rl_background);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            cb = itemView.findViewById(R.id.cb);
            s = itemView.findViewById(R.id.s);
        }
    }
}
