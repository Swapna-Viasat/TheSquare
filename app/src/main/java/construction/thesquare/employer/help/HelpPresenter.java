package construction.thesquare.employer.help;

import android.content.Context;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.models.HelpWorkerResponse;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.employer.help.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by swapna on 3/15/2017.
 */

public class HelpPresenter implements construction.thesquare.worker.help.HelpContract.UserActionListener {

    public static final String TAG = "HelpPresenter";
    private final construction.thesquare.employer.help.HelpContract.View mHelpView;
    private Context context;

    public HelpPresenter(construction.thesquare.employer.help.HelpContract.View view, Context context) {
        this.context = context;
        this.mHelpView = view;
    }

    @Override
    public void fetchSearch(final String search) {

        Call<HelpWorkerResponse> call = HttpRestServiceConsumer.getBaseApiClient().getSearchData(search);
        call.enqueue(new Callback<HelpWorkerResponse>() {
            @Override
            public void onResponse(Call<HelpWorkerResponse> call, Response<HelpWorkerResponse> response) {
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mHelpView.displaySearchData(response.body().response);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HelpWorkerResponse> call, Throwable t) {
                TextTools.log(TAG, (t.getMessage() != null) ? t.getMessage() : "");
            }
        });
    }


}
