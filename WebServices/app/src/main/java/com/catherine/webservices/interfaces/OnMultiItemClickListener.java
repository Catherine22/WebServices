package com.catherine.webservices.interfaces;

import android.view.View;

/**
 * Created by Catherine on 2017/9/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface OnMultiItemClickListener {
    void onItemClick(View view, String title, int position);

    void onItemLongClick(View view, String title, int position);
}