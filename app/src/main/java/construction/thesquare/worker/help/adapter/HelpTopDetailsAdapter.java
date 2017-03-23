package construction.thesquare.worker.help.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import construction.thesquare.shared.models.Help;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by swapna on 3/13/2017.
 */

public class HelpTopDetailsAdapter extends RecyclerView.Adapter<HelpTopDetailsAdapter.HelpTopDetailsHolder> {
    private List<Help> data = new ArrayList<>();
    private Context context;
    private HelpTopDetailsListener listener;

    public HelpTopDetailsAdapter(List<Help> list, Context context, HelpTopDetailsListener listener) {
        this.data = list;
        this.context = context;
        this.listener = listener;
    }

    public interface HelpTopDetailsListener {

    }

    @Override
    public HelpTopDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HelpTopDetailsHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_top_five_help_details, parent, false));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HelpTopDetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question)
        JosefinSansTextView question;

        @BindView(R.id.webview)
        WebView webview;
        public HelpTopDetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBindViewHolder(final HelpTopDetailsHolder holder, int position) {
        final Help faq = data.get(position);
        holder.webview.setVisibility(View.GONE);
        if (faq.question != null) {
            holder.question.setText(faq.question);
        }

       WebSettings settings= holder.webview.getSettings();
        settings.setDefaultFontSize(10);
        settings.setSansSerifFontFamily("sans-serif");
        if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.webview.setVisibility(View.VISIBLE);
                    holder.webview.loadDataWithBaseURL(null, faq.answer, "text/html", "utf-8", null);
                }
            });
        }
    }
}