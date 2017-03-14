package construction.thesquare.employer.reviews.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.reviews.RateWorkerActivity;
import construction.thesquare.employer.reviews.ReviewDetailsActivity;
import construction.thesquare.employer.reviews.adapter.ReviewsAdapter;
import construction.thesquare.employer.reviews.presenter.ReviewsContract;
import construction.thesquare.employer.reviews.presenter.ReviewsPresenter;
import construction.thesquare.shared.reviews.Review;

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
    @BindView(R.id.worker_reviews_rv) RecyclerView recyclerView;
    @BindView(R.id.reviews_no_data) LinearLayout noData;

    public static ReviewsListFragment newInstance(int category) {
        ReviewsListFragment reviewsListFragment = new ReviewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        reviewsListFragment.setArguments(bundle);
        return reviewsListFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PENDING) {
                    data.add(review);
                }
            }
        } else if (getArguments().getInt("category") == Review.TAB_GIVEN) {
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PUBLISHED
                        && review.type.id == Review.TYPE_GIVEN) {
                    data.add(review);
                }
            }
        } else if (getArguments().getInt("category") == Review.TAB_RECEIVED) {
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PUBLISHED
                        && review.type.id == Review.TYPE_RECEIVED) {
                    data.add(review);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayReview(Review review) {
        Intent intent = new Intent(getActivity(), ReviewDetailsActivity.class);
        Bundle data = new Bundle(); data.putSerializable("data", review);
        intent.putExtras(data); startActivity(intent);
    }

    @Override
    public void onViewDetails(Review review) {
        mUserActionListener.fetchReview(review);
    }

    @Override
    public void onCompleteReview(Review review) {
        //Toast.makeText(getContext(), "on complete review", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), RateWorkerActivity.class);
        Bundle data = new Bundle(); data.putSerializable("data", review);
        intent.putExtras(data); startActivity(intent);
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (data.isEmpty()) noData.setVisibility(View.VISIBLE);
            else noData.setVisibility(View.GONE);
        }
    };
}