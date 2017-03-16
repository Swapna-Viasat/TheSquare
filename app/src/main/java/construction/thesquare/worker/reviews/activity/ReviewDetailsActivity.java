package construction.thesquare.worker.reviews.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getIntent().getExtras()) {
            TextTools.log(TAG, "bundle isn't null");
            if (null != getIntent().getExtras().getSerializable("data")) {
                TextTools.log(TAG, "review object isn't null");
                Review review = (Review) getIntent().getExtras().getSerializable("data");
                TextTools.log(TAG, String.valueOf(review.safe));
                TextTools.log(TAG, String.valueOf(review.attitude));
                TextTools.log(TAG, String.valueOf(review.reliability));
                TextTools.log(TAG, String.valueOf(review.quality));
                populate(review);
            }
        }
    }

    private void populate(Review review) {
        quality.makeStarsRed();
        quality.setRating((int) review.quality);
        TextTools.log(TAG, String.valueOf(review.quality));
        attitude.makeStarsRed();
        attitude.setRating((int) review.attitude);
        TextTools.log(TAG, String.valueOf(review.attitude));
        reliability.makeStarsRed();
        reliability.setRating((int) review.reliability);
        TextTools.log(TAG, String.valueOf(review.reliability));
        safety.makeStarsRed();
        safety.setRating((int) review.safe);
        TextTools.log(TAG, String.valueOf(review.safe));
        again.setVisibility(review.wouldHireAgain ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }
}