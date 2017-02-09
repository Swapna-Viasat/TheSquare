package com.hellobaytree.graftrs.worker.signup.model;

import com.hellobaytree.graftrs.shared.data.model.User;

/**
 * Created by juanmaggi on 26/7/16.
 */
public class WorkerVerify {

    private String token;
    private User user;
    private boolean verified;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
