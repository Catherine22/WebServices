package com.catherine.webservices.network;

/**
 * Created by Catherine on 2017/9/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface SocketListener {
    void connectSuccess(String message);

    void connectFailure(Exception e);
}
