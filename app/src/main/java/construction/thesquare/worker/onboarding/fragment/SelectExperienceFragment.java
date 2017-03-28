package construction.thesquare.worker.onboarding.fragment;

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
import android.os.Build;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.EnglishLevel;
import construction.thesquare.shared.models.ExperienceQualification;
import construction.thesquare.shared.models.Language;
import construction.thesquare.shared.models.Nationality;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.utils.MediaTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.onboarding.OnLanguagesSelectedListener;
import construction.thesquare.worker.onboarding.adapter.FluencyAdapter;
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectExperienceFragment extends Fragment
        implements FluencyAdapter.FluencyListener, OnLanguagesSelectedListener {

    public static final String TAG = "SelectExperienceFragment";
    private int workerId;
    private String workerSurname;
    private int english;
    private int experience;
    private int cscsStatus;
    private List<String> selectedLanguages = new ArrayList<>();
    @BindView(R.id.years)
    JosefinSansTextView years;
    @BindView(R.id.seek)
    SeekBar seekBar;
    @BindView(R.id.english)
    RecyclerView fluency;
    @BindView(R.id.others)
    LinearLayout others;
    @BindView(R.id.top)
    JosefinSansTextView top;
    private EditText current;
    private EditText next;
    @BindView(R.id.spinner_nationality)
    Spinner spinnerNationality;
    @BindView(R.id.spinner_day)
    Spinner spinnerDay;
    @BindView(R.id.spinner_month)
    Spinner spinnerMonth;
    @BindView(R.id.spinner_year)
    Spinner spinnerYear;
    @BindView(R.id.cscs_details)
    LinearLayout cscs;
    @BindView(R.id.openDialog)
    View openDialog;
    @BindView(R.id.lang)
    TextView languagesTextView;
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
    private List<Nationality> nationalities;
    private FluencyAdapter fluencyAdapter;
    private List<EnglishLevel> levels = new ArrayList<>();
    private List<ExperienceQualification> qualifications = new ArrayList<>();
    private Worker currentWorker;
    private List<ExperienceQualification> selected = new ArrayList<>();
    private Map<String, Integer> countryIds = new HashMap<>();
    private List<Language> fetchedLanguages;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECTION = 2;
    static final int REQUEST_PERMISSIONS = 3;
    static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private Uri imageUri;

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
        for (EditText e : reg) {
            e.addTextChangedListener(regListener);
        }
        for (EditText e : nis) {
            e.addTextChangedListener(nisListener);
        }
        // Create an ArrayAdapter using the string array and a default spinner
        dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_day,
                android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        monthAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_month,
                android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        yearAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_year,
                android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
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
                            try {
                                populateCSCSDetails(response.body());
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
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
                            showLanguagesSelectDialog(response.body().getResponse());
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

    private void fetchCurrentWorker(final boolean requiredFieldsOnly) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                if (!requiredFieldsOnly) {
                                    populateDetails(response.body().getResponse());
                                    if (response.body().getResponse() != null) {
                                        workerSurname = response.body().getResponse().lastName;
                                        if (workerSurname != null) surname.setText(workerSurname);
                                    }
                                } else if (response.body().getResponse() != null) {
                                    workerSurname = response.body().getResponse().lastName;
                                    if (workerSurname != null) surname.setText(workerSurname);

                                    Picasso.with(getContext())
                                            .load(response.body().getResponse().passportUpload)
                                            .fit()
                                            .centerCrop()
                                            .error(R.drawable.passport)
                                            .placeholder(R.drawable.passport)
                                            .into(passport_photo);
                                }
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
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
            if (!TextUtils.isEmpty(worker.passportUpload)) {
                Picasso.with(getContext()).load(worker.passportUpload).fit().centerCrop().into(passport_photo);
            } else {
                passport_photo.setImageResource(R.drawable.passport);
            }

            populateExperienceYears();
            populateDateOfBirth();
            populateNis();
            populateLanguages();
            showPassportImage();
            if (workerSurname != null) surname.setText(workerSurname);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
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
                CrashLogHelper.logException(e);
            }
        }
    }

    private void processQualifications(List<ExperienceQualification> fetchedQualifications) {
        try {
            qualifications.clear();
            qualifications.addAll(fetchedQualifications);
            populateQualifications();
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void populateQualifications() {
        others.removeAllViews();
        if (CollectionUtils.isEmpty(qualifications)) return;

        for (final ExperienceQualification qualification : qualifications) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_experience, null, false);
            JosefinSansTextView title = (JosefinSansTextView) item.findViewById(R.id.title);
            CheckBox checkBox = (CheckBox) item.findViewById(R.id.check);

            checkBox.setChecked(qualification.selected);
            title.setText(qualification.name);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onExperienceClick(qualification);
                }
            });
            others.addView(item);
        }
    }

    private void populateCSCSDetails(ResponseObject<CSCSCardWorker> dataResponse) {
        if (workerSurname != null) surname.setText(workerSurname);
        surname.setEnabled(false);
        String regnum = dataResponse.getResponse().registrationNumber;
        populateCscsStatus(dataResponse.getResponse().verificationStatus);
        if (!regnum.isEmpty()) {

            for (EditText e : reg) {
                e.removeTextChangedListener(regListener);
            }

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

            for (EditText e : reg) {
                e.addTextChangedListener(regListener);
            }
        }
    }

    private void populateNis() {
        if (currentWorker != null && !TextUtils.isEmpty(currentWorker.niNumber)) {
            for (EditText e : nis) {
                e.removeTextChangedListener(nisListener);
            }

            final char ni[] = currentWorker.niNumber.toCharArray();
            ButterKnife.Setter<JosefinSansEditText, Boolean> ENABLED = new ButterKnife.Setter<JosefinSansEditText, Boolean>() {
                @Override
                public void set(JosefinSansEditText view, Boolean value, int index) {

                    switch (view.getId()) {
                        case R.id.nis_01:
                            nis.get(0).setText(Character.toString(ni[0]));
                        case R.id.nis_02:
                            nis.get(1).setText(Character.toString(ni[1]));
                        case R.id.nis_03:
                            nis.get(2).setText(Character.toString(ni[2]));
                        case R.id.nis_04:
                            nis.get(3).setText(Character.toString(ni[3]));
                        case R.id.nis_05:
                            nis.get(4).setText(Character.toString(ni[4]));
                        case R.id.nis_06:
                            nis.get(5).setText(Character.toString(ni[5]));
                        case R.id.nis_07:
                            nis.get(6).setText(Character.toString(ni[6]));
                        case R.id.nis_08:
                            nis.get(7).setText(Character.toString(ni[7]));
                        case R.id.nis_09:
                            nis.get(8).setText(Character.toString(ni[8]));
                    }
                }
            };
            ButterKnife.apply(nis, ENABLED, true);

            for (EditText e : nis) {
                e.addTextChangedListener(nisListener);
            }
        }
    }

    private void processNationality(List<Nationality> nationalityList) {
        nationalities = nationalityList;
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
                spinnerNationality.setAdapter(nationalityAdapter);

                populateNationality();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
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
                showOriginalImage();
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = MediaTools.getOutputImageUri(getContext());
            } else {
                File file = MediaTools.getOutputImageFile();
                if (file != null) imageUri = Uri.fromFile(file);
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (imageUri != null) takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            else {
                DialogBuilder.showStandardDialog(getContext(), "Error", "Can not store image file to local storage");
                return;
            }
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                REQUEST_IMAGE_SELECTION);
    }

    private void uploadPicture(Context context, Bitmap file) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("passport_upload", MediaTools.encodeToBase64(file));
        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(
                        SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), payload)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);

                            if (currentWorker != null && response.body() != null && response.body().getResponse() != null) {
                                currentWorker.passportUpload = response.body().getResponse().passportUpload;
                            }
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
                    .placeholder(R.drawable.passport)
                    .error(R.drawable.passport)
                    .centerCrop()
                    .into(passport_photo);
        }
    }

    private void showOriginalImage() {
        LayoutInflater layoutInflater
                = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final Dialog settingsDialog = new Dialog(getContext());
        if (currentWorker != null && currentWorker.passportUpload != null) {
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(layoutInflater.inflate(R.layout.popup_passport_image, null));
            ImageButton close = (ImageButton) settingsDialog.findViewById(R.id.passport_preview_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            ImageView iv = (ImageView) settingsDialog.findViewById(R.id.original_image);
            Picasso.with(getContext()).load(currentWorker.passportUpload).fit().centerCrop().into(iv);
            //settingsDialog.getWindow().setLayout(700, 700);
            settingsDialog.show();
        } else {
            DialogBuilder.showStandardDialog(getContext(), "",
                    getString(R.string.passport_nophoto));
        }
    }

    public void verify() {
        try {

            HashMap<String, Object> request = new HashMap<>();
            request.put("surname", workerSurname);
            request.put("registration_number", getReg());
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .persistOnboardingWorkerCSCSCard(workerId, request)
                    .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                               Response<ResponseObject<CSCSCardWorker>> response) {
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                                cscsStatus = response.body().getResponse().verificationStatus;
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
            CrashLogHelper.logException(e);
        }
    }

    private void populateCscsStatus(int status) {
        if (status == 4) {
            verify.setText(R.string.verified_cscs_success);
            cscsErrorMsg.setText("");
            verify.setEnabled(false);
        } else if (status == 2) {
            verify.setText(R.string.verified_cscs_failed);
            cscsErrorMsg.setText(R.string.cscs_status_infrastructure_issue);
            cscsErrorMsg.setLineSpacing(1, 1.5f);
        } else if (status == 3) {
            verify.setText(R.string.verified_cscs_failed);
            cscsErrorMsg.setText(R.string.cscs_status_carddetails_invalid);
            cscsErrorMsg.setLineSpacing(1, 1.5f);
        }
    }

    private List<Integer> getLanguagesIds() {
        List<Integer> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(selectedLanguages) && !CollectionUtils.isEmpty(fetchedLanguages)) {
            for (String language : selectedLanguages) {
                for (Language language1 : fetchedLanguages) {
                    if (TextUtils.equals(language, language1.name))
                        result.add(language1.id);
                }
            }
        }
        return result;
    }

    private List<Language> getSavedLanguages() {
        List<Language> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(selectedLanguages) && !CollectionUtils.isEmpty(fetchedLanguages)) {
            for (String language : selectedLanguages) {
                for (Language language1 : fetchedLanguages) {
                    if (TextUtils.equals(language, language1.name))
                        result.add(language1);
                }
            }
        }
        return result;
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            body[i] = selected.get(i).id;
        }

        HashMap<String, Object> request = new HashMap<>();
        request.put("qualifications_ids", body);
        request.put("english_level_id", english);
        request.put("years_experience", experience);
        request.put("ni_number", getNIS());
        request.put("date_of_birth", getDateOfBirth());
        request.put("nationality_id", countryIds.get(spinnerNationality.getSelectedItem()));
        request.put("languages_ids", getLanguagesIds());

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

    public void onExperienceClick(ExperienceQualification experience) {
        if (experience != null && experience.name != null && experience.name.equals("CSCS Card")) {
            experience.selected = !experience.selected;
            populateQualifications();
            if (!experience.selected) {
                //
                cscs.setVisibility(View.GONE);
            } else {
                fetchCscsDetails(workerId);
                if (cscsStatus > 0)
                    populateCscsStatus(cscsStatus);
                cscs.setVisibility(View.VISIBLE);
            }
        } else {
            experience.selected = !experience.selected;
            populateQualifications();
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
        int monthValue = spinnerMonth.getSelectedItemPosition();
        if (spinnerDay.getSelectedItemPosition() > 0 & spinnerMonth.getSelectedItemPosition() > 0 & spinnerYear.getSelectedItemPosition() > 0) {
            birthDate = spinnerYear.getSelectedItem().toString() + "-" +
                    ((monthValue > 9) ? String.valueOf(monthValue) : "0" +
                            String.valueOf(monthValue)) + "-" +
                    spinnerDay.getSelectedItem().toString();
        } else {
            birthDate = null;
        }
        return birthDate;
    }

    private void showLanguagesSelectDialog(List<Language> languageList) {
        fetchedLanguages = languageList;

        List<String> languageNames = new ArrayList<>();
        for (Language language : languageList) languageNames.add(language.name);

        final CharSequence[] dialogList = languageNames.toArray(new CharSequence[languageNames.size()]);
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
        if (currentWorker == null || getArguments().getBoolean(Constants.KEY_SINGLE_EDIT))
            fetchCurrentWorker(false);
        else fetchCurrentWorker(true);
        fetchEnglishLevels();
        fetchQualifications();
        fetchNationality();
        fetchLanguage();
        fetchCscsDetails(workerId);

        populateData();
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
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
                    if (selectedQualification.name.equals("CSCS Card"))
                        cscs.setVisibility(View.VISIBLE);
                }
            }
            populateQualifications();
        }
    }

    private void populateNationality() {
        if (currentWorker != null && currentWorker.nationality != null && nationalities != null) {
            for (Nationality nationality : nationalities) {
                if (nationality.id == currentWorker.nationality.id) {
                    spinnerNationality.setSelection(nationalities.indexOf(nationality) + 1);
                }
            }
        }
    }

    private void populateDateOfBirth() {
        List<String> days = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_day));
        List<String> months = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_month));
        List<String> years = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_year));

        if (currentWorker != null && !TextUtils.isEmpty(currentWorker.dateOfBirth)) {
            LocalDate dateOfBirth = DateUtils.getParsedLocalDate(currentWorker.dateOfBirth);

            for (String day : days) {
                if (dateOfBirth != null && TextUtils.equals(day.startsWith("0") ? day.substring(1) : day,
                        String.valueOf(dateOfBirth.getDayOfMonth()))) {
                    spinnerDay.setSelection(days.indexOf(day));
                }
            }

            for (String month : months) {
                if (dateOfBirth != null && TextUtils.equals(month, dateOfBirth.toString("MMMM"))) {
                    spinnerMonth.setSelection(months.indexOf(month));
                }
            }

            for (String year : years) {
                if (dateOfBirth != null && TextUtils.equals(year, String.valueOf(dateOfBirth.getYear()))) {
                    spinnerYear.setSelection(years.indexOf(year));
                }
            }
        }
    }

    private void populateLanguages() {
        selectedLanguages = new ArrayList<>();
        if (currentWorker != null && !CollectionUtils.isEmpty(currentWorker.languages)) {
            for (Language language : currentWorker.languages) {
                selectedLanguages.add(language.name);
            }
        }
        languagesTextView.setText(TextUtils.join(", ", selectedLanguages));
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
        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) return;

        if (currentWorker != null) {
            currentWorker.yearsExperience = experience;
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
            currentWorker.niNumber = getNIS();
            currentWorker.languages = getSavedLanguages();

            if (nationalities != null && countryIds != null)
                for (Nationality nationality : nationalities) {
                    if (countryIds.get(spinnerNationality.getSelectedItem()) != null
                            && countryIds.get(spinnerNationality.getSelectedItem()) == nationality.id)
                        currentWorker.nationality = nationality;
                }
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
    public void onLanguagesSelected(List<String> selectedLangs) {
        languagesTextView.setText(TextUtils.join(", ", selectedLangs));
        selectedLanguages = selectedLangs;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
                passport_photo.setImageBitmap(bitmap);
                uploadPicture(getActivity(), bitmap);
            } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
                passport_photo.setImageBitmap(imageBitmap);
                uploadPicture(getActivity(), imageBitmap);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
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