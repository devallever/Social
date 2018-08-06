package com.allever.social.pojo;

import java.io.Serializable;

/**
 * Created by Allever on 2016/11/5.
 */

public class FriendLocationItem  implements Serializable{
    private String user_id;
    private String nickname;
    private String username;
    private String user_head_path;
    private double longitude;
    private double latitude;
    private String address;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_head_path() {
        return user_head_path;
    }

    public void setUser_head_path(String user_head_path) {
        this.user_head_path = user_head_path;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
