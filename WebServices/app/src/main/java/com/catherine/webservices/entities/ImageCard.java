package com.catherine.webservices.entities;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Catherine on 2017/9/22.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ImageCard implements Parcelable {
    public String title, subtitle, image;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ImageCard createFromParcel(Parcel in) {
            return new ImageCard(in);
        }

        public ImageCard[] newArray(int size) {
            return new ImageCard[size];
        }
    };

    public ImageCard() {

    }

    public ImageCard(String title, String subtitle, String image) {
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;

    }

    // Parcelling part
    public ImageCard(Parcel in) {
        this.title = in.readString();
        this.subtitle = in.readString();
        this.image = in.readString();
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
    }

    @Override
    public String toString() {
        return "ImageCard{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
