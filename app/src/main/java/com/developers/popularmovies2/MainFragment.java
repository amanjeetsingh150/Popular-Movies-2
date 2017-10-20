package com.developers.popularmovies2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.developers.popularmovies2.activities.SettingsActivity;
import com.developers.popularmovies2.data.DataContract;
import com.developers.popularmovies2.adapters.GridAdapter;
import com.developers.popularmovies2.sync.MovieSyncAdapter;
import com.developers.popularmovies2.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    private static final String SELECTED_KEY = "selected_position";
    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String[] MOVIE_COLUMNS = {
            DataContract.Popular._ID,
            DataContract.Popular.COLUMN_ID,
            DataContract.Popular.COLUMN_TITLE,
            DataContract.Popular.COLUMN_POSTER
    };
    private static final String[] MOVIE_COLUMN_TWO = {
            DataContract.Rated._ID,
            DataContract.Rated.COLUMN_ID,
            DataContract.Rated.COLUMN_TITLE,
            DataContract.Rated.COLUMN_POSTER
    };
    private static final int MOVIES_LOADER = 0;
    public static boolean changed = false;
    @BindView(R.id.movie_grid)
    GridView movieGrid;
    private String sort;
    private Cursor cur;
    private GridAdapter gridAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    private SharedPreferences preferences;
    private CursorLoader curr;

    public MainFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.bind(this, v);
        preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sort = preferences.getString(getActivity().getString(R.string.preferences_key), "0");
        Uri movieUri = null;
        switch (sort) {
            case "0":
                movieUri = DataContract.Popular.CONTENT_URI;
                break;
            case "1":
                movieUri = DataContract.Rated.CONTENT_URI;
                break;
            case "2":
                movieUri = DataContract.Favourite.CONTENT_URI;
                break;
        }
        if (!sort.equals("2")) {
            MovieSyncAdapter.syncImmediately(getActivity());
        }
        switch (sort) {
            case "0":
                cur = getActivity().getContentResolver().query(movieUri, MOVIE_COLUMNS, null, null, null);
                break;
            case "1":
                cur = getActivity().getContentResolver().query(movieUri, MOVIE_COLUMN_TWO, null, null, null);
                break;
            case "2":
                cur = getActivity().getContentResolver().query(movieUri, MOVIE_COLUMNS, null, null, null);
                break;
        }
        gridAdapter = new GridAdapter(getActivity(), cur, 0);
        movieGrid.setAdapter(gridAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                String movieid = c.getString(COL_MOVIE_ID);
                String sortKey = preferences
                        .getString(getActivity().getString(R.string.preferences_key), "0");
                Log.d(TAG, "val of sort " + sortKey);
                Uri movieUri = null;
                switch (sortKey) {
                    case "0":
                        movieUri = DataContract.Popular.buildPopularIdUri(movieid);
                        Log.d(TAG, "URI is " + movieUri);
                        break;
                    case "1":
                        movieUri = DataContract.Rated.buildRatedIDUri(movieid);
                        Log.d(TAG, "URI is " + movieUri);
                        break;
                    case "2":
                        movieUri = DataContract.Favourite.buildFavourIdUri(movieid);
                        Log.d(TAG, "URI is " + movieUri);
                        break;
                }
                ((Callback) getActivity()).onItemSelected(movieUri);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkConnected()) {
            if (changed) {
                updateMovie();
                getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
            }
            changed = false;
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences preferences = getActivity()
                .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sort = preferences.getString("order", "0");
        Uri movieUri = null;
        switch (sort) {
            case "0":
                movieUri = DataContract.Popular.CONTENT_URI;
                curr = new CursorLoader(getActivity(), movieUri, MOVIE_COLUMNS, null, null, null);
                Log.d(TAG, "URI is " + movieUri);
                break;
            case "1":
                movieUri = DataContract.Rated.CONTENT_URI;
                curr = new CursorLoader(getActivity(), movieUri, MOVIE_COLUMN_TWO, null, null, null);
                if (curr == null) {
                    Log.d(TAG, "RATED NULL");
                }
                Log.d(TAG, "URI is " + movieUri);
                break;
            case "2":
                movieUri = DataContract.Favourite.CONTENT_URI;
                curr = new CursorLoader(getActivity(), movieUri, MOVIE_COLUMNS, null, null, null);
                Log.d(TAG, "URI is " + movieUri);
                break;
        }
        return curr;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        gridAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            movieGrid.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridAdapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        if (cur != null) {
            cur.close();
        }
        super.onDestroy();
    }


    public interface Callback {
        void onItemSelected(Uri dataUri);
    }

}
