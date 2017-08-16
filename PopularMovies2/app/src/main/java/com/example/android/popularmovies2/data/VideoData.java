package com.example.android.popularmovies2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project name PopularMovies2
 * Created by kenneth on 14/08/2017.
 */

public class VideoData implements Parcelable {
    private final String key;
    private final String name;

    public String Key(){
        return key;
    }
    public String Name(){
        return name;
    }

    public VideoData(String key, String name){
        this.key = key;
        this.name = name;
    }

    private VideoData(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoData> CREATOR = new Creator<VideoData>() {
        @Override
        public VideoData createFromParcel(Parcel in) {
            return new VideoData(in);
        }

        @Override
        public VideoData[] newArray(int size) {
            return new VideoData[size];
        }
    };



}
