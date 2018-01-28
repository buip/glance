package com.prestonbui.glancesocial.models;

/**
 * Created by phuongbui on 10/24/17.
 */

public class UpVote {

    public String user_id;

    public UpVote() {

    }

    public UpVote(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UpVote{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
