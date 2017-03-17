package construction.thesquare.employer.myjobs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.adapter.DeclineReasonsAdapter;
import construction.thesquare.shared.models.Reason;


public class WorkerDeclineFragment extends Fragment {

    public static final String TAG = "WorkerDeclineFragment";

    @BindView(R.id.rv) RecyclerView rv;

    public WorkerDeclineFragment() {
        // Required empty public constructor
    }

    public static WorkerDeclineFragment newInstance() {
        WorkerDeclineFragment fragment = new WorkerDeclineFragment();
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
        View view = inflater.inflate(R.layout.fragment_worker_decline, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        List<Reason> data = new ArrayList<>();
        Reason reason = new Reason();
        reason.name = "sfdsfsfsd";
        data.add(reason); data.add(reason); data.add(reason);
        DeclineReasonsAdapter adapter = new DeclineReasonsAdapter(getContext(), data);
//        adapter.setListener(new DeclineReasonsAdapter.DeclineReasonListener() {
//            @Override
//            public void onReason(Reason reason) {
//                reason.selected = true;
//                adapter.notifyDataSetChanged();
//            }
//        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }
}