package com.hellobaytree.graftrs.shared.phone.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.MainEmployerActivity;
import com.hellobaytree.graftrs.employer.onboarding.OnboardingEmployerActivity;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.LoginUser;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.worker.main.ui.MainWorkerActivity;
import com.hellobaytree.graftrs.worker.onboarding.OnboardingWorkerActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class EmailLoginFragment extends Fragment {

    private static final int TYPE_EMPLOYER = 1;
    private static final int TYPE_WORKER = 2;

    @BindView(R.id.emailEditText)
    EditText emailInput;

    @BindView(R.id.passwordEditText)
    EditText passwordInput;

    public EmailLoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_email, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(getContext()).getEmail()))
            emailInput.setText(SharedPreferencesManager.getInstance(getContext()).getEmail());
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(getContext()).getPass()))
            passwordInput.setText(SharedPreferencesManager.getInstance(getContext()).getPass());
    }

    @OnClick(R.id.loginButton)
    void login() {
        if (validateInput()) {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("email", emailInput.getText().toString());
            payload.put("password", passwordInput.getText().toString());
            callApi(payload);
        }
    }

    @OnClick(R.id.forgotPass)
    void goToForgotPasswordScreen() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.phone_verify_content, new ForgotPasswordFragment())
                .commit();
    }

    private boolean validateInput() {
        boolean result = true;
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.empty_email));
            result = false;
        } else if (TextUtils.isEmpty(passwordInput.getText().toString())) {
            result = false;
            passwordInput.setError("Please enter your password");
        }
        return result;
    }

    private void callApi(HashMap<String, String> body) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .loginUser(body)
                .enqueue(new Callback<ResponseObject<LoginUser>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<LoginUser>> call,
                                           Response<ResponseObject<LoginUser>> response) {
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            if (response.body().getResponse().user.userType == TYPE_EMPLOYER) {
                                processEmployer(response);

                            } else if (response.body().getResponse().user.userType == TYPE_WORKER) {
                                processWorker(response);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<LoginUser>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void processWorker(Response<ResponseObject<LoginUser>> response) {
        String name = response.body().getResponse().
                user.getFirst_name() + " " +
                response.body().getResponse()
                        .user.getLast_name();

        SharedPreferencesManager.getInstance(getContext())
                .persistSessionInfoWorker(response.body().getResponse().token,
                        response.body().getResponse().user,
                        "", emailInput.getText().toString(), name, passwordInput.getText().toString());
        if (response.body().getResponse().user.isOnboarding_done()) {
            startAnotherActivity(new Intent(getContext(), MainWorkerActivity.class));
        } else {
            startAnotherActivity(new Intent(getActivity(), OnboardingWorkerActivity.class));
        }
    }

    private void processEmployer(Response<ResponseObject<LoginUser>> response) {
        String name = response.body().getResponse().
                user.getFirst_name() + " " +
                response.body().getResponse()
                        .user.getLast_name();

        SharedPreferencesManager.getInstance(getContext())
                .persistSessionInfoEmployer2(response.body().getResponse().token,
                        response.body().getResponse().user,
                        "", emailInput.getText().toString(), name, passwordInput.getText().toString());
        if (response.body().getResponse().user.isOnboarding_done()) {
            startAnotherActivity(new Intent(getContext(), MainEmployerActivity.class));
        } else {
            startAnotherActivity(new Intent(getActivity(), OnboardingEmployerActivity.class));
        }
    }

    private void startAnotherActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
