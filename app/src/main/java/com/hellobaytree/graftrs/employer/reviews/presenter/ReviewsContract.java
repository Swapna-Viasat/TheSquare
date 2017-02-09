package com.hellobaytree.graftrs.employer.reviews.presenter;

import com.hellobaytree.graftrs.shared.reviews.Review;

import java.util.List;

/**
 * Created by Evgheni on 11/11/2016.
 */

public interface ReviewsContract {
    interface View {
        void displayReviews(List<Review> data);
        void displayError(String message);
        void displayProgress(boolean show);
        void displayReview(Review review);
    }
    interface UserActionListener {
        void fetchReview(Review review);
        void fetchReviews();
    }
}