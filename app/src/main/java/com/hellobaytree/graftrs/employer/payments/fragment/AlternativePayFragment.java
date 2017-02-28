package com.hellobaytree.graftrs.employer.payments.fragment;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_price_plan_nested, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                getActivity().getSupportFragmentManager()
                        .popBackStack();
                return true;
        }
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Alternative Payments");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.payments_alt_confirm)
    public void onConfirm() {
        //
        // Toast.makeText(getContext(), "confirm", Toast.LENGTH_LONG).show();

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_NoTitleBar);
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
