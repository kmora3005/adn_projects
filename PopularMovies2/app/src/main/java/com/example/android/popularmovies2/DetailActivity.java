package com.example.android.popularmovies2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.data.ReviewData;
import com.example.android.popularmovies2.data.VideoData;
import com.example.android.popularmovies2.databinding.ActivityDetailBinding;
import com.example.android.popularmovies2.sync.MovieSyncUtils;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;


public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, VideoAdapter.VideoAdapterOnClickHandler {
    private static final String[] DETAIL_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_SYPNOSIS,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE,
    };

    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_TITLE = 1;
    private static final int INDEX_MOVIE_RELEASE_DATE = 2;
    private static final int INDEX_MOVIE_POSTER = 3;
    private static final int INDEX_MOVIE_VOTE_AVG = 4;
    private static final int INDEX_MOVIE_SYPNOSIS = 5;
    private static final int INDEX_MOVIE_IS_FAVORITE = 6;

    private static final int ID_DETAIL_LOADER = 31;
    private static final String SCROLL_POSITION_VIDEOS_KEY = "ScrollPositionVideos";
    private static final String SCROLL_POSITION_REVIEWS_KEY = "ScrollPositionReviews";

    private Uri mUri;
    private boolean isFavorite;
    private int movieId;

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private ActivityDetailBinding mDetailBinding;
    private GridLayoutManager mLayoutManagerVideos;
    private GridLayoutManager mLayoutManagerReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        int numColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2:4;
        mLayoutManagerVideos = new GridLayoutManager(this,numColumns);
        mDetailBinding.rvVideos.setLayoutManager(mLayoutManagerVideos);
        mDetailBinding.rvVideos.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this,this);
        mDetailBinding.rvVideos.setAdapter(mVideoAdapter);

        mLayoutManagerReviews = new GridLayoutManager(this,1);
        mDetailBinding.rvReviews.setLayoutManager(mLayoutManagerReviews);
        mDetailBinding.rvReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter();
        mDetailBinding.rvReviews.setAdapter(mReviewAdapter);

        if(savedInstanceState != null){
            int position = savedInstanceState.getInt(SCROLL_POSITION_VIDEOS_KEY);
            mLayoutManagerVideos.scrollToPosition(position);
            position = savedInstanceState.getInt(SCROLL_POSITION_REVIEWS_KEY);
            mLayoutManagerReviews.scrollToPosition(position);
        }

        mUri = getIntent().getData();
        if (mUri == null)
            throw new NullPointerException("URI for DetailActivity cannot be null");
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        DETAIL_MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        movieId= data.getInt(INDEX_MOVIE_ID);
        String title = data.getString(INDEX_MOVIE_TITLE);
        mDetailBinding.tvTitle.setText(title);
        String poster = data.getString(INDEX_MOVIE_POSTER);
        String urlPoster = NetworkUtils.POSTER_IMAGE_BASE_URL +poster;
        Picasso.with(this).load(urlPoster).placeholder(R.mipmap.ic_launcher).into(mDetailBinding.ivPoster,  new ImageLoadedCallback(mDetailBinding.pbLoadingImage) {
            @Override
            public void onSuccess() {
                if (this.progressBar != null) {
                    this.progressBar.setVisibility(View.GONE);
                }
            }
        });

        String releaseDate =  data.getString(INDEX_MOVIE_RELEASE_DATE);
        mDetailBinding.tvReleaseDate.setText(releaseDate);
        String voteAverage = String.valueOf(data.getString(INDEX_MOVIE_VOTE_AVG));
        mDetailBinding.tvVoteAverage.setText(voteAverage);
        String sypnosis = data.getString(INDEX_MOVIE_SYPNOSIS);
        mDetailBinding.tvSynopsis.setText(sypnosis);
        int favorite= data.getInt(INDEX_MOVIE_IS_FAVORITE);
        if (favorite==1){
            isFavorite = true;
            mDetailBinding.ibFavorite.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        }
        else{
            isFavorite = false;
            mDetailBinding.ibFavorite.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.star_default_color, null));
        }

        showVideoDataView();
        showReviewDataView();
        new FetchVideoTask().execute(movieId);
        new FetchReviewTask().execute(movieId);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onToggleStar(View view) {
        ContentValues values = new ContentValues();
        if (isFavorite){
            values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            Toast.makeText(this,R.string.movie_as_no_favorite,Toast.LENGTH_LONG).show();
        }
        else {
            values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            Toast.makeText(this,R.string.movie_as_favorite,Toast.LENGTH_LONG).show();
        }

        String[] selectionArguments = new String[]{Integer.toString(movieId) } ;
        MovieSyncUtils.updateFavorite(this,mUri,values,selectionArguments);
    }

    @Override
    public void onClick(VideoData videoData) {
        Uri uri =Uri.parse("vnd.youtube:" + videoData.Key());
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            uri = Uri.parse(NetworkUtils.YOUTUBE_VIDEO_BASE_URL + videoData.Key());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browserIntent);
        }
    }

    private class ImageLoadedCallback implements Callback {
        final ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progressBar){
            this.progressBar = progressBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

    private void showVideoDataView() {
        mDetailBinding.tvErrorMessageVideos.setVisibility(View.INVISIBLE);
        mDetailBinding.rvVideos.setVisibility(View.VISIBLE);
    }

    private void showVideoErrorMessage() {
        mDetailBinding.rvVideos.setVisibility(View.INVISIBLE);
        mDetailBinding.tvErrorMessageVideos.setVisibility(View.VISIBLE);
    }

    private class FetchVideoTask extends AsyncTask<Integer, Void, VideoData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDetailBinding.pbLoadingVideos.setVisibility(View.VISIBLE);
        }

        @Override
        protected VideoData[] doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            int movieId = params[0];
            URL requestUrl = NetworkUtils.buildVideosUrl(movieId);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);

                return NetworkUtils.getVideosDataFromJson(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(VideoData[] videosData) {
            mDetailBinding.pbLoadingVideos.setVisibility(View.INVISIBLE);
            if (videosData != null) {
                showVideoDataView();
                mVideoAdapter.setVideosData(videosData);
            } else {
                showVideoErrorMessage();
            }
        }
    }

    private void showReviewDataView() {
        mDetailBinding.tvErrorMessageReviews.setVisibility(View.INVISIBLE);
        mDetailBinding.rvReviews.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage() {
        mDetailBinding.rvReviews.setVisibility(View.INVISIBLE);
        mDetailBinding.tvErrorMessageReviews.setVisibility(View.VISIBLE);
    }

    private class FetchReviewTask extends AsyncTask<Integer, Void, ReviewData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDetailBinding.pbLoadingReviews.setVisibility(View.VISIBLE);
        }

        @Override
        protected ReviewData[] doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            int movieId = params[0];
            URL requestUrl = NetworkUtils.buildReviewsUrl(movieId);
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);

                return NetworkUtils.getReviewsDataFromJson(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewData[] reviewsData) {
            mDetailBinding.pbLoadingReviews.setVisibility(View.INVISIBLE);
            if (reviewsData != null) {
                showReviewDataView();
                mReviewAdapter.setReviewsData(reviewsData);
                if (reviewsData.length==0){
                    mDetailBinding.tvErrorMessageReviews.setText(R.string.info_no_data);
                    showReviewErrorMessage();
                }
            } else {
                showReviewErrorMessage();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = mLayoutManagerVideos.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_POSITION_VIDEOS_KEY, position);
        position = mLayoutManagerReviews.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_POSITION_REVIEWS_KEY, position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null){
            int position = savedInstanceState.getInt(SCROLL_POSITION_VIDEOS_KEY);
            mLayoutManagerVideos.scrollToPosition(position);
            position = savedInstanceState.getInt(SCROLL_POSITION_REVIEWS_KEY);
            mLayoutManagerReviews.scrollToPosition(position);
        }
    }
}
