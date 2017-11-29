package com.catherine.webservices.interfaces;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface ADID_Callback {
    void onResponse(String ADID);

    void onError(Exception e);
}
