package com.catherine.webservices.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.catherine.webservices.fragments.P01_Apache;
import com.catherine.webservices.fragments.P02_HttpURLConnection;
import com.catherine.webservices.fragments.P03_Downloader;
import com.catherine.webservices.fragments.P04_Cache;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private String[] mTitles = new String[]{"HttpClient", "HttpURLConnection", "Downloader", "Cache"};

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return P01_Apache.newInstance(true);
        } else if (position == 1) {
            return P02_HttpURLConnection.newInstance(true);
        } else if (position == 2) {
            return P03_Downloader.newInstance(true);
        } else if (position == 3) {
            return P04_Cache.newInstance(true);
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
