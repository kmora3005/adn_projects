package com.example.android.popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Project name PopularMovies2
 * Created by kenneth on 12/08/2017.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies2";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_SYPNOSIS = "sypnosis";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";
        public static final String COLUMN_IS_POPULAR = "is_popular";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieUriWithId(int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }

        public static String getSqlSelectForTopRated() {
            return MovieContract.MovieEntry.COLUMN_IS_TOP_RATED + " == 1" ;
        }

        public static String getSqlSelectForPopular() {
            return MovieContract.MovieEntry.COLUMN_IS_POPULAR + " == 1" ;
        }

        public static String getSqlSelectForFavorite() {
            return MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " == 1" ;
        }
    }
}
