package com.catherine.webservices.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.catherine.webservices.Constants;
import com.catherine.webservices.fragments.P01_Apache;
import com.catherine.webservices.fragments.P02_HttpURLConnection;
import com.catherine.webservices.fragments.P03_Downloader;
import com.catherine.webservices.fragments.P04_Cache;
import com.catherine.webservices.fragments.P06_Upload;
import com.catherine.webservices.fragments.P07_Socket;
import com.catherine.webservices.fragments.P12_WebView;
import com.catherine.webservices.toolkits.CLog;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == Constants.P01_APACHE) {
            return P01_Apache.newInstance(true);
        } else if (position == Constants.P02_HTTP_URL_CONNECTION) {
            return P02_HttpURLConnection.newInstance(true);
        } else if (position == Constants.P03_DOWNLOADER) {
            return P03_Downloader.newInstance(true);
        } else if (position == Constants.P04_CACHE) {
            return P04_Cache.newInstance(true);
        } else if (position == Constants.P06_UPLOAD) {
            return P06_Upload.newInstance(true);
        } else if (position == Constants.P07_SOCKET) {
            return P07_Socket.newInstance(true);
        } else if (position == Constants.P12_WEBVIEW) {
            return P12_WebView.newInstance(true);
        }
        return P01_Apache.newInstance(true);
    }

    @Override
    public int getCount() {
        return Constants.MAIN_TABS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.MAIN_TABS[position];
    }
}
