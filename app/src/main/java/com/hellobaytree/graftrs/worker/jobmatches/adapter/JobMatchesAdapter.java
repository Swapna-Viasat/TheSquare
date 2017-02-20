package com.hellobaytree.graftrs.worker.jobmatches.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.utils.DateUtils;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgheni on 10/28/2016.
 */

public class JobMatchesAdapter extends RecyclerView.Adapter<JobMatchesAdapter.JobHolder> {

    private List<Job> data = new ArrayList<>();
    private JobMatchesActionListener listener;
    private Context context;

    public JobMatchesAdapter(Context context, JobMatchesActionListener jobMatchesActionListener) {
        this.context = context;
        this.listener = jobMatchesActionListener;
    }

    public interface JobMatchesActionListener {
        void onViewDetails(Job job);

        void onLikeJob(Job job);
    }

    public void setData(List<Job> input) {
        this.data = input;
        notifyDataSetChanged();
    }

    public JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JobHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false));
    }

    public void onBindViewHolder(JobHolder holder, int position) {
        final Job job = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onViewDetails(job);
            }
        });

        try {
            holder.salary.setText(String.valueOf("£ " + job.budget));
            holder.salaryPeriod.setText("PER " + job.budgetType.name);

            Picasso.with(context)
                    .load(job.owner.picture)
                    .fit()
                    .centerCrop()
                    .into(holder.logo);

            holder.occupation.setText(job.role.name);
            holder.experience
                    .setText(String.format(context.getString(R.string.item_match_format_experience),
                            job.experience, context.getResources().getQuantityString(R.plurals.year_plural, job.experience)));

            if (null != job.company) {
                if (null != job.company.postCode) {
                    holder.location.setText(job.company.postCode);
                }
            }

            setLiked(job.liked, holder.likeImage);
            holder.likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onLikeJob(job);
                }
            });

            if (!TextUtils.isEmpty(job.startTime)) {
                holder.startDateTextView.setText(String.format(context.getString(R.string.item_match_format_starts),
                        DateUtils.formatDateDayAndMonth(job.startTime, true)));
            }
            holder.companyName.setText(job.company.name);
            holder.jobId.setText(context.getString(R.string.job_id, job.jobRef));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLiked(boolean liked, ImageView imageView) {
        imageView.setImageResource(liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
    }

    public int getItemCount() {
        return (data == null || data.isEmpty()) ? 0 : data.size();
    }

    public static class JobHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.match_logo)
        ImageView logo;
        @BindView(R.id.item_match_location)
        JosefinSansTextView location;
        @BindView(R.id.worker_details_salary_period)
        JosefinSansTextView salaryPeriod;
        @BindView(R.id.worker_details_salary_number)
        JosefinSansTextView salary;
        @BindView(R.id.worker_details_occupation)
        JosefinSansTextView occupation;
        @BindView(R.id.worker_details_experience)
        JosefinSansTextView experience;
        @BindView(R.id.likeImage)
        ImageView likeImage;
        @BindView(R.id.item_match_start_date)
        JosefinSansTextView startDateTextView;
        @BindView(R.id.item_job_company_name)
        JosefinSansTextView companyName;
        @BindView(R.id.job_id)
        JosefinSansTextView jobId;

        public JobHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
