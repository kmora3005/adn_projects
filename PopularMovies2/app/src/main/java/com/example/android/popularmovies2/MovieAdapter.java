package com.example.android.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Project name PopularMovies
 * Created by kenneth on 28/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private Cursor mCursor;

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
        mCursor.moveToPosition(position);

        String urlPoster= NetworkUtils.POSTER_IMAGE_BASE_URL + mCursor.getString(MainActivity.INDEX_MOVIE_POSTER);
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
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
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
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieId);
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
}
