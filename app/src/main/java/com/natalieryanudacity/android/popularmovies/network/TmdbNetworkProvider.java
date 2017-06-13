package com.natalieryanudacity.android.popularmovies.network;

import android.net.Uri;
import android.util.Log;

import com.natalieryanudacity.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Natalie Ryan on 6/2/17
 *
 * NOTE: Based on the network utils class in the udacity Sunshine app
 *
 * Builds correct URLs for tmdb.org's APIS and performs network request to
 * retrieve movie metadata
 */
final class TmdbNetworkProvider {

    private static final String TAG = TmdbNetworkProvider.class.getSimpleName();
    private static final String URI_SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String API_KEY_PARAM = "api_key";
    private static final String EXTENDED_DATA_PARAM = "append_to_response";

    /**
     * Builds URLs used to request metadata from tmdb.org
     *
     * @param endpoint         tmdb endpoint (e.g. "movie"
     * @param searchType       tmds search type (movie by ID, top_rated, popula)
     * @param extraDataParams  extra url params, e.g. append_to_response=release_dates
     * @return                 tmdb search URL
     */
     static URL buildTmdbURL(String endpoint, String searchType, String extraDataParams) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION)
                .appendPath(endpoint)
                .appendPath(searchType)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY);

        if(extraDataParams != null){
            builder.appendQueryParameter(EXTENDED_DATA_PARAM, extraDataParams);
        }

        URL url = null;
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built TMDB URL " + url);

        return url;
    }

    /**
     * Builds URLs used to request metadata from tmdb.org
     * Note: This is an overloaded method - no extra info
     * param needed here
     *
     * @param endpoint         tmdb endpoint (e.g. "movie"
     * @param searchType       tmds search type (movie by ID, top_rated, popula)
     * @return                 tmdb search URL
     */
    static URL buildTmdbURL(String endpoint, String searchType) {
        return buildTmdbURL(endpoint, searchType, null);
    }

    /**
     *
     * @param url            URL to query tmdb.org
     * @return               string containing json movie metadata
     * @throws IOException   thrown when scanner problem occurs
     */
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
