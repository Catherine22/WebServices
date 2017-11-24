package com.catherine.webservices.entities;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Catherine on 2017/9/22.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ImageCardEx implements Parcelable {
    private String title, subtitle, image;
    private int style;

    public static final Creator CREATOR = new Creator() {
        public ImageCardEx createFromParcel(Parcel in) {
            return new ImageCardEx(in);
        }

        public ImageCardEx[] newArray(int size) {
            return new ImageCardEx[size];
        }
    };

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }


    public ImageCardEx() {
    }

    // Parcelling part
    public ImageCardEx(Parcel in) {
        this.title = in.readString();
        this.subtitle = in.readString();
        this.image = in.readString();
        this.style = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeString(this.image);
        dest.writeInt(this.style);
    }

    @Override
    public String toString() {
        return "ImageCard{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", image='" + image + '\'' +
                ", style='" + style + '\'' +
                '}';
    }
}
