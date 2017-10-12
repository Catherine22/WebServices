package com.catherine.webservices.network;

import java.security.cert.X509Certificate;
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
    private X509Certificate certificate;
    private CacheControl cacheControl;
    private HttpResponseListener listener;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.cacheControl = builder.cacheControl;
        this.listener = builder.listener;
        this.certificate = builder.certificate;
    }

    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private String body;
        private CacheControl cacheControl;
        private HttpResponseListener listener;
        private X509Certificate certificate;

        public Builder() {
            this.body = "";
            this.headers = MyHttpURLConnection.getDefaultHeaders();
            this.cacheControl = new CacheControl(new CacheControl.Builder());
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

        public Builder cacheControl(CacheControl cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        public Builder certificate(X509Certificate certificate) {
            this.certificate = certificate;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }

    public CacheControl getCacheControl() {
        return cacheControl;
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

    public X509Certificate getCertificate() {
        return certificate;
    }

    public HttpResponseListener getListener() {
        return listener;
    }
}
