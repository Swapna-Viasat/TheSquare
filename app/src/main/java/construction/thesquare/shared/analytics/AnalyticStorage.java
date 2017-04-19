package construction.thesquare.shared.analytics;

import android.content.Context;

/**
 * Created by gherg on 4/19/17.
 */

public class AnalyticStorage {

    public static final String TAG = "AnalyticStorage";
    private static final String sharedPrefName = "analytics_storage";
    private static final String keyId = "key_client_id";

    public static void persistGAClientId(Context c, String key) {
        try {
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .edit().putString(keyId, key).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getGAClientId(Context c) {
        try {
            return
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .getString(keyId, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
