package com.catherine.webservices.entities;

/**
 * Created by Catherine on 2017/9/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ProgressBarInfo {
    public int MAX_PROGRESS = -1;
    public int cur_progress = -1;

    public ProgressBarInfo() {

    }

    public ProgressBarInfo(int MAX_PROGRESS, int cur_progress) {
        this.MAX_PROGRESS = MAX_PROGRESS;
        this.cur_progress = cur_progress;
    }
}
