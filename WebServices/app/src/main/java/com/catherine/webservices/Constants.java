package com.catherine.webservices;

import android.os.Environment;

/**
 * Created by Catherine on 2017/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Constants {
    public final static int PERMISSION_OVERLAY = 1;
    public final static int PERMISSION_WRITE_SETTINGS = 2;
    public final static int OPEN_SETTINGS = 3;

    public final static int THREAD_POOL_TIMEOUT = 2000;
    public final static int TIMEOUT = 10000;
    public final static int MAX_CONNECTIONS = 5;
    public final static String DOWNLOAD_HOST = "http://192.168.3.131:8080/WebApplication/";
    public final static String HOST = "http://192.168.3.131:8080/WebApplication/servlet/";
    public final static String AUTHORIZATION = "5fb638b4df37d8175138dfe0d072458a105ea572cff7ecd04c5e8f056c6dbad5";

    public final static String SOCKET_HOST = "192.168.3.131";
    public final static int TCP_SOCKET_PORT = 11223;
    public final static int NIO_SOCKET_PORT = 11345;
    public final static int UDP_SOCKET_PORT = 12435;


    public final static String GITHUB_API_DOMAIN = "https://api.github.com/";

    public final static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WebServices/";
    public final static String EXT_PATH = ROOT_PATH + "data/";
    protected final static String CACHE_PATH = ROOT_PATH + "cache/";

    //Fragments
    public final static int P05_Gallery = 5;
    public final static int P08_BLOCKING_SOCKET = 8;
    public final static int P09_NIO_SOCKET = 9;
    public final static int P10_UDP_SOCKET = 10;

}
