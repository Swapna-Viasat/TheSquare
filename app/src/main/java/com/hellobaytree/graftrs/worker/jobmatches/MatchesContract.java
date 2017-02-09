package com.hellobaytree.graftrs.worker.jobmatches;

import android.content.Context;

import com.hellobaytree.graftrs.worker.jobmatches.model.Job;
import com.hellobaytree.graftrs.worker.jobmatches.model.Ordering;

import java.util.List;

/**
 * Created by Evgheni on 11/1/2016.
 */

public interface MatchesContract {

    interface View {
        void displayMatches(List<Job> data);
        void displayError(String message);
        void displayProgress(boolean show);
        void displayHint(boolean show);
    }

    interface UserActionListener {
        void onShowDetails(Context context, Job job);
        void onLikeJobClick(Context context, Job job);
        void fetchJobMatches();
        void setMatchesFilters(Ordering ordering, int commuteTime);
        Ordering getOrdering();
        void fetchMe(Context context);
    }
}
