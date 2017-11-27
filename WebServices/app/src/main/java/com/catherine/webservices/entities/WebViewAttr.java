package com.catherine.webservices.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.webkit.WebSettings;

import com.catherine.webservices.R;

import java.util.Arrays;

/**
 * Created by Catherine on 2017/10/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class WebViewAttr {
    private SharedPreferences sp;
    private Context ctx;

    //WebView Attribute
    private boolean VerticalScrollBarEnabled = true;
    private boolean HorizontalScrollBarEnabled = true;

    //WebSettings
    private boolean UseWideViewPort = true;
    private boolean LoadWithOverviewMode = true;
    private boolean BuiltInZoomControls = false;
    private boolean SupportZoom = true;
    private int TextZoom = 100;
    private boolean DisplayZoomControls = false;
    private boolean NeedInitialFocus = true;
    private boolean JavaScriptEnabled = true;
    private boolean JavaScriptCanOpenWindowsAutomatically = true;
    private boolean LoadsImagesAutomatically = true;
    private boolean SaveFormData = false;
    private boolean AllowContentAccess = true;
    private boolean AllowFileAccess = true;
    private boolean AllowUniversalAccessFromFileURLs = false;
    private boolean AllowFileAccessFromFileURLs = false;
    private boolean MediaPlaybackRequiresUserGesture = true;
    private boolean SafeBrowsingEnabled = true;
    private int DefaultFontSize = 16;
    private int MinimumFontSize = 8;
    private String DefaultTextEncodingName = "UTF-8";
    private String StandardFontFamily = "sans-serif";
    private int CacheMode = WebSettings.LOAD_DEFAULT;
    private int MixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE;
    private String UserAgent;

    private String[] user_agent, user_agent_detail;

    public WebViewAttr(Context ctx) {
        this.ctx = ctx;
        user_agent = ctx.getResources().getStringArray(R.array.user_agent);
        user_agent_detail = ctx.getResources().getStringArray(R.array.user_agent_detail);
        sp = ctx.getSharedPreferences("wv_settings", Context.MODE_PRIVATE);

        VerticalScrollBarEnabled = sp.getBoolean("VerticalScrollBarEnabled", true);
        HorizontalScrollBarEnabled = sp.getBoolean("HorizontalScrollBarEnabled", true);
        UseWideViewPort = sp.getBoolean("UseWideViewPort", true);
        LoadWithOverviewMode = sp.getBoolean("LoadWithOverviewMode", true);
        BuiltInZoomControls = sp.getBoolean("BuiltInZoomControls", false);
        SupportZoom = sp.getBoolean("SupportZoom", true);
        DisplayZoomControls = sp.getBoolean("DisplayZoomControls", false);
        NeedInitialFocus = sp.getBoolean("NeedInitialFocus", true);
        JavaScriptEnabled = sp.getBoolean("JavaScriptEnabled", true);
        JavaScriptCanOpenWindowsAutomatically = sp.getBoolean("JavaScriptCanOpenWindowsAutomatically", true);
        LoadsImagesAutomatically = sp.getBoolean("LoadsImagesAutomatically", true);
        SaveFormData = sp.getBoolean("SaveFormData", false);
        AllowContentAccess = sp.getBoolean("AllowContentAccess", true);
        AllowFileAccess = sp.getBoolean("AllowFileAccess", true);
        AllowUniversalAccessFromFileURLs = sp.getBoolean("AllowUniversalAccessFromFileURLs", false);
        AllowFileAccessFromFileURLs = sp.getBoolean("AllowFileAccessFromFileURLs", false);
        MediaPlaybackRequiresUserGesture = sp.getBoolean("MediaPlaybackRequiresUserGesture", true);
        SafeBrowsingEnabled = sp.getBoolean("SafeBrowsingEnabled", true);
        TextZoom = sp.getInt("TextZoom", 100);
        DefaultFontSize = sp.getInt("DefaultFontSize", 16);
        MinimumFontSize = sp.getInt("MinimumFontSize", 8);
        DefaultTextEncodingName = sp.getString("DefaultTextEncodingName", "UTF-8");
        StandardFontFamily = sp.getString("StandardFontFamily", "sans-serif");
        CacheMode = sp.getInt("CacheMode", WebSettings.LOAD_DEFAULT);
        MixedContentMode = sp.getInt("MixedContentMode", WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        UserAgent = sp.getString("UserAgent", user_agent[0]);
    }

    public boolean isVerticalScrollBarEnabled() {
        VerticalScrollBarEnabled = sp.getBoolean("VerticalScrollBarEnabled", true);
        return VerticalScrollBarEnabled;
    }

    public boolean isHorizontalScrollBarEnabled() {
        HorizontalScrollBarEnabled = sp.getBoolean("HorizontalScrollBarEnabled", true);
        return HorizontalScrollBarEnabled;
    }

    public boolean isUseWideViewPort() {
        UseWideViewPort = sp.getBoolean("UseWideViewPort", true);
        return UseWideViewPort;
    }

    public boolean isLoadWithOverviewMode() {
        LoadWithOverviewMode = sp.getBoolean("LoadWithOverviewMode", true);
        return LoadWithOverviewMode;
    }

    public boolean isBuiltInZoomControls() {
        BuiltInZoomControls = sp.getBoolean("BuiltInZoomControls", false);
        return BuiltInZoomControls;
    }

    public boolean isSupportZoom() {
        SupportZoom = sp.getBoolean("SupportZoom", true);
        return SupportZoom;
    }

    public boolean isDisplayZoomControls() {
        DisplayZoomControls = sp.getBoolean("DisplayZoomControls", false);
        return DisplayZoomControls;
    }

    public boolean isNeedInitialFocus() {
        NeedInitialFocus = sp.getBoolean("NeedInitialFocus", true);
        return NeedInitialFocus;
    }

    public boolean isJavaScriptEnabled() {
        JavaScriptEnabled = sp.getBoolean("JavaScriptEnabled", true);
        return JavaScriptEnabled;
    }

    public boolean isJavaScriptCanOpenWindowsAutomatically() {
        JavaScriptCanOpenWindowsAutomatically = sp.getBoolean("JavaScriptCanOpenWindowsAutomatically", true);
        return JavaScriptCanOpenWindowsAutomatically;
    }

    public boolean isSaveFormData() {
        SaveFormData = sp.getBoolean("SaveFormData", false);
        return SaveFormData;
    }

    public boolean isLoadsImagesAutomatically() {
        LoadsImagesAutomatically = sp.getBoolean("LoadsImagesAutomatically", true);
        return LoadsImagesAutomatically;
    }

    public boolean isAllowContentAccess() {
        AllowContentAccess = sp.getBoolean("AllowContentAccess", true);
        return AllowContentAccess;
    }

    public boolean isAllowFileAccess() {
        AllowFileAccess = sp.getBoolean("AllowFileAccess", true);
        return AllowFileAccess;
    }

    public boolean isAllowUniversalAccessFromFileURLs() {
        AllowUniversalAccessFromFileURLs = sp.getBoolean("AllowUniversalAccessFromFileURLs", false);
        return AllowUniversalAccessFromFileURLs;
    }

    public boolean isAllowFileAccessFromFileURLs() {
        AllowFileAccessFromFileURLs = sp.getBoolean("AllowFileAccessFromFileURLs", false);
        return AllowFileAccessFromFileURLs;
    }

    public boolean isMediaPlaybackRequiresUserGesture() {
        MediaPlaybackRequiresUserGesture = sp.getBoolean("MediaPlaybackRequiresUserGesture", true);
        return MediaPlaybackRequiresUserGesture;
    }

    public boolean isSafeBrowsingEnabled() {
        SafeBrowsingEnabled = sp.getBoolean("SafeBrowsingEnabled", true);
        return SafeBrowsingEnabled;
    }

    public int getTextZoom() {
        TextZoom = sp.getInt("TextZoom", 100);
        return TextZoom;
    }

    public int getDefaultFontSize() {
        DefaultFontSize = sp.getInt("DefaultFontSize", 16);
        return DefaultFontSize;
    }

    public int getMinimumFontSize() {
        MinimumFontSize = sp.getInt("MinimumFontSize", 8);
        return MinimumFontSize;
    }

    public String getDefaultTextEncodingName() {
        DefaultTextEncodingName = sp.getString("DefaultTextEncodingName", "UTF-8");
        return DefaultTextEncodingName;
    }

    public String getStandardFontFamily() {
        StandardFontFamily = sp.getString("StandardFontFamily", "sans-serif");
        return StandardFontFamily;
    }

    public int getCacheMode() {
        CacheMode = sp.getInt("CacheMode", WebSettings.LOAD_DEFAULT);
        return CacheMode;
    }

    //from WebSettings
    public int getCacheMode(String name) {
        if ("LOAD_DEFAULT".equals(name))
            return -1;
        else if ("LOAD_NORMAL".equals(name))
            return 0;
        else if ("LOAD_CACHE_ELSE_NETWORK".equals(name))
            return 1;
        else if ("LOAD_NO_CACHE".equals(name))
            return 2;
        else if ("LOAD_CACHE_ONLY".equals(name))
            return 3;
        else
            return Integer.MAX_VALUE;
    }

    //from WebSettings
    public String getCacheModeName(int cacheMode) {
        switch (cacheMode) {
            case -1:
                return "LOAD_DEFAULT";
            case 0:
                return "LOAD_NORMAL";
            case 1:
                return "LOAD_CACHE_ELSE_NETWORK";
            case 2:
                return "LOAD_NO_CACHE";
            case 3:
                return "LOAD_CACHE_ONLY";
            default:
                return "NULL";
        }
    }

    public int getMixedContentMode() {
        MixedContentMode = sp.getInt("MixedContentMode", WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        return MixedContentMode;
    }

    //from WebSettings
    public int getMixedContentMode(String name) {
        if ("MIXED_CONTENT_ALWAYS_ALLOW".equals(name))
            return 0;
        else if ("MIXED_CONTENT_NEVER_ALLOW".equals(name))
            return 1;
        else if ("MIXED_CONTENT_COMPATIBILITY_MODE".equals(name))
            return 2;
        else
            return Integer.MAX_VALUE;
    }


    public String getMixedContentModeName(int MixedContentMode) {
        switch (MixedContentMode) {
            case 0:
                return "MIXED_CONTENT_ALWAYS_ALLOW";
            case 1:
                return "MIXED_CONTENT_NEVER_ALLOW";
            case 2:
                return "MIXED_CONTENT_COMPATIBILITY_MODE";
            default:
                return "NULL";
        }
    }

    public String getUserAgent() {
        UserAgent = sp.getString("UserAgent", user_agent[0]);
        return UserAgent;
    }

    public String getUserAgentString(String UserAgent) {
        String temp = "N/A";
        for (int i = 0; i < user_agent.length; i++) {
            if (user_agent[i].equals(UserAgent)) {
                temp = user_agent_detail[i];
                break;
            }
        }

        if (user_agent[0].equals(UserAgent)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                temp = WebSettings.getDefaultUserAgent(ctx);
            } else {
                temp = System.getProperty("http.agent");
            }
        }

        return temp;
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

    public void setSaveFormData(boolean SaveFormData) {
        this.SaveFormData = SaveFormData;
        sp.edit().putBoolean("SaveFormData", SaveFormData).apply();
    }

    public void setLoadsImagesAutomatically(boolean LoadsImagesAutomatically) {
        this.LoadsImagesAutomatically = LoadsImagesAutomatically;
        sp.edit().putBoolean("LoadsImagesAutomatically", LoadsImagesAutomatically).apply();
    }

    public void setAllowContentAccess(boolean AllowContentAccess) {
        this.AllowContentAccess = AllowContentAccess;
        sp.edit().putBoolean("AllowContentAccess", AllowContentAccess).apply();
    }

    public void setAllowFileAccess(boolean AllowFileAccess) {
        this.AllowFileAccess = AllowFileAccess;
        sp.edit().putBoolean("AllowFileAccess", AllowFileAccess).apply();
    }

    public void setAllowUniversalAccessFromFileURLs(boolean AllowUniversalAccessFromFileURLs) {
        this.AllowUniversalAccessFromFileURLs = AllowUniversalAccessFromFileURLs;
        sp.edit().putBoolean("AllowUniversalAccessFromFileURLs", AllowUniversalAccessFromFileURLs).apply();
    }

    public void setAllowFileAccessFromFileURLs(boolean AllowFileAccessFromFileURLs) {
        this.AllowFileAccessFromFileURLs = AllowFileAccessFromFileURLs;
        sp.edit().putBoolean("AllowFileAccessFromFileURLs", AllowFileAccessFromFileURLs).apply();
    }

    public void setMediaPlaybackRequiresUserGesture(boolean MediaPlaybackRequiresUserGesture) {
        this.MediaPlaybackRequiresUserGesture = MediaPlaybackRequiresUserGesture;
        sp.edit().putBoolean("MediaPlaybackRequiresUserGesture", MediaPlaybackRequiresUserGesture).apply();
    }

    public void setSafeBrowsingEnabled(boolean SafeBrowsingEnabled) {
        this.SafeBrowsingEnabled = SafeBrowsingEnabled;
        sp.edit().putBoolean("SafeBrowsingEnabled", SafeBrowsingEnabled).apply();
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

    public void setCacheMode(int CacheMode) {
        this.CacheMode = CacheMode;
        sp.edit().putInt("CacheMode", CacheMode).apply();
    }

    public void setMixedContentMode(int MixedContentMode) {
        this.MixedContentMode = MixedContentMode;
        sp.edit().putInt("MixedContentMode", MixedContentMode).apply();
    }

    public void setUserAgent(String UserAgent) {
        this.UserAgent = UserAgent;
        sp.edit().putString("UserAgent", UserAgent).apply();
    }

    @Override
    public String toString() {
        return "WebViewAttr{" +
                "sp=" + sp +
                ", ctx=" + ctx +
                ", VerticalScrollBarEnabled=" + VerticalScrollBarEnabled +
                ", HorizontalScrollBarEnabled=" + HorizontalScrollBarEnabled +
                ", UseWideViewPort=" + UseWideViewPort +
                ", LoadWithOverviewMode=" + LoadWithOverviewMode +
                ", BuiltInZoomControls=" + BuiltInZoomControls +
                ", SupportZoom=" + SupportZoom +
                ", TextZoom=" + TextZoom +
                ", DisplayZoomControls=" + DisplayZoomControls +
                ", NeedInitialFocus=" + NeedInitialFocus +
                ", JavaScriptEnabled=" + JavaScriptEnabled +
                ", JavaScriptCanOpenWindowsAutomatically=" + JavaScriptCanOpenWindowsAutomatically +
                ", LoadsImagesAutomatically=" + LoadsImagesAutomatically +
                ", SaveFormData=" + SaveFormData +
                ", AllowContentAccess=" + AllowContentAccess +
                ", AllowFileAccess=" + AllowFileAccess +
                ", AllowUniversalAccessFromFileURLs=" + AllowUniversalAccessFromFileURLs +
                ", AllowFileAccessFromFileURLs=" + AllowFileAccessFromFileURLs +
                ", MediaPlaybackRequiresUserGesture=" + MediaPlaybackRequiresUserGesture +
                ", SafeBrowsingEnabled=" + SafeBrowsingEnabled +
                ", DefaultFontSize=" + DefaultFontSize +
                ", MinimumFontSize=" + MinimumFontSize +
                ", DefaultTextEncodingName='" + DefaultTextEncodingName + '\'' +
                ", StandardFontFamily='" + StandardFontFamily + '\'' +
                ", CacheMode=" + CacheMode +
                ", MixedContentMode=" + MixedContentMode +
                ", UserAgent='" + UserAgent + '\'' +
                ", user_agent=" + Arrays.toString(user_agent) +
                ", user_agent_detail=" + Arrays.toString(user_agent_detail) +
                '}';
    }
}
