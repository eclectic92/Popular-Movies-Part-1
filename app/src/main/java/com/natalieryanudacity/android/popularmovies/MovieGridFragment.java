package com.natalieryanudacity.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.natalieryanudacity.android.popularmovies.moviedetails.MovieDetailActivity;
import com.natalieryanudacity.android.popularmovies.network.ConnectionChecker;
import com.natalieryanudacity.android.popularmovies.network.FetchMovieList;
import com.natalieryanudacity.android.popularmovies.utils.ui.GridSpacingItemDecoration;

import java.util.ArrayList;

/**
 * Fragment to render movie posters in a grid based on movile list data returned from
 * searching the popular or top_rated API endpoints at tmdb.org
 *
 * Will render in a two-column grid in portait and a three-column grid in landscape
 * Will show an error text view in failed load/no movies found/no internet connnection states
 * Saves user's state on rotation or clicking the "home" button so APIs are not repeatedly called
 * and will save place in scrolled view on rotation
 */
public class MovieGridFragment extends Fragment implements TmdbMovieAdapter.ItemClickListener {

    private static final String MOVIES = "MOVIES";
    private static final String MOVIE = "MOVIE";
    private static final String SORT = "SORT";
    private static final String GRID_LAYOUT = "GRID_LAYOUT";

    private RecyclerView mPosterView;
    private ProgressBar mLoaderSpinner;
    private TextView mErrorMessageView;
    private TmdbMovieAdapter mTmdbMovieAdapter;
    private String mSortType;

    /**
     * Default constructor
     */
    public MovieGridFragment() {
    }

    /**
     * Turn on option menu on create
     *
     * @param savedInstanceState saved instance state bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Creates the main movie grid view
     * will restore user's previous state and selected menu options if available
     * otherwise will call tmdb.org to get list of most popular movies
     *
     * @param inflater              view inflater
     * @param container             view container
     * @param savedInstanceState    saved instance as bundle
     * @return                      inflated view of movie grid
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the main view
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        //get handles to the main element's we need to toggle on/off
        mPosterView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movielist);
        mLoaderSpinner = (ProgressBar) rootView.findViewById(R.id.grid_loader_pb);
        mErrorMessageView = (TextView) rootView.findViewById(R.id.no_movies_tv);

        // create a new layout manager with the right number of columns for device's orientation
        int colCount = getColumnCount();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                colCount, GridLayoutManager.VERTICAL, false);

        // set all the display properties on the grid layout
        mPosterView.addItemDecoration(new GridSpacingItemDecoration(colCount,
                getResources().getDimensionPixelSize(R.dimen.movie_grid_margin_spacing), true));
        mPosterView.setLayoutManager(layoutManager);
        mPosterView.setHasFixedSize(true);

        // create the adapter and bind it to the grid
        mTmdbMovieAdapter = new TmdbMovieAdapter();
        mTmdbMovieAdapter.setClickListener(this);
        mPosterView.setAdapter(mTmdbMovieAdapter);

        //call tmdb api to fetch the movie list if there's no saved state
        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getString(SORT);
            if(savedInstanceState.containsKey(MOVIES)){
                ArrayList<TmdbMovie> movieList = savedInstanceState.getParcelableArrayList(MOVIES);
                mTmdbMovieAdapter.setMovieData(movieList);
                Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(GRID_LAYOUT);
                mPosterView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            }
            updateUi();
        } else{
            if (ConnectionChecker.isOnline(getActivity())) {
                loadMovieSearchData(FetchMovieList.SORT_POPULAR);
            } else {
                updateUi();
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TmdbMovie> movieList = mTmdbMovieAdapter.getMovieList();

        if (movieList != null && !movieList.isEmpty()) {
            outState.putParcelable(GRID_LAYOUT, mPosterView.getLayoutManager().onSaveInstanceState());
            outState.putParcelableArrayList(MOVIES, movieList);
        }
        outState.putString(SORT, mSortType);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_grid, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if(mSortType != null) {
            switch (mSortType){
                case FetchMovieList.SORT_POPULAR:
                    menu.findItem(R.id.menu_item_popular).setChecked(true);
                    break;
                case FetchMovieList.SORT_TOP_RATED:
                    menu.findItem(R.id.menu_tem_top_rated).setChecked(true);
                default:
                    //do nothing
            }
        }else {
            menu.findItem(R.id.menu_item_popular).setChecked(true);
        }
    }

    /**
     * Allows user to switch between viewing popular or top rated movies in grid
     *
     * @param item  menu item clicked
     * @return      boolean that click handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_popular:
                mSortType = FetchMovieList.SORT_POPULAR;
                loadMovieSearchData(mSortType);
                item.setChecked(true);
                return true;
            case R.id.menu_tem_top_rated:
                mSortType = FetchMovieList.SORT_TOP_RATED;
                loadMovieSearchData(mSortType);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initiates async task to get data from tmdb.org
     * Sets adapter's list of movies and shows the movie grid
     *
     * @param searchType popular or top_rated
     */
    private void loadMovieSearchData(String searchType) {
        mLoaderSpinner.setVisibility(View.VISIBLE);
        FetchMovieList.AsyncCompleteListener asyncListener =
                new FetchMovieList.AsyncCompleteListener() {
            @Override
            public void onDataFetched (ArrayList<TmdbMovie> movieArrayList) {
                mTmdbMovieAdapter.setMovieData(movieArrayList);
                mLoaderSpinner.setVisibility(View.GONE);
                updateUi();
            }
        };
        FetchMovieList fetchMovies = new FetchMovieList(asyncListener);
        fetchMovies.execute(searchType);
    }

    /**
     * Utility function to show movie grid or error based on state
     */
    private void updateUi(){
        if(mTmdbMovieAdapter != null){
            if (mTmdbMovieAdapter.getItemCount() != 0){
                showMovieGrid();
            }else{
                showErrorMessage();
            }
        }else{
            showErrorMessage();
        }
    }

    /**
     * Utility function to hide loading spinner
     */
    private void showMovieGrid(){
        mPosterView.setVisibility(View.VISIBLE);
        mErrorMessageView.setVisibility(View.INVISIBLE);
    }

    /**
     * Utility function to show appropriate error message based on state
     */
    private void showErrorMessage(){
        String errMessage;
        if(ConnectionChecker.isOnline(getActivity())){
            errMessage = getActivity().getString(R.string.grid_no_movies_found);
        }else{
            errMessage = getActivity().getString(R.string.grid_no_connection);
        }
        mPosterView.setVisibility(View.INVISIBLE);
        mErrorMessageView.setText(errMessage);
        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    /**
     * Utility function get number of columns to display
     *
     * @return number of columns to display
     */
    private int getColumnCount() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 2;
        }else{
            return 3;
        }
    }
    /**
     * sends intent to movie detail view when user clicks on a movie poster
     * will send all basic movie details as a parcelable
     *
     * @param view      calling view
     * @param position  recycler view position of clicked poster
     */
    @Override
    public void onClick(View view, int position) {
        final TmdbMovie singleMovie = mTmdbMovieAdapter.getItem(position);
        Intent intent = new Intent(getActivity(),MovieDetailActivity.class);
        intent.putExtra(MOVIE, singleMovie);
        startActivity(intent);
    }
}