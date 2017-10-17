package com.developers.popularmovies2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developers.popularmovies2.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 16/10/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private Context context;
    private List<String> trailerList;
    private List<String> trailerUrlList;

    public TrailersAdapter(Context context, List<String> trailerList, List<String> trailerUrlList) {
        this.context = context;
        this.trailerList = trailerList;
        this.trailerUrlList = trailerUrlList;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailerUrlList.equals("NOT AVAILABLE")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(trailerUrlList.get(position)));
                    context.startActivity(intent);
                }
            }
        });
        holder.trailerText.setText(trailerList.get(position));
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_card_view)
        CardView cardView;
        @BindView(R.id.trailer_text_view)
        TextView trailerText;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
