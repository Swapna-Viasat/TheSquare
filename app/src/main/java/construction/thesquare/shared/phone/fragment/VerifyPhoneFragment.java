package construction.thesquare.shared.phone.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.signup.model.Employer;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.start.activity.TermsActivity;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.DataUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.worker.signup.model.Worker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import io.intercom.android.sdk.Intercom;
//import io.intercom.android.sdk.identity.Registration;

/**
 * Created by gherg on 12/27/2016.
 */

public class VerifyPhoneFragment extends Fragment {

    public static final String TAG = "VerifyPhoneFragment";

    @BindView(R.id.tvAskForPhoneFirstTitle)
    TextView tvAskForPhoneFirstTitle;
    @BindView(R.id.tvAskForPhoneSecondTitle)
    TextView tvAskForPhoneSecondTitle;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.askForPhonePhoneNumberEditText)
    EditText editTextPhoneNumber;
    @BindView(R.id.askForPhonePhoneNumberEditTextWrapper)
    TextInputLayout phoneLayout;

    private int action;

    public static VerifyPhoneFragment newInstance(int key) {
        VerifyPhoneFragment fragment = new VerifyPhoneFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_VERIFY_PHONE, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_phone, container, false);
        ButterKnife.bind(this, view);

        action = getActivity().getIntent().getIntExtra(Constants.KEY_VERIFY_PHONE, 1);
        tvAskForPhoneFirstTitle.setText(R.string.phone_verification_first_title);
        tvAskForPhoneSecondTitle.setText(R.string.phone_verification_second_title);
        editTextPhoneNumber.setOnEditorActionListener(imeActionListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.terms)
    public void openTerms() {
        startActivity(new Intent(getActivity(), TermsActivity.class));
     /*   getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, SettingsTermsConditionsFragment.newInstance())
                .addToBackStack("contact")
                .commit();*/
    }

    @OnClick(R.id.verify)
    public void validatePhone(View view) {
        if (validateFields()) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                TextTools.log(TAG, "requesting permissions");
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 99);
            } else {
                callApi();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callApi();
            }
        }
    }

    private void callApi() {
        final String deviceEmail = DataUtils.getDeviceEmail(getActivity());
//        final String deviceEmail = "testingsc38480@gmail.com";
        if (!(TextUtils.isEmpty(deviceEmail))) {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HashMap<String, String> registrationRequest = new HashMap<>();
            registrationRequest.put("phone_number", editTextPhoneNumber.getText().toString());
            registrationRequest.put("country_code", ccp.getSelectedCountryCodeWithPlus());

            if (action == Constants.KEY_VERIFY_PHONE_WORKER) {
                HttpRestServiceConsumer.getBaseApiClient()
                        .registrationWorker(registrationRequest)
                        .enqueue(new Callback<ResponseObject<Worker>>() {
                            @Override
                            public void onResponse(Call<ResponseObject<Worker>> call,
                                                   Response<ResponseObject<Worker>> response) {
                                DialogBuilder.cancelDialog(dialog);
                                if (response.isSuccessful()) {

                                    try {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phone", editTextPhoneNumber.getText().toString());
                                        bundle.putString("country", ccp.getSelectedCountryCodeWithPlus());
                                        bundle.putString("email", deviceEmail);
                                        bundle.putInt(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_WORKER);


                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.phone_verify_content, EnterCodeFragment
                                                        .newInstance(bundle))
                                                .addToBackStack("")
                                                .commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //
                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response,
                                            null,
                                            new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            action = Constants.KEY_VERIFY_PHONE_LOGIN;
                                            tvAskForPhoneFirstTitle.setText(R.string.phone_verification_first_title_welcome_back);
                                            tvAskForPhoneSecondTitle.setText(R.string.phone_verification_second_title_reconfirm);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            }
                        });
            } else if (action == Constants.KEY_VERIFY_PHONE_EMPLOYER) {
                HttpRestServiceConsumer.getBaseApiClient()
                        .registrationEmployer(registrationRequest)
                        .enqueue(new Callback<ResponseObject<Employer>>() {
                            @Override
                            public void onResponse(Call<ResponseObject<Employer>> call,
                                                   Response<ResponseObject<Employer>> response) {
                                DialogBuilder.cancelDialog(dialog);
                                if (response.isSuccessful()) {

                                    try {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phone", editTextPhoneNumber.getText().toString());
                                        bundle.putString("country", ccp.getSelectedCountryCodeWithPlus());
                                        bundle.putString("email", deviceEmail);
                                        bundle.putInt(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_EMPLOYER);

                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.phone_verify_content, EnterCodeFragment
                                                        .newInstance(bundle))
                                                .addToBackStack("")
                                                .commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //
                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            }
                        });
            }

        } else {
            DialogBuilder.showStandardDialog(getActivity(),
                    "Error", "A Gmail account is required to register");
        }
    }

    private boolean validateFields() {
        boolean result = true;
        if ((TextUtils.isEmpty(phoneLayout.getEditText().getText().toString()))) {
            result = false;
            phoneLayout.setError("Please enter phone number");
        }
        return result;
    }

    private TextView.OnEditorActionListener imeActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validatePhone(v);
                handled = true;
            }
            return handled;
        }
    };
}