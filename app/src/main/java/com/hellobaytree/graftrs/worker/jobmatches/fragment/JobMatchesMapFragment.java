package com.hellobaytree.graftrs.worker.jobmatches.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.utils.CollectionUtils;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.jobdetails.JobDetailActivity;
import com.hellobaytree.graftrs.worker.jobdetails.LikeJobConnector;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.jobmatches.model.MatchesResponse;
import com.hellobaytree.graftrs.worker.myaccount.ui.activity.MyAccountViewProfileActivity;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobMatchesMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        LikeJobConnector.Callback {

    @BindView(R.id.map_view)
    MapView mapView;

    public static final String TAG = "JobMatchesMap";

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private List<Job> jobs;
    private Dialog dialog;
    private LikeJobConnector likeJobConnector;

    public static JobMatchesMapFragment newInstance(Bundle bundle) {
        JobMatchesMapFragment fragment = new JobMatchesMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .build();
        }
        jobs = new ArrayList<>();
        likeJobConnector = new LikeJobConnector(this);
    }

    private void showNoMatchesDialog() {
        dialog = DialogBuilder.showTwoOptionsStandardDialog(
                getContext(), "",
                getString(R.string.worker_no_matches),
                getString(R.string.worker_update_profile),
                getString(R.string.onboarding_cancel),
                new DialogBuilder.OnClickTwoOptionsStandardDialog() {
                    @Override
                    public void onClickOptionOneStandardDialog(Context context) {
                        getActivity().startActivity(new Intent(getContext(), MyAccountViewProfileActivity.class));
                    }

                    @Override
                    public void onClickOptionTwoStandardDialog(Context context) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void destroyDialog() {
        DialogBuilder.cancelDialog(dialog);
        dialog = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_matches_map, container, false);
        ButterKnife.bind(this, view);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 53);
        } else {
            mapView.getMapAsync(this);
        }
    }

    public void fetchJobMatches() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        Call<MatchesResponse> call = HttpRestServiceConsumer.getBaseApiClient()
                .getJobMatches(null, null);
        call.enqueue(new Callback<MatchesResponse>() {
            @Override
            public void onResponse(Call<MatchesResponse> call, Response<MatchesResponse> response) {
                DialogBuilder.cancelDialog(dialog);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response && response.isSuccessful()) {
                            jobs.clear();
                            jobs.addAll(response.body().response);
                            processJobs();
                        } else HandleErrors.parseError(getContext(), dialog, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MatchesResponse> call, Throwable t) {
                HandleErrors.parseFailureError(getContext(), dialog, t);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_worker_matches_map, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.worker_list:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, JobMatchesFragment.newInstance())
                        .commit();
                break;
        }
        return true;
    }

    public void onMapReady(GoogleMap googleMap) {
        TextTools.log(TAG, "on map ready");
        map = googleMap;
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                processOnWindowClick(marker);
            }
        });
        map.setOnMarkerClickListener(this);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            TextTools.log(TAG, "connecting the google api client");
        }
        fetchJobMatches();
    }

    private void processOnWindowClick(final Marker marker) {
        CharSequence[] options = {getString(R.string.worker_job_like),
                getString(R.string.worker_job_unlike),
                getString(R.string.worker_job_open_details)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        likeJobConnector.likeJob(getContext(), Integer.valueOf(marker.getTitle()));
                        break;
                    case 1:
                        likeJobConnector.unlikeJob(getContext(), Integer.valueOf(marker.getTitle()));
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                        intent.putExtra(JobDetailActivity.JOB_ARG, (int) Integer.valueOf(marker.getTitle()));
                        getActivity().startActivity(intent);
                        break;
                }
            }
        });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, JobMatchesFragment.newInstance())
                    .commit();
        }
    }

    public void onPause() {
        destroyDialog();
        mapView.onPause();
        super.onPause();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    private void processJobs() {
        if (!CollectionUtils.isEmpty(jobs)) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (map != null) {
                    try {
                        map.clear();
                        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(jobs, getActivity()));

                        for (Job job : jobs) {
                            if (job == null || job.location == null) continue;

                            map.addMarker(new MarkerOptions()
                                    .title(String.valueOf(job.id))
                                    .position(new LatLng(Double.valueOf(job.location.latitude),
                                            Double.valueOf(job.location.longitude)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_active)));

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(Double.valueOf(job.location.latitude),
                                            Double.valueOf(job.location.longitude)), 12));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(getContext()).setMessage("Something went wrong").show();
                    }
                }
            }
        } else showNoMatchesDialog();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // test2();
        // marker.showInfoWindow();

        return false;
    }

    @Override
    public void onConnectorSuccess() {
        fetchJobMatches();
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private List<Job> jobs = new ArrayList<>();
        private Context context;

        public CustomInfoWindowAdapter(List<Job> jobs, Context context) {
            this.jobs = jobs;
            this.context = context;
        }

        public View getInfoContents(Marker marker) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_marker_job, null);
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    width / 20 * 18,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            view.setLayoutParams(params);
            for (Job job : jobs) {
                if (marker.getTitle().equals(String.valueOf(job.id))) {
                    // populate info window here
                    TextView name = (TextView) view.findViewById(R.id.company_name);
                    ImageView logo = (ImageView) view.findViewById(R.id.logo);
                    if (null != job.owner) {
                        if (null != job.owner.picture) {
                            logo.setVisibility(View.VISIBLE);
                            name.setVisibility(View.GONE);
                            Picasso.with(getContext())
                                    .load(job.owner.picture)
                                    .fit()
                                    .into(logo);
                        } else {
                            logo.setVisibility(View.GONE);
                            name.setVisibility(View.VISIBLE);
                        }
                    }

                    if (null != job.company) {
                        if (null != job.company.name) {
                            name.setText(job.company.name);
                        }
                    }


                    if (null != job.role) {
                        if (null != job.role.name) {
                            ((JosefinSansTextView) view.findViewById(R.id.role)).setText(job.role.name);
                        }
                    }
                    ((JosefinSansTextView) view.findViewById(R.id.experience))
                            .setText(String.format(context.getString(R.string.item_match_format_experience),
                                    job.experience, context.getResources().getQuantityString(R.plurals.year_plural, job.experience)));

                    if (!TextUtils.isEmpty(job.startTime)) {
                        ((JosefinSansTextView) view.findViewById(R.id.start_date)).setText(String.format(getString(R.string.item_match_format_starts),
                                DateUtils.formatDateDayAndMonth(job.startTime, true)));
                    }

                    if (null != job.locationName) {
                        ((JosefinSansTextView) view.findViewById(R.id.location))
                                .setText(job.locationName);
                    }
                    if (null != job.budgetType) {
                        if (null != job.budgetType.name) {
                            ((JosefinSansTextView) view.findViewById(R.id.period))
                                    .setText("PER " + job.budgetType.name);
                        }
                    }
                    ((JosefinSansTextView) view.findViewById(R.id.salary))
                            .setText(String.valueOf("£" +
                                    String.valueOf(NumberFormat
                                            .getInstance(Locale.UK).format(Double.valueOf(job.budget)))));
                    ((JosefinSansTextView) view.findViewById(R.id.job_id))
                            .setText(String.valueOf(job.jobRef));
                    ((ImageView) view.findViewById(R.id.likeImage))
                            .setImageResource(job.liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
                }
            }
            return view;
        }

        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}