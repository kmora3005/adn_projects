package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.objects.MovieData;

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
import java.util.Locale;
import java.util.Scanner;

/**
 * Project name PopularMovies
 * Created by kenneth on 29/07/2017.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIEDB_POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String MOVIEDB_TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    //Add your own api key here
    private static final String API_KEY = "";
    private static final String QUERY_PARAM = "api_key";

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

    public static URL buildUrl(boolean isPopular) {
        String baseUrl = isPopular ? MOVIEDB_POPULAR_BASE_URL : MOVIEDB_TOP_RATED_BASE_URL;
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(QUERY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Url " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

    public static MovieData[] getMovieDataFromJson(String jsonStr)
            throws JSONException {

        final String JSON_RESULTS = "results";
        final String JSON_MESSAGE_CODE = "cod";

        final String JSON_VOTE_AVERAGE = "vote_average";
        final String JSON_TITLE = "title";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_OVERVIEW = "overview";
        final String JSON_RELEASE_DATE = "release_date";

        MovieData[] parsedMovieData;

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

        parsedMovieData = new MovieData[moviesDataArray.length()];

        for (int i = 0; i < moviesDataArray.length(); i++) {

            JSONObject movieDataObject = moviesDataArray.getJSONObject(i);
            float voteAverage = movieDataObject.getInt(JSON_VOTE_AVERAGE);
            String title = movieDataObject.getString(JSON_TITLE);
            String posterPath = movieDataObject.getString(JSON_POSTER_PATH);
            String overview = movieDataObject.getString(JSON_OVERVIEW);
            String newFormattedDate = convertDate( movieDataObject.getString(JSON_RELEASE_DATE));
            String releaseDate = newFormattedDate != null ? newFormattedDate: movieDataObject.getString(JSON_RELEASE_DATE);

            parsedMovieData[i] = new MovieData(title, posterPath, releaseDate, voteAverage, overview);
            Log.v(TAG, "Movie title: " + title);
        }

        return parsedMovieData;
    }
}
