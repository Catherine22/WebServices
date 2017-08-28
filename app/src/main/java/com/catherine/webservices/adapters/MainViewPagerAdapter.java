package com.catherine.webservices.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.catherine.webservices.fragments.P01_Apache;
import com.catherine.webservices.fragments.P02_HttpURLConnection;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private String[] mTitles = new String[]{"HttpClient", "HttpURLConnection"};

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return P01_Apache.newInstance(true);
        } else if (position == 1) {
            return P02_HttpURLConnection.newInstance(true);
        }
        return P01_Apache.newInstance(true);


    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
