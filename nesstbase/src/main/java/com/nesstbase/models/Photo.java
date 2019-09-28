
package com.nesstbase.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Photo implements Parcelable {

    public static String ONLINE = "online";
    public static String OFFLINE = "offline";
    private String url;
    private String type = "OFFLINE";

    public Photo() {
    }

    public String url() {
        return url;
    }

    public Photo url(String url) {
        this.url = url;
        return this;
    }

    public String type() {
        return type;
    }

    public Photo type(String type) {
        this.type = type;
        return this;
    }

    public boolean isOnLine() {
        return type.equals(ONLINE);
    }

    public boolean isOffLine() {
        return !isOnLine();
    }

    public Photo online() {
        return type(ONLINE);
    }

    public Photo offline() {
        return type(OFFLINE);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.type);
    }

    protected Photo(Parcel in) {
        this.url = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
