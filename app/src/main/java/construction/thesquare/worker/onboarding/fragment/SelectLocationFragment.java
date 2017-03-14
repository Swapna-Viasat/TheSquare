package construction.thesquare.worker.onboarding.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.ZipCodeVerifier;
import construction.thesquare.shared.data.model.Location;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.ZipResponse;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.CommuteTimeSeekBar;
import construction.thesquare.worker.onboarding.OnGeoCodingFinishedListener;
import construction.thesquare.worker.onboarding.ReverseGeocodeRunnable;
import construction.thesquare.worker.onboarding.adapter.PlacesAutocompleteAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectLocationFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlacesAutocompleteAdapter.PlacesListener, OnGeoCodingFinishedListener {

    public static final String TAG = "SelectLocationFragment";
    private int workerId;
    private Worker currentWorker;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location centerMapLocation;
    private boolean mapInitialized;
    private View rootView;
    private static final double londonLat = 51.5074;
    private static final double londonLong = 0.1278;

    @BindView(R.id.filter)
    TextView filter;
    @BindView(R.id.seek_commute)
    CommuteTimeSeekBar seekCommute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentWorker = (Worker) getArguments().getSerializable(Constants.KEY_CURRENT_WORKER);
    }

    public static SelectLocationFragment newInstance(boolean singleEdition, Worker worker) {
        SelectLocationFragment selectLocationFragment = new SelectLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        bundle.putSerializable(Constants.KEY_CURRENT_WORKER, worker);
        selectLocationFragment.setArguments(bundle);
        return selectLocationFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_worker_select_location, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        centerMapLocation = new construction.thesquare.shared.data.model.Location();

        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
        } else {
            requestPermissions(new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }

        filter.setOnClickListener(listener);
    }

    @Override
    public void onPlace(String placeId, String name, Dialog dialog) {
        if (null != dialog) dialog.dismiss();
        filter.setText(name);
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(googleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
    }

    @OnClick(R.id.next)
    public void next() {
        patchWorker();
    }

    private void proceed() {
        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectRoleFragment
                        .newInstance(false, currentWorker))
                .addToBackStack("")
                .commit();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> location = new HashMap<>();
        location.put("latitude", centerMapLocation.getLatitude());
        location.put("longitude", centerMapLocation.getLongitude());

        HashMap<String, Object> request = new HashMap<>();
        request.put("address", filter.getText().toString());
        request.put("location", location);
        request.put("commute_time", seekCommute.getRate());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            proceed();
                        } else HandleErrors.parseError(getContext(), dialog, response);
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (null != mapFragment) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;

                    moveToInitialLocation();

                    centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
                    centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;

                    populateData();
                }
            });
        }
    }

    private GoogleMap.OnCameraIdleListener cameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
            centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;

            startReverseGeoCoding(googleMap.getCameraPosition().target);

            TextTools.log(TAG, String.valueOf(googleMap.getCameraPosition().target.toString()));
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // TODO: add a default location fallback
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: add a default location fallback
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.autocomplete);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    getResources().getDisplayMetrics().heightPixels * 8 / 12);
            ((TextView) dialog.findViewById(R.id.autocomplete_title))
                    .setText(getString(R.string.create_job_address));

            final PlacesAutocompleteAdapter adapter =
                    new PlacesAutocompleteAdapter(getActivity(), googleApiClient);
            adapter.setListener(SelectLocationFragment.this, dialog);


            ListView listView = (ListView) dialog.findViewById(R.id.autocomplete_rv);
            listView.setAdapter(adapter);

            final EditText search = (EditText) dialog.findViewById(R.id.autocomplete_search);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            search.setText(filter.getText().toString());
            dialog.findViewById(R.id.autocomple_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.autocomple_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter.setText(search.getText().toString());
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess() || places.getCount() == 0) {
                TextTools.log(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }

            Place place = places.get(0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13f));
            places.release();
        }
    };

    private void moveToInitialLocation() {
        LatLng london = new LatLng(londonLat, londonLong);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 13f));
        if (currentWorker != null) centerOnPostalCode(currentWorker.zip);
    }

    private void centerOnPostalCode(String code) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        ZipCodeVerifier.getInstance()
                .api()
                .verify(code, ZipCodeVerifier.API_KEY)
                .enqueue(new Callback<ZipResponse>() {
                    @Override
                    public void onResponse(Call<ZipResponse> call, Response<ZipResponse> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.body().message == null) {

                                googleMap.setOnCameraIdleListener(null);
                                if (currentWorker != null) filter.setText(currentWorker.zip);
                                LatLng latLng = new LatLng(response.body().lat, response.body().lang);

                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                                googleMap.setOnCameraIdleListener(cameraIdleListener);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ZipResponse> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void startReverseGeoCoding(LatLng target) {
        new Thread(new ReverseGeocodeRunnable(getContext(), target, this)).start();
    }

    @Override
    public void onReverseGeoCodingFinished(final Address address) {
        if (address != null) {

            int maxLine = address.getMaxAddressLineIndex();
            final StringBuilder addressLabel = new StringBuilder();
            for (int i = 0; i <= maxLine; i++) {
                addressLabel.append(address.getAddressLine(i)).append(i < maxLine ? ", " : "");
            }

            if (mapInitialized)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filter.setText(addressLabel);
                    }
                });
            else mapInitialized = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        googleApiClient.connect();
    }

    private void populateData() {
        if (currentWorker != null) {
            if (!TextUtils.isEmpty(currentWorker.address)) filter.setText(currentWorker.address);

            if (currentWorker.location != null) {
                googleMap.setOnCameraIdleListener(null);
                LatLng latLng = new LatLng(currentWorker.location.getLatitude(),
                        currentWorker.location.getLongitude());

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

                centerMapLocation = currentWorker.location;
                seekCommute.setRate(currentWorker.commuteTime);
            }
        }
    }

    @Override
    public void onPause() {
        persistProgress();
        googleApiClient.disconnect();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private void persistProgress() {
        if (currentWorker != null) {
            currentWorker.address = filter.getText().toString();
            currentWorker.location = new construction.thesquare.shared.data.model.Location();
            currentWorker.location.latitude = centerMapLocation.getLatitude();
            currentWorker.location.longitude = centerMapLocation.getLongitude();
            currentWorker.commuteTime = seekCommute.getRate();
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
