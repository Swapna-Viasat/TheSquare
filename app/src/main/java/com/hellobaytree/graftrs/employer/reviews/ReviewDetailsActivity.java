package com.hellobaytree.graftrs.employer.reviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.reviews.Review;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class ReviewDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ReviewDetailsActivity";
    @BindView(R.id.rating_view_attitude) RatingView attitude;
    @BindView(R.id.rating_view_quality) RatingView quality;
    @BindView(R.id.rating_view_reliability) RatingView reliability;
    @BindView(R.id.rating_view_safety) RatingView safety;
    @BindView(R.id.review_details_name) JosefinSansTextView name;
    @BindView(R.id.review_details_overview) JosefinSansTextView again;
  //  @BindView(R.id.review_details_logo) ImageView logo;

    @BindView(R.id.field1) JosefinSansTextView field1;
    @BindView(R.id.field2) JosefinSansTextView field2;
    @BindView(R.id.field3) JosefinSansTextView field3;
    @BindView(R.id.field4) JosefinSansTextView field4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        field1.setText(getString(R.string.worker_reviews_quality));
        field2.setText(getString(R.string.worker_reviews_attitude));
        field3.setText(getString(R.string.worker_reviews_reliability));
        field4.setText(getString(R.string.worker_reviews_safety));

        if (null != getIntent().getExtras()) {
            if (null != getIntent().getExtras().getSerializable("data")) {
                Review review = (Review) getIntent().getExtras().getSerializable("data");
                populate(review);

            }
        }
    }

    private void populate(Review review) {
        quality.makeStarsRed();
        quality.setRating(review.quality);
        attitude.makeStarsRed();
        attitude.setRating(review.attitude);
        reliability.makeStarsRed();
        reliability.setRating(review.reliability);
        safety.makeStarsRed();
        safety.setRating(review.safe);

        again.setVisibility(review.wouldHireAgain ? View.VISIBLE : View.GONE);
        again.setText(String.format(getString(R.string.employer_rate_given), review.workerSummary.name));

        if (null != review.workerSummary.name) {
                name.setText(review.workerSummary.name);
        }
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }
}