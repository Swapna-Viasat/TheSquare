package construction.thesquare.worker.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.settings.fragments.SettingsContactFragment;
import construction.thesquare.shared.view.widget.JosefinSansEditText;

/**
 * Created by swapna on 3/8/2017.
 */

public class HelpFragment extends Fragment{
    @BindView(R.id.search_input)
    JosefinSansEditText search;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_worker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick({R.id.search_button, R.id.contact_us})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                if (validateFields())
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, HelpDetailsFragment.newInstance(search.getText().toString()))
                        .addToBackStack("")
                        .commit();
                break;
            case R.id.contact_us:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, SettingsContactFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
        }
    }

    private boolean validateFields() {
        boolean result = true;
        if (TextUtils.isEmpty(search.getText().toString())) {
            search.setError("Please enter your message");
            result = false;
        }
        return result;
    }
}
