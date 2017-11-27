package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.MultiStyleRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.MultiStyleItem;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemSelectListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import java.io.File;
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
    private List<MultiStyleItem> wvAttr, wvSettings, caches;
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
                DialogManager.showPermissionDialog(getActivity(), String.format(getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
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

    private WebViewAttr attr;
    private String[] wv_attr_array, wv_settings_array, wv_cache_array;

    private void fillInData() {
        wv_attr_array = getActivity().getResources().getStringArray(R.array.wv_attr_array);
        wv_settings_array = getActivity().getResources().getStringArray(R.array.wv_settings_array);
        wv_cache_array = getActivity().getResources().getStringArray(R.array.wv_cache_array);

        titles = new String[]{"WebView Attribute", "WebSettings", "Cache", "Debug"};
        attr = new WebViewAttr(getActivity());
        wvAttr = new ArrayList<>();
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_attr_array[0], "setVerticalScrollBarEnabled()", attr.isVerticalScrollBarEnabled() ? 1 : 0, null));
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_attr_array[1], "setHorizontalScrollBarEnabled()", attr.isHorizontalScrollBarEnabled() ? 1 : 0, null));

        wvSettings = new ArrayList<>();
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[0], "setUseWideViewPort()", attr.isUseWideViewPort() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[1], "setLoadWithOverviewMode()", attr.isLoadWithOverviewMode() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[2], "setBuiltInZoomControls()", attr.isBuiltInZoomControls() ? 1 : 0, null));
        if (!attr.isBuiltInZoomControls())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[3], "setSupportZoom()", -1, null));//depend on the above (BuiltInZoomControls)
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[3], "setSupportZoom()", attr.isSupportZoom() ? 1 : 0, null));//depend on the above (BuiltInZoomControls)
        if (!attr.isSupportZoom() || !attr.isBuiltInZoomControls())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));//depend on the above (BuiltInZoomControls)
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));//depend on the above (BuiltInZoomControls)
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[5], "setDisplayZoomControls()", attr.isDisplayZoomControls() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[6], "setNeedInitialFocus()", attr.isNeedInitialFocus() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[7], "setJavaScriptEnabled()", attr.isJavaScriptEnabled() ? 1 : 0, null));
        if (!attr.isJavaScriptEnabled())
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[8], "setJavaScriptCanOpenWindowsAutomatically()", -1, null));
        else
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[8], "setJavaScriptCanOpenWindowsAutomatically()", attr.isJavaScriptCanOpenWindowsAutomatically() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[9], "setLoadsImagesAutomatically()", attr.isLoadsImagesAutomatically() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[10], "setDefaultFontSize()", 0, String.valueOf(attr.getDefaultFontSize())));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[11], "setMinimumFontSize()", 0, String.valueOf(attr.getMinimumFontSize())));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[12], "setDefaultTextEncodingName()", 0, attr.getDefaultTextEncodingName()));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[13], "setStandardFontFamily()", 0, attr.getStandardFontFamily()));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[14], "setUserAgentString()", 0, attr.getUserAgent()));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[15], "setSaveFormData()", attr.isSaveFormData() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[16], "setAllowContentAccess()", attr.isAllowContentAccess() ? 1 : 0, null));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[17], "setAllowFileAccess()", attr.isAllowFileAccess() ? 1 : 0, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[18], "setAllowUniversalAccessFromFileURLs()", attr.isAllowUniversalAccessFromFileURLs() ? 1 : 0, null));
            if (attr.isAllowUniversalAccessFromFileURLs())
                wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[19], "setAllowFileAccessFromFileURLs()", -1, null));
            else
                wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[19], "setAllowFileAccessFromFileURLs()", attr.isAllowFileAccessFromFileURLs() ? 1 : 0, null));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[20], "setMediaPlaybackRequiresUserGesture()", attr.isMediaPlaybackRequiresUserGesture() ? 1 : 0, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[21], "setMixedContentMode()", 0, attr.getMixedContentModeName(attr.getMixedContentMode())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[22], "setSafeBrowsingEnabled()", attr.isSafeBrowsingEnabled() ? 1 : 0, null));

        caches = new ArrayList<>();
        caches.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_cache_array[0], "history", 0, ""));
        caches.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_cache_array[1], "setCacheMode()", 0, attr.getCacheModeName(attr.getCacheMode())));
        caches.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_cache_array[2], "clear history", 0, ""));
        caches.add(new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_cache_array[3], "clear cache", 0, ""));

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
                CLog.Companion.i(TAG, title + "[" + position + "]");
                if (titles[2].equals(title)) {
                    switch (position) {
                        case 0:
                            mainInterface.callFragment(Constants.P16_WEBVIEW_HISTORY);
                            break;
                        case 2:
                            SharedPreferences sp = getActivity().getSharedPreferences("wv_history", Context.MODE_PRIVATE);
                            sp.edit().clear().apply();
                            break;
                        case 3:
                            File dir = new File(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
                            clearFolder(dir);
                            break;
                    }
                }
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
                            adapter.updateItem(titles[1], 2, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[2], "setBuiltInZoomControls()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                attr.setSupportZoom(true);
                                adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[3], "setSupportZoom()", 1, null));
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));
                            } else {
                                attr.setSupportZoom(false);
                                adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[3], "setSupportZoom()", -1, null));
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));
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
                            adapter.updateItem(titles[1], 3, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[3], "setSupportZoom()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", 0, String.valueOf(attr.getTextZoom())));
                            } else {
                                adapter.updateItem(titles[1], 4, new MultiStyleItem(MultiStyleRVAdapter.EDITTEXT, wv_settings_array[4], "setTextZoom()", -1, String.valueOf(attr.getTextZoom())));
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
                                DialogManager.showErrorDialog(getActivity(), data + " is not allowed, try to type an number!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                            break;
                        case 5:
                            attr.setDisplayZoomControls(isSelect);
                            break;
                        case 6:
                            attr.setNeedInitialFocus(isSelect);
                            break;
                        case 7:
                            attr.setJavaScriptEnabled(isSelect);
                            adapter.updateItem(titles[1], 7, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[7], "setJavaScriptEnabled()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                adapter.updateItem(titles[1], 8, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[8], "setJavaScriptCanOpenWindowsAutomatically()", 1, null));
                            } else {
                                adapter.updateItem(titles[1], 8, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[8], "setJavaScriptCanOpenWindowsAutomatically()", -1, null));
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 8:
                            attr.setJavaScriptCanOpenWindowsAutomatically(isSelect);
                            break;
                        case 9:
                            attr.setLoadsImagesAutomatically(isSelect);
                            break;
                        case 10:
                            try {
                                //hide soft keyboard
                                mainInterface.hideKeyboard();
                                int n = Integer.parseInt(data);
                                attr.setDefaultFontSize(n);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                DialogManager.showErrorDialog(getActivity(), data + " is not allowed, try to type an number!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                            break;
                        case 11:
                            try {
                                //hide soft keyboard
                                mainInterface.hideKeyboard();
                                int n = Integer.parseInt(data);
                                attr.setMinimumFontSize(n);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                DialogManager.showErrorDialog(getActivity(), data + " is not allowed, try to type an number!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                            break;
                        case 12:
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
                                    adapter.updateItem(titles[1], 13, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[13], "setDefaultTextEncodingName()", 0, data));
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            break;
                        case 13:
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
                                    adapter.updateItem(titles[1], 14, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[14], "setStandardFontFamily()", 0, data));
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
                            int s = 0;
                            String[] ua = getActivity().getResources().getStringArray(R.array.user_agent);
                            for (int i = 0; i < ua.length; i++) {
                                if (ua[i].equals(attr.getUserAgent())) {
                                    s = i;
                                    break;
                                }
                            }
                            showRBDialog(ua, s, new DialogCallback() {
                                @Override
                                public void dismiss(String data) {
                                    attr.setUserAgent(data);
                                    adapter.updateItem(titles[1], 16, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[16], "setUserAgentString()", 0, data));
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            break;
                        case 15:
                            attr.setSaveFormData(isSelect);
                            break;
                        case 16:
                            attr.setAllowContentAccess(isSelect);
                            break;
                        case 17:
                            attr.setAllowFileAccess(isSelect);
                            break;
                        case 18:
                            attr.setAllowUniversalAccessFromFileURLs(isSelect);
                            adapter.updateItem(titles[1], 17, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[17], "setAllowUniversalAccessFromFileURLs()", isSelect ? 1 : 0, null));
                            if (isSelect) {
                                adapter.updateItem(titles[1], 18, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[18], "setAllowFileAccessFromFileURLs()", -1, null));
                            } else {
                                adapter.updateItem(titles[1], 18, new MultiStyleItem(MultiStyleRVAdapter.SWITCH, wv_settings_array[18], "setAllowFileAccessFromFileURLs()", 0, null));
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 19:
                            attr.setAllowFileAccessFromFileURLs(isSelect);
                            break;
                        case 20:
                            attr.setMediaPlaybackRequiresUserGesture(isSelect);
                            break;
                        case 21:
                            //show selector
                            int r = 0;
                            String[] modes = getActivity().getResources().getStringArray(R.array.mixed_content_mode);
                            for (int i = 0; i < modes.length; i++) {
                                if (modes[i].equals(attr.getMixedContentModeName(attr.getMixedContentMode()))) {
                                    r = i;
                                    break;
                                }
                            }
                            showRBDialog(modes, r, new DialogCallback() {
                                @Override
                                public void dismiss(String data) {
                                    attr.setMixedContentMode(attr.getMixedContentMode(data));
                                    adapter.updateItem(titles[1], 15, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_settings_array[15], "setMixedContentMode()", 0, data));
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            break;
                        case 22:
                            attr.setSafeBrowsingEnabled(isSelect);
                            break;

                    }
                } else if (titles[2].equals(title)) {
                    switch (position) {
                        case 0:
//                            mainInterface.callFragment(Constants.P16_WEBVIEW_HISTORY);
                            break;
                        case 1:
                            //show selector
                            int p = 0;
                            String[] modes = getActivity().getResources().getStringArray(R.array.cache_mode);
                            for (int i = 0; i < modes.length; i++) {
                                if (modes[i].equals(attr.getCacheModeName(attr.getCacheMode()))) {
                                    p = i;
                                    break;
                                }
                            }
                            showRBDialog(modes, p, new DialogCallback() {
                                @Override
                                public void dismiss(String data) {
                                    attr.setCacheMode(attr.getCacheMode(data));
                                    adapter.updateItem(titles[2], 1, new MultiStyleItem(MultiStyleRVAdapter.TEXTVIEW, wv_cache_array[1], "setCacheMode()", 0, data));
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
        adapter.mergeList(titles[2], caches);
        rv_main_list.setAdapter(adapter);
        srl_container.setRefreshing(false);
    }

    private void clearFolder(File dir) {
        try {
            if (dir.exists() && dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    if (f.exists() && f.isDirectory())
                        clearFolder(f);
                    else
                        f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
