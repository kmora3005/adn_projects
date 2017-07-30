package com.example.android.popularmovies;


import android.content.Intent;
import android.content.res.Configuration;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.objects.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        int numColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2:4;
        GridLayoutManager layout = new GridLayoutManager(this,numColumns);
        mRecyclerView.setLayoutManager(layout);
        mMovieAdapter = new MovieAdapter(this,this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMoviesData(true);
    }

    private void showDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void loadMoviesData(boolean isPopular) {
        showDataView();

        new FetchMovieTask().execute(isPopular);
    }

    @Override
    public void onClick(MovieData movieData) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movieData);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mMovieAdapter.setMoviesData(null);
        if ((id == R.id.action_most_popular)||(id == R.id.action_refresh)) {
            loadMoviesData(true);
            return true;
        }
        else if (id == R.id.action_highest_rated) {
            loadMoviesData(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchMovieTask extends AsyncTask<Boolean, Void, MovieData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieData[] doInBackground(Boolean... params) {

            if (params.length == 0) {
                return null;
            }

            boolean isPopular = params[0];
            URL requestUrl = NetworkUtils.buildUrl(isPopular);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);

                return NetworkUtils.getMovieDataFromJson(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showDataView();
                mMovieAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage();
            }
        }
    }
}
