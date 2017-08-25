package com.catherine.webservices;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyPagerAdapter extends PagerAdapter {
    List<View> mListViews;

    public MyPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}

