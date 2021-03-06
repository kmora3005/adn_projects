package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.objects.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Project name PopularMovies
 * Created by kenneth on 28/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private MovieData[] mMoviesData;
    private final MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_grid;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        MovieData movieData = mMoviesData[position];
        String urlPoster= NetworkUtils.POSTER_IMAGE_BASE_URL + movieData.PosterPath();
        holder.mProgressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(urlPoster).placeholder(R.mipmap.ic_launcher).into(holder.mPosterImageView,  new ImageLoadedCallback(holder.mProgressBar) {
            @Override
            public void onSuccess() {
                if (this.progressBar != null) {
                    this.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData)
            return 0;
        return mMoviesData.length;
    }

    public void setMoviesData(MovieData[] movieData) {
        mMoviesData = movieData;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieData movieData);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImageView;
        public final ProgressBar mProgressBar;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.iv_poster);
            mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieData movieData = mMoviesData[adapterPosition];
            mClickHandler.onClick(movieData);
        }
    }

    private class ImageLoadedCallback implements Callback {
        final ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }
}
