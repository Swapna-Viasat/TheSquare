package com.hellobaytree.graftrs.worker.reviews;

import android.app.Dialog;
import android.content.Context;

import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.reviews.Review;
import com.hellobaytree.graftrs.shared.reviews.ReviewResponse;
import com.hellobaytree.graftrs.shared.reviews.ReviewsResponse;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import java.lang.reflect.WildcardType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class ReviewsPresenter implements ReviewsContract.UserActionListener {

    public static final String TAG = "ReviewsPresenter";
    private final ReviewsContract.View mReviewsView;

    public ReviewsPresenter(ReviewsContract.View view) {
        this.mReviewsView = view;
    }

    @Override
    public void fetchReview(final Review review) {
        mReviewsView.displayProgress(true);
        Call<ReviewResponse> call = HttpRestServiceConsumer.getBaseApiClient().getReview(review.id);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                mReviewsView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mReviewsView.displayReview(response.body().response);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                mReviewsView.displayProgress(false);
                TextTools.log(TAG, (null != t.getMessage()) ? t.getMessage() : "");
            }
        });
    }

    @Override
    public void fetchReviews() {
        mReviewsView.displayProgress(true);
        Call<ReviewsResponse> call = HttpRestServiceConsumer.getBaseApiClient().getReviews();
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                mReviewsView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mReviewsView.displayReviews(response.body().response);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                mReviewsView.displayProgress(false);
                TextTools.log(TAG, (t.getMessage() != null) ? t.getMessage() : "");
            }
        });
    }



}
