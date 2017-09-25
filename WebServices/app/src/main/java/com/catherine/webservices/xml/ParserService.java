package com.catherine.webservices.xml;

import java.io.InputStream;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface ParserService {

    void init(InputStream content, XMLParserListener listener);

    void parser();

    void getValue(String tag);
}
