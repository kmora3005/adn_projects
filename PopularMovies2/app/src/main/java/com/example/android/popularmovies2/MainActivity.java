package com.example.android.popularmovies2;


import android.content.Intent;
import android.content.res.Configuration;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.sync.MovieSyncUtils;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER = 1;

    private static final int ID_MOVIE_LOADER = 30;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private TextView mInfoMessageView;
    private ProgressBar mLoadingIndicator;
    private boolean isFavorite;
    private boolean isPopular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mInfoMessageView = (TextView) findViewById(R.id.tv_info_no_favorites);

        int numColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2:4;
        GridLayoutManager layout = new GridLayoutManager(this,numColumns);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this,this);
        mRecyclerView.setAdapter(mMovieAdapter);

        showLoadingIndicator();
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
        MovieSyncUtils.initialize(this);
    }

    private void showErrorView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mInfoMessageView.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mInfoMessageView.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mInfoMessageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(int movieId) {
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(movieId);
        intent.setData(uri);
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
        showLoadingIndicator();
        if ((id == R.id.action_most_popular)||(id == R.id.action_refresh)) {
            isPopular =true;
            isFavorite = false;
        }
        else if (id == R.id.action_highest_rated) {
            isPopular =false;
            isFavorite = false;
        }
        else if (id == R.id.action_favorites) {
            isPopular =false;
            isFavorite = true;
        }

        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_MOVIE_LOADER:
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                String selectionStatement = isFavorite ? MovieContract.MovieEntry.getSqlSelectForFavorite() :
                        isPopular ? MovieContract.MovieEntry.getSqlSelectForPopular():MovieContract.MovieEntry.getSqlSelectForTopRated();

                return new CursorLoader(this,
                        uri,
                        MAIN_MOVIE_PROJECTION,
                        selectionStatement,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;

        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0){
            showDataView();
        }
        else {
            showErrorView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mMovieAdapter.swapCursor(null);
    }


}
