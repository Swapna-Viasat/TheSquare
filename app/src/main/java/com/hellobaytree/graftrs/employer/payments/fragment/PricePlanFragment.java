package com.hellobaytree.graftrs.employer.payments.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PricePlanFragment extends Fragment {

    public static final String TAG = "PricePlanFragment";

    @BindView(R.id.due_date) TextView dueDate;
    @BindView(R.id.plan) TextView plan;

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
        Toast.makeText(getContext(), "Change card", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.top_up)
    public void topUp() {
        Toast.makeText(getContext(), "Top Up", Toast.LENGTH_LONG).show();
    }
}