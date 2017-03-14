package construction.thesquare.worker.reviews;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.reviews.ReviewResponse;
import construction.thesquare.shared.reviews.ReviewsResponse;
import construction.thesquare.shared.utils.TextTools;
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
