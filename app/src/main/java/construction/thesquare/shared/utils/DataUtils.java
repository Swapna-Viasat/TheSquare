package construction.thesquare.shared.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by juanmaggi on 16/9/16.
 */
public class DataUtils {

    @Nullable
    public static String getDeviceEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        return ((account == null) ? null : account.name);
    }

    @Nullable
    private static Account getAccount(AccountManager accountManager) {
        try {
            Account[] accounts = accountManager.getAccountsByType("com.google");
            return ((accounts.length > 0) ? accounts[0] : null);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
