package com.example.android.popularmovies2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project name PopularMovies2
 * Created by kenneth on 14/08/2017.
 */

public class ReviewData implements Parcelable {
    private final String author;
    private final String content;

    public String Author(){
        return author;
    }
    public String Content(){
        return content;
    }

    public ReviewData(String author, String content){
        this.author = author;
        this.content = content;
    }

    private ReviewData(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };
}
