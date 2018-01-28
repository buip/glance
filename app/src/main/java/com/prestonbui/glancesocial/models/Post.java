package com.prestonbui.glancesocial.models;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by phuongbui on 10/22/17.
 */

public class Post {

    private String caption;
    private String image_path;
    private String post_id;
    private String user_id;
    private String tags;
    private long date_created;
    private long points;
    private long comments;

    public Post() {

    }

    public Post(String caption, String image_path, String post_id, String user_id, String tags, long date_created, long points, long comments) {
        this.caption = caption;
        this.image_path = image_path;
        this.post_id = post_id;
        this.user_id = user_id;
        this.tags = tags;
        this.date_created = date_created;
        this.points = points;
        this.comments = comments;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "caption='" + caption + '\'' +
                ", image_path='" + image_path + '\'' +
                ", post_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", date_created=" + date_created +
                ", points=" + points +
                ", comments=" + comments +
                '}';
    }

}
