package com.example.android.popularmovies2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.data.ReviewData;
import com.example.android.popularmovies2.data.VideoData;
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

    private Uri mUri;
    private boolean isFavorite;
    private int movieId;

    private RecyclerView mVideoRecyclerView, mReviewRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    private TextView mTitleTextView,mReleaseDateTextView, mVoteAverageTextView,mPlotSypnosisTextView, mErrorMessageVideos, mErrorMessageReviews;
    private ImageView mPosterImageView;
    private ImageButton mFavorite;
    private ProgressBar mLoadingImage,mLoadingVideos,mLoadingReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mPosterImageView = (ImageView) findViewById(R.id.iv_poster);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mPlotSypnosisTextView = (TextView) findViewById(R.id.tv_synopsis);
        mFavorite = (ImageButton) findViewById(R.id.ib_favorite);

        mLoadingImage = (ProgressBar) findViewById(R.id.pb_loading_image);
        mLoadingVideos = (ProgressBar) findViewById(R.id.pb_loading_videos);
        mLoadingReviews = (ProgressBar) findViewById(R.id.pb_loading_reviews);
        mErrorMessageVideos = (TextView) findViewById(R.id.tv_error_message_videos);
        mErrorMessageReviews = (TextView) findViewById(R.id.tv_error_message_reviews);

        mVideoRecyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        int numColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2:4;
        GridLayoutManager layout = new GridLayoutManager(this,numColumns);
        mVideoRecyclerView.setLayoutManager(layout);
        mVideoRecyclerView.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this,this);
        mVideoRecyclerView.setAdapter(mVideoAdapter);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        layout = new GridLayoutManager(this,1);
        mReviewRecyclerView.setLayoutManager(layout);
        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);

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
        mTitleTextView.setText(title);
        String poster = data.getString(INDEX_MOVIE_POSTER);
        String urlPoster = NetworkUtils.POSTER_IMAGE_BASE_URL +poster;
        Picasso.with(this).load(urlPoster).placeholder(R.mipmap.ic_launcher).into(mPosterImageView,  new ImageLoadedCallback(mLoadingImage) {
            @Override
            public void onSuccess() {
                if (this.progressBar != null) {
                    this.progressBar.setVisibility(View.GONE);
                }
            }
        });

        String releaseDate =  data.getString(INDEX_MOVIE_RELEASE_DATE);
        mReleaseDateTextView.setText(releaseDate);
        String voteAverage = String.valueOf(data.getString(INDEX_MOVIE_VOTE_AVG));
        mVoteAverageTextView.setText(voteAverage);
        String sypnosis = data.getString(INDEX_MOVIE_SYPNOSIS);
        mPlotSypnosisTextView.setText(sypnosis);
        int favorite= data.getInt(INDEX_MOVIE_IS_FAVORITE);
        if (favorite==1){
            isFavorite = true;
            mFavorite.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        }
        else{
            isFavorite = false;
            mFavorite.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.star_default_color, null));
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
        }
        else {
            values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
        }

        String[] selectionArguments = new String[]{Integer.toString(movieId) } ;
        MovieSyncUtils.updateFavorite(this,mUri,values,selectionArguments);
    }

    @Override
    public void onClick(VideoData videoData) {
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoData.Key()));
        try {
            startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.YOUTUBE_VIDEO_BASE_URL + videoData.Key()));
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
        mErrorMessageVideos.setVisibility(View.INVISIBLE);
        mVideoRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showVideoErrorMessage() {
        mVideoRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageVideos.setVisibility(View.VISIBLE);
    }

    private class FetchVideoTask extends AsyncTask<Integer, Void, VideoData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingVideos.setVisibility(View.VISIBLE);
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
            mLoadingVideos.setVisibility(View.INVISIBLE);
            if (videosData != null) {
                showVideoDataView();
                mVideoAdapter.setVideosData(videosData);
            } else {
                showVideoErrorMessage();
            }
        }
    }

    private void showReviewDataView() {
        mErrorMessageReviews.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage() {
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageReviews.setVisibility(View.VISIBLE);
    }

    private class FetchReviewTask extends AsyncTask<Integer, Void, ReviewData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingReviews.setVisibility(View.VISIBLE);
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
            mLoadingReviews.setVisibility(View.INVISIBLE);
            if (reviewsData != null) {
                showReviewDataView();
                mReviewAdapter.setReviewsData(reviewsData);
                if (reviewsData.length==0){
                    mErrorMessageReviews.setText(R.string.info_no_data);
                    showReviewErrorMessage();
                }
            } else {
                showReviewErrorMessage();
            }
        }
    }
}
