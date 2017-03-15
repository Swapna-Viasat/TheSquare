package construction.thesquare.employer.help.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 3/13/2017.
 */

public class Help implements Serializable {
    public int id;
    @SerializedName("question") public String question;
    @SerializedName("answer") public String answer;
    @SerializedName("order") public String order;
}
