package com.hellobaytree.graftrs.worker.onboarding.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.adapter.ExperienceAdapter;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.EnglishLevel;
import com.hellobaytree.graftrs.shared.models.ExperienceQualification;
import com.hellobaytree.graftrs.shared.models.Language;
import com.hellobaytree.graftrs.shared.models.Nationality;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.CollectionUtils;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.MediaTools;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansEditText;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.onboarding.OnLanguagesSelectedListener;
import com.hellobaytree.graftrs.worker.onboarding.adapter.FluencyAdapter;
import com.hellobaytree.graftrs.worker.signup.model.CSCSCardWorker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectExperienceFragment extends Fragment
        implements FluencyAdapter.FluencyListener,
        ExperienceAdapter.ExperienceListener, OnLanguagesSelectedListener {

    public static final String TAG = "SelectExperienceFragment";
    private int workerId;
    private int english;
    private int experience;
    private int id;
    private int cscsStatus;
    private String lastname = null;
    private String selectLangs = null;
    private List<String> languageId = new ArrayList<>();
    private List<String> langIds = new ArrayList<>();
    @BindView(R.id.years)
    JosefinSansTextView years;
    @BindView(R.id.seek)
    SeekBar seekBar;
    @BindView(R.id.english)
    RecyclerView fluency;
    @BindView(R.id.others)
    RecyclerView others;
    @BindView(R.id.top)
    JosefinSansTextView top;
    private EditText current;
    private EditText next;
    @BindView(R.id.spinner_nationality)
    Spinner nationality;
    @BindView(R.id.spinner_day)
    Spinner day;
    @BindView(R.id.spinner_month)
    Spinner month;
    @BindView(R.id.spinner_year)
    Spinner year;
    @BindView(R.id.cscs_details)
    LinearLayout cscs;
    @BindView(R.id.openDialog)
    View openDialog;
    @BindView(R.id.lang)
    TextView lang;
    @BindView(R.id.surname)
    JosefinSansEditText surname;
    @BindViews({R.id.reg_01, R.id.reg_02, R.id.reg_03, R.id.reg_04, R.id.reg_05,
            R.id.reg_06, R.id.reg_07, R.id.reg_08})
    List<JosefinSansEditText> reg;
    @BindViews({R.id.nis_01, R.id.nis_02, R.id.nis_03, R.id.nis_04, R.id.nis_05,
            R.id.nis_06, R.id.nis_07, R.id.nis_08, R.id.nis_09})
    List<JosefinSansEditText> nis;
    @BindView(R.id.verify_cscs)
    Button verify;
    @BindView(R.id.error_message)
    JosefinSansTextView cscsErrorMsg;
    @BindView(R.id.passport_photo)
    ImageView passport_photo;
    @BindView(R.id.maximize)
    ImageView maximize;

    private ArrayAdapter<CharSequence> monthAdapter;
    private ArrayAdapter<CharSequence> dayAdapter;
    private ArrayAdapter<CharSequence> yearAdapter;
    private ArrayAdapter nationalityAdapter;
    private ArrayAdapter languagesAdapter;
    private FluencyAdapter fluencyAdapter;
    private List<EnglishLevel> levels = new ArrayList<>();
    private ExperienceAdapter experienceAdapter;
    private List<ExperienceQualification> qualifications = new ArrayList<>();
    private Worker currentWorker;
    private List<ExperienceQualification> selected = new ArrayList<>();
    private Map<String, Integer> countryIds = new HashMap<String, Integer>();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECTION = 2;
    static final int REQUEST_PERMISSIONS = 3;
    static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    public static SelectExperienceFragment newInstance(boolean singleEdition) {
        SelectExperienceFragment selectExperienceFragment = new SelectExperienceFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectExperienceFragment.setArguments(bundle);
        return selectExperienceFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_experience, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        top.setText(getString(R.string.onboarding_experience));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                years.setText(String.valueOf(i)
                        + ((seekBar.getMax() == i) ? "+ " : " ")
                        + getResources().getQuantityString(R.plurals.year_plural, i));
                experience = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        current = reg.get(0);
        reg.get(0).requestFocus();
        for (EditText e : reg) {
            e.addTextChangedListener(regListener);
        }
        for (EditText e : nis) {
            e.addTextChangedListener(nisListener);
        }
        // Create an ArrayAdapter using the string array and a default spinner
        dayAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.spinner_day,
                        android.R.layout.simple_spinner_item);
        dayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dayAdapter);

        monthAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.spinner_month,
                        android.R.layout.simple_spinner_item);
        monthAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(monthAdapter);

        yearAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.spinner_year,
                        android.R.layout.simple_spinner_item);
        yearAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearAdapter);

        if (null != getArguments().getSerializable(Constants.KEY_CURRENT_WORKER)) {
            currentWorker = (Worker) getArguments().getSerializable(Constants.KEY_CURRENT_WORKER);
            showPassportImage();
        }

        fetchEnglishLevels();
        fetchQualifications();
        //fetchCscsDetails(workerId);
        fetchCurrentWorker();
        fetchNationality();
        fetchLanguage();
    }

    private void fetchEnglishLevels() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchEnglishLevels()
                .enqueue(new Callback<ResponseObject<List<EnglishLevel>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<EnglishLevel>>> call,
                                           Response<ResponseObject<List<EnglishLevel>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processEnglishLevels(response.body().getResponse());
                            populateSavedEnglishLevel();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<EnglishLevel>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchQualifications() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchExperienceQualifications()
                .enqueue(new Callback<ResponseObject<List<ExperienceQualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ExperienceQualification>>> call,
                                           Response<ResponseObject<List<ExperienceQualification>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processQualifications(response.body().getResponse());
                            populateSavedRequirements();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<ExperienceQualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchCscsDetails(int currentWorker) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerCSCSCard(currentWorker)
                .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                           Response<ResponseObject<CSCSCardWorker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populate(response.body());
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

    private void fetchLanguage() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchLanguage()
                .enqueue(new Callback<ResponseObject<List<Language>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Language>>> call,
                                           Response<ResponseObject<List<Language>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            showMultiSelectDialog(response.body().getResponse());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Language>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchNationality() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchNationality()
                .enqueue(new Callback<ResponseObject<List<Nationality>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Nationality>>> call,
                                           Response<ResponseObject<List<Nationality>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processNationality(response.body().getResponse());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Nationality>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void fetchCurrentWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                id = response.body().getResponse().id;
                                lastname = response.body().getResponse().lastName;
                                populateDetails(response.body().getResponse());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void populateDetails(Worker worker) {
        try {
            currentWorker = worker;
            if (!worker.passportUpload.isEmpty()) {
                Picasso.with(getContext()).load(worker.passportUpload).into(passport_photo);
            } else {
                passport_photo.setImageResource(R.drawable.passport);
            }
            if (worker.nationalityId > 0) {
                nationality.setSelection(worker.nationalityId.intValue());
            }else{
                nationality.setSelection(0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processEnglishLevels(List<EnglishLevel> englishLevels) {
        if (englishLevels != null) {
            try {
                levels.clear();
                levels.addAll(englishLevels);
                fluencyAdapter = new FluencyAdapter(levels);
                fluencyAdapter.setListener(this);
                fluency.setLayoutManager(new LinearLayoutManager(getContext()));
                fluency.setAdapter(fluencyAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processQualifications(List<ExperienceQualification> fetchedQualifications) {
        try {
            qualifications.clear();
            qualifications.addAll(fetchedQualifications);
            experienceAdapter = new ExperienceAdapter(qualifications);
            experienceAdapter.setListener(this);
            others.setLayoutManager(new LinearLayoutManager(getContext()));
            others.setAdapter(experienceAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate(ResponseObject<CSCSCardWorker> dataResponse) {
        surname.setText(lastname);
        surname.setEnabled(false);
        String regnum = dataResponse.getResponse().getRegistration_number();
        populateCscsStatus(dataResponse.getResponse().getVerification_status());
       if(!regnum.isEmpty()) {
           final char ca[] = regnum.toCharArray();
           ButterKnife.Setter<JosefinSansEditText, Boolean> ENABLED = new ButterKnife.Setter<JosefinSansEditText, Boolean>() {
            @Override
            public void set(JosefinSansEditText view, Boolean value, int index) {

                switch (view.getId()) {
                    case R.id.reg_01:
                        reg.get(0).setText(Character.toString(ca[0]));
                    case R.id.reg_02:
                        reg.get(1).setText(Character.toString(ca[1]));
                    case R.id.reg_03:
                        reg.get(2).setText(Character.toString(ca[2]));
                    case R.id.reg_04:
                        reg.get(3).setText(Character.toString(ca[3]));
                    case R.id.reg_05:
                        reg.get(4).setText(Character.toString(ca[4]));
                    case R.id.reg_06:
                        reg.get(5).setText(Character.toString(ca[5]));
                    case R.id.reg_07:
                        reg.get(6).setText(Character.toString(ca[6]));
                    case R.id.reg_08:
                        reg.get(7).setText(Character.toString(ca[7]));
                }
            }
        };
            ButterKnife.apply(reg, ENABLED, true);
        }
    }

    private void processNationality(List<Nationality> nationalityList) {
        if (nationalityList != null) {
            List<String> countrynames = new ArrayList<String>();
            try {
                countrynames.add("Please select");
                for (Nationality country : nationalityList) {
                    countrynames.add(country.name);
                    countryIds.put(country.name, country.id);
                }
                nationalityAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, countrynames);
                nationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                nationality.setAdapter(nationalityAdapter);
                nationality.setSelection(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.next, R.id.verify_cscs, R.id.passport_photo, R.id.maximize})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                selected.clear();
                for (ExperienceQualification exp : qualifications) {
                    if (exp.selected) {
                        selected.add(exp);
                    }
                }
                if (english > 0) {
                        patchWorker();
                    break;
                } else
                    DialogBuilder.showStandardDialog(getContext(), "",
                            getString(R.string.onboarding_english_level_error));
                break;
            case R.id.verify_cscs:
                verify();
                break;
            case R.id.passport_photo:
                showChooserDialog();
                break;
            case R.id.maximize:
                showOrginalImage();
                break;
        }
    }

    private void showChooserDialog() {
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
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
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
            file.createNewFile();
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
                MultipartBody.Part.createFormData("passport_upload", file.getName(), request);
        HttpRestServiceConsumer.getBaseApiClient()
                .uploadProfileImageWorker(
                        SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), body)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            currentWorker = response.body().getResponse();
                            showPassportImage();
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

    private void showPassportImage() {
        if (currentWorker != null && currentWorker.passportUpload != null) {
            Picasso.with(getContext())
                    .load(currentWorker.passportUpload)
                    .fit()
                    .centerCrop()
                    .into(passport_photo);
        }
    }
    private void showOrginalImage() {
        LayoutInflater layoutInflater
                = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final Dialog settingsDialog = new Dialog(getContext());
        if (currentWorker != null && currentWorker.passportUpload != null) {
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(layoutInflater.inflate(R.layout.popup_passport_image
                    , null));
            ImageButton close = (ImageButton) settingsDialog.findViewById(R.id.passport_preview_close);
            close.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    settingsDialog.dismiss();
                }
            });
            ImageView iv = (ImageView) settingsDialog.findViewById(R.id.original_image);
            Picasso.with(getContext()).load(currentWorker.passportUpload).into(iv);
            //settingsDialog.getWindow().setLayout(700, 700);
            settingsDialog.show();
        }else {
            DialogBuilder.showStandardDialog(getContext(), "",
                    getString(R.string.passport_nophoto));
        }
    }

    public void verify() {
        try {

            HashMap<String, Object> request = new HashMap<>();
            TextTools.log("lastname", lastname);
            request.put("surname", lastname);
            request.put("registration_number", getReg());
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .persistOnboardingWorkerCSCSCard(id, request)
                    .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                               Response<ResponseObject<CSCSCardWorker>> response) {
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                                cscsStatus = response.body().getResponse().getVerification_status();
                                populateCscsStatus(cscsStatus);
                            } else {
                                HandleErrors.parseError(getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<CSCSCardWorker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateCscsStatus(int status){
        if(status == 4){
            verify.setText(R.string.verified_cscs_success);
            cscsErrorMsg.setText("");
            verify.setEnabled(false);
        }
        else if(status == 2){
            verify.setText(R.string.verified_cscs_failed);
            cscsErrorMsg.setText(R.string.cscs_status_infrastructure_issue);
            cscsErrorMsg.setLineSpacing(1,1.5f);
        } else if(status == 3){
            verify.setText(R.string.verified_cscs_failed);
            cscsErrorMsg.setText(R.string.cscs_status_carddetails_invalid);
            cscsErrorMsg.setLineSpacing(1,1.5f);
        }
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            body[i] = selected.get(i).id;
        }

        HashMap<String, Object> request = new HashMap<>();
        request.put("qualifications_ids", body);
        request.put("update_filtered", "requirements");
        request.put("english_level_id", english);
        request.put("years_experience", experience);
        request.put("ni_number", getNIS());
        request.put("date_of_birth", getDateOfBirth());
        request.put("nationality_id", countryIds.get(nationality.getSelectedItem()));
        request.put("languages_ids", langIds);

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                             proceed();
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

    private void proceed() {
        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectQualificationsFragment.newInstance(false))
                .addToBackStack("")
                .commit();
    }

    @Override
    public void onFluency(EnglishLevel level) {
        for (EnglishLevel e : levels) {
            if (e.id != level.id) {
                e.selected = false;
            }
        }
        english = level.id;
        level.selected = true;
        fluencyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExperience(ExperienceQualification experience) {
        //
        if (experience.name.equals("CSCS Card")) {
            experience.selected = !experience.selected;
            experienceAdapter.notifyDataSetChanged();
            if (!experience.selected) {
                //
                cscs.setVisibility(View.GONE);
            } else {
                fetchCscsDetails(workerId);
               if(cscsStatus > 0)
                populateCscsStatus(cscsStatus);
                cscs.setVisibility(View.VISIBLE);
            }
        } else {
            experience.selected = !experience.selected;
            experienceAdapter.notifyDataSetChanged();
        }
    }

    private String getNIS() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!nis.isEmpty()) {
            for (EditText e : nis) {
                stringBuilder.append(e.getText().toString());
            }
        }
        return stringBuilder.toString();
    }

    private String getReg() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!reg.isEmpty()) {
            for (EditText e : reg) {
                stringBuilder.append(e.getText().toString());
            }
        }
        return stringBuilder.toString();
    }

    private String getDateOfBirth() {
        String birthDate;
        int monthValue = month.getSelectedItemPosition();
        if(day.getSelectedItemPosition() > 0 & month.getSelectedItemPosition() > 0 & year.getSelectedItemPosition() > 0) {
            birthDate = year.getSelectedItem().toString() + "-" +
                    ((monthValue > 9) ? String.valueOf(monthValue) : "0" +
                            String.valueOf(monthValue)) + "-" +
                    day.getSelectedItem().toString();
        }else {
            birthDate = null;
        }
        return birthDate;
    }

    private void showMultiSelectDialog(List<Language> languageList) {

        String langId;
        List<String> languages = new ArrayList<>();
        for (Language la : languageList) {
            languages.add(la.name);
            langId = la.name + "~" + la.id;
            languageId.add(langId);
        }
        final CharSequence[] dialogList = languages.toArray(new CharSequence[languages.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getContext());
        builderDialog.setTitle(getString(R.string.onboarding_select_language));
        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageSelectDialog(dialogList);
            }
        });
    }

    private void openLanguageSelectDialog(CharSequence[] dialogList) {
        DialogBuilder.showMultiSelectDialog(getContext(), dialogList, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchEnglishLevels();
        fetchQualifications();
        populateExperienceYears();
        populateData();
    }

    @Override
    public void onPause() {
        persistProgress();
        super.onPause();
    }

    private void populateData() {
        if (currentWorker != null) {
            populateDetails(currentWorker);
        }
    }

    private void populateExperienceYears() {
        if (currentWorker != null) {
            seekBar.setProgress(currentWorker.yearsExperience);
        }
    }

    private void populateSavedEnglishLevel() {
        if (currentWorker != null && currentWorker.englishLevel != null) {
            for (EnglishLevel level : levels) {
                if (currentWorker.englishLevel.id == level.id) {
                    level.selected = true;
                    english = level.id;
                }
            }
            fluencyAdapter.notifyDataSetChanged();
        }
    }

    private void populateSavedRequirements() {
        if (currentWorker != null && !CollectionUtils.isEmpty(qualifications)
                && !CollectionUtils.isEmpty(currentWorker.qualifications)) {

            for (ExperienceQualification qualification : qualifications) {
                for (Qualification selectedQualification : currentWorker.qualifications) {
                    if (qualification.id == selectedQualification.id && qualification.onExperience)
                        qualification.selected = true;
                }
            }
            experienceAdapter.notifyDataSetChanged();
        }
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private List<Qualification> requirementsToQualifications(List<ExperienceQualification> list) {
        List<Qualification> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (ExperienceQualification e : list) {
                result.add(new Qualification(e));
            }
        }
        return result;
    }

    private void persistProgress() {
        if (currentWorker != null) {
            currentWorker.yearsExperience = experience;
            currentWorker.nationalityId = nationality.getSelectedItemId();
            currentWorker.dateOfBirth = getDateOfBirth();
            if (english > 0) {
                for (EnglishLevel level : levels) {
                    if (level.id == english)
                        currentWorker.englishLevel = level;
                }
            }

            selected.clear();
            for (ExperienceQualification exp : qualifications) {
                if (exp.selected) {
                    selected.add(exp);
                }
            }

            if (!CollectionUtils.isEmpty(currentWorker.qualifications)) {
                List<Qualification> workerQualifications = new ArrayList<>(currentWorker.qualifications);

                for (Qualification qualification : currentWorker.qualifications) {
                    if (qualification.onExperience) workerQualifications.remove(qualification);
                }

                currentWorker.qualifications = workerQualifications;
            }

            currentWorker.qualifications.addAll(requirementsToQualifications(selected));
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }

    //NIN & REG number
    private EditText nextReg(EditText currentEditText, boolean goRight) {
        switch (currentEditText.getId()) {
            case R.id.reg_01:
                return goRight ? reg.get(1) : null;
            case R.id.reg_02:
                return goRight ? reg.get(2) : reg.get(0);
            case R.id.reg_03:
                return goRight ? reg.get(3) : reg.get(1);
            case R.id.reg_04:
                return goRight ? reg.get(4) : reg.get(2);
            case R.id.reg_05:
                return goRight ? reg.get(5) : reg.get(3);
            case R.id.reg_06:
                return goRight ? reg.get(6) : reg.get(4);
            case R.id.reg_07:
                return goRight ? reg.get(7) : reg.get(5);
            case R.id.reg_08:
                return goRight ? null : reg.get(6);
        }
        return null;
    }

    private EditText nextNis(EditText currentEditText, boolean goRight) {
        switch (currentEditText.getId()) {
            case R.id.nis_01:
                return goRight ? nis.get(1) : null;
            case R.id.nis_02:
                return goRight ? nis.get(2) : nis.get(0);
            case R.id.nis_03:
                return goRight ? nis.get(3) : nis.get(1);
            case R.id.nis_04:
                return goRight ? nis.get(4) : nis.get(2);
            case R.id.nis_05:
                return goRight ? nis.get(5) : nis.get(3);
            case R.id.nis_06:
                return goRight ? nis.get(6) : nis.get(4);
            case R.id.nis_07:
                return goRight ? nis.get(7) : nis.get(5);
            case R.id.nis_08:
                return goRight ? nis.get(8) : nis.get(6);
            case R.id.nis_09:
                return goRight ? null : nis.get(7);
        }
        return null;
    }

    private TextWatcher regListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
            if (charSequence.toString().isEmpty()) {
                next = nextReg(current, false);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            } else if (charSequence.toString().length() == 1) {
                next = nextReg(current, true);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher nisListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
            if (charSequence.toString().isEmpty()) {
                next = nextNis(current, false);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            } else if (charSequence.toString().length() == 1) {
                next = nextNis(current, true);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    @OnFocusChange({
            R.id.reg_01, R.id.reg_02, R.id.reg_03, R.id.reg_04,
            R.id.reg_05, R.id.reg_06, R.id.reg_07, R.id.reg_08,
            R.id.nis_01, R.id.nis_02, R.id.nis_03, R.id.nis_04,
            R.id.nis_05, R.id.nis_06, R.id.nis_07, R.id.nis_08, R.id.nis_09
    })
    void onFocusChange(View view, boolean hasFocus) {
        current = (EditText) view;
        if (!current.getText().toString().isEmpty()) {
            current.setSelection(0, 1);
        }

        DrawableCompat.setTint(current.getBackground(),
                ContextCompat.getColor(getContext(), hasFocus ? R.color.redSquareColor : R.color.graySquareColor));

    }

    @Override
    public void onLanguagesSelected(String selectedLangs) {
        selectLangs = selectedLangs;
        lang.setText(selectLangs);

        String langId;
        String langName;
        for (String langs : languageId) {
            langId = langs.substring(langs.indexOf("~") + 1, langs.length());
            langName = langs.substring(0, langs.indexOf("~"));
            for (String retval : selectedLangs.split(",")) {
                if (langName.equalsIgnoreCase(retval)) {
                    langIds.add(langId);

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            passport_photo.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
            passport_photo.setImageBitmap(imageBitmap);
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

}