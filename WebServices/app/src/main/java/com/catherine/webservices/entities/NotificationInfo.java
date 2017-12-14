package com.catherine.webservices.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Catherine on 2017/12/6.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
@SuppressWarnings("unused")
public class NotificationInfo implements Parcelable {
    private int id;
    private String title;
    private String description;
    private boolean enableLight;
    private int lightColor;
    private boolean enableVibration;
    private long[] vibrationPattern;

    public NotificationInfo(int id, String title, String description, boolean enableLight, int lightColor, boolean enableVibration, long[] vibrationPattern) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.enableLight = enableLight;
        this.lightColor = lightColor;
        this.enableVibration = enableVibration;
        this.vibrationPattern = vibrationPattern;
    }

    public static final Creator CREATOR = new Creator() {
        public NotificationInfo createFromParcel(Parcel in) {
            return new NotificationInfo(in);
        }

        public NotificationInfo[] newArray(int size) {
            return new NotificationInfo[size];
        }
    };

    // Parcelling part
    public NotificationInfo(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.enableLight = in.readByte() != 0;
        this.lightColor = in.readInt();
        this.enableVibration = in.readByte() != 0;
        this.vibrationPattern = in.createLongArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (enableLight ? 1 : 0));
        dest.writeInt(lightColor);
        dest.writeByte((byte) (enableVibration ? 1 : 0));
        dest.writeLongArray(vibrationPattern);
    }

    private NotificationInfo(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.enableLight = builder.enableLight;
        this.lightColor = builder.lightColor;
        this.enableVibration = builder.enableVibration;
        this.vibrationPattern = builder.vibrationPattern;
    }

    public static class Builder {
        private int id;
        private String title;
        private String description;
        private boolean enableLight;
        private int lightColor;
        private boolean enableVibration;
        private long[] vibrationPattern;

        public Builder() {
            id = (int) Calendar.getInstance(Locale.ENGLISH).getTimeInMillis();
            title = "Downloading...";
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder enableLight(boolean enableLight) {
            this.enableLight = enableLight;
            return this;
        }

        public Builder lightColor(int lightColor) {
            this.lightColor = lightColor;
            return this;
        }

        public Builder enableVibration(boolean enableVibration) {
            this.enableVibration = enableVibration;
            return this;
        }

        public Builder vibrationPattern(long[] vibrationPattern) {
            this.vibrationPattern = vibrationPattern;
            return this;
        }

        public NotificationInfo build() {
            return new NotificationInfo(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnableLight() {
        return enableLight;
    }

    public int getLightColor() {
        return lightColor;
    }

    public boolean isEnableVibration() {
        return enableVibration;
    }

    public long[] getVibrationPattern() {
        return vibrationPattern;
    }

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", enableLight=" + enableLight +
                ", lightColor=" + lightColor +
                ", enableVibration=" + enableVibration +
                ", vibrationPattern=" + Arrays.toString(vibrationPattern) +
                '}';
    }
}

