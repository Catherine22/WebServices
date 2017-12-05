# Thread

> Don't hold references to any type of UI specific objects in any
> threading scenarios.     
> Don't declare your task as an inner class of an activity.

Looper class keeps the thread alive, holds a message queue and pop works off a queue and execute on.
Handler class helps put work at the head, the tail or even set a time-based delay.

## AsyncTask：
*Help get work on/off the UI thread.*

- Basically, all AsyncTasks are created in a same thread. That means them will execute in a serial fashion from a single message queue.
- There is a way to force AsyncTask works in thread-pooled way : AsyncTask.executeOnExecutor

## HandlerThread
*Dedicate thread for API callbacks.*

- HandlerThread is a nifty solution for the work that not deal with UI updates.
- Don't forget to assign the priority because CPU can only execute a few parallel threads.

## ThreadPool
*Run a lot of parallel small works.*

## IntentService
*It's ideal for background tasks. It also helps get intents off UI thread.*

*It's the easiest way to update UIs by running on AsyncTask, and HandlerThread is also a excellent solution for the work that not deal with UI updates.*

# Web


## Tips
[![TCP/IP model](https://raw.githubusercontent.com/Catherine22/WebServices/master/tcp_ip_model.png)](https://technet.microsoft.com/en-us/library/cc958821.aspx)


> - TCP/IP specifies how data is exchanged over the internet.
> - Socket is kind of like APIs which packages TCP and UDP operations.
> - HTTP, FTP, SMTP and the other protocols map to the application layer which only standardizes communication and depends upon the underlying
> transport layer protocols to establish host-to-host data transfer
> channels and manage the data exchange in a client-server or
> peer-to-peer networking model.
> - In real life scenario examples:
> 	- TCP : FTP, HTTP
> 	- UDP : Voice and Video, Streaming movies online



## HttpClient (org.apache.http)
- Android 6.0 release removes support for the Apache HTTP client. You still want to import this library while your target API is higher than 23, you have to declare the following codes in build.gradle:
```
android {
    useLibrary 'org.apache.http.legacy'
}
```

- Create an HttpClient for the whole project, and make ThreadSafeClientConnManager to manager the thread. Initialize HttpClient in [MyApplication] and use [MyApache] which has been packaged doGet() and doPost.
- [P01_Apache]
- HttpClient settings:[MyApplication], [MyApache]

## HttpURLConnection (java.net.HttpURLConnection)
- [P02_HttpURLConnection]
- HttpURLConnection settings:[MyHttpURLConnection]


## OkHttp

## Multiple-threading download
- Download a single file with multiple threads
- Show the progressbar
- [P03_Downloader]
- [DownloaderAsyncTask]

## Upload files to the server
- [P06_Upload]


## Cache
>Under normal usage, the starting point for any developer should be to add as an aggressive caching strategy to the files in the application that will not change. 		
>Normally this will include static files that are served by the application such as **images**, **CSS file** and **Javascript files**. 		
>As these files are typically re-requested on each page, a large performance improvement can be had with little effort.		

An application using HTTP cache headers is able to control this caching behavior and alleviate server-side load.		
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

The ```Cache-Control``` header is the most important header to set as it effectively ‘switches on’ caching in the browser.		

| value | meaning |
| --- | --- |
| no-store | No cache |
| no-cache | It's no need to cache the HTTP response you got, but still allowing caching |
| public | 1. It can be cached, even if it has HTTP authentication associated with it.		
 2. Public resources can be cached not only by the end-user’s browser but also by any intermediate proxies that may be serving many other users as well.|
| private | 1. Only control where the response may be cached, and cannot ensure the privacy of the message content.		
 2. Private resources are bypassed by intermediate proxies and can only be cached by the end-client.|
| max-age=300 | Response can be cached for up to 5 minutes. And where it is cached refer to 'private' or 'public'|


- **Expires**

Superseded by ```Cache-Control``` header. And ```Cache-Control``` has priority over ```Expires```.		
If both ```Expires``` and ```max-age``` are set ```max-age``` will take precedence.		
```Expires``` header defines a precise time but some of the users can't synchronize the latest response because they are in other time zones.		

```html
Expires:Tue, 03 May 2016 09:33:34 GMT
```

- **Last-Modified**

You get the response headers like that
```html
Cache-Control:public, max-age=31536000
Last-Modified: Mon, 03 Jan 2011 17:45:57 GMT
```

And next time, your request headers contain
```html
Last-Modified: Mon, 03 Jan 2011 17:45:57 GMT
```
Your request must contain ```If-Modified-Since``` so that the server will check the timestamp.    
If the resource hasn't changed since ```Mon, 03 Jan 2011 17:45:57 GMT```, the server'll return **304** with an empty body.


- **ETag**

ETag (or Entity Tag) is typically a hash or some other fingerprint of the contents of the file (for instance, an MD5 hash).

You get the response headers like that
```html
Cache-Control:public, max-age=31536000
ETag:"751F63A30AB5F98F855D1D90D217B356"
```

And next time, your request headers contain
```html
If-None-Match: "751F63A30AB5F98F855D1D90D217B356"
```

If the resource hasn't changed, the server'll return **304** with an empty body.


Even if your cache is up, it doesn't mean your cache is not working. Your response might contain an ETag which instructs the client to cache it for up to a specific period and provides a validation token ( MD5 or something ) that can be used after the response has expired to check if the resource has been modified.		
If the token hasn't been changed, the server returns a "304 Not Modified" response.

So your cache strategy will be:

[![Http caching](https://raw.githubusercontent.com/Catherine22/WebServices/master/http_cache_decision_tree.png)](https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching)

First of all, make sure your browser, application or something are available to cache responses. Then check whether the response has expired.
```html
Expires：current time + maxAge(=0)
```

or

```html
Cache-Control: max-age=(0)
```

If your cache is not available, then you request ETag (with If-None-Match) or Last-Modified (with If-Modified-Since) to your server (Of course you could alse use both of them to double-check).
Your server would return 304 when it's okay to use the cache that has been stored or you get 200 with new resources.

- **Force to refresh**
F5: ```Cache-Control:max-age=0```
Ctrl + F5: ```Cache-Control:no-cache``` or ```Pragma:no-cache```

- **No cache**

Both values are required as IE uses ```no-cache```, and Firefox uses ```no-store```.

```html
Cache-Control:no-cache, no-store
```

- WebView cache

## HTTPS
[![HTTPS workflos](https://raw.githubusercontent.com/Catherine22/WebServices/master/https_workflow.png)](http://limboy.me/tech/2011/02/19/https-workflow.html)

 1. Client requests a https url
 2. There is a keypair in server.
 3. Server passes the public key to client.
 4. Client validates the public key and generate a key (I call it client key). If the public key is able to be truthed, client encrypts the client key with the public key.
 5. Client sends the encrypted client key to server.
 6. Server decrypts the client key with private key.
 7. Server sends messages encrypted with the client key to client.
 8. Client decrypts the messages with the client key.


> In step 4, how does client know that public key is valid?		 
> Let CA (Certificate Authority) list tells client.
> There are hundreds of CAs in the world. Which CA you can trust depends on a CAs list in client. (Normally, your mobile phone had been saved a list of trusted CAs before you bought it.)

### Android HTTPS
***DO NOT IMPLEMENT "X509TrustManager" TO SKIP VALIDATION***        
That means man-in-the-middle attacks are allowed.

As I was mentioning, If you open a url ([https://kyfw.12306.cn/otn/regist/init][1]) with your Android device and you get an exception:

``` html
java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
```
Because the CA of kyfw.12306.cn is not in default trusted CA list of your device.

In this scenario, if you are sure this website is trusted and you've got to request it, you create your SSLTrustManager which implements X509TrustManager and do nothing in the override methods. It means you skip certificates verification. Hackers can send you a fake public key to connect to client because you don't validate the public key in step 4, your device finally connect to the hacker's server.

----------

There are two solutions, the last one is better.
1. Get the certificate of kyfw.12306.cn and keep it in assets folder. You add this certificate to trusted CA list of client.<br>
*After the end of the validity period, the certificate is no longer considered an acceptable.*

2. Get the certificate of CA which sponsors kyfw.12306.cn and keep it in assets folder. You add this certificate to the trusted CA list of client.<br>
Normally, the CAs of the department sponsors specific domains have a longer validity period than domains. That's why this solution is recommended.

**Android example**

Take kyfw.12306.cn for example.
1. Download the certificate of kyfw.12306.cn and add to assets file.
2. Add this certificate to trustManager[]
3. Let "HttpsURLConnection" trust this certificate
4. Go to [P02_HttpURLConnection] to see more.

Another example - request to [github APIs]
1. Add the PEM formatted String of the certificate.
2. Add this certificate to trustManager[]
3. Let "HttpsURLConnection" trust this certificate
4. Go to [P02_HttpURLConnection] to see more.

Check SSL certificates here : [https://www.ssllabs.com/ssltest/][2]

## Download and cache images
**ImageView + DiskLruCache**
1. Download a url list.		
2. Check internal or external storage of the device. If the images have had been cached, skip step 3 and show them.
3. Download each image from the list and try to cache them.		
4. Show images whatever they have been cached.		

Here is the example: [P05_Gallery], [ImageCardRVAdapter]

**Fresco - SimpleDraweeView**

Three ways to deal with images:
- Show images directly by calling setImageURI().
- Prefetch images and show them at the same time. That means there're at least two threads -  the one downloads the images and another displays the images.      
- As the app launches, prefetch images and save them to your cache folder even though the fragment or activity which displays the images hasn't opened yet.

Here is the example: [P11_Fresco], [FrescoRVAdapter]

## TCP Sockets
**Blocking socket**
- Server : Run [MySocket] with eclipse
```java
tcpSocketReceiver();
```
- Client : Run WebServices and open [P08_Blocking_Socket] fragment on Android devices.


**Non-blocking socket (NIO Socket)**
- Server : Run [MySocket] with eclipse
```java
startNIOSocket();
```
- Client : Run WebServices and open [P09_NIO_Socket] fragment on Android devices.

## UDP Sockets
- Server : Run [MySocket] with eclipse
```java
udpSocket();
```
- Client : Run WebServices and open [P10_UDP_Socket] fragment on Android devices.

## WebView
**[MyWebView]**, [P14_Full_WebView] and [P13_Nested_WebView] supports the functions of...

1. Going back to previous pages.
2. Showing a ProgressBar while WebView is loading resources.
3. Launching other apps installed in your device by url scheme.
4. Handling JavaScript alert(), confirm() and prompt() and displaying the message with a used-defined dialog.
>Before using JavaScript, you should have WebView enable JavaScript. Go to [P15_WebView_Settings] to set.		

5. Calling Java functions from JavaScript
> Two tips:
>  1. Don't forget to ignore your JavaScriptInterface with Android proguard
>  2. Add @JavascriptInterface Annotation and you can go to [MyJavaScriptInterface] to see more

In this project, all classes that can not be obfuscated implement IgnoreProguard interface.     
In proguard-rules.pro
```gradle
-keep public class com.catherine.webservices.toolkits.IgnoreProguard
-keep public class * implements com.catherine.webservices.toolkits.IgnoreProguard
-keepclassmembers class * implements com.catherine.webservices.toolkits.IgnoreProguard {
    <methods>;
}
```
6. Calling JavaScript functions from Java.
7. Saving photos to your device from the Internet.
> Save the image after long-clicking it.

8. Visiting a HTTPS website and you get a SSL error.
> Again, you could use [https://kyfw.12306.cn/otn/regist/init][1] to test.    
> Override ```onReceivedSslError()``` and pop up a dialog to let users decide to continue (it would be unsafe maybe) or stop visiting the website.		

9. Switching desktop style or mobile style websites by user-agent
10. Getting media and location permission
>You need to add following permission in your AndroidManifest.xml
>And override ```onGeolocationPermissionsShowPrompt()``` and ```onPermissionRequest()``` in WebChromeClient.

```xml
<!--getUserMedia-->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<!--location-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```
11. HTML5
>Go to [http://html5test.com/][6] to see the browser compatibility.

12. Supporting Dom storage
13. Supporting Web SQL database and IndexedDB
>Web SQL has been deprecated by W3C
>IndexedDB is available on Android 4.4+

14. Launching FileChooser
You could also go to **[P17_WebView_Test_List]** to test all the websites I listed.

**[P15_WebView_Settings]**
Set attributes of WebView that includes WebViewClient and WebSettings (setAllowFileAccess(), setJavaScriptEnabled(), setSupportZoom() and so forth.)

## OAuth 2.0


## References
- [Tencent bugly]
- [HTTP 1.1 doc]
- [HTTP cache cn]
- [Google Web Fundamentals]
- [increasing-application-performance-with-http-cache-headers]
- [Socket tutorial]
- [What are examples of TCP and UDP in real life scenario ?]
- [Android HTTPS]
- [WebView tutorial 1]
- [WebView tutorial 2]
- [OAuth 2.0 doc]
- [OAuth 2.0 cn]


[MainActivity]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/MainActivity.kt>
[MyApplication]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/MyApplication.java>
[MyApache]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/network/MyApache.java>
[MyHttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/network/MyHttpURLConnection.java>
[HttpAsyncTask]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/network/HttpAsyncTask.java>
[DownloaderAsyncTask]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/network/DownloaderAsyncTask.java>
[ImageCardRVAdapter]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/adapters/ImageCardRVAdapter.java>
[FrescoRVAdapter]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/adapters/FrescoRVAdapter.java>
[Socket tutorial]:<http://blog.csdn.net/coder_pig/article/details/48519629>
[MyWebView]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/components/MyWebView.java>
[MySocket]:<https://github.com/Catherine22/WebServices/blob/master/JavaSocketServer/MySocket/src/Main.java>
[MyJavaScriptInterface]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/network/MyJavaScriptInterface.java>
[P01_Apache]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/P01_Apache.java>
[P02_HttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/P02_HttpURLConnection.java>
[P03_Downloader]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/P03_Downloader.java>
[P05_Gallery]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/cache/P05_Gallery.java>
[P06_Upload]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/P06_Upload.java>
[P08_Blocking_Socket]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/socket/P08_Blocking_Socket.java>
[P09_NIO_Socket]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/socket/P09_NIO_Socket.java>
[P10_UDP_Socket]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/socket/P10_UDP_Socket.java>
[P11_Fresco]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/cache/P11_Fresco.java>
[P13_Nested_WebView]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/webview/P13_Nested_WebView.java>
[P14_Full_WebView]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/webview/P14_Full_WebView.java>
[P15_WebView_Settings]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/webview/P15_WebView_Settings.java>
[P17_WebView_Test_List]:<https://github.com/Catherine22/WebServices/blob/master/WebServices/app/src/main/java/com/catherine/webservices/fragments/webview/P17_WebView_Test_List.java>


[Tencent bugly]:<https://mp.weixin.qq.com/s/qOMO0LIdA47j3RjhbCWUEQ>
[HTTP 1.1 doc]:<https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html>
[Google Web Fundamentals]:<https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching>
[increasing-application-performance-with-http-cache-headers]:<https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers>
[What are examples of TCP and UDP in real life scenario ?]:<https://learningnetwork.cisco.com/thread/87103>
[Android HTTPS]:<http://blog.csdn.net/iispring/article/details/51615631>
[github APIs]:<https://api.github.com/>
[HTTP cache cn]:<https://segmentfault.com/a/1190000004132566#articleHeader3>
[WebView tutorial 1]:<http://www.jianshu.com/p/3fcf8ba18d7f>
[WebView tutorial 2]:<http://blog.csdn.net/huaxun66/article/details/73179187>
[OAuth 2.0 doc]:<https://tools.ietf.org/html/draft-ietf-oauth-v2-23>
[OAuth 2.0 cn]:<http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html>

  [1]: https://kyfw.12306.cn/otn/regist/init
  [2]: https://www.ssllabs.com/ssltest/
  [3]: https://www.javascript.com/
  [4]: https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en
  [5]: https://github.com/Catherine22
  [6]: http://html5test.com/
