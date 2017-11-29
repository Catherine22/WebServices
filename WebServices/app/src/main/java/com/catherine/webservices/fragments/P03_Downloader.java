package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.ProgressCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.DownloadRequest;
import com.catherine.webservices.network.DownloaderAsyncTask;
import com.catherine.webservices.network.DownloaderListener;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.toolkits.CLog;

import java.net.SocketTimeoutException;
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
    private FloatingActionButton fab_stop;
    private List<String> features, descriptions, infos;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private ProgressCardRVAdapter adapter;
    private DownloaderAsyncTask[] tasks;
    private DownloadRequest[] requests;
    private int total = 0;
    private int THREAD_NUM = 3;

    //--Debug--
    private int[] isRunning;
    private long[] periods;
    private int[] threadProgress;

    private final int ERROR = -1;
    private final int IDLE = 0;
    private final int PAUSED = 1;
    private final int DOWNLOADING = 2;
    //--Debug--

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
                        context.append(p);
                        context.append(", ");
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
        features.add("Download an APK");
        features.add("Download an APK");

        descriptions = new ArrayList<>();
        descriptions.add("Download a file with a single thread.");
        descriptions.add("Download a file with three threads.");

        infos = new ArrayList<>();
        infos.add("");
        infos.add("");

        threadProgress = new int[THREAD_NUM];
        isRunning = new int[features.size()];//two elements of RecyclerView
        periods = new long[features.size()];//two elements of RecyclerView
        tasks = new DownloaderAsyncTask[features.size()];//two elements of RecyclerView
        requests = new DownloadRequest[features.size()];//two elements of RecyclerView

        //DownloadRequest
        total = 0;
        final long time0 = System.currentTimeMillis();
        final int position0 = 0;
        requests[0] = new DownloadRequest.Builder()
                .url(String.format(Locale.ENGLISH, "%sfmc1.apk", Constants.DOWNLOAD_HOST))
                .THREAD_NUM(1)
                .listener(new DownloaderListener() {
                    @Override
                    public void update(final int threadID, final int downloadedLength, final int LENGTH) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                total += downloadedLength;
                                adapter.updateProgress(position0, LENGTH, total);
                                adapter.notifyDataSetChanged();
                                periods[position0] = System.currentTimeMillis() - time0;
                                if (total == LENGTH) {
                                    CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(periods[position0])));
                                    updateView(position0, IDLE);
                                }
                            }

                        });
                    }

                    @Override
                    public void connectFailure(final HttpResponse response, final Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                CLog.e(TAG, sb.toString());
                                if (e != null) {
                                    sb.append("\n");
                                    sb.append(e.getMessage());
                                    CLog.e(TAG, e.getMessage());

                                    if (e instanceof SocketTimeoutException) {
                                        DialogManager.showAlertDialog(getActivity(), "Connection timeout. Please check your server.", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                    }
                                }
                                infos.set(position0, sb.toString());
                                updateView(position0, ERROR);
                            }
                        });

                    }
                }).build();


        final long time1 = System.currentTimeMillis();
        final int position1 = 1;
        requests[1] = new DownloadRequest.Builder()
                .url(String.format(Locale.ENGLISH, "%sfmc2.apk", Constants.DOWNLOAD_HOST))
                .THREAD_NUM(THREAD_NUM)
                .listener(new DownloaderListener() {
                    @Override
                    public void update(final int threadID, final int downloadedLength, final int LENGTH) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                threadProgress[threadID] += downloadedLength;
                                total += downloadedLength;
                                infos.set(position1, String.format(Locale.ENGLISH, "Start to download...\n Thread0: %d\n Thread1: %d\n Thread2: %d", threadProgress[0], threadProgress[1], threadProgress[2]));
                                adapter.updateProgress(position1, LENGTH, total);
                                adapter.notifyDataSetChanged();
                                periods[position1] = System.currentTimeMillis() - time1;
                                if (total == LENGTH) {
                                    CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(periods[position1])));
                                    updateView(position1, IDLE);
                                }
                            }
                        });
                    }

                    @Override
                    public void connectFailure(final HttpResponse response, final Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                CLog.e(TAG, sb.toString());
                                if (e != null) {
                                    sb.append("\n");
                                    sb.append(e.getMessage());
                                    CLog.e(TAG, e.getMessage());

                                    if (e instanceof SocketTimeoutException) {
                                        DialogManager.showAlertDialog(getActivity(), "Connection timeout. Please check your server.", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                    }
                                }
                                infos.set(position1, sb.toString());
                                updateView(position1, ERROR);
                            }
                        });

                    }
                }).build();
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (int i = 0; i < tasks.length; i++) {
                    if (tasks[i] != null) {
                        tasks[i].stop();
                        tasks[i].cancel(true);
                    }
                    updateView(i, IDLE);
                }
                init();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProgressCardRVAdapter(getActivity(), null, features, descriptions, null);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                switch (isRunning[position]) {
                    case IDLE:
                        total = 0;
                        tasks[position] = new DownloaderAsyncTask(requests[position]);
                        tasks[position].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        updateView(position, DOWNLOADING);
                        break;
                    case PAUSED:
                        total = 0;
                        tasks[position] = new DownloaderAsyncTask(requests[position]);
                        tasks[position].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        updateView(position, DOWNLOADING);
                        break;
                    case DOWNLOADING:
                        tasks[position].stop();
                        tasks[position].cancel(true);
                        updateView(position, PAUSED);
                        break;
                    case ERROR:
                        updateView(position, ERROR);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);

        fab_stop = (FloatingActionButton) findViewById(R.id.fab_stop);
        fab_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int running = 0;
                for (int anIsRunning : isRunning) {
                    if (anIsRunning == DOWNLOADING)
                        running++;
                }

                if (running > 0) {
                    fab_stop.setImageDrawable(getResources().getDrawable(R.drawable.pause_selector));
                    for (int i = 0; i < isRunning.length; i++) {
                        if (isRunning[i] == DOWNLOADING) {
                            tasks[i].stop();
                            tasks[i].cancel(true);
                            updateView(i, PAUSED);
                        }
                    }
                } else {
                    for (int i = 0; i < isRunning.length; i++) {
                        if (isRunning[i] == PAUSED) {
                            total = 0;
                            tasks[i] = new DownloaderAsyncTask(requests[i]);
                            tasks[i].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            updateView(i, DOWNLOADING);
                        }
                    }
                }
            }
        });
    }

    private void updateView(int position, int state) {
        switch (state) {
            case IDLE:
                isRunning[position] = IDLE;
                infos.set(position, "Click to download");
                adapter.updateInfo(infos);
                adapter.notifyDataSetChanged();
                break;
            case DOWNLOADING:
                isRunning[position] = DOWNLOADING;
                infos.set(position, "Downloading...");
                adapter.updateInfo(infos);
                adapter.notifyDataSetChanged();
                break;
            case PAUSED:
                isRunning[position] = PAUSED;
                infos.set(position, "Paused");
                adapter.updateInfo(infos);
                adapter.notifyDataSetChanged();
                break;
            case ERROR:
                isRunning[position] = ERROR;
                infos.set(position, "Error");
                adapter.updateInfo(infos);
                adapter.notifyDataSetChanged();
                break;
        }

        if (total == 0 && adapter != null) {
            adapter.updateProgress(position, -1, total);
            adapter.notifyDataSetChanged();
        }

        int running = 0;
        for (int anIsRunning : isRunning) {
            if (anIsRunning == DOWNLOADING)
                running++;
        }

        if (running > 0)
            fab_stop.setImageDrawable(getResources().getDrawable(R.drawable.pause_selector));
        else
            fab_stop.setImageDrawable(getResources().getDrawable(R.drawable.play_selector));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
