package com.example.android.popularmovies2.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Project name PopularMovies2
 * Created by kenneth on 12/08/2017.
 */

public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService()
    {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieSyncTask.syncMovie(this);
    }
}
