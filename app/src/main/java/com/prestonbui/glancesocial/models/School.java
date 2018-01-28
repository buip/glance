package com.prestonbui.glancesocial.models;

/**
 * Created by phuongbui on 11/11/17.
 */

public class School {

    private String color;
    private String email;
    private String name;
    private String image_path;
    private long num_members;
    private long points;

    public School() {

    }

    public School(String color, String email, String name, String image_path, long num_members, long points) {
        this.color = color;
        this.email = email;
        this.name = name;
        this.image_path = image_path;
        this.num_members = num_members;
        this.points = points;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public long getNum_members() {
        return num_members;
    }

    public void setNum_members(long num_members) {
        this.num_members = num_members;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "School{" +
                "color='" + color + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", image_path='" + image_path + '\'' +
                ", num_members=" + num_members +
                ", points=" + points +
                '}';
    }
}
