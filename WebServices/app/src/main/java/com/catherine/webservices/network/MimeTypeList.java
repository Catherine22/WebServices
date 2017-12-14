package com.catherine.webservices.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Catherine on 2017/12/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
//https://android.googlesource.com/platform/frameworks/base/+/android-4.4_r1.1/media/java/android/media/MediaFile.java
public class MimeTypeList {
    public static Map<String, String> MIME_TYPE_LIST = new HashMap<>();

    static {
        MIME_TYPE_LIST.put("MP3", "audio/mpeg");
        MIME_TYPE_LIST.put("MPGA", "audio/mpeg");
        MIME_TYPE_LIST.put("M4A", "audio/mp4");
        MIME_TYPE_LIST.put("WAV", "audio/x-wav");
        MIME_TYPE_LIST.put("AMR", "audio/amr");
        MIME_TYPE_LIST.put("AWB", "audio/amr-wb");
        MIME_TYPE_LIST.put("WMA", "audio/x-ms-wma");
        MIME_TYPE_LIST.put("OGG", "audio/ogg");
        MIME_TYPE_LIST.put("OGG", "application/ogg");
        MIME_TYPE_LIST.put("OGA", "application/ogg");
        MIME_TYPE_LIST.put("AAC", "audio/aac");
        MIME_TYPE_LIST.put("AAC", "audio/aac-adts");
        MIME_TYPE_LIST.put("MKA", "audio/x-matroska");

        MIME_TYPE_LIST.put("MID", "audio/midi");
        MIME_TYPE_LIST.put("MIDI", "audio/midi");
        MIME_TYPE_LIST.put("XMF", "audio/midi");
        MIME_TYPE_LIST.put("RTTTL", "audio/midi");
        MIME_TYPE_LIST.put("SMF", "audio/sp-midi");
        MIME_TYPE_LIST.put("IMY", "audio/imelody");
        MIME_TYPE_LIST.put("RTX", "audio/midi");
        MIME_TYPE_LIST.put("OTA", "audio/midi");
        MIME_TYPE_LIST.put("MXMF", "audio/midi");

        MIME_TYPE_LIST.put("MPEG", "video/mpeg");
        MIME_TYPE_LIST.put("MPG", "video/mpeg");
        MIME_TYPE_LIST.put("MP4", "video/mp4");
        MIME_TYPE_LIST.put("M4V", "video/mp4");
        MIME_TYPE_LIST.put("3GP", "video/3gpp");
        MIME_TYPE_LIST.put("3GPP", "video/3gpp");
        MIME_TYPE_LIST.put("3G2", "video/3gpp2");
        MIME_TYPE_LIST.put("3GPP2", "video/3gpp2");
        MIME_TYPE_LIST.put("MKV", "video/x-matroska");
        MIME_TYPE_LIST.put("WEBM", "video/webm");
        MIME_TYPE_LIST.put("TS", "video/mp2ts");
        MIME_TYPE_LIST.put("AVI", "video/avi");
        MIME_TYPE_LIST.put("WMV", "video/x-ms-wmv");
        MIME_TYPE_LIST.put("ASF", "video/x-ms-asf");
        MIME_TYPE_LIST.put("JPG", "image/jpeg");
        MIME_TYPE_LIST.put("JPEG", "image/jpeg");
        MIME_TYPE_LIST.put("GIF", "image/gif");
        MIME_TYPE_LIST.put("PNG", "image/png");
        MIME_TYPE_LIST.put("BMP", "image/x-ms-bmp");
        MIME_TYPE_LIST.put("WBMP", "image/vnd.wap.wbmp");
        MIME_TYPE_LIST.put("WEBP", "image/webp");

        MIME_TYPE_LIST.put("M3U", "audio/x-mpegurl");
        MIME_TYPE_LIST.put("M3U", "application/x-mpegurl");
        MIME_TYPE_LIST.put("PLS", "audio/x-scpls");
        MIME_TYPE_LIST.put("WPL", "application/vnd.ms-wpl");
        MIME_TYPE_LIST.put("M3U8", "application/vnd.apple.mpegurl");
        MIME_TYPE_LIST.put("M3U8", "audio/mpegurl");
        MIME_TYPE_LIST.put("M3U8", "audio/x-mpegurl");
        MIME_TYPE_LIST.put("FL", "application/x-android-drm-fl");
        MIME_TYPE_LIST.put("TXT", "text/plain");
        MIME_TYPE_LIST.put("HTM", "text/html");
        MIME_TYPE_LIST.put("HTML", "text/html");
        MIME_TYPE_LIST.put("PDF", "application/pdf");
        MIME_TYPE_LIST.put("DOC", "application/msword");
        MIME_TYPE_LIST.put("XLS", "application/vnd.ms-excel");
        MIME_TYPE_LIST.put("PPT", "application/mspowerpoint");
        MIME_TYPE_LIST.put("FLAC", "audio/flac");
        MIME_TYPE_LIST.put("ZIP", "application/zip");
        MIME_TYPE_LIST.put("MPG", "video/mp2p");
        MIME_TYPE_LIST.put("MPEG", "video/mp2p");

        MIME_TYPE_LIST.put("APK", "application/vnd.android.package-archive");
    }
}