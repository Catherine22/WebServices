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
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.UploadRequest;
import com.catherine.webservices.network.UploaderAsyncTask;
import com.catherine.webservices.network.UploaderListener;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P06_Upload extends LazyFragment {
    public final static String TAG = "P06_Upload";
    private List<String> features, descriptions, contents;
    private MainInterface mainInterface;
    private SwipeRefreshLayout srl_container;
    private TextCardRVAdapter adapter;
    private ADID_AsyncTask adid_asyncTask;

    public static P06_Upload newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P06_Upload fragment = new P06_Upload();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_06_upload);
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
        features = new ArrayList<>();
        features.add("Upload an image from assets");
        features.add("Upload an image from assets");
        features.add("Upload a large file from assets");

        descriptions = new ArrayList<>();
        descriptions.add("GET");
        descriptions.add("POST");
        descriptions.add("POST");


        contents = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            contents.add("");
        }
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
        adapter = new TextCardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, final int position) {
                switch (position) {
                    case 0:
                        adid_asyncTask = new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
                            @Override
                            public void onResponse(@NotNull String ADID) {
                                try {
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/big_o_cheat_sheet_poster.jpg"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(@NotNull Exception ex) {
                                CLog.Companion.e(TAG, ex.getMessage());
                                try {
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/big_o_cheat_sheet_poster.jpg"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;

                    case 1:
                        adid_asyncTask = new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
                            @Override
                            public void onResponse(@NotNull String ADID) {
                                try {
                                    Map<String, String> body = new HashMap<>();
                                    body.put("ADID", ADID);
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/big_o_cheat_sheet_poster.jpg"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .body(body)
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(@NotNull Exception ex) {
                                CLog.Companion.e(TAG, ex.getMessage());
                                try {
                                    Map<String, String> body = new HashMap<>();
                                    body.put("ADID", "UNKNOWN");
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/big_o_cheat_sheet_poster.jpg"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .body(body)
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    case 2:
                        adid_asyncTask = new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
                            @Override
                            public void onResponse(@NotNull String ADID) {
                                try {
                                    Map<String, String> body = new HashMap<>();
                                    body.put("ADID", ADID);
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/邓俊辉_数据结构.pdf"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .body(body)
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(@NotNull Exception ex) {
                                ex.printStackTrace();
                                try {
                                    Map<String, String> body = new HashMap<>();
                                    body.put("ADID", "KNOWN");
                                    UploadRequest request = new UploadRequest(new UploadRequest.Builder()
                                            .file(new File(MyApplication.INSTANCE.getDataCacheDir() + "/邓俊辉_数据结构.pdf"))
                                            .url(String.format(Locale.ENGLISH, "%sUploadServlet", Constants.HOST))
                                            .body(body)
                                            .listener(new UploaderListener() {
                                                @Override
                                                public void connectSuccess(@NotNull HttpResponse response) {
                                                    CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void connectFailure(@NotNull HttpResponse response, @org.jetbrains.annotations.Nullable Exception e) {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                                    CLog.Companion.e(TAG, sb.toString());
                                                    if (e != null) {
                                                        sb.append("\n");
                                                        sb.append(e.getMessage());
                                                        CLog.Companion.e(TAG, e.getMessage());
                                                    }
                                                    contents.set(position, sb.toString());
                                                    adapter.setContents(contents);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }));
                                    new UploaderAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        adid_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }
}
