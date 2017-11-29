package com.catherine.webservices.network;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface HttpResponseListener {
    void connectSuccess(HttpResponse response);

    void connectFailure(HttpResponse response, Exception e);
}
