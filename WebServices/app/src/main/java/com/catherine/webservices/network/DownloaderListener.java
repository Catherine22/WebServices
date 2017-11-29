package com.catherine.webservices.network;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface DownloaderListener {
    void update(int threadID, int downloadedLength, int LENGTH);

    void connectFailure(HttpResponse response, Exception e);
}
