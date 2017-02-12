package com.hellobaytree.graftrs.employer.account;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.payments.PaymentsActivity;
import com.hellobaytree.graftrs.employer.reviews.ReviewsActivity;
import com.hellobaytree.graftrs.employer.settings.EmployerSettingsActivity;
import com.hellobaytree.graftrs.employer.subscription.SubscriptionActivity;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Employer;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maizaga on 27/12/16.
 * 
 */
public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";

    @BindView(R.id.employer_account_logo) CircleImageView logo;
    @BindView(R.id.employer_account_name) TextView name;
    @BindView(R.id.employer_account_owner) TextView owner;
    @BindView(R.id.employer_account_rating) RatingView rating;
    @BindView(R.id.employer_company_description) TextView description;
    @BindView(R.id.employer_account_reviews_counter) TextView reviewsCounter;
    @BindView(R.id.employer_account_task_counter) TextView myTasksCounter;

    @BindView(R.id.employer_account_my_tasks_layout) RelativeLayout myTasksLayout;
    @BindView(R.id.employer_account_invoices_layout) RelativeLayout invoicesLayout;
    @BindView(R.id.employer_account_leaderboards_layout) RelativeLayout leaderBoardsLayout;
    @BindView(R.id.employer_account_user_management) RelativeLayout accountUserManagementLayout;

    private Employer meEmployer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_employer_account, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchEmployer();
        configureView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("My Account");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_employer_account, menu);
        menu.getItem(0).getIcon().mutate().setColorFilter(ContextCompat
                .getColor(getContext(), R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.employer_settings:
                startActivity(new Intent(getActivity(), EmployerSettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchEmployer() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
            @Override
            public void onResponse(Call<ResponseObject<Employer>> call,
                                   Response<ResponseObject<Employer>> response) {
                if (response.isSuccessful()) {
                    populateView(response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                Log.e(TAG, "Error updating worker: " + t.getMessage());
            }
        });
    }

    private void populateView(Employer employer) {

        try {
            if (null != employer) meEmployer = employer;
            if (null != employer.company) {
//                if (null != employer.company.logo) {
//                    if (!TextUtils.isEmpty(employer.company.logo)) {
//                        Picasso.with(getContext())
//                                .load(employer.company.logo)
//                                .into(logo);
//                    }
//                }
                if (null != employer.company.name) {
                    name.setText(employer.company.name);
                }
                if (null != employer.company.description) {
                    description.setText(employer.company.description);
                }
            }

            owner.setText(employer.firstName + " " + employer.lastName);
            rating.setRating(employer.reviewInt);

            if (null != employer.picture) {
                Picasso.with(getContext())
                        .load(employer.picture)
                        .into(logo);
            }

            if (employer.reviewCount > 0) {
                reviewsCounter.setText(String.valueOf(employer.reviewCount));
                reviewsCounter.setVisibility(View.VISIBLE);
            } else {
                reviewsCounter.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureView() {
        reviewsCounter.setVisibility(View.GONE);
        myTasksCounter.setVisibility(View.GONE);

        // TODO Enable for next versions
        myTasksLayout.setVisibility(View.GONE);
        invoicesLayout.setVisibility(View.GONE);
        leaderBoardsLayout.setVisibility(View.GONE);
        accountUserManagementLayout.setVisibility(View.GONE);
    }

    @OnClick({R.id.employer_account_reviews_layout,
            R.id.employer_account_subscription_plan_management})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.employer_account_reviews_layout:
                startActivity(new Intent(getActivity(), ReviewsActivity.class));
                break;
            case R.id.employer_account_subscription_plan_management:
                startActivity(new Intent(getActivity(), PaymentsActivity.class));
                break;
        }
    }
}