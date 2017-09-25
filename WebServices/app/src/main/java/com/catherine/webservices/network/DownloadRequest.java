package com.catherine.webservices.network;

import java.util.Map;

/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public final class DownloadRequest {
    private String url;
    private Map<String, String> headers;
    private String body;
    private int THREAD_NUM = 1;
    private DownloaderListener listener;

    public DownloadRequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.THREAD_NUM = builder.THREAD_NUM;
        this.listener = builder.listener;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private String body;
        private int THREAD_NUM = 1;
        private DownloaderListener listener;

        public Builder() {
            this.body = "";
            this.headers = MyHttpURLConnection.getDefaultHeaders();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder THREAD_NUM(int THREAD_NUM) {
            this.THREAD_NUM = THREAD_NUM;
            return this;
        }

        public Builder listener(DownloaderListener listener) {
            this.listener = listener;
            return this;
        }

        public DownloadRequest build() {
            return new DownloadRequest(this);
        }
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public int getTHREAD_NUM() {
        return THREAD_NUM;
    }

    public DownloaderListener getListener() {
        return listener;
    }
}
