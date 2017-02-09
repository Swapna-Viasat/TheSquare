package com.hellobaytree.graftrs.worker.jobmatches.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JobMatchesMapFragment extends Fragment implements OnMapReadyCallback,
        ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener,
        OnConnectionFailedListener {

    @BindView(R.id.map_view)
    MapView mapView;

    public static final String TAG = "JobMatchesMap";

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    public static JobMatchesMapFragment newInstance(Bundle bundle) {
        JobMatchesMapFragment fragment = new JobMatchesMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void onStart() {
        super.onStart();
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
            case R.id.worker_tune:
                break;
        }
        return true;
    }

    public void onMapReady(GoogleMap googleMap) {
        TextTools.log(TAG, "on map ready");
        map = googleMap;
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter((ArrayList) getArguments().getSerializable("data"), getActivity()));
        map.setOnMarkerClickListener(this);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            TextTools.log(TAG, "connecting the google api client");
        }
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
        super.onPause();
        mapView.onPause();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @SuppressWarnings("unchecked")
    public void onConnected(Bundle connectionHint) {
        TextTools.log(TAG, "on google api client connected");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            List<Job> jobs = (ArrayList<Job>) getArguments().getSerializable("data");

            if (map != null && jobs != null) {
                try {
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
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        // test2();
        // marker.showInfoWindow();

        return false;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: handle a failed connection
    }

    public void onConnectionSuspended(int info) {
        //
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

                    Picasso.with(getContext())
                            .load(job.company.logo)
                            .into((ImageView) view.findViewById(R.id.logo));


                    ((JosefinSansTextView) view.findViewById(R.id.role)).setText(job.role.name);
                    ((JosefinSansTextView) view.findViewById(R.id.experience))
                            .setText(String.format(context.getString(R.string.item_match_format_experience),
                                    job.experience, context.getResources().getQuantityString(R.plurals.year_plural, job.experience)));
                    ((JosefinSansTextView) view.findViewById(R.id.start_date))
                            .setText("Starts 12th Jan");
                    ((JosefinSansTextView) view.findViewById(R.id.location))
                            .setText(job.address);
                    ((JosefinSansTextView) view.findViewById(R.id.period))
                            .setText(job.budgetType.name);
                    ((JosefinSansTextView) view.findViewById(R.id.salary))
                            .setText(String.valueOf(job.budget));
                }
            }
            return view;
        }

        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}