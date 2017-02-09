package com.hellobaytree.graftrs.worker.jobdetails;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.TextTools;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.jobmatches.model.Application;
import com.hellobaytree.graftrs.worker.jobmatches.model.ApplicationStatus;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class JobDetailsFragment extends Fragment implements JobDetailsContract {

    @BindView(R.id.companyLogo) ImageView companyLogo;
    @BindView(R.id.job_role) TextView itemRole;
    @BindView(R.id.job_experience_years) TextView experienceYears;
    @BindView(R.id.job_item_payment_rate_per) TextView paymentRatePer;
    @BindView(R.id.job_item_payment_rate) TextView paymentRate;
    @BindView(R.id.job_item_start_date) TextView startDate;
    @BindView(R.id.job_item_work_place) TextView workPlace;
    @BindView(R.id.btnWorkerFirstStepNext) TextView ctaButton;
    @BindView(R.id.appliedHintView) TextView appliedHeaderView;
    @BindView(R.id.approvedHintView) View approvedHeaderView;
    @BindView(R.id.reportingToTextView) TextView reportingToTextView;
    @BindView(R.id.dateToArriveTextView) TextView dateToArriveTextView;
    @BindView(R.id.elseToNoteTextView) TextView elseToNoteTextView;
    @BindView(R.id.approvedHint) View approvedHintView;
    //
    @BindView(R.id.job_details_description) JosefinSansTextView description;
    @BindView(R.id.job_details_skills) JosefinSansTextView skills;
    @BindView(R.id.job_details_qualifications) JosefinSansTextView qualifications;
    @BindView(R.id.job_details_qualifications2) JosefinSansTextView qualifications2;
    @BindView(R.id.job_details_experience_types) JosefinSansTextView experienceTypes;

    private static final String TAG = "JobDetailsFragment";
    private static final String KEY_JOB = "KEY_JOB";

    private int currentJobId;
    private Job currentJob;
    private JobDetailsPresenter presenter;
    private MenuItem likeJobMenuItem, unlikeJobMenuItem;

    private SupportMapFragment mapFragment;

    public JobDetailsFragment() {
    }

    public static JobDetailsFragment newInstance(int jobId) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_JOB, jobId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentJobId = getArguments().getInt(KEY_JOB);
        presenter = new JobDetailsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_job_details_worker, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment_worker);
    }

    private void populateData() {
        if (currentJob != null) {
            if (currentJob.company != null) {
                Picasso.with(getActivity())
                        .load(currentJob.company.logo)
                        .fit().centerCrop().into(companyLogo);
            }

            if (currentJob.role != null) {
                itemRole.setText(currentJob.role.name.toUpperCase());
            }

            experienceYears.setText(String.format(getString(R.string.item_match_format_experience),
                    currentJob.experience, getResources().getQuantityString(R.plurals.year_plural, currentJob.experience)));

            workPlace.setText(currentJob.address);
            paymentRate.setText(getString(R.string.pound_sterling) + " " + String.valueOf(currentJob.budget));
            paymentRatePer.setText(currentJob.getBudgetTypeLabel());

            if (!TextUtils.isEmpty(currentJob.startTime)) {
                startDate.setText(String.format(getString(R.string.item_match_format_starts),
                        DateUtils.formatDateDayAndMonth(currentJob.startTime, true)));
            }


//            qualificationsTextView.setText(currentJob.getQualificationsList());

            try {
                description.setText(currentJob.description);
                skills.setText(TextTools.toBulletList(currentJob.getSkillsList2(), true));
                qualifications.setText(TextTools.toBulletList(currentJob.getQualificationsList2(), true));
                experienceTypes.setText(TextTools.toBulletList(currentJob.getExperienceTypesList(), true));
            } catch (Exception e) {
                e.printStackTrace();
            }

//            experienceTextView.setText(getString(R.string.job_experience_value, currentJob.experience));


//            availabilityTextView.setText(currentJob.availableNow
//                    ? getString(R.string.job_availability_immediately)
//                    : DateUtils.formatDateDayAndMonth(currentJob.startTime, true));


            if (getCurrentAppStatus() == ApplicationStatus.STATUS_APPROVED) {
                elseToNoteTextView.setText(currentJob.extraNotes);
                dateToArriveTextView.setText(DateUtils
                        .formatDateMonthDayAndTime(currentJob.startTime));
                reportingToTextView.append(currentJob.contactName);
                reportingToTextView.append("\n");
                reportingToTextView.append(currentJob.contactPhone);

                if (currentJob.company != null) {
                    reportingToTextView.append("\n");
                    reportingToTextView.append(currentJob.company.addressFirstLine);
                }
            }
        }
    }

    private void setupApplicationData() {
        if (currentJob != null) {
            if (showCtaButton()) {
                ctaButton.setVisibility(View.VISIBLE);
                ctaButton.setText((getCurrentAppStatus() == ApplicationStatus.STATUS_APPROVED
                        || getCurrentAppStatus() == ApplicationStatus.STATUS_PENDING)
                        ? getString(R.string.employer_workers_cancel)
                        : getString(R.string.apply));
            } else {
                ctaButton.setVisibility(View.GONE);
            }

            appliedHeaderView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_PENDING ? View.VISIBLE : View.GONE);
            approvedHeaderView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_APPROVED ? View.VISIBLE : View.GONE);
            approvedHintView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_APPROVED ? View.VISIBLE : View.GONE);
        }
    }

    private boolean showCtaButton() {
        return (getCurrentAppStatus() == 0 ||
                getCurrentAppStatus() != ApplicationStatus.STATUS_CANCELLED
                        && getCurrentAppStatus() != ApplicationStatus.STATUS_DENIED
                        && getCurrentAppStatus() != ApplicationStatus.STATUS_END_CONTRACT);
    }

    private void setupMenuIconsVisibility() {
        if (currentJob == null) return;
        if (currentJob.liked) {
            likeJobMenuItem.setVisible(false);
            unlikeJobMenuItem.setVisible(true);
        } else {
            likeJobMenuItem.setVisible(true);
            unlikeJobMenuItem.setVisible(false);
        }
    }

    @OnClick(R.id.btnWorkerFirstStepNext)
    void onCtaClick() {
        if (currentJob != null) {
            if (getCurrentAppStatus() == ApplicationStatus.STATUS_APPROVED
                    || getCurrentAppStatus() == ApplicationStatus.STATUS_PENDING) {
                cancelBooking();
            } else presenter.applyToJob(currentJob.id);
        }
    }

    @Nullable
    private Application getCurrentApplication() {
        try {
            if (currentJob.application != null
                    && !currentJob.application.isEmpty()) {
                return currentJob.application.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getCurrentAppStatus() {
        int status = 0;
        if (getCurrentApplication() != null) status = getCurrentApplication().status.id;
        return status;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextTools.log(TAG, "onResume");
        presenter.fetchJob(currentJobId);
    }

    @Override
    public void onPause() {
        TextTools.log(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_worker_job_details, menu);
        super.onCreateOptionsMenu(menu, inflater);

        likeJobMenuItem = menu.findItem(R.id.job_like);
        unlikeJobMenuItem = menu.findItem(R.id.job_unlike);

        Drawable likeDrawable = menu.findItem(R.id.job_like).getIcon();
        likeDrawable = DrawableCompat.wrap(likeDrawable);
        DrawableCompat.setTint(likeDrawable, ContextCompat.getColor(getActivity(), R.color.redSquareColor));
        menu.findItem(R.id.job_like).setIcon(likeDrawable);

        Drawable shareDrawable = menu.findItem(R.id.job_share).getIcon();
        shareDrawable = DrawableCompat.wrap(shareDrawable);
        DrawableCompat.setTint(shareDrawable, ContextCompat.getColor(getActivity(), R.color.redSquareColor));
        menu.findItem(R.id.job_share).setIcon(shareDrawable);

        setupMenuIconsVisibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.job_like:
                presenter.onLikeJobClick();
                break;
            case R.id.job_unlike:
                presenter.onUnlikeJobClick();
                break;
            case R.id.job_share:
                presenter.onShareJobClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelBooking() {
        DialogBuilder.showCancelBookingDialog(getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showCancelBookingFeedbackDialog();
            }
        });
    }

    private void showCancelBookingFeedbackDialog() {
        DialogBuilder.showInputDialog(
                getContext(),
                R.string.job_feedback,
                R.string.job_feedback_hint,
                new DialogBuilder.OnTextInputDialogListener() {

                    @Override
                    public void onInputFinished(String input) {
                        if (!TextTools.trimIsEmpty(input))
                            presenter.cancelBooking(currentJob.application.get(0).id, input);
                        else
                            DialogBuilder.showStandardDialog(getContext(), "",
                                    getString(R.string.error_input_empty));
                    }
                });
    }

    @Override
    public void onJobFetched() {
        if (getActivity() == null || !isAdded()) return;
        currentJob = presenter.getCurrentJob();
        updateViews();
        if (null != mapFragment) {
            TextTools.log(TAG, "getting map async");
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //
                    if (null != currentJob.location) {
                        TextTools.log(TAG, "current job location isn't null");

                        LatLng latLng = new LatLng(Double.valueOf(currentJob.location.latitude),
                                Double.valueOf(currentJob.location.longitude));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                    } else {
                        TextTools.log(TAG, "current job location is null");
                    }
                    //
                }
            });
        } else {
            TextTools.log(TAG, "map fragment null");
        }
    }

    private void updateViews() {
        populateData();
        setupApplicationData();
        setupMenuIconsVisibility();
    }

    @Override
    public void onJobApply(Application application) {
        if (getActivity() == null || !isAdded()) return;
        TextTools.log(TAG, "onJobApply");
        JobAppliedDialog.newInstance(currentJob).show(getFragmentManager(), "JobAppliedDialog");
    }

    @Override
    public void onBookingCanceled() {
        if (getActivity() == null || !isAdded()) return;
        TextTools.log(TAG, "onBookingCanceled");
        setupApplicationData();
        DialogBuilder.showStandardDialog(getContext(), "",
                getString(R.string.job_details_booking_canceled));
    }
}
