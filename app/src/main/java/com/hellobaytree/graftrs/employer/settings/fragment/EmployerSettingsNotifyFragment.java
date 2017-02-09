package com.hellobaytree.graftrs.employer.settings.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Employer;
import com.hellobaytree.graftrs.shared.utils.TextTools;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class EmployerSettingsNotifyFragment extends Fragment {

    public static final String TAG = "EmployerSettingsNotify";

    @BindView(R.id.notify_worker_app_messages)
    Switch workerAppMessages;
    @BindView(R.id.notify_job_acceptance_rejections)
    Switch jobAcceptanceRejections;
    @BindView(R.id.notify_reviews)
    Switch notifyReviews;

    private Employer meEmployer;

    public static EmployerSettingsNotifyFragment newInstance() {
        EmployerSettingsNotifyFragment fragment = new EmployerSettingsNotifyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_settings_notify, container, false);
        ButterKnife.bind(this, view);
        fetchEmployer();
        return view;
    }

    private void fetchEmployer() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
            @Override
            public void onResponse(Call<ResponseObject<Employer>> call,
                                   Response<ResponseObject<Employer>> response) {
                if (response.isSuccessful()) {
                    meEmployer = response.body().getResponse();
                    populate();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                Log.e("EmployerSettingsNotify", t.getLocalizedMessage());
            }
        });
    }

    private void populate() {
        workerAppMessages.setChecked(meEmployer.workerAppNotifications);
        jobAcceptanceRejections.setChecked(meEmployer.jobNotifications);
        notifyReviews.setChecked(meEmployer.reviewNotifications);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.settings_notifications));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.notify_worker_app_messages, R.id.notify_job_acceptance_rejections, R.id.notify_reviews})
    public void toggles(View view) {
        String paramName = "";
        switch (view.getId()) {
            case R.id.notify_worker_app_messages:
                paramName = "worker_app_notifications";
                break;
            case R.id.notify_job_acceptance_rejections:
                paramName = "job_notifications";
                break;
            case R.id.notify_reviews:
                paramName = "reviews_notifications";
                break;
        }

        if (meEmployer != null && !TextUtils.isEmpty(paramName)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put(paramName, ((Switch) view).isChecked());
            HttpRestServiceConsumer.getBaseApiClient()
                    .patchEmployer(meEmployer.id, params)
                    .enqueue(new Callback<ResponseObject<Employer>>() {
                @Override
                public void onResponse(Call<ResponseObject<Employer>> call,
                                       Response<ResponseObject<Employer>> response) {
                    TextTools.log("EmployerSettingsNotify", "Modified value "
                            + (response.isSuccessful() ? "successfully" : "unsuccessfully"));
                }

                @Override
                public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                    Log.e("EmployerSettingsNotify", t.getLocalizedMessage());
                }
            });
        }
    }
}
