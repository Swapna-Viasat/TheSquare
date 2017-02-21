package com.hellobaytree.graftrs.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.adapter.ExperienceTypeAdapter;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.ExperienceType;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.KeyboardUtils;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansEditText;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectExperienceTypeFragment extends Fragment
        implements ExperienceTypeAdapter.ExperienceTypeListener {

    public static final String TAG = "SelectExpTypeFragment";
    private int workerId;

    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.create_job_experience_type)
    RecyclerView list;

    @BindView(R.id.title)
    JosefinSansTextView title;

    private List<ExperienceType> data = new ArrayList<>();
    private List<ExperienceType> filtered = new ArrayList<>();
    private ExperienceTypeAdapter adapter;
    private Worker currentWorker;
    private List<ExperienceType> selected = new ArrayList<>();

    public static SelectExperienceTypeFragment newInstance(boolean singleEdition) {
        SelectExperienceTypeFragment selectExperienceTypeFragment
                = new SelectExperienceTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectExperienceTypeFragment.setArguments(bundle);
        return selectExperienceTypeFragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_experience_type, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        filter.addTextChangedListener(filterTextWatcher);

        try {
            title.setText(getString(R.string.onboarding_experience_type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchExperienceTypes() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchExperienceTypes()
                .enqueue(new Callback<ResponseObject<List<ExperienceType>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ExperienceType>>> call,
                                           Response<ResponseObject<List<ExperienceType>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processExperienceTypes(response.body().getResponse());
                            populateData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<ExperienceType>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processExperienceTypes(List<ExperienceType> experienceTypes) {
        try {

            data.clear();
            data.addAll(experienceTypes);

            filtered.clear();
            filtered.addAll(data);

            adapter = new ExperienceTypeAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.next)
    public void next() {
        selected.clear();
        for (ExperienceType experienceType : data) {
            if (experienceType.selected) {
                selected.add(experienceType);
            }
        }

        patchWorker();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            body[i] = selected.get(i).id;
        }

        HashMap<String, Object> request = new HashMap<>();
        request.put("experience_types_ids", body);

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            proceed();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        //
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
                .replace(R.id.onboarding_content, SelectCompaniesFragment.newInstance(false))
                .addToBackStack("")
                .commit();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("")) {
                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (ExperienceType o : data) {
                    if (o.name.contains(charSequence)) {
                        filtered.add(o);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @OnClick(R.id.clear_filter)
    public void clear() {
        filter.setText("");
    }

    @Override
    public void onExperienceType(ExperienceType experienceType) {
        if (experienceType.selected) {
            experienceType.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (ExperienceType experienceType1 : filtered) {
                if (experienceType1.selected)
                    count++;
            }
            if (count < 5) {
                experienceType.selected = true;
                adapter.notifyDataSetChanged();
            } else DialogBuilder
                    .showStandardDialog(getContext(), "", getString(R.string.onboarding_selected_max, 5));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchExperienceTypes();
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void populateData() {
        if (currentWorker != null) {
            selected.clear();
            selected.addAll(currentWorker.experienceTypes);

            for (ExperienceType experienceType : filtered) {
                for (ExperienceType selectedExperienceType : selected) {
                    if (experienceType.id == selectedExperienceType.id)
                        experienceType.selected = true;
                }
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private void persistProgress() {
        if (currentWorker != null) {

            selected.clear();
            for (ExperienceType experienceType : data) {
                if (experienceType.selected) {
                    selected.add(experienceType);
                }
            }

            currentWorker.experienceTypes = selected;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
