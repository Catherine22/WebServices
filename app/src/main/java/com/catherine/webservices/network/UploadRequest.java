package com.catherine.webservices.network;

import java.io.File;
import java.util.Map;

/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public final class UploadRequest {
    private String url;
    private Map<String, String> headers;
    private Map<String, String> body;
    private File file;
    private UploaderListener listener;

    public UploadRequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.file = builder.file;
        this.listener = builder.listener;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private Map<String, String> body;
        private File file;
        private UploaderListener listener;

        public Builder() {
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

        public Builder body(Map<String, String> body) {
            this.body = body;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder listener(UploaderListener listener) {
            this.listener = listener;
            return this;
        }

        public UploadRequest build() {
            return new UploadRequest(this);
        }
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public File getFile() {
        return file;
    }

    public UploaderListener getListener() {
        return listener;
    }
}
