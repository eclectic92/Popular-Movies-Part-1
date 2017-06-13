package com.natalieryanudacity.android.popularmovies.data;

/**
 * Created by Natalie Ryan on 6/7/17.
 *
 * Stores all of the JSON keys needed to parse through
 * tmdb.org's API.
 *
 * Will be expanded during content provider implementation
 * for iteration 2 of this project.
 *
 */

class TmdbMovieData {
    //JSON Keys for basic movie data returned from movie/popular and movie/top_rated searches
    static final String RESULTS = "results";
    static final String ID = "id";
    static final String TITLE= "title";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "release_date";
    static final String VOTE_AVG = "vote_average";
    static final String POSTER_PATH = "poster_path";
    static final String BANNER_PATH = "backdrop_path";

    //JSON Keys for extended movie data returned from movie/[movie_id] searches
    static final String RELEASE_DATES = "release_dates";
    static final String GENRES = "genres";
    static final String NAME = "name";
    static final String TYPE = "type";
    static final String ISO_REGION = "iso_3166_1";
    static final String CERTIFICATION = "certification";
    static final String RUNTIME = "runtime";
}
