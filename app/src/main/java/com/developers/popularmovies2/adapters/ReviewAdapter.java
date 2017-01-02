package com.developers.popularmovies2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.developers.popularmovies2.R;

import java.util.ArrayList;

/**
 * Created by Amanjeet Singh on 26-Dec-16.
 */
public class ReviewAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> review=new ArrayList<String>();
    ArrayList<String> reviewauthor=new ArrayList<String>();

    public ReviewAdapter(Context context,ArrayList review,ArrayList reviewauthor){
        this.context=context;
        this.review=review;
        this.reviewauthor=reviewauthor;
    }

    @Override
    public int getCount() {
        return reviewauthor.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewauthor.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=layoutInflater.inflate(R.layout.review_list,null);
        String name=reviewauthor.get(position)+":";
        String reviewcontent=review.get(position);
        TextView authname=(TextView)convertView.findViewById(R.id.authorname);
        TextView authcontent=(TextView)convertView.findViewById(R.id.content);
        authname.setText(name);
        authcontent.setText(reviewcontent);
        return convertView;
    }
}
