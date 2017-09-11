package com.catherine.webservices.network;

import java.util.Map;

/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public final class HttpRequest {
    private String url;
    private Map<String, String> headers;
    private String body;
    private HttpResponseListener listener;

    public HttpRequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.listener = builder.listener;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private String body;
        private HttpResponseListener listener;

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

        public Builder listener(HttpResponseListener listener) {
            this.listener = listener;
            return this;
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

    public HttpResponseListener getListener() {
        return listener;
    }
}
