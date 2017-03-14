package com.hellobaytree.graftrs.worker.reviews.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.reviews.Review;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.hellobaytree.graftrs.worker.reviews.ReviewsContract;
import com.hellobaytree.graftrs.worker.reviews.ReviewsPresenter;
import com.hellobaytree.graftrs.worker.reviews.activity.ReviewDetailsActivity;
import com.hellobaytree.graftrs.worker.reviews.activity.ReviewRequestActivity;
import com.hellobaytree.graftrs.worker.reviews.adapter.ReviewsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class ReviewsListFragment extends Fragment
        implements ReviewsAdapter.ReviewsListener,
        ReviewsContract.View {

    public static final String TAG = "ReviewsListFragment";
    private ReviewsContract.UserActionListener mUserActionListener;
    private List<Review> data = new ArrayList<>();
    private ReviewsAdapter adapter;
    @BindView(R.id.worker_reviews_rv)
    RecyclerView recyclerView;
    @BindView(R.id.reviews_no_data) LinearLayout noData;
    private int workerId;
    private Worker worker;
    @BindView(R.id.rating_view_attitude)
    RatingView attitude;
    @BindView(R.id.rating_view_quality) RatingView quality;
    @BindView(R.id.rating_view_reliability) RatingView reliability;
    @BindView(R.id.rating_view_safety) RatingView safety;
    @BindView(R.id.aggregate_worker_review) LinearLayout aggregate;
    @BindView(R.id.review_details_turned_up_value) JosefinSansTextView turnedUpReviews;
    @BindView(R.id.review_details_companies_turned_up_value) JosefinSansTextView companiesTurnedUpReviews;
    @BindView(R.id.review_details_companies_reviews_value) JosefinSansTextView companyReviews;
    @BindView(R.id.global) RatingView global;


    public static ReviewsListFragment newInstance(int category) {
        ReviewsListFragment reviewsListFragment = new ReviewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        reviewsListFragment.setArguments(bundle);
        return reviewsListFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();
        mUserActionListener = new ReviewsPresenter(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewsAdapter(data, getContext(), this);
        adapter.registerAdapterDataObserver(observer);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserActionListener.fetchReviews();
    }

    @Override
    public void displayProgress(boolean show) {
       }

    @Override
    public void displayError(String message) {
        new AlertDialog.Builder(getContext()).setMessage(message).show();
    }

    @Override
    public void displayReviews(List<Review> reviews) {
        if (!data.isEmpty()) data.clear();
        if (getArguments().getInt("category") == Review.CAT_PENDING) {
            aggregate.setVisibility(View.GONE);
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PENDING) {
                    data.add(review);
                }
            }
        } else if (getArguments().getInt("category") == Review.TAB_GIVEN) {
            aggregate.setVisibility(View.GONE);
           /* for (Review review : reviews) {
                if (review.status.id == Review.CAT_PUBLISHED
                        && review.type.id == Review.TYPE_GIVEN) {
                    data.add(review);
                }
            }*/
        } else if (getArguments().getInt("category") == Review.TAB_RECEIVED) {
           /* for (Review review : reviews) {
                if (review.status.id == Review.CAT_PUBLISHED
                        && review.type.id == Review.TYPE_RECEIVED) {
                    data.add(review);
                }
            }*/
           fetchAggregateReviews();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayReview(Review review) {
        TextTools.log(TAG, String.valueOf(review.safe));
        TextTools.log(TAG, String.valueOf(review.attitude));
        TextTools.log(TAG, String.valueOf(review.reliability));
        TextTools.log(TAG, String.valueOf(review.quality));
        Intent intent = new Intent(getActivity(), ReviewDetailsActivity.class);
        Bundle data = new Bundle(); data.putSerializable("data", review);
        intent.putExtras(data); startActivity(intent);
    }


    /*@Override
    public void onViewDetails(Review review) {
        mUserActionListener.fetchReview(review);
    }
*/
   /* @Override
    public void onCompleteReview(Review review) {
        Toast.makeText(getContext(), "on complete review", Toast.LENGTH_LONG).show();
    }*/

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (data.isEmpty()) noData.setVisibility(View.VISIBLE);
            else noData.setVisibility(View.GONE);

            if((getArguments().getInt("category") == Review.TAB_GIVEN) || (getArguments().getInt("category") == Review.CAT_PENDING)) {
                aggregate.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            }
        }
    };


    @OnClick(R.id.no_matches)
    void onRequestReferenceClicked() {
        openCreateRequest();
    }

    private void openCreateRequest() {
        Intent intent = new Intent(getActivity(), ReviewRequestActivity.class);
        startActivity(intent);
    }


    public Worker fetchAggregateReviews() {
      displayProgress(true);
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerAggregateReview(workerId)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            noData.setVisibility(View.GONE);
                            displayProgress(false);
                            aggregate.setVisibility(View.VISIBLE);
                            worker = response.body().getResponse();
                            companyReviews.setText(String.valueOf(worker.reviewData.reviewsCount));
                            turnedUpReviews.setText(String.valueOf(worker.reviewData.showedToWorkTotal)+"%");
                            companiesTurnedUpReviews.setText(String.valueOf(worker.reviewData.wouldWorkTotal)+"%");
                            quality.makeStarsRed();
                            quality.setRating(worker.reviewData.quality);
                            attitude.makeStarsRed();
                            attitude.setRating(worker.reviewData.attitude);
                            reliability.makeStarsRed();
                            reliability.setRating(worker.reviewData.reliability);
                            safety.makeStarsRed();
                            safety.setRating(worker.reviewData.safe);
                            global.makeStarsRed();
                            global.setRating(worker.reviewData.globalRating);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {

                    }
                });
        return worker;
    }
}