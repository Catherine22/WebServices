package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P12_WebView extends LazyFragment {
    public final static String TAG = "P12_WebView";
    private List<String> features;
    private List<String> descriptions;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;

    public static P12_WebView newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P12_WebView fragment = new P12_WebView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_12_webview);
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
                initComponent();
            }

            @Override
            public void onDenied(@org.jetbrains.annotations.Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);
                DialogManager.showPermissionDialog( getActivity(), String.format( getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onRetry() {
                init();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void fillInData() {
        features = new ArrayList<>();
        features.add("Launch a browser");
        features.add("Nested WebView");
        features.add("Full screen WebView");
        features.add("Test WebView");


        descriptions = new ArrayList<>();
        descriptions.add("Load a url");
        descriptions.add("Load a url");
        descriptions.add("Load a url");
        descriptions.add("Load a url");
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextCardRVAdapter adapter = new TextCardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constants.MY_GITHUB));
                        startActivity(intent);
                        break;
                    case 1:
                        Fragment fragment = P13_Nested_WebView.newInstance(true);
                        String tag = "P13";
                        String title = "P13_Nested_WebView";
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.add(R.id.fl_container, fragment, tag);
                        transaction.addToBackStack(title);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 2:
                        mainInterface.callFragment(Constants.P14_FULL_WEBVIEW);
                        break;
                    case 3:
                        mainInterface.callFragment(Constants.P17_WEBVIEW_TEST_LIST);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);

        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    getChildFragmentManager().popBackStack();
                    mainInterface.restoreBottomLayout();
                } else
                    mainInterface.backToPreviousPage();
            }
        });

        //restore bottom layout when back to this page.
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getChildFragmentManager().getBackStackEntryCount() == 0) {
                    mainInterface.restoreBottomLayout();
                }

            }
        });
    }
}
