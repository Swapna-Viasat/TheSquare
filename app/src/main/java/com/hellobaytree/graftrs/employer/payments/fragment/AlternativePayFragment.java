package com.hellobaytree.graftrs.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlternativePayFragment extends Fragment {

    public static final String TAG = "AltPayFragment";

    @BindView(R.id.payments_plan_spinner) Spinner planSpinner;

    @BindView(R.id.alt_pay_order_input) EditText orderInput;
    @BindView(R.id.alt_pay_email_input) EditText emailInput;
    @BindView(R.id.alt_pay_name_input) EditText nameInput;

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

    @OnClick(R.id.payments_alt_confirm)
    public void onConfirm() {
        //
        Toast.makeText(getContext(), "confirm", Toast.LENGTH_LONG).show();

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.dialog_alt_pay_done);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.pay_alt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
