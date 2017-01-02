package com.developers.popularmovies2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Amanjeet Singh on 30-Oct-16.
 */
public class DataContract {
    public static final String CONTENT_AUTHORITY="com.developers.popularmovies2";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_POPULAR="popular";
    public static final String PATH_FAVOUR="favourite";
    public static final String PATH_RATED="rated";
    private final static String TAG=DataContract.class.getSimpleName();
    public static final class Popular implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_POPULAR;
        public static final String TABLE_NAME="popular";
        public static final String COLUMN_ID="movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POSTER="poster";
        public static final String COLUMN_RELEASE_DATE="date";
        public static final String COLUMN_VOTE_AVERAGE="average";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_TRAILER="trailer";
        public static final String COLUMN_REVIEWS="reviews";

        public static Uri buildPopularUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildPopularIdUri(String Popularid){
            return CONTENT_URI.buildUpon().appendPath(Popularid).build();
        }
        public static String getPopularIdfromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class Favourite implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOUR).build();
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_FAVOUR;
        public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_FAVOUR;
        public static final String TABLE_NAME="favourite";
        public static final String COLUMN_ID="movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POSTER="poster";
        public static final String COLUMN_RELEASE_DATE="date";
        public static final String COLUMN_VOTE_AVERAGE="average";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_TRAILER="trailer";
        public static final String COLUMN_REVIEWS="reviews";
        public static Uri buildFavourUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildFavourIdUri(String Favourid){
            return CONTENT_URI.buildUpon().appendPath(Favourid).build();
        }
        public static String getFavourIdfromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static final class Rated implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATED).build();
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_RATED;
        public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_RATED;
        public static final String TABLE_NAME="rated";
        public static final String COLUMN_ID="movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POSTER="poster";
        public static final String COLUMN_RELEASE_DATE="date";
        public static final String COLUMN_VOTE_AVERAGE="average";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_TRAILER="trailer";
        public static final String COLUMN_REVIEWS="reviews";

        public static Uri buildRatedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildRatedIDUri(String ratedID) {
            return CONTENT_URI.buildUpon().appendPath(ratedID).build();
        }

        public static String getRatedIDFromUri(Uri uri) {
            Log.d(TAG,"---------> "+uri.getPathSegments().get(1));
            return uri.getPathSegments().get(1);
        }
    }


}
