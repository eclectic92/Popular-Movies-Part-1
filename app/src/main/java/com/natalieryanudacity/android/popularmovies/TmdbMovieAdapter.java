package com.natalieryanudacity.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Natalie Ryan on 6/3/17.
 *
 * Adapted for recycler view that holds movie information for the main activity's movie poster grid
 */
@SuppressWarnings("unused")
public class TmdbMovieAdapter extends RecyclerView.Adapter<TmdbMovieAdapter.TmdbMovieAdapterViewHolder> {

    private ArrayList<TmdbMovie> mMovieList;
    private ItemClickListener clickListener;

    /**
     * Default Constructor
     */
    public TmdbMovieAdapter() {
    }

    /**
     * Click listener for individual item in movie grid. MUST be implemented by calling class
     */
    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    /**
     * View holder for recycler view item
     */
    public class TmdbMovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mMoviePosterImageView;

        public TmdbMovieAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.movie_poster_image);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public TmdbMovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TmdbMovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TmdbMovieAdapterViewHolder tmdbMovieAdapterViewHolder,
                                 int position) {
        String moviePosterPath = mMovieList.get(position).getPosterPath();
        Picasso.with(tmdbMovieAdapterViewHolder.mMoviePosterImageView.getContext())
                .load(moviePosterPath)
                .into(tmdbMovieAdapterViewHolder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    public TmdbMovie getItem(int position) {
        if(mMovieList != null) {
            return mMovieList.get(position);
        }else{
            return new TmdbMovie();
        }
    }

    public ArrayList<TmdbMovie> getMovieList() {
        return mMovieList;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    protected void setMovieData(ArrayList<TmdbMovie> movieData) {
        mMovieList = movieData;
        notifyDataSetChanged();
    }
}