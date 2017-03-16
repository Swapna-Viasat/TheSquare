package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class Country implements Serializable {
    public int id;
    public String name;
    @SerializedName("short_code") public String shortCode;
    @SerializedName("phone_prefix") public String phonePrefix;
    public String flag;
}
