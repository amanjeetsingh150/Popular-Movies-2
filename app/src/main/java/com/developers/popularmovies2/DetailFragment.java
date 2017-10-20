package com.developers.popularmovies2;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developers.coolprogressviews.SimpleArcProgress;
import com.developers.popularmovies2.adapters.ReviewsAdapter;
import com.developers.popularmovies2.adapters.TrailersAdapter;
import com.developers.popularmovies2.data.DataContract;
import com.developers.popularmovies2.data.MoviesDB;
import com.developers.popularmovies2.util.Constants;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String DETAIL = "URI";
    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    public static final int COL_RELEASE = 4;
    public static final int COL_RATE = 5;
    public static final int COL_OVERVIEW = 6;
    public static final int COL_TRAILER = 7;
    public static final int COL_REVIEWS = 8;
    public static final int COL_BACKDROP_IMG = 9;
    private static final int DETAIL_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            DataContract.Popular._ID,
            DataContract.Popular.COLUMN_ID,
            DataContract.Popular.COLUMN_TITLE,
            DataContract.Popular.COLUMN_POSTER,
            DataContract.Popular.COLUMN_RELEASE_DATE,
            DataContract.Popular.COLUMN_VOTE_AVERAGE,
            DataContract.Popular.COLUMN_OVERVIEW,
            DataContract.Popular.COLUMN_TRAILER,
            DataContract.Popular.COLUMN_REVIEWS,
            DataContract.Popular.COLUMN_BACKDROP_IMG
    };
    @BindView(R.id.trailer_recycler_view)
    RecyclerView trailerRecyclerView;
    @BindView(R.id.review_recycler_view)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.poster_image_view)
    ImageView poster;
    @BindView(R.id.overview_text_view)
    TextView overview;
    @BindView(R.id.toolbar_image)
    ImageView barimage;
    @BindView(R.id.title_text_view)
    TextView title;
    @BindView(R.id.release_text_view)
    TextView release;
    @BindView(R.id.rating_text_view)
    TextView rate;
    @BindView(R.id.nested_scroll)
    NestedScrollView nested;
    @BindView(R.id.image_progress_bar)
    SimpleArcProgress progressBar;
    @BindView(R.id.favorite_material_button)
    MaterialFavoriteButton favoriteButton;
    private Uri uri, trailerUri;
    private ArrayList<String> trailerList = new ArrayList<String>();
    private ArrayList<String> trailerUrlList = new ArrayList<String>();
    private ArrayList<String> reviewList = new ArrayList<String>();
    private ArrayList<String> reviewAuthorList = new ArrayList<String>();
    private String bannerImg, rating, releaseDate;
    private String trailerjson, reviewsjson;
    private JSONArray reviewJsonArray;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            uri = arguments.getParcelable(DetailFragment.DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v2 = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, v2);
        return v2;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != uri) {
            return new CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        nested.setVisibility(View.VISIBLE);
        if (data == null)
            Log.d(TAG, "SOME ERROR");
        if (data != null) {
            data.moveToFirst();
        }
        try {
            String imgPath = data.getString(COL_MOVIE_POSTER);
            String movieTitle = data.getString(COL_MOVIE_TITLE);
            releaseDate = data.getString(COL_RELEASE);
            rating = data.getString(COL_RATE);
            String overviewText = data.getString(COL_OVERVIEW);
            bannerImg = data.getString(COL_BACKDROP_IMG);
            trailerjson = data.getString(COL_TRAILER);
            reviewsjson = data.getString(COL_REVIEWS);
            boolean found = checkIfPresentInFavorites(data.getString(COL_MOVIE_ID));
            if (found) {
                favoriteButton.setFavorite(true, false);
            }
            JSONArray trailerJsonArray = new JSONArray(trailerjson);
            if(trailerJsonArray.length() == 0) {
                trailerList.add(getActivity().getString(R.string.not_available_text));
                trailerUrlList.add(getActivity().getString(R.string.not_available_text));
            }
            for (int i = 0; i < trailerJsonArray.length(); i++) {
                JSONObject obj = trailerJsonArray.getJSONObject(i);
                String key = obj.getString(getActivity().getString(R.string.key_attr));
                trailerUri = Uri.parse(Constants.TRAILER_BASE_URL).buildUpon()
                        .appendEncodedPath(getActivity().getString(R.string.watch_path_youtube_attr))
                        .appendQueryParameter(getActivity().getString(R.string.query_parameter_youtube), key)
                        .build();
                trailerUrlList.add(trailerUri.toString());
                String name = obj.getString(getActivity().getString(R.string.name_video_attr));
                trailerList.add(name);
            }
            TrailersAdapter trailersAdapter = new TrailersAdapter(getActivity(), trailerList, trailerUrlList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            trailerRecyclerView.setLayoutManager(linearLayoutManager);
            trailerRecyclerView.setAdapter(trailersAdapter);
            reviewJsonArray = new JSONArray(reviewsjson);
            if (reviewJsonArray.length() == 0) {
                reviewAuthorList.add(getActivity().getString(R.string.not_available_text));
                reviewList.add(getActivity().getString(R.string.not_available_text));
            }
            for (int j = 0; j < reviewJsonArray.length(); j++) {
                JSONObject o = reviewJsonArray.getJSONObject(j);
                String auth = o.getString(getActivity().getString(R.string.author_attr));
                String cont = o.getString(getActivity().getString(R.string.content_attr));
                reviewAuthorList.add(auth);
                reviewList.add(cont);
            }
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), reviewAuthorList, reviewList);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
            linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            reviewsRecyclerView.setLayoutManager(linearLayoutManager1);
            reviewsRecyclerView.setAdapter(reviewsAdapter);
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        ContentValues movieValues = new ContentValues();
                        movieValues.put(DataContract.Favourite.COLUMN_ID, data.getString(COL_MOVIE_ID));
                        movieValues.put(DataContract.Favourite.COLUMN_TITLE, data.getString(COL_MOVIE_TITLE));
                        movieValues.put(DataContract.Favourite.COLUMN_POSTER, data.getString(COL_MOVIE_POSTER));
                        movieValues.put(DataContract.Favourite.COLUMN_RELEASE_DATE, data.getString(COL_RELEASE));
                        movieValues.put(DataContract.Favourite.COLUMN_VOTE_AVERAGE, data.getString(COL_RATE));
                        movieValues.put(DataContract.Favourite.COLUMN_OVERVIEW, data.getString(COL_OVERVIEW));
                        movieValues.put(DataContract.Favourite.COLUMN_TRAILER, data.getString(COL_TRAILER));
                        movieValues.put(DataContract.Favourite.COLUMN_REVIEWS, data.getString(COL_REVIEWS));
                        movieValues.put(DataContract.Favourite.COLUMN_BACKDROP_IMG, data.getString(COL_BACKDROP_IMG));
                        getActivity().getContentResolver().insert(DataContract.Favourite.CONTENT_URI, movieValues);
                        Toast.makeText(getActivity(), getActivity().getString(R.string.add_favorite_message), Toast.LENGTH_SHORT).show();
                    }
                    if (!favorite) {
                        String deleteid[] = {data.getString(COL_MOVIE_ID)};
                        getActivity().getContentResolver()
                                .delete(DataContract.Favourite
                                        .buildFavourIdUri(data.getString(COL_MOVIE_ID)), null, deleteid);
                        Toast.makeText(getActivity(), getActivity().getString(R.string.remove_favorites_message), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Picasso.with(getActivity()).load(bannerImg).into(barimage, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            Picasso.with(getActivity()).load(imgPath).into(poster);
            title.setText(movieTitle);
            release.setText(releaseDate);
            rate.setText(rating);
            overview.setText(overviewText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfPresentInFavorites(String id) {
        Cursor cursor = getActivity().getContentResolver().query(DataContract.Favourite.CONTENT_URI,
                MOVIE_COLUMNS,
                DataContract.Favourite.COLUMN_ID + "='" + id + "'",
                null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
