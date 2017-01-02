package com.developers.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amanjeet Singh on 30-Oct-16.
 */
public class MoviesDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="movies.db";
    public MoviesDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_POPULAR_TABLE="CREATE TABLE "+DataContract.Popular.TABLE_NAME+" ("+
                DataContract.Popular._ID+" INTEGER PRIMARY KEY,"+
                DataContract.Popular.COLUMN_ID+" INTEGER NOT NULL, "+
                DataContract.Popular.COLUMN_TITLE+" TEXT NOT NULL, "+
                DataContract.Popular.COLUMN_POSTER+" TEXT NOT NULL, "+
                DataContract.Popular.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                DataContract.Popular.COLUMN_VOTE_AVERAGE+" REAL NOT NULL, "+
                DataContract.Popular.COLUMN_OVERVIEW+" TEXT NOT NULL, "+
                DataContract.Popular.COLUMN_TRAILER+" TEXT NOT NULL,"+
                DataContract.Popular.COLUMN_REVIEWS+" TEXT NOT NULL "+
                " );";
        final String SQL_CREATE_FAVOURITE_TABLE="CREATE TABLE "+DataContract.Favourite.TABLE_NAME+" ("+
                DataContract.Favourite._ID+" INTEGER PRIMARY KEY,"+
                DataContract.Favourite.COLUMN_ID+" INTEGER NOT NULL, "+
                DataContract.Favourite.COLUMN_TITLE+" TEXT NOT NULL, "+
                DataContract.Favourite.COLUMN_POSTER+" TEXT NOT NULL, "+
                DataContract.Favourite.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                DataContract.Favourite.COLUMN_VOTE_AVERAGE+" REAL NOT NULL, "+
                DataContract.Favourite.COLUMN_OVERVIEW+" TEXT NOT NULL, "+
                DataContract.Favourite.COLUMN_TRAILER+" TEXT NOT NULL, "+
                DataContract.Favourite.COLUMN_REVIEWS+" TEXT NOT NULL "+
                " );";
        final String SQL_CREATE_RATED_TABLE="CREATE TABLE "+DataContract.Rated.TABLE_NAME+" ("+
                DataContract.Rated._ID+" INTEGER PRIMARY KEY,"+
                DataContract.Rated.COLUMN_ID+" INTEGER NOT NULL, "+
                DataContract.Rated.COLUMN_TITLE+" TEXT NOT NULL, "+
                DataContract.Rated.COLUMN_POSTER+" TEXT NOT NULL, "+
                DataContract.Rated.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                DataContract.Rated.COLUMN_VOTE_AVERAGE+" REAL NOT NULL, "+
                DataContract.Rated.COLUMN_OVERVIEW+" TEXT NOT NULL, "+
                DataContract.Rated.COLUMN_TRAILER+" TEXT NOT NULL,"+
                DataContract.Rated.COLUMN_REVIEWS+" TEXT NOT NULL"+
                " );";
        db.execSQL(SQL_CREATE_POPULAR_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
        db.execSQL(SQL_CREATE_RATED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+DataContract.Popular.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS "+DataContract.Favourite.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS "+DataContract.Rated.TABLE_NAME);
        onCreate(db);
    }
}
