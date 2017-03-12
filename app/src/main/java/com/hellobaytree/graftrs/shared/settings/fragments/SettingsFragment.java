package com.hellobaytree.graftrs.shared.settings.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.settings.dialog.UpdateEmailDialog;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.AccountType;
import com.hellobaytree.graftrs.shared.data.model.Logout;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.main.activity.MainActivity;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.ShareUtils;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment
        implements UpdateEmailDialog.UpdateEmailListener {

    @BindView(R.id.phoneValue)
    JosefinSansTextView phoneValueTextView;
    @BindView(R.id.emailValue)
    JosefinSansTextView emailValueTextView;
    private UpdateEmailDialog updateEmailDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.settings);
        populateData();
    }

    @Override
    public void onEmailUpdate(String email) {
        updateEmailDialog.dismiss();
        emailValueTextView.setText(email);
    }

    @OnClick({R.id.phone, R.id.email, R.id.terms,
            R.id.about, R.id.share, R.id.logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone:
                break;
            case R.id.email:
                //
                updateEmailDialog = UpdateEmailDialog.newInstance(
                        emailValueTextView.getText().toString(),
                        this
                );
                updateEmailDialog.setCancelable(false);
                updateEmailDialog.show(getChildFragmentManager(), "updateEmail");
                break;
            case R.id.terms:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsDocsFragment.newInstance())
                        .addToBackStack("docs")
                        .commit();
                break;
            case R.id.about:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsAboutFragment.newInstance())
                        .addToBackStack("about")
                        .commit();
                break;
            case R.id.share:
                ShareUtils.employerLink(getContext());
                break;
            case R.id.logout:
                DialogBuilder.afterShowSetProperties(new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.employer_settings_logout_prompt))
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }), getContext());
                break;
        }
    }

    private void logout() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .logoutEmployer()
                .enqueue(new Callback<ResponseObject<Logout>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Logout>> call,
                                           Response<ResponseObject<Logout>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            onLogoutSuccess();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Logout>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    public void onLogoutSuccess() {
        SharedPreferencesManager.getInstance(getContext()).deleteToken();
        SharedPreferencesManager.getInstance(getContext()).deleteSessionInfoEmployer();
        SharedPreferencesManager.getInstance(getContext()).deleteIsInComingSoon();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void populateData() {
        if (getAccountType() != null) {
            String phone;
            String countryCode;
            String email;
            if (getAccountType() == AccountType.employer) {
                email = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getEmail();
                phone = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getPhoneNumber();
                countryCode = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getCountryCode();
            } else {
                email = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getEmail();
                phone = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getPhoneNumber();
                countryCode = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getCountryCode();
            }
            phoneValueTextView.setText(countryCode + phone);
            emailValueTextView.setText(email);
        }
    }

    @Nullable
    private AccountType getAccountType() {
        if (SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getUserId() > 0) {
            return AccountType.employer;
        } else if (SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getUserId() > 0) {
            return AccountType.worker;
        }
        return null;
    }
}
