package com.hellobaytree.graftrs.shared.phone.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.MainEmployerActivity;
import com.hellobaytree.graftrs.employer.onboarding.OnboardingEmployerActivity;
import com.hellobaytree.graftrs.employer.signup.model.EmployerVerify;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.model.SMSSent;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.phone.OnSmsReceivedListener;
import com.hellobaytree.graftrs.shared.phone.SmsInterceptor;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.worker.main.ui.MainWorkerActivity;
import com.hellobaytree.graftrs.worker.onboarding.OnboardingWorkerActivity;
import com.hellobaytree.graftrs.worker.signup.model.WorkerVerify;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/27/2016.
 */

public class EnterCodeFragment extends Fragment implements OnSmsReceivedListener {

    public static final String TAG = "EnterCodeFragment";

    @BindView(R.id.eVerificationCode1)
    EditText eVerificationCode1;
    @BindView(R.id.eVerificationCode2)
    EditText eVerificationCode2;
    @BindView(R.id.eVerificationCode3)
    EditText eVerificationCode3;
    @BindView(R.id.eVerificationCode4)
    EditText eVerificationCode4;
    @BindView(R.id.btnPhoneVerificationContinue)
    Button btnContinue;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;

    private EditText selectedEditText;
    private String verificationCode;
    private static final int REQUEST_SMS = 1;
    public static final long WAIT_DELAY = 3000;
    private SmsInterceptor smsInterceptor;
    private Dialog dialog;
    private Handler handler;
    private String currentPhone, currentCountryCode;

    public static EnterCodeFragment newInstance(Bundle bundle) {
        EnterCodeFragment enterCodeFragment = new EnterCodeFragment();
        enterCodeFragment.setArguments(bundle);
        return enterCodeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smsInterceptor = new SmsInterceptor();
        smsInterceptor.setListener(this);
        handler = new Handler(Looper.getMainLooper());
        currentPhone = getArguments().getString("phone");
        currentCountryCode = getArguments().getString("country");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_code, container, false);
        ButterKnife.bind(this, view);

        selectedEditText = eVerificationCode1;
        eVerificationCode1.addTextChangedListener(textWatcher);
        eVerificationCode2.addTextChangedListener(textWatcher);
        eVerificationCode3.addTextChangedListener(textWatcher);
        eVerificationCode4.addTextChangedListener(textWatcher);

