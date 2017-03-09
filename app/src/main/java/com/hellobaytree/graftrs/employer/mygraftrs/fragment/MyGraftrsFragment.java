package com.hellobaytree.graftrs.employer.mygraftrs.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.mygraftrs.WorkerDetailsActivity;
import com.hellobaytree.graftrs.employer.mygraftrs.adapter.WorkersAdapter;
import com.hellobaytree.graftrs.employer.mygraftrs.model.Worker;
import com.hellobaytree.graftrs.employer.mygraftrs.presenter.WorkerContract;
import com.hellobaytree.graftrs.employer.mygraftrs.presenter.WorkersPresenter;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyGraftrsFragment extends Fragment
        implements WorkersAdapter.WorkersActionListener,
                        WorkerContract.View {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.no_matches) View noMatches;
    private List<Worker> workers = new ArrayList<>();
    private WorkersAdapter adapter;
    private WorkersPresenter presenter;
    private Dialog dialog;
    private Calendar calendar;
    private Typeface josefineSans;
    public static final int PAGE_BOOKED = 99;
    public static final int PAGE_OFFERS = 98;
    public static final int PAGE_LIKED = 97;
    public static final int PAGE_PREVIOUS = 96;
    private int employerId;

    public static MyGraftrsFragment newInstance(int category) {
        Bundle bundle = new Bundle(); bundle.putInt("category", category);
        MyGraftrsFragment myGraftrsFragment = new MyGraftrsFragment();
        myGraftrsFragment.setArguments(bundle); return myGraftrsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        employerId = SharedPreferencesManager.getInstance(getContext()).getEmployerId();
        dialog = new Dialog(getContext());
        calendar = Calendar.getInstance();
        presenter = new WorkersPresenter(this);
        josefineSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/JosefinSans-Italic.ttf");
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_graftrs_stub, null);
        ButterKnife.bind(this, view); return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkersAdapter(workers, getContext(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.fetchWorkers(employerId);
    }

    // Presenter callbacks
    public void showWorkerList(List<Worker> data) {
        noMatches.setVisibility(View.GONE);
        if (!workers.isEmpty()) workers.clear();

        if (getArguments().getInt("category") == PAGE_LIKED) {
            for (Worker worker : data) {
                if (worker.liked) workers.add(worker);
            }
        } else {
            workers.addAll(data);
        }

        adapter.notifyDataSetChanged();
    }
    public void showEmptyList() {
        noMatches.setVisibility(View.VISIBLE);
    }
    public void showProgress(boolean show) {
        if (show) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }
    public void showError(String message) {
        new AlertDialog.Builder(getContext()).setMessage(message).show();
    }

    // Adapter callbacks
    public void onViewDetails(final Worker worker) {
        startActivity(new Intent(getActivity(), WorkerDetailsActivity.class));
    }
    public void onQuickInvite(final Worker worker) {
        dialog.setContentView(R.layout.dialog_quick_invite);
        ((JosefinSansTextView) dialog.findViewById(R.id.dialog_quick_invite_main))
                .setText(String.format(getString(R.string.employer_dialog_quick_invite), worker.name));
        dialog.findViewById(R.id.dialog_quick_invite_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("Under development").create().show();
            }
        });
        dialog.findViewById(R.id.dialog_quick_invite_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("Under development").create().show();
            }
        });
        dialog.show();
    }
    public void onCancelBooking(final Worker worker) {
        dialog.setContentView(R.layout.dialog_booking_cancel);
        dialog.findViewById(R.id.dialog_booking_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("Under development").create().show();
            }
        });
        dialog.findViewById(R.id.dialog_booking_keep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("Under development").create().show();
            }
        });
        dialog.show();
    }
    public void onEndContract(final Worker worker) {
        dialog.setContentView(R.layout.dialog_contract_end);
        final RadioButton workedYes = ((RadioButton) dialog.findViewById(R.id.worked_yes));
        final RadioButton workedNo = ((RadioButton) dialog.findViewById(R.id.worked_no));
        workedYes.setText(String.format(getString(R.string.employer_dialog_contract_worked), worker.name));
        workedYes.setTypeface(josefineSans);
        workedNo.setText(String.format(getString(R.string.employer_dialog_contract_worked_not), worker.name));
        workedNo.setTypeface(josefineSans);
        dialog.findViewById(R.id.dialog_contract_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_contract_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmEndContract((workedYes.isChecked()) ? true : false, worker);
            }
        });
        dialog.show();
    }

    private void showConfirmEndContract(boolean worked, final Worker worker) {
        dialog.setContentView(worked ? R.layout.dialog_contract_worked : R.layout.dialog_contract_worked_not);
        dialog.findViewById(R.id.dialog_contract_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_contract_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showConfirmedEndContract(worker);
            }
        });
        if (worked) {
            final JosefinSansTextView dateTextView = (JosefinSansTextView) dialog.findViewById(R.id.dialog_date_text);
            ((JosefinSansTextView) dialog.findViewById(R.id.dialog_contract_worked))
                    .setText(String.format(getString(R.string.employer_dialog_contract_last_day), worker.name));
            dialog.findViewById(R.id.dialog_last_day).setOnClickListener(showDatePickerListener(dateTextView));
            dateTextView.setOnClickListener(showDatePickerListener(dateTextView));
        } else {
            final CheckBox noShow = (CheckBox) dialog.findViewById(R.id.dialog_contract_worked_not_check);
            noShow.setTypeface(josefineSans);
        }
    }
    private void showConfirmedEndContract(Worker worker) {
        dialog.setContentView(R.layout.dialog_contract_end_confirm);
        dialog.findViewById(R.id.dialog_contract_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((JosefinSansTextView) dialog.findViewById(R.id.dialog_contract_name))
                .setText(String.format(getString(R.string.employer_dialog_contract_ended_name), worker.name));
    }

    private View.OnClickListener showDatePickerListener(final JosefinSansTextView resultTextView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                resultTextView.setText(formatDate(year, monthOfYear, dayOfMonth));
                            }
                        },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        };
    }

    private String formatDate(int year, int month, int day) {
        String result = String.valueOf(month + 1) + " / "
                + String.valueOf(day) + " / " + String.valueOf(year);
        return result;
    }
}