package com.hellobaytree.graftrs.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Employer;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricePlanFragment extends Fragment {

    public static final String TAG = "PricePlanFragment";

    @BindView(R.id.due_date) TextView dueDate;
    @BindView(R.id.plan) TextView plan;

    @BindView(R.id.topup_digits) TextView topupDigits;
    @BindView(R.id.plan_digits) TextView planDigits;

    @BindView(R.id.plan_expiration) TextView planExpiration;
    @BindView(R.id.topup_expiration) TextView topupExpiration;

    public PricePlanFragment() {
        // Required empty public constructor
    }

    public static PricePlanFragment newInstance() {
        PricePlanFragment fragment = new PricePlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_plan, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("My Price Plan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //
        fetchEmployer();
    }

    private void fetchEmployer() {
        //
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            if (null != response.body()) {
                                if (null != response.body().getResponse()) {
                                    populate(response.body().getResponse());
                                }
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void displayCurrentPlan(String planName) {
        try {
            plan.setText(planName);
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate(Employer employer) {
        //
        if (null != employer) {
            displayBookings(employer.maxForPlan, employer.bookedWithPlan,
                    employer.maxForTopups, employer.bookedWithTopups);
            if (null != employer.planExpiration) {
                displayPlanExpiration(employer.planExpiration);
            }
            if (null != employer.topupExpiration) {
                displayTopupExpiration(employer.topupExpiration);
            }
            if (null != employer.planName) {
                displayCurrentPlan(employer.planName);
            }
        }
    }

    private void displayPlanExpiration(String planExpiry) {
        try {
            planExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), planExpiry));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTopupExpiration(String topupExpiry) {
        try {
            topupExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), topupExpiry));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayBookings(int planMax, int planUsed, int topMax, int topUsed) {
        try {
            planDigits.setText(String.format(getString(R.string.payments_plans_display_bookings),
                    planUsed, planMax));
            topupDigits.setText(String.format(getString(R.string.payments_topups_display_bookings),
                    topUsed, topMax));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.change_plan)
    public void changePlan() {
        //Toast.makeText(getContext(), "Change", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, SubscriptionFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.cancel)
    public void cancelPlan() {
        Toast.makeText(getContext(), "Cancel", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.change_card)
    public void changeCard() {
        //Toast.makeText(getContext(), "Change card", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, PaymentFragment.newInstance(1))
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.top_up)
    public void topUp() {
        //Toast.makeText(getContext(), "Top Up", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, TopUpFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.alternative_payment)
    public void alternative() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, AlternativePayFragment.newInstance())
                .addToBackStack("")
                .commit();
    }
}