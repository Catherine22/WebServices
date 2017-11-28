package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.components.MyWebView;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.MyJavaScriptInterface;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P14_Full_WebView extends LazyFragment {
    public final static String TAG = "P14_Full_WebView";
    private MainInterface mainInterface;
    private MyWebView wv;
    private ImageView iv_menu, iv_refresh;
    private AutoCompleteTextView actv_url;
    private ProgressBar pb;
    private String currentUrl;
    private String displayUrl;
    private Client client;
    private WebViewAttr attr;
    private Dialog jsDialog;
    private SharedPreferences sp;
    //test js -> https://www.javascript.com/

    public static P14_Full_WebView newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P14_Full_WebView fragment = new P14_Full_WebView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_14_full_webview);
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
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
                DialogManager.showPermissionDialog(getActivity(), String.format(getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
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

    private void initComponent() {
        Bundle b = getArguments();
        if (b != null) {
            currentUrl = NetworkHelper.Companion.formattedUrl(b.getString("url", Constants.MY_GITHUB));
        } else {
            currentUrl = NetworkHelper.Companion.formattedUrl(Constants.MY_GITHUB);
        }
        displayUrl = getShortName(currentUrl);

        sp = getActivity().getSharedPreferences("wv_history", Context.MODE_PRIVATE);
        client = new Client(getActivity(), new CustomReceiver() {
            @Override
            public void onBroadcastReceive(@NotNull Result result) {
                Bundle b = result.getMBundle();
                if (b != null) {
                    if (b.getBoolean("clear_history", false)) {
                        wv.clearHistory();
                    }

                    if (b.getBoolean("clear_cache", false)) {
                        wv.clearCache(true);
                    }
                }
                refresh();
            }
        });
        client.gotMessages(Commands.WV_SETTINGS);
        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                CLog.Companion.i(TAG, "back");
//                //history
//                WebBackForwardList history = wv.copyBackForwardList();
//                for (int i = 0; i < history.getSize(); i++) {
//                    CLog.Companion.d(TAG, history.getItemAtIndex(i).getOriginalUrl());
//                }
                if (wv.canGoBack())
                    wv.goBack();
                else {
                    mainInterface.backToPreviousPage();
                }
            }
        });
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainInterface.openSlideMenu();
            }
        });
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide soft keyboard
                mainInterface.hideKeyboard();

                // Perform action on key press
                wv.loadUrl(currentUrl);
                displayUrl = getShortName(currentUrl);
                actv_url.setText(displayUrl);
            }
        });

        actv_url = (AutoCompleteTextView) findViewById(R.id.actv_url);
        actv_url.setText(displayUrl);
        //handle "enter" event
        actv_url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide soft keyboard
                    mainInterface.hideKeyboard();

                    // Perform action on key press
                    currentUrl = actv_url.getText().toString();
                    wv.loadUrl(currentUrl);
                    displayUrl = getShortName(currentUrl);
                    actv_url.setText(displayUrl);

                    return true;
                }
                return false;
            }
        });

        actv_url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    actv_url.setText(currentUrl);
                } else
                    actv_url.setText(displayUrl);
            }
        });

        String[] urls = getResources().getStringArray(R.array.url_suggestions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Arrays.asList(urls));
        actv_url.setAdapter(adapter);

        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setMax(100);
        wv = (MyWebView) findViewById(R.id.wv);
        refresh();
    }

    private void refresh() {
        attr = new WebViewAttr(getActivity());
        //可滑动，默认为true
        wv.setVerticalScrollBarEnabled(attr.isVerticalScrollBarEnabled());
        //可滑动，默认为true
        wv.setHorizontalScrollBarEnabled(attr.isHorizontalScrollBarEnabled());
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (pb.getProgress() == 100)
                    pb.setVisibility(View.GONE);
                else
                    pb.setVisibility(View.VISIBLE);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                CLog.Companion.i(TAG, "onReceivedTitle:" + title);
                displayUrl = title;
                actv_url.setText(title);
                actv_url.dismissDropDown();
                super.onReceivedTitle(view, title);
            }

            //处理alert弹出框，html弹框的一种方式
            @Override
            public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsAlert:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_alert);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS alert from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.confirm();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//                return super.onJsAlert(view, url, message, result);
                return true;
            }

            //处理confirm弹出框
            @Override
            public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsConfirm:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_text);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS confirm from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.confirm();
                        }
                    });
                    final Button bt_cancel = jsDialog.findViewById(R.id.bt_cancel);
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.cancel();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
//                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, final String url, final String message, final String defaultValue, final JsPromptResult result) {
                CLog.Companion.i(TAG, "onJsPrompt:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_edittext);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS prompt from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final EditText et_input = jsDialog.findViewById(R.id.et_input);
                    if (!TextUtils.isEmpty(defaultValue))
                        et_input.setText(defaultValue);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(et_input.getText())) {
                                et_input.setHint(getActivity().getString(R.string.et_hint));
                            } else {
                                jsDialog.dismiss();
                                result.confirm(et_input.getText().toString());
                            }
                        }
                    });
                    final Button bt_cancel = jsDialog.findViewById(R.id.bt_cancel);
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.cancel();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
//                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        wv.setWebViewClient(
                new WebViewClient() {
                    //打开网页时不调用系统浏览器， 而是在此WebView中显示，返回上一页时并不会呼叫
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        CLog.Companion.i(TAG, "shouldOverrideUrlLoading:" + url);
                        currentUrl = url;
                        displayUrl = getShortName(currentUrl);
                        actv_url.setText(displayUrl);
                        actv_url.dismissDropDown();
                        wv.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        CLog.Companion.i(TAG, "onPageFinished:" + url);
                        String cookies = CookieManager.getInstance().getCookie(url);
                        CLog.Companion.i(TAG, "Eat a cookie:" + cookies);

                        Handler handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //save to my history
                                try {
                                    String time = System.currentTimeMillis() + "";
                                    String h = sp.getString("data", "");
                                    JSONArray ja;
                                    if (TextUtils.isEmpty(h)) {
                                        ja = new JSONArray();
                                    } else
                                        ja = new JSONArray(h);

                                    JSONObject jo = new JSONObject();
                                    jo.put("shortName", displayUrl);
                                    jo.put("url", currentUrl);
                                    jo.put("time", time);
                                    ja.put(jo);

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("data", ja.toString());
                                    editor.apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        super.onPageFinished(view, url);
                    }
                }
        );
        wv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;

                // 这里可以拦截很多类型，我们只处理图片类型就可以了
                switch (type) {
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        // 获取图片的路径
                        final String saveImgUrl = result.getExtra();

                        final Dialog myDialog = new Dialog(getActivity());
                        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        myDialog.setContentView(R.layout.dialog_text);
                        //设置dialog背景透明
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();

                        final TextView tv_title = myDialog.findViewById(R.id.tv_title);
                        tv_title.setText("Alert!");
                        final TextView tv_message = myDialog.findViewById(R.id.tv_message);
                        tv_message.setText("How do you deal with this image?");
                        final Button bt_ok = myDialog.findViewById(R.id.bt_ok);
                        bt_ok.setText("Save");
                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();

                                try {
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
                                    File dir = new File(path);
                                    if (!dir.exists())
                                        dir.mkdirs();

                                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(saveImgUrl));
                                    int start = saveImgUrl.lastIndexOf("/") + 1;
                                    String fileName = saveImgUrl.substring(start, saveImgUrl.length());
                                    request.setDestinationInExternalPublicDir("/Download/", fileName);
                                    request.setTitle("WebServices");
                                    request.setDescription("Downloading...");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    downloadManager.enqueue(request);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        final Button bt_cancel = myDialog.findViewById(R.id.bt_cancel);
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });

                        final Button bt_copy = myDialog.findViewById(R.id.bt_copy);
                        bt_copy.setText("Open");
                        bt_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wv.loadUrl(saveImgUrl);
                                myDialog.dismiss();
                            }
                        });
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        WebSettings settings = wv.getSettings();
        //将图片调整到适合WebView的大小
        settings.setUseWideViewPort(attr.isUseWideViewPort());
        //缩放至屏幕的大小
        settings.setLoadWithOverviewMode(attr.isLoadWithOverviewMode());
        //支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(attr.isSupportZoom());
        //设置内置的缩放控件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false。
        settings.setBuiltInZoomControls(attr.isBuiltInZoomControls());
        //设置文本的缩放倍数，默认为 100，若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。
        settings.setTextZoom(attr.getTextZoom());
        //隐藏原生的缩放控件
        settings.setDisplayZoomControls(attr.isDisplayZoomControls());

        //支持内容重新布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //多窗口
        settings.setSupportMultipleWindows(false);
        //当WebView调用requestFocus时为WebView设置节点
        settings.setNeedInitialFocus(attr.isNeedInitialFocus());
        //支持JS
        settings.setJavaScriptEnabled(attr.isJavaScriptEnabled());
        //支持JS呼叫MyJavaScriptInterface提供的方法
        MyJavaScriptInterface javaScriptInterface = new MyJavaScriptInterface(getActivity());
        //In JS, your code would be name.function_of_your_javaScriptInterface(). For example, AndroidFunction.vibrate(500)
        wv.addJavascriptInterface(javaScriptInterface, "AndroidFunction");
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(attr.isJavaScriptCanOpenWindowsAutomatically());
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(attr.isLoadsImagesAutomatically());
        //是否允许获取WebView的内容URL ，可以让WebView访问ContentProvider存储的内容。
        settings.setAllowContentAccess(attr.isAllowContentAccess());
        //设置可以访问文件
        settings.setAllowFileAccess(attr.isAllowFileAccess());
        /*
         * 是否允许定位，默认true。注意：为了保证定位可以使用，要保证以下几点：
         * Application 需要有android.Manifest.permission#ACCESS_COARSE_LOCATION的权限
         * Application 需要实现WebChromeClient#onGeolocationPermissionsShowPrompt的回调，
         * 接收Js定位请求访问地理位置的通知
         */
        settings.setGeolocationEnabled(attr.isGeolocationEnabled());
        settings.setGeolocationEnabled(attr.isGeolocationEnabled());
        //设置编码格式
        settings.setDefaultTextEncodingName(attr.getDefaultTextEncodingName());
        //设置WebView的字体，默认字体为 "sans-serif"
        settings.setStandardFontFamily(attr.getStandardFontFamily());
        //设置WebView字体的大小，默认大小为 16
        settings.setDefaultFontSize(attr.getDefaultFontSize());
        //设置WebView支持的最小字体大小，默认为 8
        settings.setMinimumFontSize(attr.getMinimumFontSize());
        //设置User Agent（手机版或桌面版）
        settings.setUserAgentString(attr.getUserAgentString(attr.getUserAgent()));
        String ua = settings.getUserAgentString();
        CLog.Companion.i(TAG, "my user agent:" + ua);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            /*
             * 是否允许Js访问任何来源的内容。包括访问file scheme的URLs。考虑到安全性，
             * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
             * 不会受到影响。ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本
             * 以上默认为false
             */
            settings.setAllowUniversalAccessFromFileURLs(attr.isAllowUniversalAccessFromFileURLs());
            /*
             * 是否允许Js访问其他file scheme的URLs。包括访问file scheme的资源。考虑到安全性，
             * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
             * 不会受到影响。如果getAllowUniversalAccessFromFileURLs为true，则该方法被忽略。
             * ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本以上默认为false
             */
            settings.setAllowFileAccessFromFileURLs(attr.isAllowFileAccessFromFileURLs());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //是否需要用户手势来播放Media，默认true
            settings.setMediaPlaybackRequiresUserGesture(attr.isMediaPlaybackRequiresUserGesture());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //在Android 5.0上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.setMixedContentMode(attr.getMixedContentMode());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //支持安全浏览
            settings.setSafeBrowsingEnabled(attr.isSafeBrowsingEnabled());
        }

        //cache
        //是否保存表单数据
        settings.setSaveFormData(attr.isSaveFormData());
        //是否存储页面DOM结构
        settings.setDomStorageEnabled(attr.isDomStorageEnabled());
        //是否允许数据库存储
        settings.setDatabaseEnabled(attr.isDatabaseEnabled());
        //是否允许Cache
        settings.setAppCacheEnabled(attr.isAppCacheEnabled());
        //设置存储定位数据库的位置
        settings.setGeolocationDatabasePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
        //设置Cache API缓存路径
        settings.setAppCachePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
        /*
         * 基于WebView导航的类型使用缓存：正常页面加载会加载缓存并按需判断内容是否需要重新验证。
         * 如果是页面返回，页面内容不会重新加载，直接从缓存中恢复。setCacheMode允许客户端根据指定的模式来
         * 使用缓存。
         * LOAD_DEFAULT 默认加载方式
         * LOAD_CACHE_ELSE_NETWORK 按网络情况使用缓存
         * LOAD_NO_CACHE 不使用缓存
         * LOAD_CACHE_ONLY 只使用缓存
         */
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }

    //return authority
    private String getShortName(String urlString) {
        String temp = urlString;
        try {
            temp = Uri.parse(urlString).getAuthority();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

}
