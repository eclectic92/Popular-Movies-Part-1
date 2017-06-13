package com.natalieryanudacity.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by Natalie Ryan on 6/3/17.
 * Simple class to store single movie details from theMovieDatabase
 */

@SuppressWarnings("unused")
public class TmdbMovie implements Parcelable{

    private int mId;
    private String mMovieTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOverview;
    private String mVoteAverage;
    private String mRunningTime;
    private String mGenres;
    private String mCertification;

    //empty constructor
    public TmdbMovie() {
    }

    //constructor for parceler
    private TmdbMovie(Parcel in){
        mId = in.readInt();
        mMovieTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readString();
        mRunningTime = in.readString();
        mGenres = in.readString();
        mCertification = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TmdbMovie> CREATOR = new Parcelable.Creator<TmdbMovie>() {
        public TmdbMovie createFromParcel(Parcel pc) {
            return new TmdbMovie(pc);
        }
        public TmdbMovie[] newArray(int size) {
            return new TmdbMovie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeString(mMovieTitle);
        out.writeString(mReleaseDate);
        out.writeString(mPosterPath);
        out.writeString(mBackdropPath);
        out.writeString(mOverview);
        out.writeString(mVoteAverage);
        out.writeString(mRunningTime);
        out.writeString(mGenres);
        out.writeString(mCertification);
    }

    //Setters and Getters---------------------------
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.mMovieTitle = movieTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.mBackdropPath = backdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(@Nullable String overview) {
        this.mOverview = overview;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage){
        this.mVoteAverage = voteAverage;
    }

    public String getRunningTime() {
        return mRunningTime;
    }

    public void setRunningTime(String runningTime) {
        this.mRunningTime = runningTime;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(@Nullable String genres) {
        this.mGenres = genres;
    }

    public String getCertification() {
        return mCertification;
    }

    public void setCertification(@Nullable String certification) {
        this.mCertification = certification;
    }
}