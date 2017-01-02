package com.developers.popularmovies2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.developers.popularmovies2.R;

import java.util.ArrayList;

/**
 * Created by Amanjeet Singh on 25-Dec-16.
 */
public class TrailerAdapter extends BaseAdapter {
    ArrayList<String> trailername=new ArrayList<String>();
    Context context;
    public TrailerAdapter(ArrayList trailername,Context context){
        this.trailername=trailername;
        this.context=context;
    }

    @Override
    public int getCount() {
        return trailername.size();
    }

    @Override
    public Object getItem(int position) {
        return trailername.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.simple_trailer_list,null);
        String name=trailername.get(position);
        TextView tt=(TextView)convertView.findViewById(R.id.trailername);
        tt.setText(name);
        return convertView;
    }
}
