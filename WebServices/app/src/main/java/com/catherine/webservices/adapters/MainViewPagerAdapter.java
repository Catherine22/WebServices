package com.catherine.webservices.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.fragments.ApacheFragment;
import com.catherine.webservices.fragments.HttpURLConnectionFragment;
import com.catherine.webservices.fragments.DownloaderFragment;
import com.catherine.webservices.fragments.OkHttp3Fragment;
import com.catherine.webservices.fragments.cache.CacheFragment;
import com.catherine.webservices.fragments.UploadFragment;
import com.catherine.webservices.fragments.socket.SocketFragment;
import com.catherine.webservices.fragments.webview.WebViewFragment;

import java.util.Locale;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private String[] tabs;
    private Context ctx;

    public MainViewPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
        tabs = ctx.getResources().getStringArray(R.array.tab_array);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == Constants.Fragments.F_APACHE) {
            return ApacheFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_HTTP_URL_CONNECTION) {
            return HttpURLConnectionFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_OKHTTP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return OkHttp3Fragment.newInstance(true);
            } else {
                DialogManager.showErrorDialog(ctx, String.format(Locale.ENGLISH, "Sorry. Only SDK version more than %d are available.", Build.VERSION_CODES.KITKAT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        } else if (position == Constants.Fragments.F_DOWNLOADER) {
            return DownloaderFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_CACHE) {
            return CacheFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_UPLOAD) {
            return UploadFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_SOCKET) {
            return SocketFragment.newInstance(true);
        } else if (position == Constants.Fragments.F_WEBVIEW) {
            return WebViewFragment.newInstance(true);
        }
        return ApacheFragment.newInstance(true);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
