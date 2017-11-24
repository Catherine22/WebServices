package com.catherine.webservices.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Catherine on 2017/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MultiStyleItem implements Parcelable {
    private int style;
    private String title, subtitle;
    private int select;//-1:disable, 0:not select, 1:selected
    private String data;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MultiStyleItem createFromParcel(Parcel in) {
            return new MultiStyleItem(in);
        }

        public MultiStyleItem[] newArray(int size) {
            return new MultiStyleItem[size];
        }
    };

    public MultiStyleItem() {

    }

    public MultiStyleItem(int style, String title, String subtitle, int select, String data) {
        this.style = style;
        this.title = title;
        this.subtitle = subtitle;
        this.select = select;
        this.data = data;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Parcelling part
    public MultiStyleItem(Parcel in) {
        this.style = in.readInt();
        this.title = in.readString();
        this.subtitle = in.readString();
        this.select = in.readInt();
        this.data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.style);
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeInt(this.select);
        dest.writeString(this.data);
    }

    @Override
    public String toString() {
        return "MultiStyleItem{" +
                "style=" + style +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", select=" + select +
                ", data='" + data + '\'' +
                '}';
    }
}
