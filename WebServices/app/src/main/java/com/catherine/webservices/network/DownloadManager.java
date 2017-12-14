package com.catherine.webservices.network;

import android.os.AsyncTask;

import com.catherine.webservices.entities.NotificationInfo;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.NotificationPlugin;

import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Catherine on 2017/12/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DownloadManager {
    private final String TAG = DownloadManager.class.getSimpleName();
    private int total;
    private NotificationPlugin notificationPlugin;
    private DownloaderAsyncTask task;

    private boolean pushNotification = true;
    private boolean showPercentage = true;
    private boolean openAutomatically = true;

    public void download(String url) {
        total = 0;
        final long beginning = System.currentTimeMillis();
        if (pushNotification) {
            NotificationInfo info = new NotificationInfo.Builder()
                    .title("Downloading...")
                    .description(url)
                    .id((int) System.currentTimeMillis())
                    .build();
            notificationPlugin = new NotificationPlugin(info);
        }
        DownloadRequest request = new DownloadRequest.Builder()
                .url(url)
                .THREAD_NUM(3)
                .listener(new DownloaderListener() {
                    @Override
                    public void update(final int threadID, final int downloadedLength, final int LENGTH) {
                        total += downloadedLength;
                        if (showPercentage) {
                            float progress = ((float) total * 100.0f) / (float) LENGTH;
                            if (pushNotification)
                                notificationPlugin.update(100, (int) progress);
                            else
                                CLog.d(TAG, String.format(Locale.ENGLISH, "%d / 100", (int) progress));
                        } else {
                            if (pushNotification)
                                notificationPlugin.update(LENGTH, total);
                            else
                                CLog.d(TAG, String.format(Locale.ENGLISH, "%d / %d", LENGTH, total));
                        }

                        if (total == LENGTH) {
                            long end = System.currentTimeMillis();
                            CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess downloadedLength:%d, LENGTH:%d \n Spent %d (sec)", total, LENGTH, TimeUnit.MILLISECONDS.toSeconds(end - beginning)));
                            notificationPlugin.setPendingIntent( task.getFile(), task.getFileName());
                            NotificationInfo info = new NotificationInfo.Builder()
                                    .title("Download finished.")
                                    .description(String.format("Open %s", task.getFileName()))
                                    .build();
                            notificationPlugin.finish(info);

                        }
                    }

                    @Override
                    public void connectFailure(final HttpResponse response, final Exception e) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                        if (e != null) {
                            sb.append("\n");
                            sb.append(e.getMessage());

                            if (e instanceof SocketTimeoutException) {
                                sb.delete(0, sb.length());
                                sb.append("Connection timeout. Please check your server.");
                            }
                        }

                        //Showing a single message is enough.
                        if (pushNotification) {
                            NotificationInfo info = new NotificationInfo.Builder()
                                    .title("Download failure.")
                                    .description(sb.toString())
                                    .id((int) System.currentTimeMillis())
                                    .build();
                            notificationPlugin.finish(info);
                        }

                        CLog.e(TAG, sb.toString());

                    }
                }).build();
        task = new DownloaderAsyncTask(request);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }

    public boolean isShowPercentage() {
        return showPercentage;
    }

    public void setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
    }

    public boolean isOpenAutomatically() {
        return openAutomatically;
    }

    public void setOpenAutomatically(boolean openAutomatically) {
        this.openAutomatically = openAutomatically;
    }

    @Override
    public String toString() {
        return "DownloadManager{" +
                ", pushNotification=" + pushNotification +
                ", showPercentage=" + showPercentage +
                ", openAutomatically=" + openAutomatically +
                '}';
    }
}
