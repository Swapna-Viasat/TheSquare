package construction.thesquare.worker.settings.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Worker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class WorkerSettingsNotifyFragment extends Fragment {

    @BindView(R.id.notify_new_job_matches)
    Switch newJobMatches;
    @BindView(R.id.notify_job_offers)
    Switch jobOffers;
    @BindView(R.id.notify_job_bookings_declines)
    Switch jobBookingDeclines;
    @BindView(R.id.notify_reviews)
    Switch reviews;

    private Worker meWorker;

    public static WorkerSettingsNotifyFragment newInstance() {
        return new WorkerSettingsNotifyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_settings_notify, container, false);
        ButterKnife.bind(this, view);
        fetchWorker();
        return view;
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

    private void fetchWorker() {
        HttpRestServiceConsumer.getBaseApiClient().meWorker().enqueue(new Callback<ResponseObject<Worker>>() {
            @Override
            public void onResponse(Call<ResponseObject<Worker>> call, Response<ResponseObject<Worker>> response) {
                if (response.isSuccessful()) {
                    meWorker = response.body().getResponse();
                    populate();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                Log.e("WorkerSettingsNotify", t.getLocalizedMessage());
            }
        });
    }

    private void populate() {
        newJobMatches.setChecked(meWorker.newJobMatchesNotifications);
        jobOffers.setChecked(meWorker.jobOffersNotifications);
        jobBookingDeclines.setChecked(meWorker.jobBookingDeclinesNotifications);
        reviews.setChecked(meWorker.reviewNotifications);
    }

    @OnClick({R.id.notify_new_job_matches, R.id.notify_job_offers, R.id.notify_job_bookings_declines, R.id.notify_reviews})
    public void toggles(View view) {
        String paramName = "";
        switch (view.getId()) {
            case R.id.notify_new_job_matches:
                paramName = "new_job_matches_notifications";
                break;
            case R.id.notify_job_offers:
                paramName = "job_offers_notifications";
                break;
            case R.id.notify_job_bookings_declines:
                paramName = "job_booking_declines_notifications";
                break;
            case R.id.notify_reviews:
                paramName = "review_notifications";
                break;
        }

        if (meWorker != null && !TextUtils.isEmpty(paramName)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put(paramName, ((Switch) view).isChecked());
            HttpRestServiceConsumer.getBaseApiClient().patchWorker(meWorker.id, params).enqueue(new Callback<ResponseObject<Worker>>() {
                @Override
                public void onResponse(Call<ResponseObject<Worker>> call, Response<ResponseObject<Worker>> response) {
                    Log.i("WorkerSettingsNotify", "Modified value " + (response.isSuccessful() ? "successfully" : "unsuccessfully"));
                }

                @Override
                public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                    Log.e("WorkerSettingsNotify", t.getLocalizedMessage());
                }
            });
        }
    }
}
