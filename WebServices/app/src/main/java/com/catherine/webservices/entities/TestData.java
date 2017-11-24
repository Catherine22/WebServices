package com.catherine.webservices.entities;

/**
 * Created by Catherine on 2017/11/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TestData {

    public static final String[] IMAGES0 = new String[]{
            // Heavy images
            "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg",
            "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg",
            "https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s1024/Another%252520Rockaway%252520Sunset.jpg",
            "https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg",
            "https://lh4.googleusercontent.com/-WIuWgVcU3Qw/URqubRVcj4I/AAAAAAAAAbs/YvbwgGjwdIQ/s1024/Antelope%252520Walls.jpg",
            // Special cases
            "http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // Very large image
            "http://www.ioncannon.net/wp-content/uploads/2011/06/test9.webp", // WebP image
            "http://4.bp.blogspot.com/-LEvwF87bbyU/Uicaskm-g6I/AAAAAAAAZ2c/V-WZZAvFg5I/s800/Pesto+Guacamole+500w+0268.jpg", // Image with "Mark has been invalidated" problem
            "http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png", // Link with UTF-8
            "https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from HTTPS
            "http://bit.ly/soBiXr", // Redirect link
            "http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
            "", // Empty link
            "http://wrong.site.com/corruptedLink" // Wrong link
    };

    public static final String[] IMAGES1 = new String[]{
            // Light images
            "http://tabletpcssource.com/wp-content/uploads/2011/05/android-logo.png",
            "http://simpozia.com/pages/images/stories/windows-icon.png",
            "http://radiotray.sourceforge.net/radio.png",
            "http://www.bandwidthblog.com/wp-content/uploads/2011/11/twitter-logo.png",
            "http://weloveicons.s3.amazonaws.com/icons/100907_itunes1.png",
            "http://weloveicons.s3.amazonaws.com/icons/100929_applications.png",
            "http://www.idyllicmusic.com/index_files/get_apple-iphone.png",
            "http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png",
            "http://3.bp.blogspot.com/-ka5MiRGJ_S4/TdD9OoF6bmI/AAAAAAAAE8k/7ydKtptUtSg/s1600/Google_Sky%2BMaps_Android.png",
            "http://www.desiredsoft.com/images/icon_webhosting.png",
            "http://goodereader.com/apps/wp-content/uploads/downloads/thumbnails/2012/01/hi-256-0-99dda8c730196ab93c67f0659d5b8489abdeb977.png",
            "http://1.bp.blogspot.com/-mlaJ4p_3rBU/TdD9OWxN8II/AAAAAAAAE8U/xyynWwr3_4Q/s1600/antivitus_free.png",
            "http://cdn3.iconfinder.com/data/icons/transformers/computer.png",
            "http://cdn.geekwire.com/wp-content/uploads/2011/04/firefox.png?7794fe",
            "https://ssl.gstatic.com/android/market/com.rovio.angrybirdsseasons/hi-256-9-347dae230614238a639d21508ae492302340b2ba",
            "http://androidblaze.com/wp-content/uploads/2011/12/tablet-pc-256x256.jpg",
            "http://www.theblaze.com/wp-content/uploads/2011/08/Apple.png",
            "http://1.bp.blogspot.com/-y-HQwQ4Kuu0/TdD9_iKIY7I/AAAAAAAAE88/3G4xiclDZD0/s1600/Twitter_Android.png",
            "http://3.bp.blogspot.com/-nAf4IMJGpc8/TdD9OGNUHHI/AAAAAAAAE8E/VM9yU_lIgZ4/s1600/Adobe%2BReader_Android.png",
            "http://cdn.geekwire.com/wp-content/uploads/2011/05/oovoo-android.png?7794fe",
            "http://icons.iconarchive.com/icons/kocco/ndroid/128/android-market-2-icon.png",
            "http://thecustomizewindows.com/wp-content/uploads/2011/11/Nicest-Android-Live-Wallpapers.png",
            "http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
            "http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg"
    };

    public static final String[] IMAGES2 = new String[]{
            "https://lh4.googleusercontent.com/-PDT167_xRdA/URqvK36mLcI/AAAAAAAAAbs/oi2ik9QseMI/s1024/Windmills.jpg",
            "https://lh5.googleusercontent.com/-kI_QdYx7VlU/URqvLXCB6gI/AAAAAAAAAbs/N31vlZ6u89o/s1024/Yet%252520Another%252520Rockaway%252520Sunset.jpg",
            "https://lh4.googleusercontent.com/-e9NHZ5k5MSs/URqvMIBZjtI/AAAAAAAAAbs/1fV810rDNfQ/s1024/Yosemite%252520Tree.jpg"
    };

    public static final String[] TITLES0 = new String[]{
            "afnsjlksdafl", "googleusercontent", "URqu7axzkAI", "aqZkgX66zlI", "ttps://lh3.googleusercontent.com/-eDLT2jHDoy4/URqu7axzkAI/AAAAAAAAAbs/iVZE-xJ7lZs/s1024/Ra", "你好", "<p>106.10.23</p>",
            "afnsjlksdafl", "googleusercontent", "URqu7axzkAI", "aqZkgX66zlI", "ttps://lh3.googleusercontent.com/-eDLT2jHDoy4/URqu7axzkAI/AAAAAAAAAbs/iVZE-xJ7lZs/s1024/Ra", "你好", "<p>106.10.23</p>"
    };
}
