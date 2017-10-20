package com.developers.popularmovies2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.developers.popularmovies2.DetailFragment;
import com.developers.popularmovies2.MainFragment;
import com.developers.popularmovies2.R;
import com.developers.popularmovies2.sync.MovieSyncAdapter;
import com.developers.popularmovies2.util.Constants;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    private static final String DETAIL_TAG = "TAG";
    private boolean mTwoPane;
    private SharedPreferences preferences;
    private String sort;
    private static final String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieSyncAdapter.initializeSyncAdapter(this);
        preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sort = preferences.getString(getString(R.string.preferences_key), "0");
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(Uri dataUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL, dataUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                    detailFragment, DETAIL_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(dataUri);
            startActivity(intent);
        }
    }
}
