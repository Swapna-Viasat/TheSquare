package com.hellobaytree.graftrs.worker.account;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.Invoice;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.data.model.Timesheet;
import com.hellobaytree.graftrs.shared.data.model.TimesheetUnit;
import com.hellobaytree.graftrs.shared.models.Worker;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.shared.view.widget.RatingView;
import com.hellobaytree.graftrs.worker.main.ui.MainWorkerActivity;
import com.hellobaytree.graftrs.worker.myaccount.ui.activity.MyAccountBillingActivity;
import com.hellobaytree.graftrs.worker.myaccount.ui.activity.MyAccountViewProfileActivity;
import com.hellobaytree.graftrs.worker.reviews.activity.ReviewActivity;
import com.hellobaytree.graftrs.worker.settings.ui.activity.WorkerSettingsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/23/2016.
 */

public class AccountFragment extends Fragment {

    public static final String TAG = "AccountWorkerFragment";

    @BindView(R.id.worker_account_avatar)
    CircleImageView avatar;
    @BindView(R.id.worker_account_name)
    JosefinSansTextView name;
    @BindView(R.id.worker_account_occupation)
    TextView ocupation;
    @BindView(R.id.worker_account_rating)
    RatingView rating;
    @BindView(R.id.worker_account_completeness)
    JosefinSansTextView completeness;
    @BindView(R.id.worker_account_level)
    JosefinSansTextView level;

    @BindView(R.id.worker_account_task_counter)
    JosefinSansTextView taskCounter;
    @BindView(R.id.worker_account_reviews_counter)
    JosefinSansTextView reviewsCounter;
    @BindView(R.id.worker_account_switch)
    SwitchCompat switchCompat;

    @BindView(R.id.worker_account_leaderboards_layout)
    RelativeLayout leaderboardsLayout;
    @BindView(R.id.worker_account_my_tasks_layout)
    RelativeLayout myTasksLayout;
    @BindView(R.id.worker_account_invoices_layout)
    RelativeLayout invoicesLayout;

