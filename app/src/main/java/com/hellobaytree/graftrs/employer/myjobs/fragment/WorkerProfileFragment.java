package com.hellobaytree.graftrs.employer.myjobs.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Company;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Skill;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.CollectionUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerProfileFragment extends Fragment {

    @BindView(R.id.worker_view_profile_avatar)
    CircleImageView avatarImage;

    @BindView(R.id.worker_view_profile_name)
    TextView nameView;

    @BindView(R.id.worker_view_profile_position)
    TextView positionView;

    @BindView(R.id.worker_view_profile_experience)
    TextView experienceYearsView;

    @BindView(R.id.worker_view_profile_rating)
    RatingView ratingView;

    @BindView(R.id.worker_profile_bio_text)
    TextView bioView;

    @BindView(R.id.worker_details_bullet_list_experience)
    TextView experienceView;

    @BindView(R.id.worker_details_bullet_list_requirements)
    TextView requirementsView;

    @BindView(R.id.worker_details_bullet_list_skills)
    TextView skillsView;

    @BindView(R.id.worker_details_bullet_list_companies)
    TextView companiesView;

    @BindView(R.id.worker_details_preferred_location)
    TextView locationView;

    @BindView(R.id.mapView)
    MapView mapView;

    private static final String KEY_WORKER_ID = "KEY_WORKER_ID";
    private Worker worker;
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private int workerId;

    public static WorkerProfileFragment newInstance(int workerId) {
        WorkerProfileFragment fragment = new WorkerProfileFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_WORKER_ID, workerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        workerId = getArguments().getInt(KEY_WORKER_ID, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_worker_profile, container, false);
        ButterKnife.bind(this, v);

        mapView.onCreate(savedInstanceState);
        return v;
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
        mapView.onStart();

        fetchWorker();
    }

    private void initComponents() {
        if (worker != null) {

            fillWorkerImage();
            fillWorkerName();
            fillWorkerPosition();
            fillExperienceAndQualifications();
            fillSkills();
            fillCompanies();
            fillWorkerBio();
            fillLocationName();
            initMap();
        }
    }

    private void fillWorkerImage() {
        if (!TextUtils.isEmpty(worker.picture)) {
            Picasso.with(getContext()).load(worker.picture).into(avatarImage);
        } else {
            avatarImage.setImageResource(R.drawable.ic_no_avatar);
        }
    }

    private void fillWorkerName() {
        StringBuilder nameString = new StringBuilder();
        if (!TextUtils.isEmpty(worker.firstName)) nameString.append(worker.firstName);
        if (!TextUtils.isEmpty(worker.lastName)) nameString.append(" ").append(worker.lastName);
        nameView.setText(nameString.toString().toUpperCase(Locale.UK));
    }

    private void fillWorkerPosition() {
        String role = "";
        if (!CollectionUtils.isEmpty(worker.roles)) role = worker.roles.get(0).name;
        positionView.setText(role);

        String positionString = getString(R.string.role_year_experience, worker.yearsExperience,
                getResources().getQuantityString(R.plurals.year_plural, worker.yearsExperience));
        experienceYearsView.setText(positionString.toUpperCase(Locale.UK));
        ratingView.setRating((int) worker.rating);
    }

    private void fillExperienceAndQualifications() {
        String qualificationsText = "";
        String requirementsText = "";

        for (Qualification qualification : worker.qualifications) {
            if (qualification.onExperience) {
                requirementsText += "• " + qualification.name + "\n";
            } else
                qualificationsText += "• " + qualification.name + "\n";
        }
        experienceView.setText(qualificationsText);
        requirementsView.setText(requirementsText);
    }

    private void fillSkills() {
        String text = "";
        for (Skill preference : worker.skills) {
            text += "• " + preference.name + "\n";
        }
        skillsView.setText(text);
    }

    private void fillCompanies() {
        String text = "";
        for (Company preference : worker.companies) {
            text += "• " + preference.name + "\n";
        }
        companiesView.setText(text);
    }

    private void fillWorkerBio() {
        if (!TextUtils.isEmpty(worker.bio)) bioView.setText(worker.bio);
    }

    private void initMap() {
        if (googleMap == null)
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    googleMap.getUiSettings().setCompassEnabled(false);
                    googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    drawMarker();
                }
            });
        else drawMarker();
    }

    private void fillLocationName() {
        if (worker != null)
            locationView.setText(worker.address);
    }

    private void drawMarker() {
        if (worker != null && worker.location != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(worker.location.getLatitude(), worker.location.getLongitude())));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(worker.location.getLatitude(), worker.location.getLongitude()), 12f));
        }
    }

    private void fetchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerProfile(workerId)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            worker = response.body().getResponse();
                            initComponents();
                        } else
                            HandleErrors.parseError(getContext(), dialog, response);
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
}