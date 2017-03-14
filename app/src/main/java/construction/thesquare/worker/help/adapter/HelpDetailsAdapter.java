package construction.thesquare.worker.help.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.help.model.Help;

/**
 * Created by swapna on 3/13/2017.
 */

public class HelpDetailsAdapter extends RecyclerView.Adapter<HelpDetailsAdapter.HelpDetailsHolder> {
    private List<Help> data = new ArrayList<>();
    private Context context;
    private HelpDetailsListener listener;
    public HelpDetailsAdapter(List<Help> list, Context context, HelpDetailsListener listener) {
        this.data = list;
        this.context = context;
        this.listener = listener;
    }

    public interface HelpDetailsListener {
        // void onViewDetails(Review review);
        // void onCompleteReview(Review review);
    }

    @Override
    public HelpDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HelpDetailsHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_help_details, parent, false));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HelpDetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question)
        JosefinSansTextView question;
        @BindView(R.id.answer)
        JosefinSansTextView answer;
        @BindView(R.id.webview)
        WebView webview;
        public HelpDetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBindViewHolder(final HelpDetailsHolder holder, int position) {
        final Help faq = data.get(position);
        holder.answer.setVisibility(View.GONE);
        if (faq.question != null) {
            holder.question.setText(faq.question);
        }
        WebSettings settings= holder.webview.getSettings();
        settings.setDefaultFontSize(12);
        if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.answer.setVisibility(View.VISIBLE);
                    holder.answer.setText(Html.fromHtml(faq.answer));
                   // holder.webview.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
                }
            });
        }
    }
}