package com.hellobaytree.graftrs.worker.myaccount.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.Company;
import com.hellobaytree.graftrs.shared.models.Language;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Skill;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.CollectionUtils;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.MediaTools;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.hellobaytree.graftrs.worker.myaccount.ui.dialog.EditAccountDetailsDialog;
import com.hellobaytree.graftrs.worker.myaccount.ui.dialog.EditCscsDetailsDialog;
import com.hellobaytree.graftrs.worker.onboarding.SingleEditActivity;
import com.hellobaytree.graftrs.worker.signup.model.CSCSCardWorker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maizaga on 30/10/16.
 */

public class MyAccountViewProfileFragment extends Fragment implements EditAccountDetailsDialog.InputFinishedListener,
        EditCscsDetailsDialog.OnCscsDetailsUpdatedListener, View.OnClickListener {

    private static final int REQUEST_EDIT_PROFILE = 100;

    @BindView(R.id.worker_view_profile_edit)
    ImageView editWorker;

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

    @BindViews({R.id.worker_profile_bio_edit,
            R.id.worker_profile_experience_edit,
            R.id.worker_profile_skills_edit,
            R.id.worker_profile_companies_edit,
            R.id.worker_profile_location_edit,
            R.id.worker_profile_requirements_edit,
            R.id.worker_profile_nationality_edit,
            R.id.worker_profile_birthday_edit,
            R.id.worker_profile_languages_edit,
            R.id.worker_profile_nis_edit,
            R.id.worker_profile_passport_edit
    })
    List<ImageView> editList;

    @BindViews({R.id.cscs1, R.id.cscs2, R.id.cscs3, R.id.cscs4, R.id.cscs5, R.id.cscs6, R.id.cscs7, R.id.cscs8})
    List<TextView> cscsNumbers;

    @BindView(R.id.workerImage)
    ImageView cscsImage;

    @BindView(R.id.cscs_status)
    TextView cscsStatus;

    @BindView(R.id.cscsContent)
    View cscsContent;

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

    @BindView(R.id.worker_profile_nationality_value)
    TextView nationalityView;

    @BindView(R.id.worker_profile_birthday_value)
    TextView dateOfBirthView;

    @BindView(R.id.worker_profile_languages_value)
    TextView languagesView;

    @BindView(R.id.worker_profile_nis_value)
    TextView nisView;

    @BindView(R.id.worker_profile_passport_value)
    ImageView passportImage;

    @BindView(R.id.cscs_expires_value)
    TextView cscsExpirationView;

    @BindView(R.id.cscsRecordsLayout)
    LinearLayout cscsRecordsLayout;

    //nationality, date of birth, languages spoken, nis, photo of passport
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;
    private static final int REQUEST_PERMISSIONS = 3;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private static final int VERIFICATION_NONE = 1;     // Verification hasn't been requested yet.
    private static final int VERIFICATION_FAILED = 2;   // Infrastructural issues: cannot verify cards (e.g. failed to connect to citb website).
    private static final int VERIFICATION_INVALID = 3;  // Supplied card details have been confirmed as invalid.
    private static final int VERIFICATION_VALID = 4;    // Supplied card details are valid.

    private Worker worker;
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private EditAccountDetailsDialog editAccountDetailsDialog;
    private String tempBio;
    private int experienceYears;

    public static MyAccountViewProfileFragment newInstance() {
        return new MyAccountViewProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_worker_edit_profile, container, false);
        ButterKnife.bind(this, v);
        ButterKnife.apply(editList, new ButterKnife.Action<ImageView>() {
            @Override
            public void apply(@NonNull ImageView view, int index) {
                view.setVisibility(View.VISIBLE);
            }
        });

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
            experienceYears = worker.yearsExperience;

            fillWorkerImage();
            fillWorkerName();
            fillWorkerPosition();
            fillExperienceAndQualifications();
            fillSkills();
            fillCompanies();
            fillWorkerBio();
            fillLocationName();
            initMap();
            if (worker.nationality != null)
                nationalityView.setText(worker.nationality.name);
            dateOfBirthView.setText(DateUtils.getParsedBirthDate(worker.dateOfBirth));
            nisView.setText(worker.niNumber);

            if (worker.passportUpload != null)
                Picasso.with(getContext())
                        .load(worker.passportUpload)
                        .fit().centerCrop().into(passportImage);

            if (!CollectionUtils.isEmpty(worker.languages)) {
                List<String> languageNames = new ArrayList<>();
                for (Language l : worker.languages) languageNames.add(l.name);
                languagesView.setText(TextTools.toBulletList(languageNames, true));
            }
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

    private void editProfile(final int page) {
        startActivityForResult(SingleEditActivity.startIntent(getActivity(), worker, page), REQUEST_EDIT_PROFILE);
    }

    private void fetchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            worker = response.body().getResponse();
                            if (worker != null) fetchCscsDetails(worker.id);
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

    private void fetchCscsDetails(int currentWorkerId) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerCSCSCard(currentWorkerId)
                .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                           Response<ResponseObject<CSCSCardWorker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateCscs(response.body());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<CSCSCardWorker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void populateCscs(ResponseObject<CSCSCardWorker> dataResponse) {
        if (dataResponse != null) {

            String regnum = dataResponse.getResponse().registrationNumber;
            populateCscsStatus(dataResponse.getResponse().verificationStatus);
            if (dataResponse.getResponse().verificationStatus == 4 && !regnum.isEmpty()) {
                final char ca[] = regnum.toCharArray();
                ButterKnife.Setter<TextView, Boolean> ENABLED = new ButterKnife.Setter<TextView, Boolean>() {
                    @Override
                    public void set(TextView view, Boolean value, int index) {

                        switch (view.getId()) {
                            case R.id.cscs1:
                                cscsNumbers.get(0).setText(Character.toString(ca[0]));
                            case R.id.cscs2:
                                cscsNumbers.get(1).setText(Character.toString(ca[1]));
                            case R.id.cscs3:
                                cscsNumbers.get(2).setText(Character.toString(ca[2]));
                            case R.id.cscs4:
                                cscsNumbers.get(3).setText(Character.toString(ca[3]));
                            case R.id.cscs5:
                                cscsNumbers.get(4).setText(Character.toString(ca[4]));
                            case R.id.cscs6:
                                cscsNumbers.get(5).setText(Character.toString(ca[5]));
                            case R.id.cscs7:
                                cscsNumbers.get(6).setText(Character.toString(ca[6]));
                            case R.id.cscs8:
                                cscsNumbers.get(7).setText(Character.toString(ca[7]));
                        }
                    }
                };
                ButterKnife.apply(cscsNumbers, ENABLED, true);
            }

            if (dataResponse.getResponse().cardPicture != null)
                Picasso.with(getContext())
                        .load(HttpRestServiceConsumer.getApiRoot() + dataResponse.getResponse().cardPicture)
                        .fit().centerCrop().into(cscsImage);

            cscsExpirationView.setText(DateUtils.getCscsExpirationDate(dataResponse.getResponse().expiryDate));

            try {
                if (!CollectionUtils.isEmpty(dataResponse.getResponse().cscsRecords)) {
                    for (List<CSCSCardWorker.CscsRecord> cscsRecordList : dataResponse.getResponse().cscsRecords.values())
                        if (!CollectionUtils.isEmpty(cscsRecordList)) {
                            for (CSCSCardWorker.CscsRecord record : cscsRecordList) {
                                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_cscs_record, null, false);
                                TextView cscsText = (TextView) itemView.findViewById(R.id.recordText);
                                cscsText.setText(record.name + " - " + record.category.name);
                                cscsRecordsLayout.addView(itemView);
                            }
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateCscsStatus(int status) {
        if (status == VERIFICATION_VALID) {
            cscsStatus.setText(R.string.worker_cscs_verified);
            cscsContent.setVisibility(View.VISIBLE);
            cscsStatus.setOnClickListener(null);
        } else {
            cscsStatus.setText(getString(R.string.worker_cscs_not_verified));
            cscsContent.setVisibility(View.GONE);
            cscsStatus.setOnClickListener(this);
        }
    }

    @Override
    public void onDone(String string, boolean onlyDigits) {
        try {
            if (!string.isEmpty()) {
                if (onlyDigits) {
                    experienceYears = Integer.parseInt(string.replaceAll("\\D", ""));
                } else {
                    tempBio = string;
                    bioView.setText(string);
                }
                patchWorker();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, Object> params = new HashMap<>();
        params.put("bio", tempBio);
        params.put("years_experience", experienceYears);
        if (worker != null) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .patchWorker(worker.id, params)
                    .enqueue(new Callback<ResponseObject<Worker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Worker>> call, Response<ResponseObject<Worker>> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                worker = response.body().getResponse();
                                initComponents();
                                if (worker != null) fetchCscsDetails(worker.id);
                            } else HandleErrors.parseError(getContext(), dialog, response);
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        }
    }

    @OnClick(R.id.worker_view_profile_edit)
    void profileEdit() {
        editProfile(Constants.KEY_ONBOARDING_DETAILS);
    }

    @OnClick(R.id.worker_profile_bio_edit)
    void bioEdit() {
        editAccountDetailsDialog = EditAccountDetailsDialog
                .newInstance("Bio",
                        (worker != null && !TextUtils.isEmpty(worker.bio)) ? worker.bio : "",
                        false,
                        this);
        editAccountDetailsDialog.setCancelable(false);
        editAccountDetailsDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_experience_edit)
    void qualificationsEdit() {
        editProfile(Constants.KEY_ONBOARDING_QUALIFICATIONS);
    }

    @OnClick(R.id.worker_profile_skills_edit)
    void skillsEdit() {
        editProfile(Constants.KEY_ONBOARDING_SKILLS);
    }

    @OnClick(R.id.worker_profile_companies_edit)
    void companiesEdit() {
        editProfile(Constants.KEY_ONBOARDING_COMPANIES);
    }

    @OnClick(R.id.worker_profile_location_edit)
    void locationEdit() {
        editProfile(Constants.KEY_ONBOARDING_LOCATION);
    }

    @OnClick(R.id.worker_view_profile_position)
    void editRole() {
        editProfile(Constants.KEY_ONBOARDING_ROLE);
    }

    @OnClick(R.id.worker_view_profile_experience)
    void editExperience() {
        editAccountDetailsDialog = EditAccountDetailsDialog.newInstance("Years of experience",
                String.valueOf(worker.yearsExperience), true, this);
        editAccountDetailsDialog.setCancelable(false);
        editAccountDetailsDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_requirements_edit)
    void editRequirements() {
        editProfile(Constants.KEY_STEP_REQUIREMENTS);
    }

    @OnClick({R.id.worker_profile_nationality_edit,
            R.id.worker_profile_birthday_edit,
            R.id.worker_profile_languages_edit,
            R.id.worker_profile_nis_edit,
            R.id.worker_profile_passport_edit})
    void openExperienceFragment() {
        editProfile(Constants.KEY_ONBOARDING_EXPERIENCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatarImage.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
            avatarImage.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            case REQUEST_PERMISSION_READ_STORAGE:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchOpenGalleryIntent();
                }
                break;
        }
    }

    @OnClick(R.id.worker_view_profile_avatar)
    void showChooserDialog() {
        CharSequence[] options = {getString(R.string.onboarding_take_photo),
                getString(R.string.onboarding_choose_from_gallery),
                getString(R.string.onboarding_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.onboarding_add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        dialog.cancel();
                        break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchOpenGalleryIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                REQUEST_IMAGE_SELECTION);
    }

    private void prepPicture(Context context, Bitmap bitmap) {
        try {
            File file = new File(getContext().getCacheDir(),
                    "temp" + String.valueOf(bitmap.hashCode()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            baos.flush();
            baos.close();

            uploadPicture(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPicture(Context context, File file) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), request);
        HttpRestServiceConsumer.getBaseApiClient()
                .uploadProfileImageWorker(SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), body)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            worker = response.body().getResponse();
                            Picasso.with(getContext())
                                    .load(worker.picture)
                                    .error(R.drawable.ic_no_avatar)
                                    .placeholder(R.drawable.ic_no_avatar)
                                    .into(avatarImage);
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

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

    @Override
    public void onCscsUpdated(int status) {
        switch (status) {
            case VERIFICATION_VALID:
                if (worker != null) fetchCscsDetails(worker.id);
                break;
            default:
                DialogBuilder.showStandardDialog(getContext(), "", getString(R.string.verified_cscs_failed));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cscs_status)
            EditCscsDetailsDialog.newInstance(worker.lastName, this).show(getActivity().getSupportFragmentManager(), "");
    }
}
