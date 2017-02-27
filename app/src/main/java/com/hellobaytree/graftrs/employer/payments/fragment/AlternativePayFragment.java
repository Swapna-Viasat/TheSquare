package com.hellobaytree.graftrs.employer.payments.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;

import butterknife.ButterKnife;


public class AlternativePayFragment extends Fragment {

    public static final String TAG = "AltPayFragment";

    public AlternativePayFragment() {
        // Required empty public constructor
    }

    public static AlternativePayFragment newInstance() {
        AlternativePayFragment fragment = new AlternativePayFragment();
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
        View view = inflater.inflate(R.layout.fragment_alternative_pay, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
    }
}
