package com.natalieryanudacity.android.popularmovies.moviedetails;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.natalieryanudacity.android.popularmovies.R;
import com.natalieryanudacity.android.popularmovies.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.data.TmdbMovies;
import com.natalieryanudacity.android.popularmovies.network.ConnectionChecker;
import com.natalieryanudacity.android.popularmovies.network.FetchMovieDetails;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Created by Natalie Ryan on 6/7/17
 *
 * Fragment to display all of the details for a particular movie selected on the
 * main activity. All of the basic movie info such as title, overview, image paths, and vote
 * average should be passed to this fragment in the calling intent, and only "extended details"
 * such as the precise theatrical release date, MPAA rating, and running time will need to be
 * fetched from a new call to the /movie/[movie_id] endpoint at tmdb.org
 * <p>
 * Since basic movie info is passed in, the "bare bones" info will still render even if
 * internet connectivity is lost between main activity loading and the details
 * display being created.
 */
public class MovieDetailFragment extends Fragment {

    private static final String MOVIE = "MOVIE";

    private LinearLayout mMovieDetailsLayout;
    private LinearLayout mLoaderSpinnerLayout;
    private LinearLayout mErrorMessageLayout;
    private TmdbMovie mSingleMovie;
    private TextView mMovieTitleTv;
    private ImageView mMoviePosterImageView;
    private TextView mMovieSummaryTitleTv;
    private TextView mMovieSummaryTv;
    private TextView mMovieReleaseDateTv;
    private TextView mAverageVoteTv;
    private TextView mGenresTv;
    private TextView mRunningTimeTv;
    private ImageView mMPAARatingImage;

    /**
     * Default empty constructor
     */
    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the movie detail view
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Get handles to our layouts
        mLoaderSpinnerLayout = (LinearLayout) rootView.findViewById(R.id.detail_loader_layout);
        mMovieDetailsLayout = (LinearLayout) rootView.findViewById(R.id.detail_movie_layout);
        mErrorMessageLayout = (LinearLayout) rootView.findViewById(R.id.detail_error_layout);

        // Get the incoming intent and its data
        Intent intent = getActivity().getIntent();
        mSingleMovie = intent.getParcelableExtra(MOVIE);

