package com.developers.popularmovies2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.developers.popularmovies2.BuildConfig;
import com.developers.popularmovies2.activities.MainActivity;
import com.developers.popularmovies2.R;
import com.developers.popularmovies2.data.DataContract;
import com.developers.popularmovies2.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Amanjeet Singh on 19-Nov-16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int SYNC_INTERVAL = 600;
    private static final int FLEX_TIME = SYNC_INTERVAL / 3;
    private static final String TAG = MovieSyncAdapter.class.getSimpleName();
    private static final int MOVIES_NOTIFICATION_ID = 3000;
    private String title, overview, release, rating, id, trailers, reviews;
    private Vector<ContentValues> cVVector;
    private Uri uri, trailerUri, posterUri, bannerUri;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account("PopularMovies2", context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, FLEX_TIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Log.d(TAG, "in sync immediate");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            SharedPreferences preferences = getContext()
                    .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String sort = preferences
                    .getString(getContext().getString(R.string.preferences_key), "0");
            String choice = null;
            if (sort.equals("0")) {
                choice = getContext().getString(R.string.popular_attr);
            }
            if (sort.equals("1")) {
                choice = getContext().getString(R.string.top_rated_attr);
            }
            uri = Uri.parse(Constants.BASE_URL).buildUpon()
                    .appendPath(choice)
                    .appendQueryParameter(getContext().getString(R.string.api_key_attr),
                            BuildConfig.MOVIE_KEY).build();
            Log.d(TAG, uri.toString());
            URL url = new URL(uri.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));
            StringBuilder json = new StringBuilder();
            String result;
            while ((result = buff.readLine()) != null) {
                json.append(result);
            }
            if (json.length() != 0) {
                JSONObject res = new JSONObject(json.toString());
                JSONArray arr = res.getJSONArray(getContext().getString(R.string.attr_results));
                cVVector = new Vector<>(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject movie = arr.getJSONObject(i);
                    posterUri = Uri.parse(Constants.IMAGE_BASE_URL).buildUpon()
                            .appendEncodedPath(movie.getString(getContext()
                                    .getString(R.string.attr_poster_path)))
                            .build();
                    Log.d(TAG, posterUri.toString());
                    title = movie.getString(getContext().getString(R.string.attr_title));
                    overview = movie.getString(getContext().getString(R.string.attr_overview));
                    release = movie.getString(getContext().getString(R.string.attr_release));
                    rating = movie.getString(getContext().getString(R.string.attr_vote));
                    bannerUri = Uri.parse(Constants.IMAGE_BASE_URL).buildUpon()
                            .appendEncodedPath(movie.getString(getContext()
                                    .getString(R.string.backdrop_attr)))
                            .build();
                    Log.d(TAG, bannerUri.toString());
                    id = movie.getString(getContext().getString(R.string.id_attr));
                    fetchTrailers(id);
                    fetchReviews(id);
                    Log.d(TAG, "JSON of trailers " + trailers);
                    Log.d(TAG, "JSON of Reviews " + reviews);
                    ContentValues movievalues = new ContentValues();
                    switch (sort) {
                        case "0":
                            movievalues.put(DataContract.Popular.COLUMN_ID, id);
                            movievalues.put(DataContract.Popular.COLUMN_POSTER, posterUri.toString());
                            movievalues.put(DataContract.Popular.COLUMN_TITLE, title);
                            movievalues.put(DataContract.Popular.COLUMN_OVERVIEW, overview);
                            movievalues.put(DataContract.Popular.COLUMN_RELEASE_DATE, release);
                            movievalues.put(DataContract.Popular.COLUMN_VOTE_AVERAGE, rating);
                            movievalues.put(DataContract.Popular.COLUMN_TRAILER, trailers);
                            movievalues.put(DataContract.Popular.COLUMN_REVIEWS, reviews);
                            movievalues.put(DataContract.Popular.COLUMN_BACKDROP_IMG, bannerUri.toString());
                            break;
                        case "1":
                            movievalues.put(DataContract.Rated.COLUMN_ID, id);
                            movievalues.put(DataContract.Rated.COLUMN_POSTER, posterUri.toString());
                            movievalues.put(DataContract.Rated.COLUMN_TITLE, title);
                            movievalues.put(DataContract.Rated.COLUMN_OVERVIEW, overview);
                            movievalues.put(DataContract.Rated.COLUMN_RELEASE_DATE, release);
                            movievalues.put(DataContract.Rated.COLUMN_VOTE_AVERAGE, rating);
                            movievalues.put(DataContract.Rated.COLUMN_TRAILER, trailers);
                            movievalues.put(DataContract.Rated.COLUMN_REVIEWS, reviews);
                            movievalues.put(DataContract.Rated.COLUMN_BACKDROP_IMG, bannerUri.toString());
                            break;
                        default:
                            break;
                    }
                    cVVector.add(movievalues);
                    //conn.disconnect();
                }
            }
            int insert = 0;
            if (cVVector.size() > 0) {
                ContentValues[] carray = new ContentValues[cVVector.size()];
                cVVector.toArray(carray);
                switch (sort) {
                    case "0":
                        getContext().getContentResolver().delete(DataContract.Popular.CONTENT_URI, null, null);
                        insert = getContext().getContentResolver().bulkInsert(DataContract.Popular.CONTENT_URI, carray);
                        break;
                    case "1":
                        getContext().getContentResolver().delete(DataContract.Rated.CONTENT_URI, null, null);
                        insert = getContext().getContentResolver().bulkInsert(DataContract.Rated.CONTENT_URI, carray);
                        break;
                }
            }
            notifyMovies();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyMovies() {
        Context context = getContext();
        String title = getContext().getString(R.string.notification_title);
        int iconId = R.mipmap.ic_launcher;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(getContext().getString(R.string.notification_text));
        Intent resultIntent = new Intent(getContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MOVIES_NOTIFICATION_ID, mBuilder.build());
    }

    private void fetchReviews(String id) {
        try {
            Uri reviewsUri = Uri.parse(Constants.BASE_URL).buildUpon()
                    .appendPath(id)
                    .appendPath(getContext().getString(R.string.reviews_attr))
                    .appendQueryParameter(getContext().getString(R.string.api_key_attr), BuildConfig.MOVIE_KEY)
                    .build();
            URL url3 = new URL(reviewsUri.toString());
            Log.d(TAG, reviewsUri.toString());
            HttpURLConnection con2 = (HttpURLConnection) url3.openConnection();
            InputStream inn = con2.getInputStream();
            BufferedReader bu = new BufferedReader(new InputStreamReader(inn));
            StringBuilder sb1 = new StringBuilder();
            String res;
            while ((res = bu.readLine()) != null) {
                sb1.append(res);
            }
            JSONObject o = new JSONObject(sb1.toString());
            JSONArray a = o.getJSONArray(getContext().getString(R.string.attr_results));
            reviews = a.toString();
        } catch (Exception e) {
            Log.d(TAG, "Exception in fetching reviews");
        }

    }

    private void fetchTrailers(String id) {

        try {
            trailerUri = Uri.parse(Constants.BASE_URL).buildUpon()
                    .appendPath(id)
                    .appendPath(getContext().getString(R.string.trailer_attr))
                    .appendQueryParameter(getContext().getString(R.string.api_key_attr),
                            BuildConfig.MOVIE_KEY).build();
            Log.d(TAG, trailerUri.toString());
            URL url2 = new URL(trailerUri.toString());
            HttpURLConnection con1 = (HttpURLConnection) url2.openConnection();
            InputStream is = con1.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String rs;
            while ((rs = buff.readLine()) != null) {
                sb.append(rs);
            }
            JSONObject obj = new JSONObject(sb.toString());
            JSONArray ar = obj.getJSONArray(getContext().getString(R.string.attr_results));
            trailers = ar.toString();
        } catch (Exception e) {
            Log.d(TAG, "Exception in trailer fetching");
        }
    }
}
