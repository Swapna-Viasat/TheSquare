package com.hellobaytree.graftrs.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 2/28/2017.
 */
public class WorkerSummary implements Serializable {
    public int id;
    @SerializedName("role") public String role;
    @SerializedName("picture") public String picture;
    @SerializedName("name") public String name;

    public WorkerSummary(int id, String role, String name, String picture) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.picture = picture;
    }
}
