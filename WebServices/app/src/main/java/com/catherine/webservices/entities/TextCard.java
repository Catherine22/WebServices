package com.catherine.webservices.entities;


/**
 * Created by Catherine on 2017/9/22.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TextCard {
    public String title, subtitle, contents;


    public TextCard(String title, String subtitle, String contents) {
        this.title = title;
        this.subtitle = subtitle;
        this.contents = contents;

    }

    @Override
    public String toString() {
        return "ImageCard{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
