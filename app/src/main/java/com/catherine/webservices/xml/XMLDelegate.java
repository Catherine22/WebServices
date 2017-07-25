package com.catherine.webservices.xml;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */


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

    public void read(String tag, ParserService service) {
        staticMutiplyAndSyncRead(tag, service);
    }

    public void add(String tag, String value) {
        staticMutiplyAndSyncAdd(tag, value);
    }

    public void modify(String tag, String value) {
        staticMutiplyAndSyncModify(tag, value);
    }

    public boolean romove(String tag) {
        staticMutiplyAndSyncRemove(tag);
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

    private static void staticMutiplyAndSyncModify(String tag, String value) {
        synchronized (XMLDelegate.getInstance()) {

        }
    }

    private static void staticMutiplyAndSyncRemove(String tag) {
        synchronized (XMLDelegate.getInstance()) {

        }
    }
}