        tvPhoneNumber.setText(getString(R.string.phone_verification_code_sent, currentCountryCode + " "
                + currentPhone));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestSmsPermission();
    }

    @Override
    public void onStop() {
        TextTools.log(TAG, "onStop");
        DialogBuilder.cancelDialog(dialog);
        disableBroadcastReceiver();
        handler.removeCallbacks(cancelDialogRunnable);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (TextUtils.equals(permissions[i], Manifest.permission.READ_SMS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    enableBroadcastReceiver();
            }
        }
    }

    @Override
    public void onSmsReceived(String code) {
        DialogBuilder.cancelDialog(dialog);

        eVerificationCode1.setText(Character.toString(code.charAt(0)));
        eVerificationCode2.setText(Character.toString(code.charAt(1)));
        eVerificationCode3.setText(Character.toString(code.charAt(2)));
        eVerificationCode4.setText(Character.toString(code.charAt(3)));

        continueClick();
    }

    @OnClick(R.id.btnPhoneVerificationContinue)
    void continueClick() {
        if (validateFields()) {
            // call api

            HashMap<String, String> verificationRequest = new HashMap<>();
            verificationRequest.put("country_code", currentCountryCode);
            verificationRequest.put("phone_number", currentPhone);
            verificationRequest.put("email", getArguments().getString("email"));
            verificationRequest.put("verification_number", verificationCode);

            callApi(getArguments().getInt(Constants.KEY_VERIFY_PHONE, 1),
                    verificationRequest);
        }
    }

    private void callApi(int key, HashMap<String, String> body) {
        TextTools.log(TAG, String.valueOf(key));
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        switch (key) {
            case Constants.KEY_VERIFY_PHONE_EMPLOYER:
                HttpRestServiceConsumer.getBaseApiClient()
                        .verifyEmployerNumber(body)
                        .enqueue(new Callback<ResponseObject<EmployerVerify>>() {
                            @Override
                            public void onResponse(Call<ResponseObject<EmployerVerify>> call,
                                                   Response<ResponseObject<EmployerVerify>> response) {
                                if (response.isSuccessful()) {
                                    //
                                    DialogBuilder.cancelDialog(dialog);

                                    SharedPreferencesManager.getInstance(getContext())
                                            .persistSessionInfoEmployer(response.body().getResponse().getToken(),
                                                    response.body().getResponse().getUser(),
                                                    currentCountryCode, currentPhone);

                                    if (response.body().getResponse().getUser().isOnboarding_done()) {
                                        startActivity(new Intent(getContext(), MainEmployerActivity.class));
                                    } else {
                                        startActivity(new Intent(getActivity(), OnboardingEmployerActivity.class));
                                    }

                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseObject<EmployerVerify>> call, Throwable t) {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            }
                        });
                break;
            case Constants.KEY_VERIFY_PHONE_WORKER:
                HttpRestServiceConsumer.getBaseApiClient()
                        .verifyWorkerNumber(body)
                        .enqueue(new Callback<ResponseObject<WorkerVerify>>() {
                            @Override
                            public void onResponse(Call<ResponseObject<WorkerVerify>> call,
                                                   Response<ResponseObject<WorkerVerify>> response) {
                                if (response.isSuccessful()) {
                                    //
                                    DialogBuilder.cancelDialog(dialog);

                                    SharedPreferencesManager.getInstance(getContext())
                                            .persistSessionInfoWorker(response.body().getResponse().getToken(),
                                                    response.body().getResponse().getUser(),
                                                    currentCountryCode, currentPhone);

                                    if (response.body().getResponse().getUser().isOnboarding_done()) {
                                        startActivity(new Intent(getContext(), MainWorkerActivity.class));
                                    } else {
                                        startActivity(new Intent(getActivity(), OnboardingWorkerActivity.class));
                                    }

                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseObject<WorkerVerify>> call, Throwable t) {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            }
                        });
                break;
        }
    }

    @OnClick({R.id.resend_code, R.id.edit_number})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.resend_code:
                resendCode(currentCountryCode, currentPhone);
                break;
            case R.id.edit_number:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void resendCode(String country, String phone) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, String> resendSMSRequest = new HashMap<>();
        resendSMSRequest.put("country_code", country);
        resendSMSRequest.put("phone_number", phone);

        if (getActivity().getIntent()
                .getIntExtra(Constants.KEY_VERIFY_PHONE, 1)
                == Constants.KEY_VERIFY_PHONE_WORKER) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .resendSMSWorker(resendSMSRequest)
                    .enqueue(new Callback<ResponseObject<SMSSent>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<SMSSent>> call,
                                               Response<ResponseObject<SMSSent>> response) {
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                            } else {
                                HandleErrors.parseError(getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<SMSSent>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } else if (getActivity().getIntent()
                .getIntExtra(Constants.KEY_VERIFY_PHONE, 1)
                == Constants.KEY_VERIFY_PHONE_EMPLOYER) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .resendSMSEmployer(resendSMSRequest)
                    .enqueue(new Callback<ResponseObject<SMSSent>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<SMSSent>> call,
                                               Response<ResponseObject<SMSSent>> response) {
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                            } else {
                                HandleErrors.parseError(getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<SMSSent>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 7) {
                setTextToEdit(eVerificationCode1, String.valueOf(editable.charAt(0)));
                setTextToEdit(eVerificationCode2, String.valueOf(editable.charAt(1)));
                setTextToEdit(eVerificationCode3, String.valueOf(editable.charAt(2)));
                setTextToEdit(eVerificationCode4, String.valueOf(editable.charAt(3)));
            } else {
                if (editable.toString().length() == 2) {
                    setTextToEdit(selectedEditText, String.valueOf(editable.charAt(0)));
                    EditText nextEditText = nextEditText(selectedEditText, true);
                    if (nextEditText != null) {
                        selectedEditText.clearFocus();
                        setTextToEdit(nextEditText, String.valueOf(editable.charAt(1)));
                        nextEditText.requestFocus();
                        nextEditText.setSelection(nextEditText.getText().length());
                        selectedEditText = nextEditText;
                    }
                } else if (editable.toString().length() == 0) {
                    EditText nextEditText = nextEditText(selectedEditText, false);
                    if (nextEditText != null) {
                        selectedEditText.clearFocus();
                        nextEditText.requestFocus();
                        nextEditText.setSelection(nextEditText.getText().length());
                        selectedEditText = nextEditText;
                    }
                }
            }
        }
    };

    private EditText nextEditText(EditText editText, boolean goRight) {
        switch (editText.getId()) {
            case R.id.eVerificationCode1:
                return goRight ? eVerificationCode2 : null;
            case R.id.eVerificationCode2:
                return goRight ? eVerificationCode3 : eVerificationCode1;
            case R.id.eVerificationCode3:
                return goRight ? eVerificationCode4 : eVerificationCode2;
            case R.id.eVerificationCode4:
                return goRight ? null : eVerificationCode3;
        }

        return null;
    }

    private void setTextToEdit(EditText editText, String text) {
        editText.removeTextChangedListener(textWatcher);
        editText.setText(text);
        editText.addTextChangedListener(textWatcher);
    }

    @OnFocusChange({
            R.id.eVerificationCode1,
            R.id.eVerificationCode2,
            R.id.eVerificationCode3,
            R.id.eVerificationCode4
    })
    void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;
        if (hasFocus) {
            DrawableCompat.setTint(editText.getBackground(), ContextCompat.getColor(getActivity(), R.color.redSquareColor));
        } else {
            DrawableCompat.setTint(editText.getBackground(), ContextCompat.getColor(getActivity(), R.color.lightSquareColor));
        }
    }

    private boolean validateFields() {
        boolean result = true;
        verificationCode = eVerificationCode1.getText().toString() +
                eVerificationCode2.getText().toString() +
                eVerificationCode3.getText().toString() +
                eVerificationCode4.getText().toString();
        if (verificationCode.length() != 4) {
            result = false;
            DialogBuilder.showStandardDialog(getActivity(), "",
                    getResources().getString(R.string.phone_verification_validate_failed));
        }
        return result;
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            dialog = DialogBuilder.showCustomDialog(getContext());
            handler.postDelayed(cancelDialogRunnable, WAIT_DELAY);
            enableBroadcastReceiver();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_SMS);
        }
    }

    private void enableBroadcastReceiver() {
        TextTools.log(TAG, "enableBroadcastReceiver");
        getActivity().registerReceiver(smsInterceptor,
                new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    private void disableBroadcastReceiver() {
        TextTools.log(TAG, "disableBroadcastReceiver");
        getActivity().unregisterReceiver(smsInterceptor);
    }

    private Runnable cancelDialogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                DialogBuilder.cancelDialog(dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}