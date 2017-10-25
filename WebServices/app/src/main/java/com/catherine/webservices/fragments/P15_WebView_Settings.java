package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.MultiStyleRVAdapter;
import com.catherine.webservices.entities.MultiStyleItem;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemSelectListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import java.util.ArrayList;
import java.util.List;

import catherine.messagecenter.AsyncResponse;
import catherine.messagecenter.Server;

/**
 * Created by Catherine on 2017/9/15.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P15_WebView_Settings extends LazyFragment {
    public final static String TAG = "P15_WebView_Settings";
    private List<MultiStyleItem> wvAttr, wvSettings;
    private String[] titles;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private MultiStyleRVAdapter adapter;
    private Server sv;

    public static P15_WebView_Settings newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P15_WebView_Settings fragment = new P15_WebView_Settings();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_15_wv_settings);
        init();
    }

    private void init() {
        mainInterface = (MainInterface) getActivity();
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

    private WebViewAttr attr;

    private void fillInData() {
        titles = new String[]{"WebView Attribute", "WebSettings"};
        attr = new WebViewAttr(getActivity());
        wvAttr = new ArrayList<>();
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "可垂直滑动", "setVerticalScrollBarEnabled()", attr.isVerticalScrollBarEnabled() ? 1 : 0, null));
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "可水平滑动", "setHorizontalScrollBarEnabled()", attr.isHorizontalScrollBarEnabled() ? 1 : 0, null));

        wvSettings = new ArrayList<>();
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "将图片调整到适合WebView的大小", "setUseWideViewPort()", attr.isUseWideViewPort() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "缩放至屏幕的大小", "setLoadWithOverviewMode()", attr.isLoadWithOverviewMode() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "设置内置的缩放控件", "setBuiltInZoomControls()", attr.isBuiltInZoomControls() ? 1 : 0, null));
        if (!attr.isBuiltInZoomControls())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom()", -1, null));//depend on the above (BuiltInZoomControls)
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom()", attr.isSupportZoom() ? 1 : 0, null));//depend on the above (BuiltInZoomControls)
        if (!attr.isSupportZoom() || !attr.isBuiltInZoomControls())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));//depend on the above (BuiltInZoomControls)
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));//depend on the above (BuiltInZoomControls)
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "隐藏原生的缩放控件", "setDisplayZoomControls()", attr.isDisplayZoomControls() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "设置可以访问文件", "setAllowFileAccess()", attr.isAllowFileAccess() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "当WebView调用requestFocus时为WebView设置节点", "setNeedInitialFocus()", attr.isNeedInitialFocus() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持JS", "setJavaScriptEnabled()", attr.isJavaScriptEnabled() ? 1 : 0, null));
        if (!attr.isJavaScriptEnabled())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持通过JS打开新窗口", "setJavaScriptCanOpenWindowsAutomatically()", -1, null));
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持通过JS打开新窗口", "setJavaScriptCanOpenWindowsAutomatically()", attr.isJavaScriptCanOpenWindowsAutomatically() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持自动加载图片", "setLoadsImagesAutomatically()", attr.isLoadsImagesAutomatically() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置WebView字体的大小", "setDefaultFontSize()", 0, String.valueOf(attr.getDefaultFontSize())));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置WebView支持的最小字体大小", "setMinimumFontSize()", 0, String.valueOf(attr.getMinimumFontSize())));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, "设置编码格式", "setDefaultTextEncodingName()", 0, attr.getDefaultTextEncodingName()));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, "设置WebView的字体", "setStandardFontFamily()", 0, attr.getStandardFontFamily()));
    }

    private void initComponent() {
        sv = new Server(getActivity(), new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.Companion.e(TAG, "LocalBroadCast error:" + errorCode);
            }
        });
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });
        srl_container.setRefreshing(true);
        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MultiStyleRVAdapter(getActivity(), null, null, new OnMultiItemClickListener() {
            @Override
            public void onItemClick(View view, String title, int position) {

            }

            @Override
            public void onItemLongClick(View view, String title, int position) {

            }
        }, new OnMultiItemSelectListener() {
            @Override
            public void onItemSelect(String title, int position, boolean isSelect, String data) {
                CLog.Companion.i(TAG, title + "[" + position + "], isSelect:" + isSelect + ", data: " + data);
                if (titles[0].equals(title)) {
                    switch (position) {
                        case 0:
                            attr.setVerticalScrollBarEnabled(isSelect);
                            break;
                        case 1:
                            attr.setHorizontalScrollBarEnabled(isSelect);
                            break;
                    }
                } else if (titles[1].equals(title)) {
                    switch (position) {
                        case 0:
                            attr.setUseWideViewPort(isSelect);
                            break;
                        case 1:
                            attr.setLoadWithOverviewMode(isSelect);
                            break;
                        case 2:
                            attr.setBuiltInZoomControls(isSelect);
                            adapter.updateItem(titles[1], 2, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "设置内置的缩放控件", "setBuiltInZoomControls()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                attr.setSupportZoom(true);
                                adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom()", 1, null));
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));
                            } else {
                                attr.setSupportZoom(false);
                                adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom()", -1, null));
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 3:
                            attr.setSupportZoom(isSelect);
                            adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));
                            } else {
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, "设置文本的缩放倍数", "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 4:
                            try {
                                //hide soft keyboard
                                mainInterface.hideKeyboard();
                                int n = Integer.parseInt(data);
                                attr.setTextZoom(n);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(false)
                                        .setTitle("Alert!")
                                        .setMessage(data + " is not allowed, try to type an number!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                myAlertDialog.show();
                            }
                            break;
                        case 5:
                            attr.setDisplayZoomControls(isSelect);
                            break;
                        case 6:
                            attr.setAllowFileAccess(isSelect);
                            break;
                        case 7:
                            attr.setNeedInitialFocus(isSelect);
                            break;
                        case 8:
                            attr.setJavaScriptEnabled(isSelect);
                            adapter.updateItem(titles[1], 8, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持JS", "setJavaScriptEnabled()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                adapter.updateItem(titles[1], 9, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持通过JS打开新窗口", "setJavaScriptCanOpenWindowsAutomatically()", 1, null));
                            } else {
                                adapter.updateItem(titles[1], 9, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持通过JS打开新窗口", "setJavaScriptCanOpenWindowsAutomatically()", -1, null));
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 9:
                            attr.setJavaScriptCanOpenWindowsAutomatically(isSelect);
                            break;
                        case 10:
                            attr.setLoadsImagesAutomatically(isSelect);
                            break;
                        case 11:
                            try {
                                //hide soft keyboard
                                mainInterface.hideKeyboard();
                                int n = Integer.parseInt(data);
                                attr.setDefaultFontSize(n);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(false)
                                        .setTitle("Alert!")
                                        .setMessage(data + " is not allowed, try to type an number!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                myAlertDialog.show();
                            }
                            break;
                        case 12:
                            try {
                                //hide soft keyboard
                                mainInterface.hideKeyboard();
                                int n = Integer.parseInt(data);
                                attr.setMinimumFontSize(n);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(false)
                                        .setTitle("Alert!")
                                        .setMessage(data + " is not allowed, try to type an number!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                myAlertDialog.show();
                            }
                            break;
                        case 13:
                            //show selector
                            int p = 0;
                            String[] texts = getActivity().getResources().getStringArray(R.array.text_encode);
                            for (int i = 0; i < texts.length; i++) {
                                if (texts[i].equals(attr.getDefaultTextEncodingName())) {
                                    p = i;
                                    break;
                                }
                            }
                            showRBDialog(texts, p, new DialogCallback() {
                                @Override
                                public void dismiss(String data) {
                                    attr.setDefaultTextEncodingName(data);
                                    adapter.updateItem(titles[1], 13, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, "设置编码格式", "setDefaultTextEncodingName()", 0, data));
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            break;
                        case 14:
                            //show selector
                            int q = 0;
                            String[] fonts = getActivity().getResources().getStringArray(R.array.font_array);
                            for (int i = 0; i < fonts.length; i++) {
                                if (fonts[i].equals(attr.getStandardFontFamily())) {
                                    q = i;
                                    break;
                                }
                            }
                            showRBDialog(fonts, q, new DialogCallback() {
                                @Override
                                public void dismiss(String data) {
                                    attr.setStandardFontFamily(data);
                                    adapter.updateItem(titles[1], 14, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, "设置WebView的字体", "setStandardFontFamily()", 0, data));
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            break;

                    }
                }
                sv.pushBoolean(Commands.WV_SETTINGS, true);
            }
        });
        adapter.mergeList(titles[0], wvAttr);
        adapter.mergeList(titles[1], wvSettings);
        rv_main_list.setAdapter(adapter);
        srl_container.setRefreshing(false);
    }

    private Dialog alertDialog;
    private int selected;

    private interface DialogCallback {
        void dismiss(String data);
    }

    private void showRBDialog(final String[] radioText, int defaultValue, final DialogCallback myCallback) {
        if (alertDialog != null && alertDialog.isShowing())
            return;

        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_selector);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final RadioGroup rg = alertDialog.findViewById(R.id.rg);
        rg.setOrientation(RadioGroup.VERTICAL);

        selected = defaultValue; //default
        int grey = getResources().getColor(R.color.grey700);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < radioText.length; i++) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setId(i);
            rb.setText(radioText[i]);
            rb.setTextColor(grey);
            rb.setTextSize(16);
            rb.setLayoutParams(params);
            rg.addView(rb);
        }
        CLog.Companion.d(TAG, "count:" + rg.getChildCount());
        rg.check(rg.getChildAt(selected).getId());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                CLog.Companion.d(TAG, "check:" + i);
                selected = i;
            }
        });
        final Button bt_ok = alertDialog.findViewById(R.id.bt);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCallback.dismiss(radioText[selected]);

                alertDialog.dismiss();
                rg.removeAllViews();
            }
        });
    }
}
