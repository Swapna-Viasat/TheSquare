package construction.thesquare.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricePlanFragment extends Fragment {

    public static final String TAG = "PricePlanFragment";

    @BindView(R.id.due_date) TextView dueDate;
    @BindView(R.id.plan) TextView plan;

    @BindView(R.id.topup_digits) TextView topupDigits;
    @BindView(R.id.plan_digits) TextView planDigits;

    @BindView(R.id.plan_expiration) TextView planExpiration;
    @BindView(R.id.topup_expiration) TextView topupExpiration;

    public PricePlanFragment() {
        // Required empty public constructor
    }

    public static PricePlanFragment newInstance() {
        PricePlanFragment fragment = new PricePlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_plan, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("My Price Plan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //
        fetchEmployer();
    }

    private void fetchEmployer() {
        //
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            if (null != response.body()) {
                                if (null != response.body().getResponse()) {
                                    populate(response.body().getResponse());
                                }
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void displayCurrentPlan(String planName) {
        try {
            plan.setText(planName);
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayBillDueDate(String date) {
        try {
            dueDate.setText(String.format(getString(R.string.payments_bill_due_format), date));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate(Employer employer) {
        //
        if (null != employer) {
            displayBookings(employer.maxForPlan, employer.bookedWithPlan,
                    employer.maxForTopups, employer.bookedWithTopups);
            if (null != employer.planExpiration) {
                displayBillDueDate(employer.planExpiration);
                displayPlanExpiration(employer.planExpiration);
            }
            if (null != employer.topupExpiration) {
                displayTopupExpiration(employer.topupExpiration);
            }
            if (null != employer.planName) {
                displayCurrentPlan(employer.planName);
            }
        }
    }

    private void displayPlanExpiration(String planExpiry) {
        try {
            planExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), planExpiry));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTopupExpiration(String topupExpiry) {
        try {
            topupExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), topupExpiry));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayBookings(int planMax, int planUsed, int topMax, int topUsed) {
        try {
            planDigits.setText(String.format(getString(R.string.payments_plans_display_bookings),
                    planUsed, planMax));
            topupDigits.setText(String.format(getString(R.string.payments_topups_display_bookings),
                    topUsed, topMax));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.change_plan)
    public void changePlan() {
        //Toast.makeText(getContext(), "Change", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, SubscriptionFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.cancel)
    public void cancelPlan() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_cancel_plan);
        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                proceedWithCancelling();
            }
        });
        dialog.show();
    }

    private void proceedWithCancelling() {
        Toast.makeText(getContext(), "Cancelling...", Toast.LENGTH_LONG).show();
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .cancelAll()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_LONG);
                            fetchEmployer();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.change_card)
    public void changeCard() {
        //Toast.makeText(getContext(), "Change card", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, PaymentFragment.newInstance(1))
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.top_up)
    public void topUp() {
        //Toast.makeText(getContext(), "Top Up", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, TopUpFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.alternative_payment)
    public void alternative() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, AlternativePayFragment.newInstance())
                .addToBackStack("")
                .commit();
    }
}