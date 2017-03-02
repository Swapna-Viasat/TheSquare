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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_plans, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(adapter);

    }

    @OnClick(R.id.payments_alt_confirm)
    public void onConfirm() {
        //
        // Toast.makeText(getContext(), "confirm", Toast.LENGTH_LONG).show();

        if (validate()) {
            callApi();
        }
    }

    private void callApi() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, Object> body = new HashMap<>();
        body.put("payments_detail", planSpinner.getSelectedItemPosition() + 2);
        body.put("bill_payers_name", nameInput.getText().toString());
        body.put("purchase_order_number", orderInput.getText().toString());
        body.put("bill_payers_email", emailInput.getText().toString());

        HttpRestServiceConsumer.getBaseApiClient()
                .submitAlternativePayment(body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            showSuccessPopup();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private boolean validate() {
        boolean result = true;

        if (nameInput.getText().toString().equals("")) {
            result = false;
        }

        if (emailInput.getText().toString().equals("")) {
            result = false;
        }

        if (orderInput.getText().toString().equals("")) {
            result = false;
        }

        return result;
    }

    private void showSuccessPopup() {

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_NoTitleBar);
        dialog.setContentView(R.layout.dialog_alt_pay_done);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.pay_alt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getActivity().getSupportFragmentManager()
                        .popBackStack();
            }
        });
        dialog.show();
    }
}