package com.example.android.popularmovies2.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Project name PopularMovies2
 * Created by kenneth on 12/08/2017.
 */

class MovieSyncTask {

    synchronized static void syncMovie(Context context) {

        try {
            URL requestUrl = NetworkUtils.buildMainUrl(true);
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            Hashtable<Integer,ContentValues> contentValuesPopular = NetworkUtils.getMovieDataFromJson(jsonResponse,true);

            requestUrl = NetworkUtils.buildMainUrl(false);
            jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            Hashtable<Integer,ContentValues> contentValuesTopRated = NetworkUtils.getMovieDataFromJson(jsonResponse,false);

            if (contentValuesTopRated!=null && contentValuesPopular!=null) {
                Iterator<Map.Entry<Integer, ContentValues>> iterator = contentValuesTopRated.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, ContentValues> entry = iterator.next();

                    if (contentValuesPopular.containsKey(entry.getKey())) {
                        ContentValues value = entry.getValue();
                        value.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR, 1);
                        contentValuesPopular.put(entry.getKey(), value);
                    } else {
                        contentValuesPopular.put(entry.getKey(), entry.getValue());
                    }
                }

                ContentValues[] contentValues = new ContentValues[contentValuesPopular.size()];
                iterator= contentValuesPopular.entrySet().iterator();
                int index=0;
                while (iterator.hasNext()) {
                    Map.Entry<Integer, ContentValues> entry = iterator.next();
                    contentValues [index]=entry.getValue();
                    index++;
                }

                if (contentValues.length != 0) {
                    ContentResolver contentResolver = context.getContentResolver();

                    contentResolver.delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null);

                    contentResolver.bulkInsert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            contentValues );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
