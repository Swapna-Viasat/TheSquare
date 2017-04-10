package construction.thesquare.shared.suggestions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 4/6/2017.
 */

public class Suggestion implements Serializable {
    public static final String SELECTED_ROLE_SUGGESTION = "Role";
    public static final String SELECTED_QUALIFICATION_SUGGESTION = "Qualifications";
    public static final String SELECTED_SKILL_SUGGESTION = "Skill";
    public static final String SELECTED_EXPERIENCE_SUGGESTION = "Experience";
    @SerializedName("category")
    public String category;
    @SerializedName("description")
    public String description;
}
