package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.ProgressCardRVAdapter;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.DownloadRequest;
import com.catherine.webservices.network.DownloaderAsyncTask;
import com.catherine.webservices.network.DownloaderListener;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.toolkits.CLog;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P03_Downloader extends LazyFragment {
    public final static String TAG = "P03_Downloader";
    private List<String> features;
    private List<String> descriptions;
    private List<String> infos;
    private List<Boolean> stopFlags;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private ProgressCardRVAdapter adapter;
    private DownloaderAsyncTask task0, task1;
    private int total = 0;
    private int THREAD_NUM = 3;
    private int[] threadProgress;

    public static P03_Downloader newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P03_Downloader fragment = new P03_Downloader();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_03_download);
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

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        startActivityForResult(intent, Constants.OPEN_SETTINGS);
                    }
                });
                myAlertDialog.show();
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
        threadProgress = new int[THREAD_NUM];

        features = new ArrayList<>();
        features.add("Download an APK");
        features.add("Download an APK");

        descriptions = new ArrayList<>();
        descriptions.add("Download a file with a single thread.");
        descriptions.add("Download a file with three threads.");

        infos = new ArrayList<>();
        infos.add("");
        infos.add("");

        stopFlags = new ArrayList<>();
        stopFlags.add(false);
        stopFlags.add(false);
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CLog.Companion.d(TAG, "refresh");
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProgressCardRVAdapter(getActivity(), null, features, descriptions, null);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                clickEvent(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    public void clickEvent(final int position) {
        switch (position) {
            case 0:
                if (!stopFlags.get(position)) {
                    final long time = System.currentTimeMillis();
                    DownloadRequest r0 = new DownloadRequest(new DownloadRequest.Builder()
                            .url(String.format(Locale.ENGLISH, "%sfmc.apk", Constants.DOWNLOAD_HOST))
                            .THREAD_NUM(1)
                            .listener(new DownloaderListener() {
                                @Override
                                public void update(final int threadID, final int downloadedLength, final int LENGTH) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            total += downloadedLength;
                                            adapter.updateProgress(position, LENGTH, total);
                                            adapter.notifyDataSetChanged();
                                            if (total == LENGTH) {
                                                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time)));
                                                total = 0;
                                                infos.set(position, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time)));
                                                adapter.updateInfo(infos);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void connectFailure(final HttpResponse response, final Exception e) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s", response.getCode(), response.getCodeString()));
                                            if (e != null)
                                                CLog.Companion.e(TAG, e.getMessage());

                                            infos.set(position, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s", response.getCode(), response.getCodeString()));
                                            adapter.updateInfo(infos);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            }));
                    stopFlags.set(position, true);
                    task0 = new DownloaderAsyncTask(r0);
                    task0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    infos.set(position, "Start to download...");
                    adapter.updateInfo(infos);
                    adapter.notifyDataSetChanged();
                } else {
                    stopFlags.set(position, false);
                    infos.set(position, "Stop");
                    adapter.updateInfo(infos);
                    adapter.notifyDataSetChanged();
                    task0.stop();
                }
                break;
            case 1:
                if (!stopFlags.get(position)) {
                    final long time = System.currentTimeMillis();
                    DownloadRequest r1 = new DownloadRequest(new DownloadRequest.Builder()
                            .url(String.format(Locale.ENGLISH, "%sfmc.apk", Constants.DOWNLOAD_HOST))
                            .THREAD_NUM(THREAD_NUM)
                            .listener(new DownloaderListener() {
                                @Override
                                public void update(final int threadID, final int downloadedLength, final int LENGTH) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            threadProgress[threadID] += downloadedLength;
                                            total += downloadedLength;
                                            infos.set(position, String.format(Locale.ENGLISH, "Start to download...\n Thread0: %d\n Thread1: %d\n Thread2: %d", threadProgress[0], threadProgress[1], threadProgress[2]));
                                            adapter.updateProgress(position, LENGTH, total);
                                            adapter.notifyDataSetChanged();
                                            if (total == LENGTH) {
                                                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time)));
                                                total = 0;
                                                infos.set(position, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time)));
                                                adapter.updateInfo(infos);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void connectFailure(final HttpResponse response, final Exception e) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s", response.getCode(), response.getCodeString()));
                                            if (e != null)
                                                CLog.Companion.e(TAG, e.getMessage());

                                            infos.set(position, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s", response.getCode(), response.getCodeString()));
                                            adapter.updateInfo(infos);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            }));
                    stopFlags.set(position, true);
                    task1 = new DownloaderAsyncTask(r1);
                    task1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    infos.set(position, "Start to download...");
                    adapter.updateInfo(infos);
                    adapter.notifyDataSetChanged();
                } else {
                    stopFlags.set(position, false);
                    infos.set(position, "Stop");
                    adapter.updateInfo(infos);
                    adapter.notifyDataSetChanged();
                    task1.stop();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
