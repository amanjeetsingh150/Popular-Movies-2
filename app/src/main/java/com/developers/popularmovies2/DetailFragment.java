package com.developers.popularmovies2;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.developers.popularmovies2.adapters.ReviewAdapter;
import com.developers.popularmovies2.adapters.TrailerAdapter;
import com.developers.popularmovies2.data.DataContract;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String DETAIL="URI";
    private Uri uri;
    private static final int DETAIL_LOADER=0;
    private ArrayList<String> trailer_name=new ArrayList<String>();
    private ArrayList<String> trailerlisturls=new ArrayList<String>();
    private ArrayList<String> review=new ArrayList<String>();
    private ArrayList<String> reviewAuthor=new ArrayList<String>();
    public static final String TAG=DetailFragment.class.getSimpleName();
    private static final String[] MOVIE_COLUMNS = {
            DataContract.Popular._ID,
            DataContract.Popular.COLUMN_ID,
            DataContract.Popular.COLUMN_TITLE,
            DataContract.Popular.COLUMN_POSTER,
            DataContract.Popular.COLUMN_RELEASE_DATE,
            DataContract.Popular.COLUMN_VOTE_AVERAGE,
            DataContract.Popular.COLUMN_OVERVIEW,
            DataContract.Popular.COLUMN_TRAILER,
            DataContract.Popular.COLUMN_REVIEWS
    };
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    public static final int COL_RELEASE=4;
    public static final int COL_RATE=5;
    public static final int COL_OVERVIEW=6;
    public static final int COL_TRAILER=7;
    public static final int COL_REVIEWS=8;
    private ImageView poster,barimage;
    private TextView title,release,rate,overview;
    private NestedScrollView nested;
    private String trailerjson,reviewsjson;
    private ListView list,list1;
    private MaterialFavoriteButton favoritebutton;
    private SharedPreferences preferences;
    private Set<String> defaultids=new HashSet<String>();
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments=getArguments();
        if(arguments!=null){
            uri=arguments.getParcelable(DetailFragment.DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v2= inflater.inflate(R.layout.fragment_detail, container, false);
        poster=(ImageView)v2.findViewById(R.id.mposter);
        title=(TextView)v2.findViewById(R.id.mtitl);
        release=(TextView)v2.findViewById(R.id.release);
        rate=(TextView)v2.findViewById(R.id.rating);
        list=(ListView)v2.findViewById(R.id.listview);
        list1=(ListView)v2.findViewById(R.id.listview2);
        overview=(TextView)v2.findViewById(R.id.overview);
        nested=(NestedScrollView)v2.findViewById(R.id.scroll);
        barimage=(ImageView)v2.findViewById(R.id.toolbar_image);
        favoritebutton = (MaterialFavoriteButton) v2.findViewById(R.id.fav);
        return v2;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null!=uri){
            return new CursorLoader(getActivity(),uri,MOVIE_COLUMNS,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        nested.setVisibility(View.VISIBLE);
        if(data==null)
            Log.d(TAG,"SOME ERRORRRRRRRRRRRRRRR");
        data.moveToFirst();
        try{
            String imgpath=data.getString(COL_MOVIE_POSTER);
            Log.d(TAG,"path "+imgpath);
            String ti=data.getString(COL_MOVIE_TITLE);
            String rel=data.getString(COL_RELEASE);
            String ra=data.getString(COL_RATE);
            String ov=data.getString(COL_OVERVIEW);
            trailerjson=data.getString(COL_TRAILER);
            reviewsjson=data.getString(COL_REVIEWS);
            Log.d(TAG," "+trailerjson);
            Log.d(TAG," "+reviewsjson);
            preferences=getActivity().getSharedPreferences("favourRecord",Context.MODE_PRIVATE);
            Set<String> idvals=preferences.getStringSet("ids",defaultids);
            if(idvals.size()!=0){
                if(idvals.contains(data.getString(COL_MOVIE_ID))){
                    favoritebutton.setFavorite(true,false);
                }
            }
            JSONArray trailjs=new JSONArray(trailerjson);
            if(trailjs.length()==0){
                trailer_name.add("NOT AVAILABLE");
                trailerlisturls.add("NOT AVAILABLE");
            }
            for(int i=0;i<trailjs.length();i++){
                JSONObject obj=trailjs.getJSONObject(i);
                String key=obj.getString("key");
                String trailer_url="http://www.youtube.com/watch?v="+key;
                trailerlisturls.add(trailer_url);
                String name=obj.getString("name");
                trailer_name.add(name);
            }
            TrailerAdapter adapter=new TrailerAdapter(trailer_name,getActivity());
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     String address=trailerlisturls.get(position);
                    if(!address.equals("NOT AVAILABLE")){
                        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
                        startActivity(intent);
                    }
                }
            });
            JSONArray reviewjs=new JSONArray(reviewsjson);
            if(reviewjs.length()==0){
                Log.d(TAG,"No objects present");
                reviewAuthor.add("No Author Available");
                review.add("No Review Available");
            }
            for(int j=0;j<reviewjs.length();j++){
                JSONObject o=reviewjs.getJSONObject(j);
                String auth=o.getString("author");
                String cont=o.getString("content");
                reviewAuthor.add(auth);
                review.add(cont);
            }
            ReviewAdapter reviewadapter =new ReviewAdapter(getActivity(),review,reviewAuthor);
            list1.setAdapter(reviewadapter);
            favoritebutton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                   Log.d(TAG,"Star clicked,,............");
                    if(favorite){
                        ContentValues moviefavour=new ContentValues();
                        moviefavour.put(DataContract.Favourite.COLUMN_ID,data.getString(COL_MOVIE_ID));
                        moviefavour.put(DataContract.Favourite.COLUMN_TITLE,data.getString(COL_MOVIE_TITLE));
                        moviefavour.put(DataContract.Favourite.COLUMN_POSTER,data.getString(COL_MOVIE_POSTER));
                        moviefavour.put(DataContract.Favourite.COLUMN_RELEASE_DATE,data.getString(COL_RELEASE));
                        moviefavour.put(DataContract.Favourite.COLUMN_VOTE_AVERAGE,data.getString(COL_RATE));
                        moviefavour.put(DataContract.Favourite.COLUMN_OVERVIEW,data.getString(COL_OVERVIEW));
                        moviefavour.put(DataContract.Favourite.COLUMN_TRAILER,data.getString(COL_TRAILER));
                        moviefavour.put(DataContract.Favourite.COLUMN_REVIEWS,data.getString(COL_REVIEWS));
                        getActivity().getContentResolver().insert(DataContract.Favourite.CONTENT_URI,moviefavour);
                        Toast.makeText(getActivity(),"Added as Favourites",Toast.LENGTH_SHORT).show();
                        Set<String> idset=new HashSet<String>();
                        idset.add(data.getString(COL_MOVIE_ID));
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putStringSet("ids",idset);
                        editor.commit();
                    }
                    if(!favorite){
                        String deleteid[]={data.getString(COL_MOVIE_ID)};
                        getActivity().getContentResolver().delete(DataContract.Favourite.buildFavourIdUri(data.getString(COL_MOVIE_ID)),null,deleteid);
                        preferences=getActivity().getSharedPreferences("favourRecord",Context.MODE_PRIVATE);
                        Set<String> idvals=preferences.getStringSet("ids",defaultids);
                        if(idvals.contains(data.getString(COL_MOVIE_ID))){
                            idvals.remove(data.getString(COL_MOVIE_ID));
                        }
                        Set<String> newvals=new HashSet<String>();
                        Iterator itarator = idvals.iterator();
                        while(itarator.hasNext()) {
                            String vals = (String) itarator.next();
                            newvals.add(vals);
                        }
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putStringSet("ïds",newvals);
                        editor.commit();
                        Toast.makeText(getActivity(),"Removed from the Favourites",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Picasso.with(getActivity()).load(imgpath).into(barimage);
            Picasso.with(getActivity()).load(imgpath).into(poster);
            title.setText(ti);
            release.setText(rel);
            rate.setText(ra);
            overview.setText(ov);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
