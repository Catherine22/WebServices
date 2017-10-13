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
    public final static String GITHUB_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIHTTCCBjWgAwIBAgIQDZ3d58+sYZrDhm+uNUWKlDANBgkqhkiG9w0BAQsFADBwMQswCQYDVQQG\n" +
            "EwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMS8w\n" +
            "LQYDVQQDEyZEaWdpQ2VydCBTSEEyIEhpZ2ggQXNzdXJhbmNlIFNlcnZlciBDQTAeFw0xNzAxMTgw\n" +
            "MDAwMDBaFw0yMDA0MTcxMjAwMDBaMGgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh\n" +
            "MRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMRUwEwYDVQQKEwxHaXRIdWIsIEluYy4xFTATBgNVBAMM\n" +
            "DCouZ2l0aHViLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKnAjzBJLbsS6AwM\n" +
            "XR0ICPeRcHh+BWuvi7JVNqpplARfqbuGTlL6SEMVVOcKnVmsWWrsRtZ2FE6wFnT29Z9LpoC7BhO1\n" +
            "mFyZ0DyXsCCuEIbmtC7K4qyHR5HMB0PNzRGI/pbMIYNH1EFEbdOlLWuWpC6Lw3STy6k7k0v57ITm\n" +
            "u+oUdKLlp66rnCy9bM3L1/6GwfjbG5q+fDK4PKa0H0aCuom85WcrFfPKj3AqXOe5aukASkNhfVoE\n" +
            "DLLEIoF30zZvJQAEPi+oAmZ4HeCnx2D0iWANO6BVprkjmpYIdNFofD1I7kRq3DcVqZoHwC0BxxWI\n" +
            "KA3A/mvPhqCZPhnSXd6J4GUCAwEAAaOCA+kwggPlMB8GA1UdIwQYMBaAFFFo/5CvAgd1PMzZZWRi\n" +
            "ohK4WXI7MB0GA1UdDgQWBBTqYVKy/gpAgOUgijA3JKDqpmxqqjAjBgNVHREEHDAaggwqLmdpdGh1\n" +
            "Yi5jb22CCmdpdGh1Yi5jb20wDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggr\n" +
            "BgEFBQcDAjB1BgNVHR8EbjBsMDSgMqAwhi5odHRwOi8vY3JsMy5kaWdpY2VydC5jb20vc2hhMi1o\n" +
            "YS1zZXJ2ZXItZzUuY3JsMDSgMqAwhi5odHRwOi8vY3JsNC5kaWdpY2VydC5jb20vc2hhMi1oYS1z\n" +
            "ZXJ2ZXItZzUuY3JsMEwGA1UdIARFMEMwNwYJYIZIAYb9bAEBMCowKAYIKwYBBQUHAgEWHGh0dHBz\n" +
            "Oi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwCAYGZ4EMAQICMIGDBggrBgEFBQcBAQR3MHUwJAYIKwYB\n" +
            "BQUHMAGGGGh0dHA6Ly9vY3NwLmRpZ2ljZXJ0LmNvbTBNBggrBgEFBQcwAoZBaHR0cDovL2NhY2Vy\n" +
            "dHMuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0U0hBMkhpZ2hBc3N1cmFuY2VTZXJ2ZXJDQS5jcnQwDAYD\n" +
            "VR0TAQH/BAIwADCCAfQGCisGAQQB1nkCBAIEggHkBIIB4AHeAHUApLkJkLQYWBSHuxOizGdwCjw1\n" +
            "mAT5G9+443fNDsgN3BAAAAFZspw+SwAABAMARjBEAiAerA7v0bVTR1blKT6IzEFdNAbS9J2+sMIy\n" +
            "N6Dad8QGQgIgA94eKijn12cROhrzND6+thVW/PdImUzTEodCGFgaCPUAdgBWFAaaL9fC7NP14b1E\n" +
            "sj7HRna5vJkRXMDvlJhV1onQ3QAAAVmynD8cAAAEAwBHMEUCICSmaZLAKILGLXy9tbDCRcqKx4Ka\n" +
            "XaOFICxUHLDavhvTAiEAiiXvucr1ZYHcoJ1ix+/UAyW4Sy1+SfIxV//PVuMumFcAdQDuS723dc5g\n" +
            "uuFCaR+r4Z5mow9+X7By2IMAxHuJeqj9ywAAAVmynEAuAAAEAwBGMEQCIHKrzA5IbzNEGMr2Nmbm\n" +
            "cJncuUTZHsHRJqU0eCZcian5AiBGGIX5adWUbjuFWiBb1ZnEkYsH81/ozbYGm5xY36IkxwB2ALvZ\n" +
            "37wfinG1k5Qjl6qSe0c4V5UKq1LoGpCWZDaOHtGFAAABWbKcPl0AAAQDAEcwRQIhAJJMvQld/c79\n" +
            "AthFEj02qVdazf/Smjkb+ggN/DjrSB28AiAGEjSwxCj4r+PnUgzFtURbLofk3iAvECLj9NZO2SZb\n" +
            "dTANBgkqhkiG9w0BAQsFAAOCAQEAfIMvSUq9Z4EUniI976aOkXTSPwa8GT+KFzlLpcyPmcU/x8AT\n" +
            "ptUsARnS96Yzx7BWtchprXsDWKdFLgmQ/YTTdgUfy/Qyy7baJvCyLwBf4cJpsBdYaKxcige2dnAJ\n" +
            "jgVIvl8jEO4k+lD5BWgqQgRElDXj0SVVQQ1wd0MZTKWlDVbxmKsXzu5I0kWCG6/mfBcJc+6H+ABW\n" +
            "c1YIK+pLP1jDYcC8wj9fRkTCpZW/3lZ/Nt+snM1ujTRZ7RTBlRG2uJLpIX15JihSprEr3u39RHUp\n" +
            "HOODLNzVAw63zfJqCJvPtaCr+/KXKrqfjk9Z+e7Nmg+IxOf4M/Mxbox4KJvLlX8pwQ==\n" +
            "-----END CERTIFICATE-----";

    public final static String SOCKET_HOST = "192.168.3.131";
    public final static int TCP_SOCKET_PORT = 11223;
    public final static int NIO_SOCKET_PORT = 11345;
    public final static int UDP_SOCKET_PORT = 12435;


    public final static String GITHUB_API_DOMAIN = "https://api.github.com/";

    public static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WebServices/";
    public final static String EXT_PATH = ROOT_PATH + "data/";
    public final static String FRESCO_DIR = "fresco";
    protected final static String CACHE_PATH = ROOT_PATH + "cache/";

    //Fragments
    public final static int P05_Gallery = 5;
    public final static int P08_BLOCKING_SOCKET = 8;
    public final static int P09_NIO_SOCKET = 9;
    public final static int P10_UDP_SOCKET = 10;
    public final static int P11_FRESCO = 11;

}
