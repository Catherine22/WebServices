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
    private DownloaderListener listener;

    public DownloadRequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.listener = builder.listener;
    }

    public DownloadRequest(String url, Map<String, String> headers, String body, DownloaderListener listener) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.listener = listener;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private String body;
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

    public DownloaderListener getListener() {
        return listener;
    }
}
