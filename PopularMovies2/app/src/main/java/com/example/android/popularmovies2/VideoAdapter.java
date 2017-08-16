package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.data.VideoData;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Project name PopularMovies2
 * Created by kenneth on 14/08/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {
    private VideoData[] mVideosData;
    private final VideoAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public VideoAdapter(Context context, VideoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.videos_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        VideoData videoData = mVideosData[position];
        String urlImageVideo = NetworkUtils.YOUTUBE_IMAGE_BASE_URL+videoData.Key()+"/0.jpg";
        holder.mNameTextView.setText(videoData.Name());
        holder.mProgressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(urlImageVideo).placeholder(R.mipmap.ic_launcher).into(holder.mVideoImageView,  new ImageLoadedCallback(holder.mProgressBar) {
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
        if (null == mVideosData)
            return 0;
        return mVideosData.length;
    }

    public void setVideosData(VideoData[] videosData) {
        mVideosData = videosData;
        notifyDataSetChanged();
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(VideoData videoData);
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mVideoImageView;
        public final TextView mNameTextView;
        public final ProgressBar mProgressBar;

        public VideoAdapterViewHolder(View view) {
            super(view);
            mVideoImageView = (ImageView) view.findViewById(R.id.iv_video);
            mNameTextView = (TextView) view.findViewById(R.id.tv_name);
            mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_video);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            VideoData videoData = mVideosData[adapterPosition];
            mClickHandler.onClick(videoData);
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
