package com.natalieryanudacity.android.popularmovies.data;

import com.natalieryanudacity.android.popularmovies.TmdbMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Natalie Ryan on 6/3/17.
 *
 * Helper class to parse through JSON data returned from tmdc.org's APIs
 * /movie/popular
 * /movie/top_rated
 * /movie/[movie_id]
 * /movie/[movie_id]?append_to_response=release_dates
 *
**/

public final class TmdbMovies {

    private static final String IMAGES_BASE = "http://image.tmdb.org/t/p/";
    private static final String POSTERS_PATH = IMAGES_BASE + "w185";
    private static final String BANNERS_PATH = IMAGES_BASE + "w500";

    /**
     * Returns an ArrayList of custom TmdbMovie objects created from
     * parsing the JSON return from tmdb.org's API calls
     * /movie/popular and /movie/top_rated
     * <p>
     * The resulting TmdbMovie objects will contain basic data on each movie
     * including title, overview, release date, and paths to posters and banners.
     * Return will be null ArrayList if no movie data found in the JSON
     *
     * @param  jsonData an absolute URL giving the base location of the image
     * @return          ArrayList of TmdbMove objects
     */
    public static ArrayList<TmdbMovie> getMoviesFromJSON (String jsonData) {

        ArrayList<TmdbMovie> movies = new ArrayList<TmdbMovie>();
        JSONArray resultsArray;

        //make sure we got some results. if we didn't, we'll return
        //an empty ArrayList and let the caller handle it
        try {
            JSONObject singlePage = new JSONObject(jsonData);
            resultsArray = singlePage.getJSONArray(TmdbMovieData.RESULTS);
        } catch (JSONException e){
            e.printStackTrace();
            return movies;
        }

        for (int i = 0; i < resultsArray.length(); i++) {
            try {
                JSONObject singleMovieJSON = resultsArray.getJSONObject(i);
                TmdbMovie singleMovie = parseSingleMovie(singleMovieJSON);
                movies.add(i, singleMovie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movies;
    }

    /**
     * Returns a Tmdbmovie object populated with movie metadata. Will return null object
     * if an invalide JSONObject is passed in
     *
     * @param  singleMovieJSON a JSONObject containing data about a specific movie
     * @return                 a single TmdbMovie object populated with movie metadata
     */
    private static TmdbMovie parseSingleMovie(JSONObject singleMovieJSON){
        TmdbMovie singleMovie = new TmdbMovie();

        try{
            singleMovie.setId(singleMovieJSON.getInt(TmdbMovieData.ID));
            singleMovie.setMovieTitle(singleMovieJSON.getString(TmdbMovieData.TITLE));
            singleMovie.setPosterPath(POSTERS_PATH +
                    singleMovieJSON.getString(TmdbMovieData.POSTER_PATH));
            singleMovie.setBackdropPath(BANNERS_PATH +
                    singleMovieJSON.getString(TmdbMovieData.BANNER_PATH));
            singleMovie.setOverview(singleMovieJSON.getString(TmdbMovieData.OVERVIEW));
            String releaseDateRaw = singleMovieJSON.getString(TmdbMovieData.RELEASE_DATE);
            singleMovie.setReleaseDate(formatDateString(releaseDateRaw));
            singleMovie.setVoteAverage(Double.toString(singleMovieJSON
                    .getDouble(TmdbMovieData.VOTE_AVG)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return singleMovie;
    }

    /**
     * Adds extended metadata to an existing TmdbMovie object.
     * Used when getting additional details such as running time and certificatioon
     * from the /movie/[movie_id] API of tmdb.org
     *
     * @param singleMovie a TmdbMovie object
     * @param jsonData    extended movie metadata returned from the tmdb.org
     */
    public static void addMovieDetails(TmdbMovie singleMovie, String jsonData){
        try {
            JSONObject movieDetails = new JSONObject(jsonData);

            //format runtime
            singleMovie.setRunningTime(parseRunningTime(movieDetails.
                    getInt(TmdbMovieData.RUNTIME)));

            //parse genres if they exist
            if(movieDetails.has(TmdbMovieData.GENRES)){
                singleMovie.setGenres(parseGenres(movieDetails.getJSONArray(TmdbMovieData.GENRES)));
            }

            //get any  additional release date and certification data
            if(movieDetails.has(TmdbMovieData.RELEASE_DATES)){
                JSONObject releaseDetails = getTheatricalReleaseData(
                        movieDetails.getJSONObject(TmdbMovieData.RELEASE_DATES));
                if (releaseDetails != null) {
                    String releaseDateRaw = releaseDetails.getString(TmdbMovieData.RELEASE_DATE);
                    //Date releaseDate = getDateFromString(releaseDateRaw,DATE_FORMAT);

                    singleMovie.setReleaseDate(formatDateString(releaseDateRaw));
                    singleMovie.setCertification(releaseDetails.
                            getString(TmdbMovieData.CERTIFICATION));
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Parses through a movie's release_date data returned from tmdb.org's
     * /movie/[movie_id]?append_to_response=release_dates API call and returns
     * a json object containing the release date and MPAA certification level
     * for the US theatrical release
     *
     * @param releasesJsonObject json object with release date metadata
     * @return                   new json object containing theatrical release metadata
     */
    private static JSONObject getTheatricalReleaseData(JSONObject releasesJsonObject) {
        JSONObject releaseInfo = null;
        String usCountryCode = "US";
        int theatricalReleaseCode = 3;

        try{
            JSONArray results = releasesJsonObject.getJSONArray(TmdbMovieData.RESULTS);

            //find the US releases (should probable extend this to use phone's region for v2)
            for (int i = 0; i < results.length(); i++) {
                JSONObject singleCountryRelease = results.getJSONObject(i);
                if (singleCountryRelease.getString(TmdbMovieData.ISO_REGION).equals(usCountryCode)){
                    JSONArray usReleases = singleCountryRelease
                            .getJSONArray(TmdbMovieData.RELEASE_DATES);

                    //if we located the US release dates, get the cert and date info
                    for (int j = 0; j < usReleases.length(); j++) {
                        JSONObject singleRelease = usReleases.getJSONObject(j);
                        if (singleRelease.getInt(TmdbMovieData.TYPE) == theatricalReleaseCode){
                            releaseInfo = new JSONObject();
                            releaseInfo.put(TmdbMovieData.CERTIFICATION,
                                    singleRelease.getString(TmdbMovieData.CERTIFICATION));
                            releaseInfo.put(TmdbMovieData.RELEASE_DATE,
                                    singleRelease.getString(TmdbMovieData.RELEASE_DATE));
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return releaseInfo;
    }

    /**
     * Parses through the genres returned from tmdb.org's API call
     * /movie/[movie_id] and returns a formatted comma separated string that
     * with all genres listed
     *
     * @param genresArray JSON array containing a movie's genre information
     * @return            formatting string of all genres found
     */
    private static String parseGenres(JSONArray genresArray){
        StringBuilder sb = new StringBuilder();
        int genreCount = genresArray.length();

        for (int i = 0; i < genreCount; i++) {
            try {
                JSONObject singleGenre = genresArray.getJSONObject(i);
                sb.append(singleGenre.get(TmdbMovieData.NAME));
                if(i < genreCount - 1 ){
                    sb.append(", ");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return sb.toString();
    }

    /**
     * Converts a running time in minutes to a formatted string of
     * hours and minutes
     *
     * @param rawMinutes movie's running time in minutes
     * @return           formatted string of hours and minutes
     */
    private static String parseRunningTime(int rawMinutes) {
        String minuteKey = "min";
        String minutesKey = "mins";
        String hoursKey = "h";

        String minutes;
        int hours = rawMinutes/60;
        int mins = rawMinutes%60;

        //do we need single or plural for our "minutes" text?
        if(mins < 2){
            minutes = String.valueOf(mins) + minuteKey;
        }else{
            minutes = String.valueOf(mins) + minutesKey;
        }

        //do we need to show hours?
        if(hours < 1){
            return minutes;
        }else{
            return String.valueOf(hours) + hoursKey + " " + minutes;
        }
    }

    /**
     * Normalizes date strings returned from tmdb.org API calls
     *
     * @param rawDateString date string in yyy-MM-dd format
     * @return              date string in MMMM d, yyyy format
     */
    private static String formatDateString(String rawDateString){
        SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat formatterOut = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

        String formattedDate = null;

        try{
            Date parsedDate = formatterIn.parse(rawDateString);
            formattedDate = formatterOut.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}