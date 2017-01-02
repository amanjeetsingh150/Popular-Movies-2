package com.developers.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.developers.popularmovies2.sync.MovieSyncAdapter;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

public class MainActivity extends AppCompatActivity implements MovieGrid.Callback{
    private boolean mTwoPane;
    private SharedPreferences preferences;
    private String sort;
    private static final String DETAIL_TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieSyncAdapter.initializeSyncAdapter(this);
        preferences= this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sort=preferences.getString("order","0");
        if(findViewById(R.id.movie_detail_container)!=null){
            mTwoPane=true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane=false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String msort=preferences.getString("order","0");
        Log.d("in on resume","value of sort "+msort);
    }

    @Override
    public void onItemSelected(Uri dataUri) {
        if(mTwoPane){
            Bundle args=new Bundle();
            args.putParcelable(DetailFragment.DETAIL,dataUri);
            DetailFragment detailFragment=new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,detailFragment,DETAIL_TAG).commit();
        }
        else{
            Intent intent=new Intent(this,DetailActivity.class).setData(dataUri);
            startActivity(intent);
        }
    }
}
