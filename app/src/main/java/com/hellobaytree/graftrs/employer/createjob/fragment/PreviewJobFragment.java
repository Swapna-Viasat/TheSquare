package com.hellobaytree.graftrs.employer.createjob.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.MainEmployerActivity;
import com.hellobaytree.graftrs.employer.createjob.CreateRequest;
import com.hellobaytree.graftrs.employer.createjob.dialog.CRNDialog;
import com.hellobaytree.graftrs.employer.myjobs.fragment.JobDetailsFragment;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.models.Job;
import com.hellobaytree.graftrs.shared.models.Role;
import com.hellobaytree.graftrs.shared.utils.Constants;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Evgheni Gherghelejiu
 * on 01/11/2016
 */
public class PreviewJobFragment extends Fragment {

    public static final String TAG = "PreviewJobFragment";

    @BindView(R.id.preview_logo) ImageView logo;
    @BindView(R.id.preview_occupation) JosefinSansTextView role;
    @BindView(R.id.preview_trades) JosefinSansTextView trades;
    @BindView(R.id.preview_experience) JosefinSansTextView experience;
    @BindView(R.id.preview_salary_period) JosefinSansTextView salaryPeriod;
    @BindView(R.id.preview_salary_number) JosefinSansTextView salaryNumber;
    @BindView(R.id.preview_start_date) JosefinSansTextView startDate;
    @BindView(R.id.preview_location) JosefinSansTextView location;
    private CreateRequest createRequest;
    @BindView(R.id.job_details_description) JosefinSansTextView description;
    @BindView(R.id.job_details_skills) JosefinSansTextView skills;
    @BindView(R.id.job_details_english_level) JosefinSansTextView englishLevel;
    @BindView(R.id.job_details_overtime) JosefinSansTextView overtime;
    @BindView(R.id.job_details_qualifications) JosefinSansTextView qualifications;
    @BindView(R.id.job_details_qualifications2) JosefinSansTextView qualifications2;
    @BindView(R.id.job_details_experience_types) JosefinSansTextView experienceTypes;
    @BindView(R.id.job_details_owner) JosefinSansTextView owner;
    @BindView(R.id.job_details_owner_phone) JosefinSansTextView ownerPhone;
    @BindView(R.id.job_details_owner_address) JosefinSansTextView ownerAddress;
    @BindView(R.id.job_details_notes) JosefinSansTextView notes;
    @BindView(R.id.job_details_date) JosefinSansTextView date;

    private SupportMapFragment mapFragment;