    private Worker meWorker;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_worker_account, menu);
        menu.getItem(0).getIcon().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.worker_settings:
                startActivity(new Intent(getActivity(), WorkerSettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_worker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureView();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchWorker();
    }

    @OnClick({R.id.worker_account_edit, R.id.worker_account_profile,
            R.id.worker_account_availability, R.id.worker_account_task,
            R.id.worker_account_reviews, R.id.worker_account_leaderboards,
            R.id.worker_account_time})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.worker_account_edit:
                startActivity(new Intent(getActivity(), MyAccountViewProfileActivity.class));
                break;
            case R.id.worker_account_profile:
                // view profile
                Intent viewProfileIntent = new Intent(getActivity(), MyAccountViewProfileActivity.class);
                startActivity(viewProfileIntent);
                break;
            case R.id.worker_account_availability:
                //
                break;
            case R.id.worker_account_task:
                // open tasks
                break;
            case R.id.worker_account_reviews:
                startActivity(new Intent(getActivity(), ReviewActivity.class));
                break;
            case R.id.worker_account_leaderboards:
                // open leaderboards
                break;
            case R.id.worker_account_time:
                startActivity(MyAccountBillingActivity.startIntent(getActivity(), mockTimesheets(), mockInvoices()));
                break;
        }
    }

    private void configureView() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean isChecked) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("available_now", isChecked);
                if (meWorker != null) {
                    HttpRestServiceConsumer.getBaseApiClient()
                            .patchWorker(meWorker.id, params)
                            .enqueue(new Callback<ResponseObject<Worker>>() {
                                @Override
                                public void onResponse(Call<ResponseObject<Worker>> call, Response<ResponseObject<Worker>> response) {
                                    setGlobalAvailability(isChecked);
                                }

                                @Override
                                public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                                    Log.e(TAG, "Error updating worker: " + t.getMessage());
                                }
                            });
                }
            }
        });
        reviewsCounter.setVisibility(View.GONE);

        // TODO Enable next version
        myTasksLayout.setVisibility(View.GONE);
        leaderboardsLayout.setVisibility(View.GONE);
        invoicesLayout.setVisibility(View.GONE);
    }

    private void setGlobalAvailability(boolean now) {
        if (getActivity() != null && getActivity() instanceof MainWorkerActivity) {
            ((MainWorkerActivity) getActivity()).setAvailability(now);
        }
    }

    private void fetchWorker() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            populate(response.body().getResponse());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        //
                    }
                });
    }

    private void populate(Worker worker) {
        try {
            meWorker = worker;
            if (!TextUtils.isEmpty(worker.picture)) {
                Picasso.with(getContext()).load(worker.picture).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.bob);
            }
            name.setText(worker.firstName + " " + worker.lastName);
            if (worker.roles.size() > 0) ocupation.setText(worker.roles.get(0).name);
            rating.setRating((int) worker.rating);
            if (worker.numReviews > 0) reviewsCounter.setVisibility(View.VISIBLE);
            reviewsCounter.setText(String.valueOf(worker.numReviews));
            switchCompat.setChecked(worker.now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Timesheet> mockTimesheets() {
        ArrayList<Timesheet> timesheets = new ArrayList<>();

        Timesheet dueOne = new Timesheet(1, "GALLIARD",
                DateUtils.stringToDate("2016-10-17T00:00:00"),
                DateUtils.stringToDate("2016-10-21T00:00:00"),
                Timesheet.TimesheetStatus.DUE,
                false,
                "");
        timesheets.add(dueOne);

        TimesheetUnit[] timesheetUnits = new TimesheetUnit[9];
        timesheetUnits[0] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-08T00:00:00"));
        timesheetUnits[1] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-09T00:00:00"));
        timesheetUnits[2] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-10-10T00:00:00"));
        timesheetUnits[3] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-10-11T00:00:00"));
        timesheetUnits[4] = new TimesheetUnit(8, 1, DateUtils.stringToDate("2016-10-12T00:00:00"));
        timesheetUnits[5] = new TimesheetUnit(8, 3, DateUtils.stringToDate("2016-10-13T00:00:00"));
        timesheetUnits[6] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-10-14T00:00:00"));
        timesheetUnits[7] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-15T00:00:00"));
        timesheetUnits[8] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-16T00:00:00"));

        Timesheet dueTwo = new Timesheet(2, "SKANSKA",
                DateUtils.stringToDate("2016-10-08T00:00:00"),
                DateUtils.stringToDate("2016-10-16T00:00:00"),
                Timesheet.TimesheetStatus.DUE,
                true,
                "Didn't work on tuesday",
                timesheetUnits);
        timesheets.add(dueTwo);


        timesheets.add(mockApprovedTimesheet());

        return timesheets;
    }

    private Timesheet mockApprovedTimesheet() {
        TimesheetUnit[] timesheetUnits = new TimesheetUnit[7];
        timesheetUnits[0] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-01T00:00:00"));
        timesheetUnits[1] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-10-02T00:00:00"));
        timesheetUnits[2] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-10-03T00:00:00"));
        timesheetUnits[3] = new TimesheetUnit(8, 1, DateUtils.stringToDate("2016-10-04T00:00:00"));
        timesheetUnits[4] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-10-05T00:00:00"));
        timesheetUnits[5] = new TimesheetUnit(8, 2, DateUtils.stringToDate("2016-10-06T00:00:00"));
        timesheetUnits[6] = new TimesheetUnit(4, 0, DateUtils.stringToDate("2016-10-07T00:00:00"));

        return new Timesheet(3, "GALLIARD",
                DateUtils.stringToDate("2016-10-01T00:00:00"),
                DateUtils.stringToDate("2016-10-07T00:00:00"),
                Timesheet.TimesheetStatus.APPROVED,
                false,
                "",
                timesheetUnits);
    }

    private Timesheet mockPaidTimesheet() {
        TimesheetUnit[] timesheetUnits = new TimesheetUnit[7];
        timesheetUnits[0] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-09-24T00:00:00"));
        timesheetUnits[1] = new TimesheetUnit(0, 0, DateUtils.stringToDate("2016-09-25T00:00:00"));
        timesheetUnits[2] = new TimesheetUnit(8, 0, DateUtils.stringToDate("2016-09-26T00:00:00"));
        timesheetUnits[3] = new TimesheetUnit(8, 4, DateUtils.stringToDate("2016-09-27T00:00:00"));
        timesheetUnits[4] = new TimesheetUnit(4, 0, DateUtils.stringToDate("2016-09-28T00:00:00"));
        timesheetUnits[5] = new TimesheetUnit(8, 5, DateUtils.stringToDate("2016-09-29T00:00:00"));
        timesheetUnits[6] = new TimesheetUnit(2, 0, DateUtils.stringToDate("2016-09-30T00:00:00"));

        return new Timesheet(4, "GALLIARD",
                DateUtils.stringToDate("2016-09-24T00:00:00"),
                DateUtils.stringToDate("2016-09-30T00:00:00"),
                Timesheet.TimesheetStatus.APPROVED,
                false,
                "",
                timesheetUnits);
    }

    private ArrayList<Invoice> mockInvoices() {
        ArrayList<Invoice> invoices = new ArrayList<>();

        Invoice approvedOne = new Invoice(mockApprovedTimesheet(), Invoice.InvoiceStatus.APPROVED, null);
        invoices.add(approvedOne);

        Invoice paidOne = new Invoice(mockPaidTimesheet(),
                Invoice.InvoiceStatus.PAID,
                DateUtils.stringToDate("2016-10-08T00:00:00"));
        invoices.add(paidOne);

        return invoices;
    }
}
