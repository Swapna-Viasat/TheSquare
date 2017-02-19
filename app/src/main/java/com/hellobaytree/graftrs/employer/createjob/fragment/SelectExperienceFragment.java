package com.hellobaytree.graftrs.employer.createjob.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.CreateRequest;
import com.hellobaytree.graftrs.employer.createjob.adapter.ExperienceAdapter;
import com.hellobaytree.graftrs.employer.createjob.adapter.FluencyAdapter;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.DataResponse;
import com.hellobaytree.graftrs.shared.models.EnglishLevel;
import com.hellobaytree.graftrs.shared.models.ExperienceQualification;
import com.hellobaytree.graftrs.shared.models.ExperienceType;
import com.hellobaytree.graftrs.shared.models.Role;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectExperienceFragment extends Fragment
        implements FluencyAdapter.FluencyListener, ExperienceAdapter.ExperienceListener {

    public static final String TAG = "SelectExperienceFragment";
    private boolean unfinished = true;

    private int english;
    private String englishString = "Basic";
    private int experience;
    private CreateRequest createRequest;

    @BindView(R.id.years) JosefinSansTextView years;
    @BindView(R.id.seek) SeekBar seekBar;
    @BindView(R.id.english) RecyclerView fluency;
    @BindView(R.id.others) RecyclerView others;

    private FluencyAdapter fluencyAdapter;
    private List<EnglishLevel> levels = new ArrayList<>();
    private ExperienceAdapter experienceAdapter;
    private List<ExperienceQualification> qualifications = new ArrayList<>();

    public static SelectExperienceFragment newInstance(CreateRequest request,
                                                       boolean singleEdit) {
        SelectExperienceFragment selectExperienceFragment = new SelectExperienceFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        bundle.putSerializable("request", request);
        selectExperienceFragment.setArguments(bundle);
        return selectExperienceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_experience_employer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createRequest = (CreateRequest) getArguments().getSerializable("request");

        if (null != createRequest) {
            int i = createRequest.experience;
            seekBar.setProgress(i);
            years.setText(String.valueOf(i)
                    + ((seekBar.getMax() == i) ? "+ " : " ")
                    + getResources().getQuantityString(R.plurals.year_plural, i));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                years.setText(String.valueOf(i)
                        + ((seekBar.getMax() == i) ? "+ " : " ")
                        + getResources().getQuantityString(R.plurals.year_plural, i));
                experience = i;
                if (null != createRequest) {
                    createRequest.experience = experience;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_create_job) {
            unfinished = false;
            if (getActivity()
                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                    .edit()
                    .putInt(Constants.KEY_STEP, 0)
                    .remove(Constants.KEY_REQUEST)
                    .commit()) {
                getActivity().finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        List<ExperienceQualification> selected = new ArrayList<>();
        selected.clear();
        for (ExperienceQualification exp : qualifications) {
            if (exp.selected) {
                selected.add(exp);
            }
        }
        int[] quals = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            quals[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        createRequest.expQualifications = quals;
        createRequest.expQualificationObjects = selected;
        createRequest.expQualificationStrings = strings;

        createRequest.english = english;

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_EXPERIENCE)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(createRequest))
                .commit();
    }

    private void fetchData() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchExperienceQualifications()
                .enqueue(new Callback<ResponseObject<List<ExperienceQualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ExperienceQualification>>> call,
                                           Response<ResponseObject<List<ExperienceQualification>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateExpQualifications(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<ExperienceQualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchEnglishLevels()
                .enqueue(new Callback<ResponseObject<List<EnglishLevel>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<EnglishLevel>>> call,
                                           Response<ResponseObject<List<EnglishLevel>>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateEnglishLevels(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<EnglishLevel>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateExpQualifications(List<ExperienceQualification> data) {
        try {
            qualifications.clear();
            qualifications.addAll(data);
            if (null != createRequest && null != createRequest.expQualifications) {
                for (ExperienceQualification expQ : data) {
                    for (int id : createRequest.expQualifications) {
                        if (expQ.id == id) {
                            expQ.selected = true;
                        }
                    }
                }
            }
            experienceAdapter = new ExperienceAdapter(qualifications);
            experienceAdapter.setListener(this);
            others.setLayoutManager(new LinearLayoutManager(getContext()));
            others.setAdapter(experienceAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (ExperienceQualification e : qualifications) {
                    for (int i : createRequest.expQualifications) {
                        if (i == e.id) {
                            e.selected = true;
                        }
                    }
                }
                experienceAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateEnglishLevels(List<EnglishLevel> data) {
        try {
            levels.clear();
            levels.addAll(data);
            if (null != createRequest) {
                for (EnglishLevel level : data) {
                    if (level.id == createRequest.english) {
                        level.selected = true;
                    }
                }
            }
            fluencyAdapter = new FluencyAdapter(levels);
            fluencyAdapter.setListener(this);
            fluency.setLayoutManager(new LinearLayoutManager(getContext()));
            fluency.setAdapter(fluencyAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            seekBar.setProgress(createRequest.experience);
            for (EnglishLevel e : levels) {
                if (e.id == createRequest.english) {
                    e.selected = true;
                    english = createRequest.english;
                }
            }
            fluencyAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.next)
    public void next() {
        if (validate()) {
            createRequest.english = english;
            createRequest.englishLevelString = englishString;
            createRequest.experience = experience;

            List<ExperienceQualification> selected = new ArrayList<>();
            selected.clear();
            for (ExperienceQualification exp : qualifications) {
                if (exp.selected) {
                    selected.add(exp);
                }
            }
            int[] quals = new int[selected.size()];
            List<String> strings = new ArrayList<>();
            for (int i = 0; i < selected.size(); i++) {
                quals[i] = selected.get(i).id;
                strings.add(selected.get(i).name);
            }
            createRequest.expQualifications = quals;
            createRequest.expQualificationObjects = selected;
            createRequest.expQualificationStrings = strings;

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                //
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, PreviewJobFragment.newInstance(createRequest))
                        .commit();
                //
            } else {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.create_job_content, SelectQualificationsFragment
                                .newInstance(createRequest, false))
                        .addToBackStack("")
                        .commit();
            }
        }
    }

    private boolean validate() {
        boolean result = true;
        if (english == 0) {
            english = 1;
            // SC228 - if no english selected, go with Basic
            // leaving this commented in case they change their mind again
            // very likely, judging by their history
//            result = false;
//            new AlertDialog.Builder(getActivity())
//                    .setMessage("Please select an \nEnglish proficiency level.")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .show();
        }
        return result;
    }

    @Override
    public void onFluency(EnglishLevel level) {
        for (EnglishLevel e : levels) {
            if (e.id != level.id) {
                e.selected = false;
            }
        }
        english = level.id;
        englishString = level.name;
        level.selected = true;
        fluencyAdapter.notifyDataSetChanged();
        if (null != createRequest) {
            createRequest.english = level.id;
            createRequest.englishLevelString = level.name;
        }
    }

    @Override
    public void onExperience(ExperienceQualification experience) {
        experience.selected = !experience.selected;
        experienceAdapter.notifyDataSetChanged();
    }
}