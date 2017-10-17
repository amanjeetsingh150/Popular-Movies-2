package com.developers.popularmovies2.adapters;

import android.content.Context;
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

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private Context context;
    private List<String> authorList;
    private List<String> reviewList;

    public ReviewsAdapter(Context context, List<String> authorList, List<String> reviewList) {
        this.context = context;
        this.authorList = authorList;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        holder.authorName.setText(authorList.get(position));
        holder.reviewsText.setText(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_text_view)
        TextView reviewsText;
        @BindView(R.id.author_text_view)
        TextView authorName;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
