package com.hellobaytree.graftrs.employer.createjob.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.createjob.CreateRequest;
import com.hellobaytree.graftrs.employer.createjob.adapter.RolesAdapter;
import com.hellobaytree.graftrs.employer.createjob.persistence.GsonConfig;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.DataResponse;
import com.hellobaytree.graftrs.shared.models.Role;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansEditText;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectRoleFragment extends Fragment
        implements RolesAdapter.RolesListener {

    public static final String TAG = "SelectRoleFragment";
    private boolean unfinished = true;
    private boolean firstTime = true;

    @BindView(R.id.create_job_roles) RecyclerView list;
    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.title) JosefinSansTextView title;

    private List<Role> data = new ArrayList<>();
    private List<Role> filtered = new ArrayList<>();
    private List<Role> tradeRoles = new ArrayList<>();
    private RolesAdapter adapter;

    private CreateRequest request;
    private Role selectedRole;

    public static SelectRoleFragment newInstance(CreateRequest createRequest,
                                                 boolean singleEdit) {
        SelectRoleFragment fragment = new SelectRoleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", createRequest);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_create_job) {
            unfinished = false;
            firstTime = false;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_role, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(getString(R.string.create_job_role));
    }

    public void onResume() {
        super.onResume();

        request = ((CreateRequest) getArguments().getSerializable("request"));

        if (null != request) {
            selectedRole = request.roleObject;
        }

        fetchRoles();
    }

    private void populate(List<Role> roles) {
        try {
            data.clear();
            data.addAll(roles);

            filtered.clear();
            filtered.addAll(data);

            adapter = new RolesAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new GridLayoutManager(getContext(), 2));
            list.setAdapter(adapter);


            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (Role role : data) {
                    if (role.id == request.role) {
                        role.selected = true;
                        role.amountWorkers = request.workersQuantity;
                    }
                }
            }

            if (getActivity()
                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                    .getBoolean(Constants.KEY_UNFINISHED, false)) {
                for (Role role : data) {
                    if (role.id == request.role) {
                        role.selected = true;
                        role.amountWorkers = request.workersQuantity;
                    }
                }
            }

            filter.addTextChangedListener(filterTextWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchRoles() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRolesBrief()
                .enqueue(new Callback<ResponseObject<List<Role>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Role>>> call,
                                           Response<ResponseObject<List<Role>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populate(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Role>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.next)
    public void next() {
        if (null != selectedRole) {
            tradeRoles.clear();
            for (Role role : data) {
                if (role.selected) {
                    if (role.hasTrades) {
                        tradeRoles.add(role);
                    }
                }
            }

            if (null == request) {
                request = new CreateRequest();
            }

            request.role = selectedRole.id;
            request.roleName = selectedRole.name;
            // instead of passing the role object separately
            // attaching it to the create job request object
            for (Role role : data) {
                if (role.id == selectedRole.id) {
                    selectedRole = role;
                }
            }
            request.roleObject = selectedRole;
            request.workersQuantity = selectedRole.amountWorkers;

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {

                if (tradeRoles.size() == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("request", request);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, PreviewJobFragment.newInstance(request))
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, SelectTradeFragment
                                    .newInstance(request, true))
                            .commit();
                }

            } else {


                if (tradeRoles.size() == 0) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .replace(R.id.create_job_content, SelectExperienceFragment
                                    .newInstance(request, false))
                            .addToBackStack("")
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .replace(R.id.create_job_content, SelectTradeFragment
                                    .newInstance(request, false))
                            .addToBackStack("")
                            .commit();
                }
                ////
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Please select at least one role")
                    .show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        try {
            // safety first
            request.workersQuantity = selectedRole.amountWorkers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextTools.log(TAG, "fragment");
        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_ROLE)
                .putBoolean(Constants.KEY_UNFINISHED, firstTime ? !unfinished : unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @Override
    public void onRoleTapped(Role role, View itemView) {
        for (Role item : data) {
            if (role.id != item.id) {
                item.selected = false;
                item.amountWorkers = 1;
            }
        }
        role.selected = !role.selected;
        role.amountWorkers = 1;
        adapter.notifyDataSetChanged();

        if (null == request) {
            request = new CreateRequest();
        }
        request.role = role.id;
        request.roleName = role.name;
        request.roleObject = role;
        selectedRole = role;
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().trim().isEmpty()) {
                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (Role o : data) {
                    if (TextTools.contains(o.name.toLowerCase(), charSequence.toString().toLowerCase())) {
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

    @OnEditorAction(R.id.filter)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == KeyEvent.KEYCODE_ENTER
                || actionId == KeyEvent.ACTION_DOWN
                ||  actionId== EditorInfo.IME_ACTION_DONE ) {
            InputMethodManager imm = (InputMethodManager)
                    v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }
}