        // Make sure we have a movie object before we try to do anything with it
        if (null == mSingleMovie){
            showErrorMessage();
        }else {
            // Get handles to our individual data display views
            mMovieTitleTv = (TextView) rootView.findViewById(R.id.detail_movie_title_tv);
            mMoviePosterImageView = (ImageView) rootView.findViewById(R.id.detail_poster_image);
            mMovieSummaryTitleTv = (TextView) rootView.findViewById(R.id.detail_movie_summary_title_tv);
            mMovieSummaryTv = (TextView) rootView.findViewById(R.id.detail_movie_summary_tv);
            mMovieReleaseDateTv = (TextView) rootView.findViewById(R.id.detail_release_date_tv);
            mAverageVoteTv = (TextView) rootView.findViewById(R.id.detail_average_vote_tv);
            mGenresTv = (TextView) rootView.findViewById(R.id.detail_genres_tv);
            mRunningTimeTv = (TextView) rootView.findViewById(R.id.detail_running_time_tv);
            mMPAARatingImage = (ImageView) rootView.findViewById(R.id.detail_mpaa_rating_image);

            // Load the basic data that came in with the intent into display views
            renderBasicMovieData();

            if(savedInstanceState != null){
                mSingleMovie = savedInstanceState.getParcelable(MOVIE);
                showMovieDetails();
                renderExtendedMovieData();
            }else{
                // Make the call to tmdb.org to get addition movie details we need to display
                if(ConnectionChecker.isOnline(getActivity())) {
                    mLoaderSpinnerLayout.setVisibility(View.VISIBLE);
                    loadMovieDetailData();
                }else{
                    //show the basic details that were passed from the grid even if connection down
                    showMovieDetails();
                }
            }
        }
        // Return the view we just built to the fragment host
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mSingleMovie != null){
            outState.putParcelable(MOVIE, mSingleMovie);
        }
    }

    /**
     * Render the basic movie data (title, overview, poster, release date, vote avg)
     * into the details layout
     */
    private void renderBasicMovieData() {
        mMovieTitleTv.setText(mSingleMovie.getMovieTitle());
        mMovieSummaryTitleTv.setText(getActivity().getString(R.string.details_summary_title));
        mMovieSummaryTv.setText(mSingleMovie.getOverview());
        mMovieReleaseDateTv.setText(mSingleMovie.getReleaseDate());
        String avgVote = mSingleMovie.getVoteAverage() +
                getActivity().getString(R.string.details_star_rating);
        mAverageVoteTv.setText(avgVote);

        // load the poster image
        Picasso.with(mMoviePosterImageView.getContext())
                .load(mSingleMovie.getPosterPath())
                .into(mMoviePosterImageView);
    }

    /**
     * Render the basic movie data (precise release date, mpaa rating, runtime)
     * into the details layout
     */
    private void renderExtendedMovieData() {
        // render running time
        if(mSingleMovie.getRunningTime() !=null){
            mRunningTimeTv.setVisibility(View.VISIBLE);
            mRunningTimeTv.setText(mSingleMovie.getRunningTime());
        }

        // render genre list if there are any
        if(mSingleMovie.getGenres() != null){
            mGenresTv.setVisibility(View.VISIBLE);
            mGenresTv.setText(mSingleMovie.getGenres());
        }

        //re-render release date, as a more accurate one should have been fetched in movie details
        mMovieReleaseDateTv.setText(mSingleMovie.getReleaseDate());

        //if we know the MPAA rating, load the correct image
        String mpaaRating = mSingleMovie.getCertification();
        if(mpaaRating != null && !mpaaRating.isEmpty()){
            mpaaRating = mpaaRating.toUpperCase(Locale.ROOT);
            switch (mpaaRating){
                case "G":
                    mMPAARatingImage .setImageDrawable(
                            getActivity().getDrawable(R.drawable.rated_g));
                    break;
                case "PG":
                    mMPAARatingImage .setImageDrawable(
                            getActivity().getDrawable(R.drawable.rated_pg));
                    break;
                case "PG-13":
                    mMPAARatingImage .setImageDrawable(
                            getActivity().getDrawable(R.drawable.rated_pg_13));
                    break;
                case "R":
                    mMPAARatingImage .setImageDrawable(
                            getActivity().getDrawable(R.drawable.rated_r));
                    break;
                case "NC-17":
                    mMPAARatingImage .setImageDrawable(
                            getActivity().getDrawable(R.drawable.rated_nc_17));
                    break;
                default:
                    //do nothing
            }
            mMPAARatingImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initiates call to tmdb.org's /movie/[movie_id] API to fetch extended
     * movie metadata. Puts the new metadata into corresponding views in
     * the details layout
     */
    private void loadMovieDetailData() {
        FetchMovieDetails.AsyncCompleteListener asyncListener =
                new FetchMovieDetails.AsyncCompleteListener() {
            @Override
            public void onDataFetched(String movieDetails) {
                if (movieDetails != null) {
                    TmdbMovies.addMovieDetails(mSingleMovie, movieDetails);
                    renderExtendedMovieData();
                }
                showMovieDetails();
            }
        };
        String movieId = Integer.toString(mSingleMovie.getId());
        String extraInfoToFetch = FetchMovieDetails.RELEASE_DATES;
        FetchMovieDetails fetchDetails = new FetchMovieDetails(asyncListener);
        fetchDetails.execute(movieId, extraInfoToFetch);
    }

    /**
     * Utility function to hide loading spinner
     */
    private void showMovieDetails(){
        mMovieDetailsLayout.setVisibility(View.VISIBLE);
        mLoaderSpinnerLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Utility function to show error message
     */
    private void showErrorMessage() {
        mMovieDetailsLayout.setVisibility(View.INVISIBLE);
        mErrorMessageLayout.setVisibility(View.VISIBLE);
    }
}