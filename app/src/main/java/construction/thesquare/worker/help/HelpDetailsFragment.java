package construction.thesquare.worker.help;

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
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.models.Help;
import construction.thesquare.shared.settings.fragments.SettingsContactFragment;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.help.adapter.HelpDetailsAdapter;


public class HelpDetailsFragment extends Fragment  implements
        HelpDetailsAdapter.HelpDetailsListener, HelpContract.View{
    private List<Help> data = new ArrayList<>();
    private HelpDetailsAdapter adapter;
    private HelpContract.UserActionListener mUserActionListener;
    @BindView(R.id.worker_search_rv)
    RecyclerView rv;
    @BindView(R.id.no_matches)
    JosefinSansTextView noMatches;

    public static HelpDetailsFragment newInstance(String search) {
        HelpDetailsFragment helpDetailsFragment = new HelpDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        helpDetailsFragment.setArguments(bundle);
        return helpDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserActionListener = new HelpPresenter(this,getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HelpDetailsAdapter(data, getContext(), this);
        adapter.registerAdapterDataObserver(observer);
        rv.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        mUserActionListener.fetchSearch(getArguments().getString("search"));
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (data.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
            } else {
                noMatches.setVisibility(View.GONE);
            }
        }
    };

    @OnClick({R.id.no_matches})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.no_matches:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, SettingsContactFragment.newInstance())
                        .commit();
                break;
        }
    }

    @Override
    public void displaySearchData(List<Help> helpdata) {
        if (!data.isEmpty()) data.clear();
        for (Help help : helpdata) {
            data.add(help);
        }
        adapter.notifyDataSetChanged();
    }

}
