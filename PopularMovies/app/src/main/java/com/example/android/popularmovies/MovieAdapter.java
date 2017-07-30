package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.objects.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
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
        Picasso.with(mContext).load(urlPoster).into(holder.mPosterImageView);
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

        public MovieAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieData movieData = mMoviesData[adapterPosition];
            mClickHandler.onClick(movieData);
        }
    }
}
