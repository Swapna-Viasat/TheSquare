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
import com.hellobaytree.graftrs.employer.createjob.adapter.SkillsAdapter;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.models.Role;
import com.hellobaytree.graftrs.shared.models.RolesRequest;
import com.hellobaytree.graftrs.shared.models.Skill;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
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

public class SelectSkillsFragment extends Fragment
        implements SkillsAdapter.SkillListener {

    public static final String TAG = "SelectSkillsFragment";
    private int workerId;

    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.create_job_skills)
    RecyclerView list;
    @BindView(R.id.title)
    JosefinSansTextView title;

    private List<Skill> data = new ArrayList<>();
    private List<Skill> filtered = new ArrayList<>();
    private SkillsAdapter adapter;
    private Worker currentWorker;
    private List<Skill> selected = new ArrayList<>();

    public static SelectSkillsFragment newInstance(boolean singleEdition) {
        SelectSkillsFragment selectSkillsFragment = new SelectSkillsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectSkillsFragment.setArguments(bundle);
        return selectSkillsFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_skills, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        try {
            title.setText(getString(R.string.onboarding_skills));
        } catch (Exception e) {
            e.printStackTrace();
        }

        filter.addTextChangedListener(filterTextWatcher);
    }

    private void fetchMe() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            TextTools.log(TAG, "success");
                            currentWorker = response.body().getResponse();
                            fetchSkills(currentWorker.roles);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchSkills(List<Role> userRoles) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        List<Integer> roleIds = new ArrayList<>();

        if (userRoles != null && !userRoles.isEmpty()) {
            for (Role role : userRoles) {
                roleIds.add(role.id);
            }
        }

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRoleSkills(new RolesRequest(roleIds))
                .enqueue(new Callback<ResponseObject<List<Skill>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Skill>>> call,
                                           Response<ResponseObject<List<Skill>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processData(response.body().getResponse());
                            populateData();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Skill>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processData(List<Skill> fetchedSkills) {
        data.clear();
        data.addAll(fetchedSkills);

        filtered.clear();
        filtered.addAll(data);

        adapter = new SkillsAdapter(filtered);
        adapter.setListener(this);

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
    }

    @OnClick(R.id.next)
    public void next() {
        selected.clear();
        for (Skill skill : data) {
            if (skill.selected) {
                selected.add(skill);
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
        request.put("skills_ids", body);

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
                .replace(R.id.onboarding_content, SelectExperienceTypeFragment
                        .newInstance(false))
                .addToBackStack("")
                .commit();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().isEmpty()) {
//                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (Skill o : data) {
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
    public void onSkill(Skill skill) {
        if (skill.selected) {
            skill.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (Skill skill1 : filtered) {
                if (skill1.selected)
                    count++;
            }
            if (count < 5) {
                skill.selected = true;
                adapter.notifyDataSetChanged();
            } else DialogBuilder
                    .showStandardDialog(getContext(), "", getString(R.string.onboarding_selected_max, 5));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchMe();
    }

    @Override
    public void onPause() {
        persistProgress();
        super.onPause();
    }

    private void populateData() {
        if (currentWorker != null) {
            selected.clear();
            selected.addAll(currentWorker.skills);

            for (Skill skill : filtered) {
                for (Skill selectedSkill : selected) {
                    if (skill.id == selectedSkill.id)
                        skill.selected = true;
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
            for (Skill skill : data) {
                if (skill.selected) {
                    selected.add(skill);
                }
            }
            currentWorker.skills = selected;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
