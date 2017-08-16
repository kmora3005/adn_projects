package com.example.android.popularmovies2.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmovies2.data.MovieContract;

/**
 * Project name PopularMovies2
 * Created by kenneth on 12/08/2017.
 */

public class MovieSyncUtils {
    private static boolean sInitialized;

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized)
            return;

        sInitialized = true;

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            String[] projectionColumns = {MovieContract.MovieEntry._ID};
            String selectionStatement = MovieContract.MovieEntry.getSqlSelectForPopular();

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projectionColumns,
                    selectionStatement,
                    null,
                    null);

            if (null == cursor || cursor.getCount() == 0) {
                startImmediateSync(context);
            }

            assert cursor != null;
                cursor.close();
            }
        });

        checkForEmpty.start();
    }

    public static void updateFavorite(@NonNull final Context context, Uri uri, ContentValues contentValues, String[] selectionArgs) {

        context.getContentResolver().update(
                uri,
                contentValues,
                null,
                selectionArgs);
    }

    private static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
