package com.hellobaytree.graftrs.worker.reviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.reviews.Review;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgheni on 11/11/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private List<Review> data = new ArrayList<>();
    private ReviewsListener listener;
    private Context context;
    private Worker worker;


    public interface ReviewsListener {
       // void onViewDetails(Review review);
       // void onCompleteReview(Review review);
    }

    public ReviewsAdapter(List<Review> reviews, Context context, ReviewsListener reviewsListener) {
        this.data = reviews;
        this.context = context;
        this.listener = reviewsListener;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater
                .from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        final Review review = data.get(position);
       /* if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onViewDetails(review);
                }
            });
        }*/



        if (null != review.requestCompany &&  review.automatedRequest == "false") {
            holder.company.setText(review.requestCompany);
            if (null != review.dateReviewRequested)
            holder.date.setText("Date Requested: "+review.dateReviewRequested);
            holder.requestedby.setText(R.string.worker_reviews_requested_by_worker);
        }
        if (null != review.company) {
            if(review.automatedRequest == "true" && null != review.dateReviewRequested) {
                holder.company.setText(review.company);
                holder.date.setText("Date Requested: " + review.dateReviewRequested);
                holder.requestedby.setText(R.string.worker_reviews_requested_by_square);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_review_company) JosefinSansTextView company;
        @BindView(R.id.item_requested_by) JosefinSansTextView requestedby;
        @BindView(R.id.item_review_date) JosefinSansTextView date;
       public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
