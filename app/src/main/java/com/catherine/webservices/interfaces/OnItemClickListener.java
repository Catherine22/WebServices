package com.catherine.webservices.interfaces;

import android.view.View;

/**
 * Created by Catherine on 2017/9/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}