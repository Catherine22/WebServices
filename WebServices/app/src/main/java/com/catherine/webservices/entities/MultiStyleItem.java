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
    private boolean select;

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

    public MultiStyleItem(int style, String title, String subtitle, boolean select) {
        this.style = style;
        this.title = title;
        this.subtitle = subtitle;
        this.select = select;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
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


    // Parcelling part
    public MultiStyleItem(Parcel in) {
        this.style = in.readInt();
        this.title = in.readString();
        this.subtitle = in.readString();
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
    }

    @Override
    public String toString() {
        return "Item{" +
                "style='" + style + '\'' +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", isSelect='" + select + '\'' +
                '}';
    }
}
