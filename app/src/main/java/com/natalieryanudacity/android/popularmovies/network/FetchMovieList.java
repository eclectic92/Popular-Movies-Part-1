package com.natalieryanudacity.android.popularmovies.network;

import android.os.AsyncTask;

import com.natalieryanudacity.android.popularmovies.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.data.TmdbMovies;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Natalie Ryan on 6/7/17.
 *
 * Async task to fetch metadata for a movie list from tmdb.org using the API endpoints
 * /movie/popular
 * /movie/top_rated
 *
 * Note: implementation adapted from
 * https://stackoverflow.com/questions/26202568/android-pass-function-reference-to-asynctask
 */
public class FetchMovieList extends AsyncTask<String, Void, String> {
    public static final String SORT_POPULAR = "popular";
    public static final String SORT_TOP_RATED = "top_rated";
    private static final String ENDPOINT = "movie";

    private final AsyncCompleteListener mAsyncListener;

    /**
     * Listener for async task completion
     * Must be implemented by hosting class
     */
    public interface AsyncCompleteListener {
        void onDataFetched(ArrayList <TmdbMovie> movieArrayList);
    }

    /**
     * Class constructor
     * Must take in a listener ojbect implemented by calling class
     *
     * @param listener listener for task completion
     */
    public FetchMovieList(AsyncCompleteListener listener) {
        mAsyncListener = listener;
    }

    /**
     *
     * @param params param 0 is search type of either "popular" or "top_rated"
     * @return       json string of results from tmdb api
     */
    @Override
    protected String doInBackground(String... params) {

        //if no search type, return a null string
        if (params.length == 0) {
            return null;
        }

        //build the URL to call
        String searchType = params[0];
        URL movieSearchUrl = TmdbNetworkProvider.buildTmdbURL(ENDPOINT, searchType);

        //perform the network request
        try {
            return TmdbNetworkProvider.getResponseFromHttpUrl(movieSearchUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String movieListData) {
        ArrayList<TmdbMovie> movieList;
        if (movieListData != null) {
            movieList = TmdbMovies.getMoviesFromJSON(movieListData);
            mAsyncListener.onDataFetched(movieList);
        }else{
            mAsyncListener.onDataFetched(new ArrayList<TmdbMovie>());
        }
    }
}