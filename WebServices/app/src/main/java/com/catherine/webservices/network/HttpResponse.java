package com.catherine.webservices.network;

import java.util.Map;

/**
 * Created by Catherine on 2017/9/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public final class HttpResponse {
    private String url;
    private Map<String, String> headers;
    private String body;
    private boolean cacheable;
    private int code;
    private String codeString;
    private String errorMessage;

    private HttpResponse(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.cacheable = builder.cacheable;
        this.code = builder.code;
        this.codeString = builder.codeString;
        this.errorMessage = builder.errorMessage;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private String body;
        private boolean cacheable;
        private int code;
        private String codeString;
        private String errorMessage;

        public Builder() {
            this.body = "";
            this.headers = MyHttpURLConnection.getDefaultHeaders();
            this.code = -1;
            this.codeString = "Initialize object first.";
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

        public Builder cacheable(boolean cacheable) {
            this.cacheable = cacheable;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder codeString(String codeString) {
            this.codeString = codeString;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public boolean isCacheable() {
        return cacheable;
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

    public int getCode() {
        return code;
    }

    public String getCodeString() {
        return codeString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
