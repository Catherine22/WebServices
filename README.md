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
- Http cache
- WebView cache




[MainActivity]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MainActivity.kt>
[MyApplication]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MyApplication.java>
[MyApache]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/MyApache.java>
[MyHttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/MyHttpURLConnection.java>
[P01_Apache]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/fragments/P01_Apache.java>
[P02_HttpURLConnection]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/fragments/P02_HttpURLConnection.java>
[HttpAsyncTask]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/HttpAsyncTask.java>
