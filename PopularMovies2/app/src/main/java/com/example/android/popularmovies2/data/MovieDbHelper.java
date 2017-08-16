package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Project name PopularMovies2
 * Created by kenneth on 12/08/2017.
 */

class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID  + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_TITLE  + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_POSTER  + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_VOTE_AVG  + " REAL NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_SYPNOSIS  + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_IS_TOP_RATED  + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_IS_POPULAR  + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE  + " INTEGER NOT NULL, " +
            " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
