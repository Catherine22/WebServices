package com.catherine.webservices.network;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface NetworkHealthListener {
    void networkConnected(String type);

    void networkDisable();
}
