package com.catherine.webservices.xml;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */


import java.io.InputStream;

/**
 * 保证一次只会有一个线程运行增删改查方法。
 */
public class XMLDelegate {
    private static class SynchronizedHolder {
        private static XMLDelegate syncInstance = new XMLDelegate();
    }

    private static XMLDelegate getInstance() {
        return SynchronizedHolder.syncInstance;
    }

    public void read(ParserService service) {
        staticMutiplyAndSyncRead(service);
    }

    public void read(String tag, InputStream is, ParserService service, XMLParserListener listener) {
        service.init(is, listener);
        staticMutiplyAndSyncRead(tag, service);
    }

    public void add(String tag, String value) {
        staticMutiplyAndSyncAdd(tag, value);
    }

    public void modify(InputStream is, XMLParserListener listener) {
        staticMutiplyAndSyncModify(is, listener);
    }

    public boolean romove(InputStream is, XMLParserListener listener) {
        staticMutiplyAndSyncRemove(is, listener);
        return true;
    }

    private static void staticMutiplyAndSyncRead(ParserService service) {
        synchronized (XMLDelegate.getInstance()) {
            service.parser();

        }
    }

    private static void staticMutiplyAndSyncRead(String tag, ParserService service) {
        synchronized (XMLDelegate.getInstance()) {
            service.getValue(tag);

        }
    }

    private static void staticMutiplyAndSyncAdd(String tag, String value) {
        synchronized (XMLDelegate.getInstance()) {

        }
    }

    private static void staticMutiplyAndSyncModify(InputStream is, XMLParserListener listener) {
        synchronized (XMLDelegate.getInstance()) {
            DOMParser service = new DOMParser();
            service.init(is, listener);
            service.modify();
        }
    }

    private static void staticMutiplyAndSyncRemove(InputStream is, XMLParserListener listener) {
        synchronized (XMLDelegate.getInstance()) {
            DOMParser service = new DOMParser();
            service.init(is, listener);
            service.delete();
        }
    }
}
