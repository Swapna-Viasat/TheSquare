package com.hellobaytree.graftrs.worker.myjobs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.hellobaytree.graftrs.R;
import com.hellobaytree.graftrs.shared.view.widget.JosefinSansTextView;
import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobHolder> {

    private List<Job> data = new ArrayList<>();
    private JobsAdapter.JobsActionListener listener;
    private Context context;

    public JobsAdapter(List<Job> input, Context context,
                             JobsAdapter.JobsActionListener listener) {
        this.data = input;
        this.context = context;
        this.listener = listener;
    }

    public interface JobsActionListener {
        void onViewDetails(Job job);
        void onLikeJob(Job job);
    }

    public JobsAdapter.JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JobsAdapter.JobHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_job_worker, parent, false));
    }

    public void onBindViewHolder(JobsAdapter.JobHolder holder, int position) {
        final Job job = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewDetails(job);
            }
        });

        try {
            holder.salary.setText(context.getString(R.string.pound_sterling) + String.valueOf(NumberFormat
                    .getInstance(Locale.UK).format(Double.valueOf(job.budget))));
            if (null != job.budgetType) {
                if (null != job.budgetType.name) {
                    holder.salaryPeriod.setText("Per " + job.budgetType.name);
                }
            }

            if (null != job.owner) {
                if (null != job.owner.picture) {
                    holder.company.setVisibility(View.GONE);
                    holder.logo.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(job.owner.picture)
                            .fit()
                            .into(holder.logo);
                } else {
                    holder.company.setVisibility(View.VISIBLE);
                    holder.logo.setVisibility(View.GONE);
                }
            }

            holder.occupation.setText(job.role.name);
            holder.experience
                    .setText(String.format(context.getString(R.string.item_match_format_experience),
                            job.experience, context.getResources().getQuantityString(R.plurals.year_plural, job.experience)));

            if (null != job.company) {
                if (null != job.company.postCode) {
                    holder.location.setText(job.company.postCode);
                }
            }

            DateTime dateTime = new DateTime(job.startTime);
            holder.startDate
                    .setText(String.format(context.getString(R.string.item_match_format_starts),
                            String.valueOf(dateTime.getMonthOfYear()
                            + "." + dateTime.getDayOfMonth()
                            + "." + dateTime.getYear())));

            setLiked(job.liked, holder.liked);
            // holder.actionBlock.setVisibility(View.VISIBLE);
            holder.actionBlock.setVisibility(View.GONE);
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                }
            });
            //
            holder.company.setText(job.company.name);
            holder.id.setText("Job ID: " + String.valueOf(job.jobRef));
            holder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onLikeJob(job);
                }
            });
            if (TextUtils.equals(job.status.name, "Old")) holder.liked.setVisibility(View.GONE);
            else holder.liked.setVisibility(View.VISIBLE);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLiked(boolean liked, ImageView imageView) {
        imageView.setImageResource(liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
    }

    public int getItemCount() {
        return data.size();
    }

    public static class JobHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_job_logo) ImageView logo;
        @BindView(R.id.item_job_liked) ImageView liked;
        @BindView(R.id.item_job_start_date) JosefinSansTextView startDate;
        @BindView(R.id.item_job_location) JosefinSansTextView location;
        @BindView(R.id.item_job_salary_period) JosefinSansTextView salaryPeriod;
        @BindView(R.id.item_job_salary_number) JosefinSansTextView salary;
        @BindView(R.id.item_job_occupation) JosefinSansTextView occupation;
        @BindView(R.id.item_job_experience) JosefinSansTextView experience;
        @BindView(R.id.item_job_status) JosefinSansTextView action;
        @BindView(R.id.item_job_action_block) View actionBlock;
        @BindView(R.id.item_job_company_name) JosefinSansTextView company;
        @BindView(R.id.item_job_id) JosefinSansTextView id;
        public JobHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}