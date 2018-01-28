package com.prestonbui.glancesocial.models;

/**
 * Created by phuongbui on 10/24/17.
 */

public class DownVote {

    public String user_id;

    public DownVote(String user_id) {
        this.user_id = user_id;
    }

    public DownVote() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "DownVote{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