    public static PreviewJobFragment newInstance(CreateRequest request) {
        PreviewJobFragment fragment = new PreviewJobFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_preview_job, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createRequest = (CreateRequest) getArguments().getSerializable("request");
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    LatLng loc = new LatLng(createRequest.location.latitude,
                            createRequest.location.longitude);
                    googleMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(loc, 12));
                    googleMap.addMarker(new MarkerOptions().position(loc));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({
            R.id.preview_occupation,
            R.id.preview_experience,
            R.id.preview_reporting_to,
            R.id.preview_start_date,
            R.id.job_details_english_level_label,
            R.id.job_details_overtime_label,
            R.id.preview_location,
            R.id.preview_salary_number,
            R.id.job_details_description_label,
            R.id.job_details_skills_label,
            R.id.job_details_reqs_label,
            R.id.job_details_qualifications_label,
            R.id.job_details_experience_types_label})
    public void edit(View view) {
        Fragment fragment = new Fragment();
        switch (view.getId()) {
            case R.id.preview_salary_number:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_reporting_to:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_start_date:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_location:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_description_label:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_skills_label:
                fragment = SelectSkillsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_occupation:
                fragment = SelectRoleFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_experience:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_reqs_label:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_english_level_label:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_qualifications_label:
                fragment = SelectQualificationsFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_experience_types_label:
                fragment = SelectExperienceTypeFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_overtime_label:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
        }
        if (null != fragment) {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar().setTitle("");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity())
                .getSupportActionBar().setTitle(getString(R.string.create_job_preview));
        try {

            role.setText(createRequest.roleName);
            if (null != createRequest.tradeStrings) {
                trades.setText("(" + TextTools.toCommaList(createRequest.tradeStrings, false) + ")");
            } else {
                trades.setVisibility(View.GONE);
            }
            experience.setText(String
                    .format(getString(R.string.create_job_experience_years),
                            createRequest.experience,
                            getResources().getQuantityString(R.plurals.year_plural,
                                    createRequest.experience)));

            /**
             * Reporting to!
             */
            if (null != createRequest.contactName) {
                owner.setText(createRequest.contactName);
            }
            ownerPhone.setText("+ " + createRequest.contactCountryCode
                    + " " + createRequest.contactPhoneNumber);
            if (null != createRequest.address) {
                ownerAddress.setText(createRequest.address);
            }
            //
            date.setText(createRequest.date + " - " + createRequest.time);
            //
            notes.setText(createRequest.notes);
            // lists
            description.setText(createRequest.description);
            skills.setText(TextTools.toBulletList(createRequest.skillStrings, true));
            qualifications.setText(TextTools.toBulletList(createRequest.expQualificationStrings, true));
            qualifications2.setText(TextTools.toBulletList(createRequest.qualificationStrings, true));
            experienceTypes.setText(TextTools.toBulletList(createRequest.experienceTypeStrings, true));
            // salary
            salaryNumber.setText(String.valueOf(getString(R.string.pound_sterling) + " " +
                    NumberFormat.getInstance(Locale.UK).format(createRequest.budget)));
            switch (createRequest.budgetType) {
                case 1:
                    salaryPeriod.setText("per hour");
                    break;
                case 2:
                    salaryPeriod.setText("per day");
                    break;
                case 3:
                    salaryPeriod.setText("per year");
            }

            // overtime
            if (createRequest.overtime) {
                overtime.setText(String.format(getString(R.string.job_details_overtime_text),
                        createRequest.overtimeValue));
            } else {
                overtime.setText("n/a");
            }
            // english level
            if (null != createRequest.englishLevelString) {
                englishLevel.setText(createRequest.englishLevelString);
            }
            // description
            if (null != createRequest.description) {
                description.setText(createRequest.description);
            }

            startDate.setText(createRequest.date);

            location.setText(createRequest.address);

            if (null != createRequest.logo) {
                Picasso.with(getContext())
                        .load(createRequest.logo)
                        .into(logo);
            }

            if (createRequest.id != 0) {
                //Toast.makeText(getContext(), String.valueOf(createRequest.id), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> loadRequest(int status) {
        HashMap<String, Object> payload = new HashMap<>();
        try {

            if (createRequest.id != 0) {
                payload.put("id", createRequest.id);
            }

            payload.put("status", status);
            payload.put("role", createRequest.role);
            payload.put("workers_quantity", (createRequest.roleObject).amountWorkers);
            payload.put("trades", createRequest.trades);
            payload.put("experience", createRequest.experience);
            payload.put("english_level_id", createRequest.english);
            payload.put("qualifications", createRequest.qualifications);
            payload.put("experience_qualifications", createRequest.expQualifications);
            payload.put("skills", createRequest.skills);
            payload.put("experience_type", createRequest.experienceTypes);
            payload.put("description", createRequest.description);
            payload.put("budget_type", createRequest.budgetType);
            payload.put("budget", createRequest.budget);
            payload.put("pay_overtime", createRequest.overtime);
            payload.put("pay_overtime_value", createRequest.overtimeValue);
            payload.put("contact_name", createRequest.contactName);

            payload.put("contact_phone", "+" + String.valueOf(createRequest.contactCountryCode) +
                    " " + String.valueOf(createRequest.contactPhoneNumber));

            payload.put("address", createRequest.address);
            // date and time
            payload.put("start_datetime", createRequest.date + "T" + createRequest.time + "+00:00");
            if (null != createRequest.rawDate) {
                payload.put("start_datetime", DateUtils.toPayloadDate(createRequest.rawDate));
            }
            TextTools.log(TAG, String.valueOf(payload.get("start_datetime")));

            payload.put("extra_notes", createRequest.notes);
            payload.put("location", createRequest.location);
            payload.put("location_name", createRequest.locationName);
            //payload.put("cscs_required", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job_post_step, menu);
        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Cancel");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createJobCancel:
                //
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton("Save as draft", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                callApi(Constants.JOB_STATUS_DRAFT);
                            }
                        })
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                discard();
                            }
                        })
                        .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                TextView message = (TextView) dialog.findViewById(android.R.id.message);
                message.setTextColor(ContextCompat.getColor(getContext(), R.color.graySquareColor));
                message.setTypeface(Typeface.createFromAsset(getActivity()
                        .getAssets(), "fonts/JosefinSans-Italic.ttf"));
                //
                return true;
        }
        return false;
    }

    private void discard() {
        getActivity()
                .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .remove(Constants.KEY_STEP)
                .putBoolean(Constants.KEY_UNFINISHED, false)
                .remove(Constants.KEY_REQUEST)
                .commit();
        startActivity(new Intent(getActivity(), MainEmployerActivity.class));
    }

    @OnClick(R.id.publish)
    public void publish() {
        callApi(Constants.JOB_STATUS_LIVE);
    }

    private void callApi(int status) {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .createJob(loadRequest(status))
                    .enqueue(new Callback<ResponseObject<Job>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Job>> call,
                                               Response<ResponseObject<Job>> response) {
                            try {

                                DialogBuilder.cancelDialog(dialog);

                                if (response.isSuccessful()) {

                                    getActivity()
                                            .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(Constants.KEY_UNFINISHED, false)
                                            .putInt(Constants.KEY_STEP, 0)
                                            .remove(Constants.KEY_REQUEST)
                                            .commit();

                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame,
                                                    JobDetailsFragment.newInstance(response.body().getResponse().id))
                                            .commit();

                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response, showCRNDialog);
                                }
                            } catch (Exception e) {
                                //
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                            try {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            } catch (Exception e) {
                                //
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(getContext())
                    .setMessage("Something went wrong!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }


    private final DialogInterface.OnClickListener showCRNDialog =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int id) {
                    //
                    if (null != dialogInterface) {

                        Log.d(TAG, String.valueOf(dialogInterface.hashCode()));
                        //
                        dialogInterface.dismiss();

                    }
                    CRNDialog.newInstance(new CRNDialog.CRNListener() {
                        @Override
                        public void onResult(final boolean success) {
                            Log.d(TAG, "yes");
                            if (success) {
                                callApi(Constants.JOB_STATUS_LIVE);
                            } else {
                                new AlertDialog.Builder(getContext())
                                        .setMessage(getString(R.string.create_job_invalid_crn))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }).show(getChildFragmentManager(), "");

                }
            };
}