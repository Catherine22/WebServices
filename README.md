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

### HttpClient (org.apache.http)
- Android 6.0 release removes support for the Apache HTTP client. You still want to import this library while your target API is higher than 23, you have to declare the following codes in build.gradle:
```
android {
    useLibrary 'org.apache.http.legacy'
}
```

- Create an HttpClient for the whole project, and make ThreadSafeClientConnManager to manager the thread. I initialize HttpClient in [MyApplication] and package doGet() and doPost in [MyApache].



[MainActivity]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MainActivity.kt>
[MyApplication]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/MyApplication.kt>
[MyApache]:<https://github.com/Catherine22/WebServices/blob/master/app/src/main/java/com/catherine/webservices/network/MyApache.java>
