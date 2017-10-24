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
    private int TextZoom = 100;
    private boolean DisplayZoomControls = false;
    private boolean AllowFileAccess = true;
    private boolean NeedInitialFocus = true;
    private boolean JavaScriptEnabled = false;
    private boolean JavaScriptCanOpenWindowsAutomatically = false;
    private boolean LoadsImagesAutomatically = true;
    private int DefaultFontSize = 16;
    private int MinimumFontSize = 8;
    private String DefaultTextEncodingName = "UTF-8";
    private String StandardFontFamily = "sans-serif";


    public WebViewAttr(Context ctx) {
        sp = ctx.getSharedPreferences("wv_settings", Context.MODE_PRIVATE);
        VerticalScrollBarEnabled = sp.getBoolean("VerticalScrollBarEnabled", true);
        HorizontalScrollBarEnabled = sp.getBoolean("HorizontalScrollBarEnabled", true);
        UseWideViewPort = sp.getBoolean("UseWideViewPort", true);
        LoadWithOverviewMode = sp.getBoolean("LoadWithOverviewMode", true);
        SupportZoom = sp.getBoolean("SupportZoom", true);
        TextZoom = sp.getInt("TextZoom", 100);
        BuiltInZoomControls = sp.getBoolean("BuiltInZoomControls", true);
        DisplayZoomControls = sp.getBoolean("DisplayZoomControls", false);
        AllowFileAccess = sp.getBoolean("AllowFileAccess", true);
        NeedInitialFocus = sp.getBoolean("NeedInitialFocus", true);
        JavaScriptEnabled = sp.getBoolean("JavaScriptEnabled", false);
        JavaScriptCanOpenWindowsAutomatically = sp.getBoolean("JavaScriptCanOpenWindowsAutomatically", false);
        LoadsImagesAutomatically = sp.getBoolean("LoadsImagesAutomatically", true);
        DefaultFontSize = sp.getInt("DefaultFontSize", 16);
        MinimumFontSize = sp.getInt("MinimumFontSize", 8);
        DefaultTextEncodingName = sp.getString("DefaultTextEncodingName", "UTF-8");
        StandardFontFamily = sp.getString("StandardFontFamily", "sans-serif");
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

    public int getTextZoom() {
        return TextZoom;
    }

    public int getDefaultFontSize() {
        return DefaultFontSize;
    }

    public int getMinimumFontSize() {
        return MinimumFontSize;
    }

    public String getDefaultTextEncodingName() {
        return DefaultTextEncodingName;
    }

    public String getStandardFontFamily() {
        return StandardFontFamily;
    }

    public void setVerticalScrollBarEnabled(boolean VerticalScrollBarEnabled) {
        this.VerticalScrollBarEnabled = VerticalScrollBarEnabled;
        sp.edit().putBoolean("VerticalScrollBarEnabled", VerticalScrollBarEnabled).apply();
    }

    public void setHorizontalScrollBarEnabled(boolean HorizontalScrollBarEnabled) {
        this.HorizontalScrollBarEnabled = HorizontalScrollBarEnabled;
        sp.edit().putBoolean("HorizontalScrollBarEnabled", HorizontalScrollBarEnabled).apply();
    }

    public void setUseWideViewPort(boolean UseWideViewPort) {
        this.UseWideViewPort = UseWideViewPort;
        sp.edit().putBoolean("UseWideViewPort", UseWideViewPort).apply();
    }

    public void setLoadWithOverviewMode(boolean LoadWithOverviewMode) {
        this.LoadWithOverviewMode = LoadWithOverviewMode;
        sp.edit().putBoolean("LoadWithOverviewMode", LoadWithOverviewMode).apply();
    }

    public void setBuiltInZoomControls(boolean BuiltInZoomControls) {
        this.BuiltInZoomControls = BuiltInZoomControls;
        sp.edit().putBoolean("BuiltInZoomControls", BuiltInZoomControls).apply();
    }

    public void setSupportZoom(boolean SupportZoom) {
        this.SupportZoom = SupportZoom;
        sp.edit().putBoolean("SupportZoom", SupportZoom).apply();
    }

    public void setDisplayZoomControls(boolean DisplayZoomControls) {
        this.DisplayZoomControls = DisplayZoomControls;
        sp.edit().putBoolean("DisplayZoomControls", DisplayZoomControls).apply();
    }

    public void setAllowFileAccess(boolean AllowFileAccess) {
        this.AllowFileAccess = AllowFileAccess;
        sp.edit().putBoolean("AllowFileAccess", AllowFileAccess).apply();
    }

    public void setNeedInitialFocus(boolean NeedInitialFocus) {
        this.NeedInitialFocus = NeedInitialFocus;
        sp.edit().putBoolean("NeedInitialFocus", NeedInitialFocus).apply();
    }

    public void setJavaScriptEnabled(boolean JavaScriptEnabled) {
        this.JavaScriptEnabled = JavaScriptEnabled;
        sp.edit().putBoolean("JavaScriptEnabled", JavaScriptEnabled).apply();
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean JavaScriptCanOpenWindowsAutomatically) {
        this.JavaScriptCanOpenWindowsAutomatically = JavaScriptCanOpenWindowsAutomatically;
        sp.edit().putBoolean("JavaScriptCanOpenWindowsAutomatically", JavaScriptCanOpenWindowsAutomatically).apply();
    }

    public void setLoadsImagesAutomatically(boolean LoadsImagesAutomatically) {
        this.LoadsImagesAutomatically = LoadsImagesAutomatically;
        sp.edit().putBoolean("LoadsImagesAutomatically", LoadsImagesAutomatically).apply();
    }

    public void setTextZoom(int TextZoom) {
        this.TextZoom = TextZoom;
        sp.edit().putInt("TextZoom", TextZoom).apply();
    }

    public void setDefaultFontSize(int DefaultFontSize) {
        this.DefaultFontSize = DefaultFontSize;
        sp.edit().putInt("DefaultFontSize", DefaultFontSize).apply();
    }

    public void setMinimumFontSize(int MinimumFontSize) {
        this.MinimumFontSize = MinimumFontSize;
        sp.edit().putInt("MinimumFontSize", MinimumFontSize).apply();
    }

    public void setDefaultTextEncodingName(String DefaultTextEncodingName) {
        this.DefaultTextEncodingName = DefaultTextEncodingName;
        sp.edit().putString("DefaultTextEncodingName", DefaultTextEncodingName).apply();
    }

    public void setStandardFontFamily(String StandardFontFamily) {
        this.StandardFontFamily = StandardFontFamily;
        sp.edit().putString("StandardFontFamily", StandardFontFamily).apply();
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
                ", TextZoom=" + TextZoom +
                ", DisplayZoomControls=" + DisplayZoomControls +
                ", AllowFileAccess=" + AllowFileAccess +
                ", NeedInitialFocus=" + NeedInitialFocus +
                ", JavaScriptEnabled=" + JavaScriptEnabled +
                ", JavaScriptCanOpenWindowsAutomatically=" + JavaScriptCanOpenWindowsAutomatically +
                ", LoadsImagesAutomatically=" + LoadsImagesAutomatically +
                ", DefaultFontSize=" + DefaultFontSize +
                ", MinimumFontSize=" + MinimumFontSize +
                ", DefaultTextEncodingName=" + DefaultTextEncodingName +
                ", StandardFontFamily=" + StandardFontFamily +
                '}';
    }
}
