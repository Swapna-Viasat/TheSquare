package construction.thesquare.employer.reviews.presenter;

import android.app.Dialog;
import android.content.Context;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.reviews.ReviewResponse;
import construction.thesquare.shared.reviews.ReviewsResponse;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
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
    private Context context;

    public ReviewsPresenter(ReviewsContract.View view, Context context) {
        this.mReviewsView = view;
        this.context = context;
    }

    @Override
    public void fetchReview(final Review review) {
        final Dialog dialog = DialogBuilder.showCustomDialog(context);
        Call<ReviewResponse> call = HttpRestServiceConsumer.getBaseApiClient().getReview(review.id);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    DialogBuilder.cancelDialog(dialog);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mReviewsView.displayReview(response.body().response);
                        }
                    }
                } }else {
                    HandleErrors.parseError(context, dialog, response);
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                HandleErrors.parseFailureError(context, dialog, t);
                TextTools.log(TAG, (null != t.getMessage()) ? t.getMessage() : "");
            }
        });
    }

    @Override
    public void fetchReviews() {
        final Dialog dialog = DialogBuilder.showCustomDialog(context);
        Call<ReviewsResponse> call = HttpRestServiceConsumer.getBaseApiClient().getReviews();
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    DialogBuilder.cancelDialog(dialog);
                    if (null != response) {
                        if (null != response.body()) {
                            if (null != response.body().response) {
                                mReviewsView.displayReviews(response.body().response);
                            }
                        }
                    }
                }else {
                    HandleErrors.parseError(context, dialog, response);
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                HandleErrors.parseFailureError(context, dialog, t);
                TextTools.log(TAG, (t.getMessage() != null) ? t.getMessage() : "");
            }
        });
    }
}
