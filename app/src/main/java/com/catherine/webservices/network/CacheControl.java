package com.catherine.webservices.network;

/**
 * Created by Catherine on 2017/9/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class CacheControl {
    /**
     * 这里不是不缓存的意思，只是每次在使用缓存之前都强制发送请求给源服务器进行验证，检查文件该没改变(其实这里和ETag/Last区别不大)。
     */
    private boolean noCache;
    /**
     * 就是禁止缓存，不让浏览器保留缓存副本。
     */
    private boolean noStore;
    /**
     * 私有缓存，不能被共有缓存代理服务器缓存，可被用户的代理缓存如浏览器。
     */
    private boolean isPrivate;
    /**
     * 共有缓存，可被缓存代理服务器缓存,比如CDN。
     */
    private boolean isPublic;
    /**
     * 告诉浏览器，你这必须再次验证检查信息是否过期, 返回的代号就不是200而是304了。
     */
    private boolean mustRevalidate;
    /**
     * 类似must-revalidate，除了只能应用于代理缓存。
     */
    private boolean proxyRevalidate;
    /**
     * 该头域表示不进行与网络相关的交互，只返回已经缓存且满足要求的数据，否则的话返回504。
     */
    private boolean onlyIfCached;
    /**
     * Cache-Control:max-stale = s :过期后的 s 秒内缓存可以继续使用(宁可返回过期的response而不是空的body)
     */
    private int maxStaleSeconds;
    /**
     * 至少在 s 秒内缓存要保持新鲜
     */
    private int minFreshSeconds;
    /**
     * 部分网络代理为了提高性能会对图片等文档进行转换处理(比如压缩)，可以强制要求网络代理不要对资源进行转换。
     */
    private boolean noTransform;
    /**
     * Cache-Control:max-age = s ：指定相对过期日期，单位为秒
     */
    private int maxAgeSeconds;
    /**
     * Cache-Control:s-maxage:意思和 max-age 类似，但是只用于公有缓存，在共有缓存中使用的时候会覆盖 max-age 的值
     */
    private int sMaxAgeSeconds;

    public CacheControl(Builder builder) {
        this.noCache = builder.noCache;
        this.noStore = builder.noStore;
        this.maxAgeSeconds = builder.maxAgeSeconds;
        this.sMaxAgeSeconds = builder.sMaxAgeSeconds;
        this.isPrivate = builder.isPrivate;
        this.isPublic = builder.isPublic;
        this.mustRevalidate = builder.mustRevalidate;
        this.proxyRevalidate = builder.proxyRevalidate;
        this.maxStaleSeconds = builder.maxStaleSeconds;
        this.proxyRevalidate = builder.proxyRevalidate;
        this.minFreshSeconds = builder.minFreshSeconds;
        this.onlyIfCached = builder.onlyIfCached;
        this.noTransform = builder.noTransform;
    }

    public CacheControl(boolean noCache, boolean noStore, boolean isPrivate, boolean isPublic, boolean mustRevalidate, boolean proxyRevalidate, boolean onlyIfCached, int maxStaleSeconds, int minFreshSeconds, boolean noTransform, int maxAgeSeconds, int sMaxAgeSeconds) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.proxyRevalidate = proxyRevalidate;
        this.onlyIfCached = onlyIfCached;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.noTransform = noTransform;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
    }

    public static class Builder {
        private boolean noCache;
        private boolean noStore;
        private int maxAgeSeconds;
        private int sMaxAgeSeconds;
        private boolean isPrivate;
        private boolean isPublic;
        private boolean mustRevalidate;
        private boolean proxyRevalidate;
        private int maxStaleSeconds;
        private int minFreshSeconds;
        private boolean onlyIfCached;
        private boolean noTransform;

        /**
         * 预设开启cache
         */
        public Builder() {
            noCache = false;
            noStore = false;
            isPrivate = true;
        }

        public Builder noCache(boolean noCache) {
            this.noCache = noCache;
            return this;
        }

        public Builder noStore(boolean noStore) {
            this.noStore = noStore;
            return this;
        }

        public Builder maxAgeSeconds(int maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
            return this;
        }

        public Builder sMaxAgeSeconds(int sMaxAgeSeconds) {
            this.sMaxAgeSeconds = sMaxAgeSeconds;
            return this;
        }

        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder mustRevalidate(boolean mustRevalidate) {
            this.mustRevalidate = mustRevalidate;
            return this;
        }

        public Builder proxyRevalidate(boolean proxyRevalidate) {
            this.proxyRevalidate = proxyRevalidate;
            return this;
        }

        public Builder maxStaleSeconds(int maxStaleSeconds) {
            this.maxStaleSeconds = maxStaleSeconds;
            return this;
        }

        public Builder minFreshSeconds(int minFreshSeconds) {
            this.minFreshSeconds = minFreshSeconds;
            return this;
        }

        public Builder onlyIfCached(boolean onlyIfCached) {
            this.onlyIfCached = onlyIfCached;
            return this;
        }

        public Builder noTransform(boolean noTransform) {
            this.noTransform = noTransform;
            return this;
        }

        public CacheControl build() {
            return new CacheControl(this);
        }
    }

    public boolean isNoCache() {
        return noCache;
    }

    public boolean isNoStore() {
        return noStore;
    }

    public int getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public int getsMaxAgeSeconds() {
        return sMaxAgeSeconds;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isMustRevalidate() {
        return mustRevalidate;
    }

    public boolean isProxyRevalidate() {
        return proxyRevalidate;
    }

    public int getMaxStaleSeconds() {
        return maxStaleSeconds;
    }

    public int getMinFreshSeconds() {
        return minFreshSeconds;
    }

    public boolean isOnlyIfCached() {
        return onlyIfCached;
    }

    public boolean isNoTransform() {
        return noTransform;
    }

}
