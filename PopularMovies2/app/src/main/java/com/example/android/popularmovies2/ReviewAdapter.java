package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.data.ReviewData;

/**
 * Project name PopularMovies2
 * Created by kenneth on 14/08/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private ReviewData[] mReviewsData;


    public ReviewAdapter() {

    }

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.reviews_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        ReviewData reviewData = mReviewsData[position];
        holder.mAuthorTextView.setText(reviewData.Author());
        holder.mContentTextView.setText(reviewData.Content());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsData)
            return 0;
        return mReviewsData.length;
    }

    public void setReviewsData(ReviewData[] reviewsData) {
        mReviewsData = reviewsData;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthorTextView;
        public final TextView mContentTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.tv_author);
            mContentTextView = (TextView) view.findViewById(R.id.tv_content);
        }

    }

}
