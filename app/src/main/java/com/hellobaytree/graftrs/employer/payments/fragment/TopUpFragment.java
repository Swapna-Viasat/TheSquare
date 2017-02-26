package com.hellobaytree.graftrs.employer.payments.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopUpFragment extends Fragment {

    public TopUpFragment() {
        // Required empty public constructor
    }

    public static TopUpFragment newInstance() {
        TopUpFragment fragment = new TopUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Top Up");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.continue_top_up)
    public void topUp() {
        getActivity().getSupportFragmentManager()
                .popBackStack();
    }

    @OnClick(R.id.change_plan)
    public void changePlan() {
        getActivity().getSupportFragmentManager()
                .popBackStack();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, SubscriptionFragment.newInstance())
                .addToBackStack("")
                .commit();
    }
}