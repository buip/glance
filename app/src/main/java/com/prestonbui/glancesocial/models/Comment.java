package com.prestonbui.glancesocial.models;

/**
 * Created by phuongbui on 10/27/17.
 */

public class Comment {

    private String caption;
    private String user_id;
    private long date_created;

    public Comment() {

    }

    public Comment(String caption, String user_id, long date_created) {
        this.caption = caption;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "caption='" + caption + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created=" + date_created +
                '}';
    }
}
