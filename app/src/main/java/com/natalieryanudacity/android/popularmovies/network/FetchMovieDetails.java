package com.natalieryanudacity.android.popularmovies.network;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by Natalie Ryan on 6/7/17.
 *
 * Asyn task to fetch metadata for a single movie from tmdb.org using the API endpoint
 * /movie/[movie_id]
 *
 * Note: implementation adapted from
 * https://stackoverflow.com/questions/26202568/android-pass-function-reference-to-asynctask
 */

public class FetchMovieDetails extends AsyncTask<String, Void, String> {

    public static final String RELEASE_DATES = "release_dates";
    private static final String ENDPOINT = "movie";

    private final AsyncCompleteListener mAsyncListener;

    /**
     * Listener for async task completion
     * Must be implemented by hosting class
     */
    public interface AsyncCompleteListener {
        void onDataFetched(String movieDetails);
    }

    /**
     * Class constructor
     * Must take in a listener ojbect implemented by calling class
     *
     * @param listener listener for task completion
     */
    public FetchMovieDetails(AsyncCompleteListener listener) {
        mAsyncListener = listener;
    }

    /**
     *
     * @param params param 0 is the tmdb movie id to get details for. param 1 is optional
     *               and is used for any additional append_to_response params that need
     *               to be passed in the API request
     * @return       json string of results from tmdb api
     */
    @Override
    protected String doInBackground(String... params) {

        URL movieSearchUrl;

        // if we don't have a movie id, return a null string
        if (params.length == 0) {
            return null;
        }

        String movieId = params[0];

        //if we got a second parameter asking for extended info, pass it along to the URL builder
        if (params.length == 2) {
            String extraInfo = params[1];
            movieSearchUrl = TmdbNetworkProvider.buildTmdbURL(ENDPOINT, movieId, extraInfo);
        }else{
            movieSearchUrl = TmdbNetworkProvider.buildTmdbURL(ENDPOINT, movieId);
        }

        try {
            return TmdbNetworkProvider.getResponseFromHttpUrl(movieSearchUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String movieDetails) {
        if (movieDetails != null) {
            mAsyncListener.onDataFetched(movieDetails);
        }else{
            mAsyncListener.onDataFetched(null);
        }
    }
}