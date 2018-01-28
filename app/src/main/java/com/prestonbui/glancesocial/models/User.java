package com.prestonbui.glancesocial.models;

/**
 * Created by phuongbui on 10/16/17.
 */

public class User {

    private String handlename;

    private String user_id;

    private String email;

    private int points;

    private int posts;

    public User(String handlename, String user_id, String email, int points, int posts) {
        this.handlename = handlename;
        this.user_id = user_id;
        this.email = email;
        this.points = points;
        this.posts = posts;
    }

    public User() {

    }

    public String getHandlename() {
        return handlename;
    }

    public void setHandlename(String handlename) {
        this.handlename = handlename;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "User{" +
                "handlename='" + handlename + '\'' +
                ", user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", points=" + points +
                ", posts=" + posts +
                '}';
    }
}
