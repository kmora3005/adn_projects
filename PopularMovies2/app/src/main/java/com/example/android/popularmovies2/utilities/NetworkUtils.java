package com.example.android.popularmovies2.utilities;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.data.ReviewData;
import com.example.android.popularmovies2.data.VideoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Scanner;

/**
 * Project name PopularMovies
 * Created by kenneth on 29/07/2017.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String MOVIEDB_POPULAR_URL = "movie/popular";
    private static final String MOVIEDB_TOP_RATED_URL = "movie/top_rated";
    private static final String MOVIEDB_MOVIE_REVIEWS_URL = "movie/{movie_id}/reviews";
    private static final String MOVIEDB_MOVIE_VIDEOS_URL = "movie/{movie_id}/videos";

    public static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi/";
    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    //Add your own api key here
    private static final String API_KEY = "";
    private static final String API_KEY_QUERY_PARAM = "api_key";

    private static final int MILISECONDS_FOR_TIMEOUT = 15000;

    private static String convertDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date convertedDate = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            return sdf2.format(convertedDate );
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static URL buildUrl(String urlString) {
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Url " + url);

        return url;
    }

    public static URL buildMainUrl(boolean isPopular) {
        String urlString = MOVIEDB_BASE_URL;
        urlString += isPopular ? MOVIEDB_POPULAR_URL : MOVIEDB_TOP_RATED_URL;

        return buildUrl(urlString);
    }

    public static URL buildVideosUrl(int movieId) {
        String urlString = MOVIEDB_BASE_URL+MOVIEDB_MOVIE_VIDEOS_URL;
        urlString = urlString.replace("{movie_id}",Integer.toString(movieId) );

        return buildUrl(urlString);
    }

    public static URL buildReviewsUrl(int movieId) {
        String urlString = MOVIEDB_BASE_URL+MOVIEDB_MOVIE_REVIEWS_URL;
        urlString = urlString.replace("{movie_id}",Integer.toString(movieId) );

        return buildUrl(urlString);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(MILISECONDS_FOR_TIMEOUT);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Hashtable<Integer,ContentValues> getMovieDataFromJson(String jsonStr, boolean isPopular)
            throws JSONException {
        final String JSON_MESSAGE_CODE = "cod";
        final String JSON_RESULTS = "results";

        final String JSON_ID = "id";
        final String JSON_VOTE_AVERAGE = "vote_average";
        final String JSON_TITLE = "title";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_OVERVIEW = "overview";
        final String JSON_RELEASE_DATE = "release_date";

        Hashtable<Integer,ContentValues> contentValues= new Hashtable<>();

        JSONObject objJson = new JSONObject(jsonStr);
        if (objJson.has(JSON_MESSAGE_CODE)) {
            int errorCode = objJson.getInt(JSON_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray moviesDataArray = objJson.getJSONArray(JSON_RESULTS);

        for (int i = 0; i < moviesDataArray.length(); i++) {

            JSONObject movieDataObject = moviesDataArray.getJSONObject(i);
            int id = movieDataObject.getInt(JSON_ID);
            double voteAverage = movieDataObject.getDouble(JSON_VOTE_AVERAGE);
            String title = movieDataObject.getString(JSON_TITLE);
            String posterPath = movieDataObject.getString(JSON_POSTER_PATH);
            String overview = movieDataObject.getString(JSON_OVERVIEW);
            String newFormattedDate = convertDate( movieDataObject.getString(JSON_RELEASE_DATE));
            String releaseDate = newFormattedDate != null ? newFormattedDate: movieDataObject.getString(JSON_RELEASE_DATE);

            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,id);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE,title);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,releaseDate);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER,posterPath);
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG,voteAverage);
            values.put(MovieContract.MovieEntry.COLUMN_SYPNOSIS,overview);
            int topRated = isPopular ? 0 : 1;
            values.put(MovieContract.MovieEntry.COLUMN_IS_TOP_RATED,topRated);
            int popular = isPopular ? 1 : 0;
            values.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR,popular);
            values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE,0);
            contentValues.put(id,values) ;

            Log.v(TAG, "Movie id: " + id);
        }

        return contentValues;
    }

    public static VideoData[] getVideosDataFromJson(String jsonStr) throws JSONException {
        final String JSON_MESSAGE_CODE = "cod";
        final String JSON_RESULTS = "results";

        final String JSON_KEY = "key";
        final String JSON_NAME = "name";

        VideoData[] videosData;

        JSONObject objJson = new JSONObject(jsonStr);
        if (objJson.has(JSON_MESSAGE_CODE)) {
            int errorCode = objJson.getInt(JSON_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonArray = objJson.getJSONArray(JSON_RESULTS);
        videosData = new VideoData[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String key = jsonObject.getString(JSON_KEY);
            String name = jsonObject.getString(JSON_NAME);

            VideoData data = new VideoData(key,name);
            videosData[i]=data ;

            Log.v(TAG, "Video key: " + key);
        }

        return videosData;
    }

    public static ReviewData[] getReviewsDataFromJson(String jsonStr) throws JSONException {
        final String JSON_MESSAGE_CODE = "cod";
        final String JSON_RESULTS = "results";

        final String JSON_AUTHOR = "author";
        final String JSON_CONTENT = "content";

        ReviewData[] reviewsData;

        JSONObject objJson = new JSONObject(jsonStr);
        if (objJson.has(JSON_MESSAGE_CODE)) {
            int errorCode = objJson.getInt(JSON_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonArray = objJson.getJSONArray(JSON_RESULTS);
        reviewsData = new ReviewData[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String author = jsonObject.getString(JSON_AUTHOR);
            String content = jsonObject.getString(JSON_CONTENT);

            ReviewData data = new ReviewData(author,content);
            reviewsData[i]=data ;

            Log.v(TAG, "Review author: " + author);
        }

        return reviewsData;
    }
}
