package com.hellobaytree.graftrs.employer.reviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.reviews.Review;
import com.hellobaytree.graftrs.shared.reviews.ReviewUpdateResponse;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/22/2016.
 */

public class RateWorkerActivity extends AppCompatActivity {

    public static final String TAG = "RateWorkerActivity";

    @BindView(R.id.review_details_name) JosefinSansTextView header;
    @BindView(R.id.rating_view_attitude) RatingView attitude;
    @BindView(R.id.rating_view_quality) RatingView quality;
    @BindView(R.id.rating_view_reliability) RatingView reliability;
    @BindView(R.id.rating_view_safety) RatingView safety;
    @BindView(R.id.again) JosefinSansTextView again;
    private boolean hireAgain;
    private Review review;
    public static final int HIRE_AGAIN_YES = 1;
    public static final int HIRE_AGAIN_NO = 2;
    @BindView(R.id.radio_group) RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_worker);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        attitude.makeStarsRed(); quality.makeStarsRed();
        reliability.makeStarsRed(); safety.makeStarsRed();
        if (null != getIntent().getExtras().getSerializable("data")) {
            this.review = (Review) getIntent().getExtras().getSerializable("data");
            populate(review);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case HIRE_AGAIN_NO:
                        //Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
                        hireAgain = false;
                        break;
                    case HIRE_AGAIN_YES:
                        //Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
                        hireAgain = true;
                        break;
                }
            }
        });
    }

    private void populate(Review review) {
        header.setText(String.format(getString(R.string.employer_rate_worker), review.worker.firstName));
        again.setText(String.format(getString(R.string.employer_rate_again), review.worker.firstName));

    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (null != review) {
            Review patchedReview = new Review();
            try {
                patchedReview.attitude = attitude.getValue();
                patchedReview.quality = quality.getValue();
                patchedReview.reliability = reliability.getValue();
                patchedReview.safe = safety.getValue();
                patchedReview.wouldHireAgain = hireAgain;
            } catch (Exception e) {
                TextTools.log("dx", (null != e.getMessage()) ? e.getMessage() : "");
            }

            HttpRestServiceConsumer.getBaseApiClient().updateReview(review.id, patchedReview)
                    .enqueue(new Callback<ReviewUpdateResponse>() {
                        @Override
                        public void onResponse(Call<ReviewUpdateResponse> call,
                                               Response<ReviewUpdateResponse> response) {
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ReviewUpdateResponse> call, Throwable t) {
                            //
                        }
                    });
        }
    }
}
