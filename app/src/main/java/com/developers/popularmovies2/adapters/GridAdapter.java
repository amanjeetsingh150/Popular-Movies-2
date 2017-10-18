package com.developers.popularmovies2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.developers.popularmovies2.MainFragment;
import com.developers.popularmovies2.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 19-Nov-16.
 */
public class GridAdapter extends CursorAdapter {

    public GridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View V = LayoutInflater.from(context).inflate(R.layout.grid_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(V);
        V.setTag(viewHolder);
        return V;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.movieTitle.setText(cursor.getString(MainFragment.COL_MOVIE_TITLE));
        Picasso.with(context).load(cursor.getString(MainFragment.COL_MOVIE_POSTER)).into(viewHolder.img);
    }

    public class ViewHolder {
        @BindView(R.id.movie_title_text)
        TextView movieTitle;
        @BindView(R.id.movie_img_view)
        ImageView img;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
