package com.hellobaytree.graftrs.worker.reviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hellobaytree.graftrs.R;
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

    public interface ReviewsListener {
        void onViewDetails(Review review);
        void onCompleteReview(Review review);
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
        if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onViewDetails(review);
                }
            });
        }

        if (null != review.job) {
            if (null != review.job.role) {
                if (null != review.job.role.name) {
                    holder.position.setText(review.job.role.name);
                }
            }
            if (null != review.job.owner) {
                if (null != review.job.owner.company) {
                    if (null != review.job.owner.company.name) {
                        holder.company.setText(review.job.owner.company.name);
                    }
                    if (null != review.job.owner.company.logo) {
                        Picasso.with(context)
                                .load(review.job.owner.company.logo)
                                .into(holder.logo);
                    }
                }
            }
        }

        holder.ratingView.setRating((int) review.rating);

        holder.ratingView.makeStarsRed();

        if (review.status.id == Review.CAT_PENDING) {
            if (review.type.id == Review.TYPE_RECEIVED) {
                holder.ratingView.setVisibility(View.GONE);
                holder.pending.setVisibility(View.VISIBLE);
                holder.due.setText("Requested 4 days ago");
                holder.action.setVisibility(View.GONE);
            } else {
                holder.ratingView.setVisibility(View.GONE);
                holder.pending.setVisibility(View.VISIBLE);
                holder.due.setText(String.format(context.getString(R.string.worker_review_due),
                        4, context.getResources().getQuantityString(R.plurals.day_plural, 4)));
                holder.action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCompleteReview(review);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.due) JosefinSansTextView due;
        @BindView(R.id.action_pending) JosefinSansTextView action;
        @BindView(R.id.pending) RelativeLayout pending;
        @BindView(R.id.item_review_date) JosefinSansTextView date;
        @BindView(R.id.item_review_logo) ImageView logo;
        @BindView(R.id.item_review_company) JosefinSansTextView company;
        @BindView(R.id.item_review_position) JosefinSansTextView position;
        @BindView(R.id.item_review_rating) RatingView ratingView;
        public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
