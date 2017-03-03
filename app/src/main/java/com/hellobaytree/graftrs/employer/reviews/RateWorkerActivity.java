package com.hellobaytree.graftrs.employer.reviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
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
    @BindView(R.id.got_hired) JosefinSansTextView gotHired;
    @BindView(R.id.rate_your_worker)
    LinearLayout rateWorker;
    @BindView(R.id.hired_view)
    LinearLayout hiredView;
    private boolean hireAgain;
    private boolean gotHiredAgain;
    private Review review;
    public static final int HIRE_AGAIN_YES = 1;
    public static final int HIRE_AGAIN_NO = 2;
    public static final int GOT_HIRE_AGAIN_YES = 1;
    public static final int GOT_HIRE_AGAIN_NO = 2;
    @BindView(R.id.radio_group) RadioGroup radioGroup;
    @BindView(R.id.radio_group_got_hired) RadioGroup radioGroupGotHired;

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
                        rateWorker.setVisibility(View.GONE);
                        break;
                    case HIRE_AGAIN_YES:
                        //Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
                        hireAgain = true;
                        rateWorker.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        radioGroupGotHired.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case GOT_HIRE_AGAIN_NO:
                        gotHiredAgain = false;

                        break;
                    case GOT_HIRE_AGAIN_YES:
                         gotHiredAgain = true;

                        break;
                }
            }
        });
    }

    private void populate(Review review) {
        header.setText(String.format(getString(R.string.employer_rate_worker), review.workerSummary.name));
        again.setText(String.format(getString(R.string.employer_rate_again), review.workerSummary.name));
        gotHired.setText(String.format(getString(R.string.worker_turned_up), review.workerSummary.name));
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
                patchedReview.gotHired = gotHiredAgain;
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
