package com.developers.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MoviesContentProvider extends ContentProvider {

    public static final int FAVOUR = 1;
    public static final int FAVOUR_WITH_ID = 2;
    public static final int POPULAR = 3;
    public static final int POPULAR_WITH_ID = 4;
    public static final int RATED = 5;
    public static final int RATED_WITH_ID = 6;
    private final static String TAG = MoviesContentProvider.class.getSimpleName();
    private static final UriMatcher urimatcher = buildUriMatcher();
    private static final String PopularSelection = DataContract.Popular.TABLE_NAME + "." + DataContract.Popular.COLUMN_ID + " =? ";
    private static final String FavourSelection = DataContract.Favourite.TABLE_NAME + "." + DataContract.Favourite.COLUMN_ID + " =? ";
    private static final String sRatedSelection = DataContract.Rated.TABLE_NAME + "." + DataContract.Rated.COLUMN_ID + " = ? ";
    MoviesDB moviesDB;

    public MoviesContentProvider() {
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_FAVOUR, FAVOUR);
        matcher.addURI(authority, DataContract.PATH_FAVOUR + "/*", FAVOUR_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, DataContract.PATH_POPULAR + "/*", POPULAR_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_RATED, RATED);
        matcher.addURI(authority, DataContract.PATH_RATED + "/*", RATED_WITH_ID);
        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = moviesDB.getWritableDatabase();
        int match = urimatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case FAVOUR:
                rowsDeleted = db.delete(DataContract.Favourite.TABLE_NAME, selection, selectionArgs);
                break;
            case RATED:
                rowsDeleted = db.delete(DataContract.Rated.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR:
                rowsDeleted = db.delete(DataContract.Popular.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOUR_WITH_ID:
                rowsDeleted = db.delete(DataContract.Favourite.TABLE_NAME, FavourSelection, selectionArgs);
                break;
            case POPULAR_WITH_ID:
                rowsDeleted = db.delete(DataContract.Popular.TABLE_NAME, PopularSelection, selectionArgs);
                break;
            case RATED_WITH_ID:
                rowsDeleted = db.delete(DataContract.Rated.TABLE_NAME, sRatedSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented " + uri);

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        final int match = urimatcher.match(uri);
        switch (match) {
            case FAVOUR:
                return DataContract.Favourite.CONTENT_TYPE;

            case FAVOUR_WITH_ID:
                return DataContract.Favourite.CONTENT_ITEM_TYPE;

            case POPULAR:
                return DataContract.Popular.CONTENT_TYPE;

            case POPULAR_WITH_ID:
                return DataContract.Popular.CONTENT_ITEM_TYPE;

            case RATED:
                return DataContract.Rated.CONTENT_TYPE;

            case RATED_WITH_ID:
                return DataContract.Rated.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final SQLiteDatabase db = moviesDB.getWritableDatabase();
        final int match = urimatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVOUR: {
                long id = db.insert(DataContract.Favourite.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DataContract.Favourite.buildFavourUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case POPULAR: {
                long id = db.insert(DataContract.Popular.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DataContract.Popular.buildPopularUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case RATED: {
                long id = db.insert(DataContract.Rated.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DataContract.Rated.buildRatedUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        moviesDB = new MoviesDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        final int matcher = urimatcher.match(uri);
        Cursor returnCursor = null;
        switch (matcher) {
            case POPULAR: {
                returnCursor = moviesDB.getReadableDatabase().query(DataContract.Popular.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case POPULAR_WITH_ID: {
                returnCursor = getPopularId(uri, projection, sortOrder);
                break;
            }
            case FAVOUR: {
                returnCursor = moviesDB.getReadableDatabase().query(DataContract.Favourite.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVOUR_WITH_ID: {
                returnCursor = getFavourId(uri, projection, sortOrder);
                break;
            }
            case RATED: {
                returnCursor = moviesDB.getReadableDatabase().query(DataContract.Rated.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case RATED_WITH_ID: {
                returnCursor = getRatedID(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    private Cursor getRatedID(Uri uri, String[] projection, String sortOrder) {
        String ratedid = DataContract.Rated.getRatedIDFromUri(uri);
        Log.d(TAG, "ID-----> " + ratedid);
        String[] selectionArgs;
        String selection;
        selection = sRatedSelection;
        selectionArgs = new String[]{ratedid};
        return moviesDB.getReadableDatabase().query(DataContract.Rated.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getPopularId(Uri uri, String[] projection, String sortOrder) {
        String popularid = DataContract.Popular.getPopularIdfromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = PopularSelection;
        selectionArgs = new String[]{popularid};
        return moviesDB.getReadableDatabase().query(DataContract.Popular.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getFavourId(Uri uri, String[] projection, String sortOrder) {
        String favourid = DataContract.Favourite.getFavourIdfromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = FavourSelection;
        selectionArgs = new String[]{favourid};
        return moviesDB.getReadableDatabase().query(DataContract.Favourite.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        final SQLiteDatabase db = moviesDB.getWritableDatabase();
        final int match = urimatcher.match(uri);
        int rowupdated;
        switch (match) {
            case FAVOUR:
                rowupdated = db.update(DataContract.Favourite.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POPULAR:
                rowupdated = db.update(DataContract.Popular.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVOUR_WITH_ID:
                rowupdated = db.update(DataContract.Favourite.TABLE_NAME, values, FavourSelection, selectionArgs);
                break;
            case POPULAR_WITH_ID:
                rowupdated = db.update(DataContract.Popular.TABLE_NAME, values, PopularSelection, selectionArgs);
                break;
            case RATED:
                rowupdated = db.update(DataContract.Rated.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RATED_WITH_ID:
                rowupdated = db.update(DataContract.Rated.TABLE_NAME, values, sRatedSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented " + uri);
        }
        if (rowupdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowupdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = moviesDB.getWritableDatabase();
        final int match = urimatcher.match(uri);
        switch (match) {
            case POPULAR:
                db.beginTransaction();
                int count = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.Popular.TABLE_NAME, null, value);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case FAVOUR:
                db.beginTransaction();
                count = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.Favourite.TABLE_NAME, null, value);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case RATED:
                db.beginTransaction();
                count = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.Rated.TABLE_NAME, null, value);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
