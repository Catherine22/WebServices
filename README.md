## Thread
Don't hold references to any type of UI specific objects in any threading scenarios.    
Don't declare your task as an inner class of an activity.

Looper class keeps the thread alive, holds a message queue and pop works off a queue and execute on.
Handler class helps put work at the head, the tail or even set a time-based delay.

### AsyncTask：
- Helps get work on/off the UI thread.

- Basically, all AsyncTasks are created in a same thread, it means them will execute in a serial fashion from a single message queue.
- There is a way to force AsyncTask works in thread-pooled way : AsyncTask.executeOnExecutor

### HandlerThread
- Dedicated thread for API callbacks.

- HandlerThread is a nifty solution for the work that not deal with UI updates.
- Don't forget to assign the priority because CPU can only execute a few parallel threads.

### ThreadPool
- Running lots of parallel small works.

### IntentService
- It's ideal for background tasks.
- Helps get intents off UI thread.

## Web

It's the easiest way to update UIs by running on AsyncTask, and HandlerThread is also a excellent solution for the work that not deal with UI updates.

### HttpClient (org.apache.http)
- Android 6.0 release removes support for the Apache HTTP client. You still want to import this library while your target API is higher than 23, you have to declare the following codes in build.gradle:
```
android {
    useLibrary 'org.apache.http.legacy'
}
```

- Create an HttpClient for the whole project, and make ThreadSafeClientConnManager to manager the thread. Initialize HttpClient in [MyApplication] and use [MyApache] which has been packaged doGet() and doPost.
- [P01_Apache]
- HttpClient settings:[MyApplication], [MyApache]

### HttpURLConnection (java.net.HttpURLConnection)
- [P02_HttpURLConnection]
- HttpURLConnection settings:[MyHttpURLConnection]


### Multiple-threading download
- Download a single file with a few threads [P02_HttpURLConnection]
- Download images


### Cache
- HTTP cache

For example, you got a HTTP response like this.
```html
HTTP/1.1 200 OK
Cache-Control: no-cache
Content-Type: image/png
Last-Modified: Tue, 08 Nov 2016 06:59:00 GMT
Accept-Ranges: bytes
Date: Thu, 10 Nov 2016 02:48:50 GMT
Content-Length: 3534
```

You can find something about cache in HTTP headers
```html
Cache-Control: no-cache
```
The upper description is equal to 
```html
Cache-Control: max-age=0 (seconds)
```

And you can also consider it to be
```html
Cache-Control: public or private
Expires: right now
```

- **Cache-Control**

| value | meaning |
| --- | --- |
| no-store | No cache |
| no-cache | It's no need to cache the HTTP response you got, but still allowing caching |
| public | It can be cached, even if it has HTTP authentication associated with it. |
| private | Only control where the response may be cached, and cannot ensure the privacy of the message content. |
| max-age=300 | Response can be cached for up to 5 minutes. And where it is cached refer to 'private' or 'public'|


- **ETag**

ETag is typically a hash or some other fingerprint of the contents of the file.

You get the response headers like that
```html
ETag:"751F63A30AB5F98F855D1D90D217B356"
```

Your request headers contain
```html
If-None-Match: "751F63A30AB5F98F855D1D90D217B356"
```


- **Last-Modified**

You get the response headers like that
```html
Last-Modified:Tue, 03 Mar 2015 01:38:18 GMT
```

Your request headers contain
```html
If-Modified-Since:Tue, 03 Mar 2015 01:38:18 GMT
```

- **Expires**

Expires header defines a precise time but some of the users can't synchronize the latest response because they are in other time zones.		
When expires header is less than 0, it's equal to Cache-Control: no-cache
```html
Expires:Tue, 03 May 2016 09:33:34 GMT
```



Even if your cache has expired, it doesn't mean your cache isn't work. Your response might contain an ETag which instructs the client to cache it for up to 120 seconds, and provides a validation token ("x234dff") that can be used after the response has expired to check if the resource has been modified.		
If the token hasn't been changed, the server returns a "304 Not Modified" response.

So your cache strategy will be:

[![Http caching](https://raw.githubusercontent.com/Catherine22/WebServices/master/app/src/main/res/drawable/http_cache_decision_tree.png)](https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching)

First of all, make sure your browser, application or something are available to cache responses. Then check whether the response has expired.
```html
Expires：current time + maxAge(=0)
```

or

```html
Cache-Control: max-age=(0)
```

If your cache is not available, then you request ETag (with If-None-Match) or Last-Modified (with If-Modified-Since) to your server.
Your server returns 304 when it's okay to use the cache you've stored or you might get 200 with new resources.



- WebView cache


### References
- [Tencent bugly]
- [HTTP 1.1 doc]
- [Google Web Fundamentals]


[MainActivity]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MainActivity.kt>
[MyApplication]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MyApplication.java>
[MyApache]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/MyApache.java>
[MyHttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/MyHttpURLConnection.java>
[P01_Apache]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/fragments/P01_Apache.java>
[P02_HttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/fragments/P02_HttpURLConnection.java>
[HttpAsyncTask]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/HttpAsyncTask.java>
[Tencent bugly]:<https://mp.weixin.qq.com/s/qOMO0LIdA47j3RjhbCWUEQ>
[HTTP 1.1 doc]:<https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html>
[Google Web Fundamentals]:<https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching>