/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StaticData implements Serializable {

    @SerializedName("job_status")
    public List<Job.Status> jobStatuses;
    @SerializedName("english_level")
    public List<EnglishLevel> englishLevels;
}
