package com.hellobaytree.graftrs.employer.payments.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.subscription.model.CreateCardRequest;
import com.hellobaytree.graftrs.employer.subscription.model.CreateCardResponse;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansEditText;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    public static final String TAG = "PaymentFragment";

    @BindView(R.id.first) JosefinSansEditText first;
    @BindView(R.id.last) JosefinSansEditText last;
    @BindView(R.id.number) JosefinSansEditText number;
    @BindView(R.id.month) JosefinSansEditText month;
    @BindView(R.id.year) JosefinSansEditText year;
    @BindView(R.id.cvc) JosefinSansEditText cvc;
    // address fields
    @BindView(R.id.voucher) JosefinSansEditText voucher;
    @BindView(R.id.address1) JosefinSansEditText address;
    @BindView(R.id.city) JosefinSansEditText city;
    @BindView(R.id.country) JosefinSansEditText country;
    @BindView(R.id.postcode) JosefinSansEditText postcode;
    private int plan;

    public static PaymentFragment newInstance(int selectedPlan) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_SELECTED_PLAN, selectedPlan);
        fragment.setArguments(bundle);
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
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        plan = getArguments().getInt(Constants.KEY_SELECTED_PLAN);
    }

    @OnClick(R.id.confirm)
    public void submit() {
        // getCard();

        HashMap<String, Object> body = new HashMap<>();
        body.put("stripe_id", "pk_test_iUGx8ZpCWm6GeSwBpfkdqjSQ");
        body.put("stripe_source_token", "dfdfdf");
        body.put("payment_detail", plan);
        HttpRestServiceConsumer.getBaseApiClient()
                .setupPayment(body)
                .enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call,
                                           Response<ResponseObject> response) {
                        //
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        //
                    }
                });
    }

    private Card getCard() {

        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int monthInt = 0;
        int yearInt = 0;

        try {
            //
            monthInt = Integer.valueOf(month.getText().toString());
            yearInt = Integer.valueOf(year.getText().toString());
            //
        } catch (Exception e) {
            e.printStackTrace();
        }

        Card card = new Card(number.getText().toString(),
                                monthInt,
                                yearInt,
                                cvc.getText().toString(),
                                first.getText().toString() + " " + last.getText().toString(),
                                address.getText().toString(), null,
                                city.getText().toString(), null,
                                postcode.getText().toString(),
                                country.getText().toString(), null);



        if (card.validateNumber()) {
            try {
                final Stripe stripe = new Stripe("pk_test_iUGx8ZpCWm6GeSwBpfkdqjSQ");
                stripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        DialogBuilder.cancelDialog(dialog);
                        new AlertDialog.Builder(getContext())
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                        error.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Token token) {
                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, token.getId());
                        // proceed
                        submitStripeInfo(token.getId());
                        //
                    }
                });
            } catch (Exception e) {
                DialogBuilder.cancelDialog(dialog);
                e.printStackTrace();
            }
        } else {
            TextTools.log(TAG, "couldn't validate");
            DialogBuilder.cancelDialog(dialog);
            new AlertDialog.Builder(getContext())
                    .setMessage("Something went wrong.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }

        return card;
    }

    private void submitStripeInfo(String token) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        CreateCardRequest request = new CreateCardRequest();
        request.name = first.getText().toString() + " " + last.getText().toString();
        request.token = token;
        HttpRestServiceConsumer.getBaseApiClient()
                .addCard(request)
                .enqueue(new Callback<CreateCardResponse>() {
                    @Override
                    public void onResponse(Call<CreateCardResponse> call,
                                           Response<CreateCardResponse> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            // TODO: clarify how to display success on screen
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateCardResponse> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }
}