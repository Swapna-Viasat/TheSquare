package com.hellobaytree.graftrs.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.employer.payments.adapter.PaymentsAdapter;
import com.hellobaytree.graftrs.shared.data.HttpRestServiceConsumer;
import com.hellobaytree.graftrs.shared.data.model.ResponseObject;
import com.hellobaytree.graftrs.shared.utils.DialogBuilder;
import com.hellobaytree.graftrs.shared.utils.HandleErrors;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    @BindView(R.id.subscription_pager) ViewPager pager;
    private int selectedPlan;

    @BindViews({R.id.top_basic, R.id.top_standard, R.id.top_premium})
    List<ViewGroup> top;

    public static SubscriptionFragment newInstance() {
        SubscriptionFragment fragment = new SubscriptionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_price_plan_nested, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                getActivity().getSupportFragmentManager()
                        .popBackStack();
                return true;
        }
        return false;
    }

    @OnClick(R.id.payments_continue)
    public void proceed() {

        getActivity().getSupportFragmentManager()
                .popBackStack();
//
//        ((TextView) getActivity().findViewById(R.id.payments_subscription_label))
//                .setTextColor(ContextCompat.getColor(getContext(), R.color.graySquareColor));
//        ((TextView) getActivity().findViewById(R.id.payments_cards_label))
//                .setTextColor(ContextCompat.getColor(getContext(), R.color.whiteSquareColor));
//        ((ImageView) getActivity().findViewById(R.id.payments_subscription))
//                .setColorFilter(ContextCompat.getColor(getContext(), R.color.graySquareColor));
//        ((ImageView) getActivity().findViewById(R.id.payments_cards))
//                .setColorFilter(ContextCompat.getColor(getContext(), R.color.whiteSquareColor));
//
//        getActivity().
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.payments_content, PaymentFragment.newInstance(selectedPlan))
//                .commit();

//        //
//        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
//        HashMap<String, Object> request = new HashMap<>();
//        // TODO: need further guidance from backend guys
//        request.put("payment_detail", 3);
//        request.put("stripe_id", "pk_test_iUGx8ZpCWm6GeSwBpfkdqjSQ");
//        HttpRestServiceConsumer.getBaseApiClient()
//                .subscribe(request)
//                .enqueue(new Callback<ResponseObject>() {
//                    @Override
//                    public void onResponse(Call<ResponseObject> call,
//                                           Response<ResponseObject> response) {
//                        //
//                        if (response.isSuccessful()) {
//                            DialogBuilder.cancelDialog(dialog);
//                            //
//                        } else {
//                            HandleErrors.parseError(getContext(), dialog, response);
//                            //
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseObject> call, Throwable t) {
//                        //
//                        HandleErrors.parseFailureError(getContext(), dialog, t);
//                    }
//                });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Change Plan");
        } catch (Exception e) {
            e.printStackTrace();
        }
        select(1);
        PaymentsAdapter adapter = new PaymentsAdapter();
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                select(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
    }

    @OnClick({R.id.top_premium, R.id.top_basic, R.id.top_standard})
    public void go(View view) {
        switch (view.getId()) {
            case R.id.top_basic:
                pager.setCurrentItem(0);
                select(0);
                break;
            case R.id.top_standard:
                pager.setCurrentItem(1);
                select(1);
                break;
            case R.id.top_premium:
                pager.setCurrentItem(2);
                select(2);
                break;
        }
    }

    private void select(int id) {
        selectedPlan = id;
        for (int i = 0; i < 3; i++) {
            if (i == id) {
                top.get(i).setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.redSquareColor));
                for (int j = 0; j < top.get(i).getChildCount(); j++) {
                    if (top.get(i).getChildAt(j) instanceof TextView) {
                        ((TextView) top.get(i).getChildAt(j)).setTextColor(ContextCompat
                                    .getColor(getContext(), R.color.whiteSquareColor));
                    } else if (top.get(i).getChildAt(j) instanceof ImageView) {
                        top.get(i).getChildAt(j)
                                .setBackgroundColor(ContextCompat
                                        .getColor(getContext(), R.color.whiteSquareColor));
                    }
                }
            } else {
                top.get(i).setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.whiteSquareColor));
                for (int j = 0; j < top.get(i).getChildCount(); j++) {
                    if (top.get(i).getChildAt(j) instanceof TextView) {
                        ((TextView) top.get(i).getChildAt(j)).setTextColor(ContextCompat
                                .getColor(getContext(), R.color.redSquareColor));
                    } else if (top.get(i).getChildAt(j) instanceof ImageView) {
                        top.get(i).getChildAt(j)
                                .setBackgroundColor(ContextCompat
                                        .getColor(getContext(), R.color.redSquareColor));
                    }
                }
            }
        }
    }
}