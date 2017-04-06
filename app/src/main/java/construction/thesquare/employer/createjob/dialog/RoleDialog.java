package construction.thesquare.employer.createjob.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.suggestions.Suggestion;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by swapna on 4/5/2017.
 */

public class RoleDialog extends DialogFragment {
    public static final String TAG = "RoleDialog";

    @BindView(R.id.dialog_role_input)
    JosefinSansEditText in;
    private RoleListener listener;

    public interface RoleListener {
        void onResult(boolean success);
    }

    public static RoleDialog newInstance(RoleListener crnListener) {
        RoleDialog dialog = new RoleDialog();
        dialog.setCancelable(false);
        dialog.listener = crnListener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_role, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
    }


    public void onDone() {
        if (validate()) {
            Suggestion suggestion = new Suggestion();
            suggestion.category = suggestion.SELECTED_ROLE_SUGGESTION;
            suggestion.description = in.getText().toString();
            final Dialog dialog = DialogBuilder.showCustomDialog(getActivity());
            HttpRestServiceConsumer.getBaseApiClient()
                        .suggestRole(suggestion)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            //
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                                TextTools.log(TAG, "successful Role suggestion call");
                                listener.onResult(true);
                            } else {
                                DialogBuilder.cancelDialog(dialog);
                                TextTools.log(TAG, "unsuccessful Role suggestion call");
                                listener.onResult(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            listener.onResult(false);
                        }
                    });
            dismiss();
        }
    }

    @OnClick({R.id.dialog_role_done, R.id.close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.dialog_role_done:
                onDone();
                break;
        }
    }

    private boolean validate() {
        boolean result = true;
        if (in.getText().toString().equals("")) {
            result = false;
        }
        return result;
    }
}
