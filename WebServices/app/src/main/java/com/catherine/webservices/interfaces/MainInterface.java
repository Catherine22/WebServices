package com.catherine.webservices.interfaces;

import android.os.Bundle;
import android.view.View;

import com.catherine.webservices.network.NetworkHealthListener;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface MainInterface {
    void getPermissions(String[] permissions, OnRequestPermissionsListener listener);

    void switchTab(int position);

    void callFragment(int id);

    void callFragment(int id, Bundle bundle);

    void callFragmentDialog(int id);

    void callFragmentDialog(int id, Bundle bundle);

    void hideKeyboard();

    void openSlideMenu();

    void backToPreviousPage();

    void clearAllFragments();

    void setBackKeyListener(BackKeyListener listener);

    void removeBackKeyListener();

    void addBottomLayout(int id);

    View getBottomLayout();

    void restoreBottomLayout();

    void listenToNetworkState(NetworkHealthListener listener);

    void stopListeningToNetworkState(NetworkHealthListener listener);
}
