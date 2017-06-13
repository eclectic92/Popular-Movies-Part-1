package com.natalieryanudacity.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by Natalie Ryan on 6/3/17
 *
 * Skeleton Android activity for displaying grid of movie posters
 * Actual rendering is done by the fragment MovieGridFragment
 */
public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }
}


