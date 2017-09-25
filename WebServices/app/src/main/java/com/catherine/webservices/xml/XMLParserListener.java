package com.catherine.webservices.xml;

import org.dom4j.Document;

import java.util.List;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface XMLParserListener {
    void onSuccess(Document xml);

    void onSuccess(String message);

    void onSuccess(List<String> message);

    void onFail();
}
