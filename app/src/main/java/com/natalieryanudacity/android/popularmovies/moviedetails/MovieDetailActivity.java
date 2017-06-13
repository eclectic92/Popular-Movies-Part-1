package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.natalieryanudacity.android.popularmovies.R;

/**
 * Created by Natalie Ryan on 6/7/17
 *
 * Skeleton Android activity for displaying movie details
 * Actual rendering is done by the fragment MovieDetalFragment
 */
public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar abSupport = this.getSupportActionBar();
        if(abSupport != null){
            abSupport.hide();
        }
        setContentView(R.layout.activity_movie_detail);
    }
}