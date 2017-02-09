package com.hellobaytree.graftrs.shared.reviews;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class Review implements Serializable {

    public static final int TYPE_GIVEN = 1;
    public static final int TYPE_RECEIVED = 2;
    public static final int CAT_PUBLISHED = 2;
    public static final int CAT_PENDING = 1;

    public static final int TAB_GIVEN = 5;
    public static final int TAB_RECEIVED = 6;

    public int id;
    public String date;
    public Status status;
    public Type type;
    @SerializedName("avg_rate") public float rating;
    public int quality;
    public int reliability;
    public int attitude;
    public int safe;
    public int environment;
    public int team;
    public int payers;
    public int induction;
    @SerializedName("work_together_again") public boolean wouldHireAgain;

    public Worker worker;
    public Job job;

    public class Job implements Serializable {
        public Role role;
        public Owner owner;
        public class Role implements Serializable {
            public String name;
        }
        public class Owner implements Serializable {
            public Company company;
            public class Company implements Serializable {
                public String name;
                public String logo;
            }
        }
    }
    public class Worker implements Serializable {
        @SerializedName("first_name") public String firstName;
        @SerializedName("last_name") public String lastName;
        @SerializedName("email") public String email;
        public String picture;
    }
}
