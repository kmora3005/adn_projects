package com.example.android.popularmovies;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.objects.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;



public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView mTitleTextView = (TextView) findViewById(R.id.tv_title);
        ImageView mPosterImageView = (ImageView) findViewById(R.id.iv_poster);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        TextView mPlotSypnosisTextView = (TextView) findViewById(R.id.tv_plot_synopsis);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("movie")) {
                Bundle bundle = intent.getExtras();
                MovieData mMovieData = bundle.getParcelable("movie");

                if  (mMovieData != null){
                    mTitleTextView.setText(mMovieData.Title());
                    String urlPoster= NetworkUtils.POSTER_IMAGE_BASE_URL + mMovieData.PosterPath();
                    Picasso.with(this).load(urlPoster).into(mPosterImageView);

                    String newText= mReleaseDateTextView.getText()+" "+ mMovieData.ReleaseDate();
                    mReleaseDateTextView.setText(newText);
                    newText= mVoteAverageTextView.getText()+" "+String.valueOf(mMovieData.VoteAverage());
                    mVoteAverageTextView.setText(newText);
                    newText= mPlotSypnosisTextView.getText()+" "+ mMovieData.PlotSynopsis();
                    mPlotSypnosisTextView.setText(newText);
                }

            }
        }
    }
}
