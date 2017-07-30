package com.example.android.popularmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project name PopularMovies
 * Created by kenneth on 29/07/2017.
 */

public class MovieData implements Parcelable {
    private final String title;
    private final String posterPath;
    private final String releaseDate;
    private final float voteAverage;
    private final String plotSynopsis;

    public String Title(){
        return title;
    }
    public String PosterPath(){
        return posterPath;
    }
    public String ReleaseDate(){
        return releaseDate;
    }
    public float VoteAverage(){
        return voteAverage;
    }
    public String PlotSynopsis(){
        return plotSynopsis;
    }

    public MovieData(String title,String posterPath, String releaseDate, float voteAverage, String plotSynopsis){
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    private MovieData(Parcel in) {
        this.title = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readFloat();
        this.plotSynopsis = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeFloat(voteAverage);
        dest.writeString(plotSynopsis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
