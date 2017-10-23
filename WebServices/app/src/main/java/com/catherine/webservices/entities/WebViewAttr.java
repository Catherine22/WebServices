package com.catherine.webservices.entities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Catherine on 2017/10/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class WebViewAttr {
    private SharedPreferences sp;

    //WebView Attribute
    private boolean VerticalScrollBarEnabled = true;
    private boolean HorizontalScrollBarEnabled = true;

    //WebSettings
    private boolean UseWideViewPort = true;
    private boolean LoadWithOverviewMode = true;
    private boolean BuiltInZoomControls = true;
    private boolean SupportZoom = true;
    private boolean DisplayZoomControls = false;
    private boolean AllowFileAccess = true;
    private boolean NeedInitialFocus = true;
    private boolean JavaScriptEnabled = false;
    private boolean JavaScriptCanOpenWindowsAutomatically = false;
    private boolean LoadsImagesAutomatically = true;


    public WebViewAttr(Context ctx) {
        sp = ctx.getSharedPreferences("wv_settings", Context.MODE_PRIVATE);
        VerticalScrollBarEnabled = sp.getBoolean("VerticalScrollBarEnabled", true);
        HorizontalScrollBarEnabled = sp.getBoolean("HorizontalScrollBarEnabled", true);
        UseWideViewPort = sp.getBoolean("UseWideViewPort", true);
        LoadWithOverviewMode = sp.getBoolean("LoadWithOverviewMode", true);
        SupportZoom = sp.getBoolean("SupportZoom", true);
        BuiltInZoomControls = sp.getBoolean("BuiltInZoomControls", true);
        DisplayZoomControls = sp.getBoolean("DisplayZoomControls", false);
        AllowFileAccess = sp.getBoolean("AllowFileAccess", true);
        NeedInitialFocus = sp.getBoolean("NeedInitialFocus", true);
        JavaScriptEnabled = sp.getBoolean("JavaScriptEnabled", false);
        JavaScriptCanOpenWindowsAutomatically = sp.getBoolean("JavaScriptCanOpenWindowsAutomatically", false);
        LoadsImagesAutomatically = sp.getBoolean("LoadsImagesAutomatically", true);
    }

    public boolean isVerticalScrollBarEnabled() {
        return VerticalScrollBarEnabled;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return HorizontalScrollBarEnabled;
    }

    public boolean isUseWideViewPort() {
        return UseWideViewPort;
    }

    public boolean isLoadWithOverviewMode() {
        return LoadWithOverviewMode;
    }

    public boolean isBuiltInZoomControls() {
        return BuiltInZoomControls;
    }

    public boolean isSupportZoom() {
        return SupportZoom;
    }

    public boolean isDisplayZoomControls() {
        return DisplayZoomControls;
    }

    public boolean isAllowFileAccess() {
        return AllowFileAccess;
    }

    public boolean isNeedInitialFocus() {
        return NeedInitialFocus;
    }

    public boolean isJavaScriptEnabled() {
        return JavaScriptEnabled;
    }

    public boolean isJavaScriptCanOpenWindowsAutomatically() {
        return JavaScriptCanOpenWindowsAutomatically;
    }

    public boolean isLoadsImagesAutomatically() {
        return LoadsImagesAutomatically;
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        VerticalScrollBarEnabled = verticalScrollBarEnabled;
        sp.edit().putBoolean("verticalScrollBarEnabled", verticalScrollBarEnabled).apply();
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        HorizontalScrollBarEnabled = horizontalScrollBarEnabled;
        sp.edit().putBoolean("horizontalScrollBarEnabled", horizontalScrollBarEnabled).apply();
    }

    public void setUseWideViewPort(boolean useWideViewPort) {
        UseWideViewPort = useWideViewPort;
        sp.edit().putBoolean("useWideViewPort", useWideViewPort).apply();
    }

    public void setLoadWithOverviewMode(boolean loadWithOverviewMode) {
        LoadWithOverviewMode = loadWithOverviewMode;
        sp.edit().putBoolean("loadWithOverviewMode", loadWithOverviewMode).apply();
    }

    public void setBuiltInZoomControls(boolean builtInZoomControls) {
        BuiltInZoomControls = builtInZoomControls;
        sp.edit().putBoolean("builtInZoomControls", builtInZoomControls).apply();
    }

    public void setSupportZoom(boolean supportZoom) {
        SupportZoom = supportZoom;
        sp.edit().putBoolean("supportZoom", supportZoom).apply();
    }

    public void setDisplayZoomControls(boolean displayZoomControls) {
        DisplayZoomControls = displayZoomControls;
        sp.edit().putBoolean("displayZoomControls", displayZoomControls).apply();
    }

    public void setAllowFileAccess(boolean allowFileAccess) {
        AllowFileAccess = allowFileAccess;
        sp.edit().putBoolean("allowFileAccess", allowFileAccess).apply();
    }

    public void setNeedInitialFocus(boolean needInitialFocus) {
        NeedInitialFocus = needInitialFocus;
        sp.edit().putBoolean("needInitialFocus", needInitialFocus).apply();
    }

    public void setJavaScriptEnabled(boolean javaScriptEnabled) {
        JavaScriptEnabled = javaScriptEnabled;
        sp.edit().putBoolean("javaScriptEnabled", javaScriptEnabled).apply();
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean javaScriptCanOpenWindowsAutomatically) {
        JavaScriptCanOpenWindowsAutomatically = javaScriptCanOpenWindowsAutomatically;
        sp.edit().putBoolean("javaScriptCanOpenWindowsAutomatically", javaScriptCanOpenWindowsAutomatically).apply();
    }

    public void setLoadsImagesAutomatically(boolean loadsImagesAutomatically) {
        LoadsImagesAutomatically = loadsImagesAutomatically;
        sp.edit().putBoolean("loadsImagesAutomatically", loadsImagesAutomatically).apply();
    }

    @Override
    public String toString() {
        return "WebViewAttr{" +
                "sp=" + sp +
                ", VerticalScrollBarEnabled=" + VerticalScrollBarEnabled +
                ", HorizontalScrollBarEnabled=" + HorizontalScrollBarEnabled +
                ", UseWideViewPort=" + UseWideViewPort +
                ", LoadWithOverviewMode=" + LoadWithOverviewMode +
                ", BuiltInZoomControls=" + BuiltInZoomControls +
                ", SupportZoom=" + SupportZoom +
                ", DisplayZoomControls=" + DisplayZoomControls +
                ", AllowFileAccess=" + AllowFileAccess +
                ", NeedInitialFocus=" + NeedInitialFocus +
                ", JavaScriptEnabled=" + JavaScriptEnabled +
                ", JavaScriptCanOpenWindowsAutomatically=" + JavaScriptCanOpenWindowsAutomatically +
                ", LoadsImagesAutomatically=" + LoadsImagesAutomatically +
                '}';
    }
}
