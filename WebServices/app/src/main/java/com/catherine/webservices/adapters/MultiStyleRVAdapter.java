package com.catherine.webservices.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    //Hexadecimal - online decimal to hex converter tool: http://www.binaryhexconverter.com/decimal-to-hex-converter
    //selector
    public final static int CHECK_BOX = 0x000001;
    public final static int SWITCH = 0x000010;
    public final static int EDITTEXT = 0x000100;
    public final static int TEXTVIEW = 0x001000;

    //background
    private final int TOP = 0x0010000;
    private final int BOTTOM = 0x0100000;

    //style
    private final int PLAIN_TEXT = 0x1000000;

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
        int select = items.get(position).getSelect();
        String data = items.get(position).getData();

        //This is a title not an item
        if ((style & PLAIN_TEXT) == PLAIN_TEXT) {
            mainRvHolder.ll_background.setBackgroundResource(R.color.checker_board_light);
            mainRvHolder.tv_title.setText(title);
            mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.colorAccentDark));
            mainRvHolder.tv_subtitle.setVisibility(View.GONE);
            mainRvHolder.cb.setVisibility(View.GONE);
            mainRvHolder.s.setVisibility(View.GONE);
            mainRvHolder.et.setVisibility(View.GONE);
            mainRvHolder.tv.setVisibility(View.GONE);
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

            if ((style & CHECK_BOX) == CHECK_BOX) {
                mainRvHolder.cb.setVisibility(View.VISIBLE);
                if (select == -1) {
                    mainRvHolder.cb.setChecked(false);
                    mainRvHolder.cb.setEnabled(false);
                    mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.checker_board_dark));
                } else {
                    boolean statue = (select == 1);
                    mainRvHolder.cb.setEnabled(true);
                    mainRvHolder.cb.setChecked(statue);
                    mainRvHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                            selector.onItemSelect(getTitle(position), getPosInSet(position), isCheck, null);
                        }
                    });
                }

            } else {
                mainRvHolder.cb.setVisibility(View.GONE);
            }

            if ((style & SWITCH) == SWITCH) {
                mainRvHolder.s.setVisibility(View.VISIBLE);
                if (select == -1) {
                    mainRvHolder.s.setChecked(false);
                    mainRvHolder.s.setEnabled(false);
                    mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.checker_board_dark));
                } else {
                    boolean statue = (select == 1);
                    mainRvHolder.s.setEnabled(true);
                    mainRvHolder.s.setChecked(statue);
                    mainRvHolder.s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                            selector.onItemSelect(getTitle(position), getPosInSet(position), isCheck, null);
                        }
                    });
                    //Disable checkBox
                    mainRvHolder.cb.setVisibility(View.GONE);
                }
            } else {
                mainRvHolder.s.setVisibility(View.GONE);
            }

            if ((style & EDITTEXT) == EDITTEXT) {
                mainRvHolder.et.setVisibility(View.VISIBLE);
                mainRvHolder.et.setText(data);
                if (select == -1) {
                    mainRvHolder.et.setEnabled(false);
                    mainRvHolder.et.setFocusableInTouchMode(false);
                    mainRvHolder.et.setFocusable(false);
                    mainRvHolder.et.setClickable(false);
                    mainRvHolder.tv_title.setTextColor(ctx.getResources().getColor(R.color.checker_board_dark));
                } else {
                    mainRvHolder.et.setEnabled(true);
                    mainRvHolder.et.setEnabled(true);
                    mainRvHolder.et.setFocusableInTouchMode(true);
                    mainRvHolder.et.setFocusable(true);
                    mainRvHolder.et.setClickable(true);
                    mainRvHolder.et.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Do nothing because the purpose of this listener is used to prevent from IllegalStateException:focus search returned a view that wasn't able to take focus!
                        }
                    });
                    mainRvHolder.et.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            // If the event is a key-down event on the "enter" button
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                selector.onItemSelect(getTitle(position), getPosInSet(position), false, mainRvHolder.et.getText().toString());
                                return true;
                            }
                            return false;
                        }
                    });
                }

                //Disable checkBox & switch
                mainRvHolder.cb.setVisibility(View.GONE);
                mainRvHolder.s.setVisibility(View.GONE);
            } else {
                mainRvHolder.et.setVisibility(View.GONE);
            }

            if ((style & TEXTVIEW) == TEXTVIEW) {
                mainRvHolder.tv.setVisibility(View.VISIBLE);
                mainRvHolder.tv.setText(data);
                mainRvHolder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selector.onItemSelect(getTitle(position), getPosInSet(position), false, mainRvHolder.tv.getText().toString());
                    }
                });

                //Disable checkBox & switch & editText
                mainRvHolder.cb.setVisibility(View.GONE);
                mainRvHolder.s.setVisibility(View.GONE);
                mainRvHolder.et.setVisibility(View.GONE);
            } else {
                mainRvHolder.tv.setVisibility(View.GONE);
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

    public void mergeList(String title, List<MultiStyleItem> list) {
//        CLog.Companion.w(TAG, "CHECK_BOX:" + CHECK_BOX);
//        CLog.Companion.w(TAG, "SWITCH:" + SWITCH);
//        CLog.Companion.w(TAG, "TOP:" + TOP);
//        CLog.Companion.w(TAG, "BOTTOM:" + BOTTOM);
//        CLog.Companion.w(TAG, "PLAIN_TEXT:" + PLAIN_TEXT);
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
            MultiStyleItem temp = items.get(0);
            if (temp.getStyle() != PLAIN_TEXT)
                temp.setStyle(temp.getStyle() | TOP);

            temp = items.get(items.size() - 1);
            if (temp.getStyle() != PLAIN_TEXT)
                temp.setStyle(temp.getStyle() | BOTTOM);

            for (int i = 1; i < items.size() - 1; i++) {
                MultiStyleItem item = items.get(i);
                if (item.getStyle() == PLAIN_TEXT) {
                    //skip
                } else {
                    if (items.get(i + 1).getStyle() == PLAIN_TEXT) {
                        item.setStyle(item.getStyle() | BOTTOM);
                    }

                    if (items.get(i - 1).getStyle() == PLAIN_TEXT) {
                        item.setStyle(item.getStyle() | TOP);
                    }
                }
            }

//            //Debug
//            for (int i = 0; i < items.size(); i++) {
//                MultiStyleItem item = items.get(i);
//                CLog.Companion.i(TAG, item.getTitle() + ":" + item.getStyle());
//            }
        }
    }

    public void updateItem(String title, int position, MultiStyleItem item) {
        if (!TextUtils.isEmpty(title)) {
            int tag = -1;
            for (int i = 0; i < items.size(); i++) {
                MultiStyleItem temp = items.get(i);
                if (title.equals(temp.getTitle())) {
                    tag = i;
                    break;
                }
            }

            if (tag == -1)
                return;

            CLog.Companion.d(TAG, "pos:" + (tag + position + 1));
            items.set((tag + position + 1), item);
        }
    }

    class MainRvHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_background;
        TextView tv_title;
        TextView tv_subtitle;
        TextView tv;
        EditText et;
        CheckBox cb;
        Switch s;

        MainRvHolder(View itemView) {
            super(itemView);
            ll_background = itemView.findViewById(R.id.ll_background);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            tv = itemView.findViewById(R.id.tv);
            et = itemView.findViewById(R.id.et);
            cb = itemView.findViewById(R.id.cb);
            s = itemView.findViewById(R.id.s);
        }
    }
}
