package construction.thesquare.shared.login.model;

import android.content.Context;

import java.util.HashMap;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import retrofit2.Callback;

/**
 * Created by gherg on 3/28/17.
 */

public class AccountValidator {

    public void validate(String email,
                             String password,
                             Callback<ResponseObject<LoginUser>> callback) {
        HashMap<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        HttpRestServiceConsumer.getBaseApiClient()
                .loginUser(body)
                .enqueue(callback);
    }
}
