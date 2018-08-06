package com.allever.social.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XM on 2016/4/19.
 */
public class News implements Serializable{

    private String id;
    private String content;
    private String user_id;
    private String username;
    private String nickname;
    private String sex;
    private int age;
    private String date;
    private String longitude;
    private String latitude;
    private String city;
    private String distance;
    private String user_head_path;
    private int commentcount;
    private int  lickcount;
    private int isLiked;
    private List<String> news_image_path;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getUser_head_path() {
        return user_head_path;
    }
    public void setUser_head_path(String user_head_path) {
        this.user_head_path = user_head_path;
    }
    public int getCommentcount() {
        return commentcount;
    }
    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }
    public int getLickcount() {
        return lickcount;
    }
    public void setLickcount(int lickcount) {
        this.lickcount = lickcount;
    }
    public int getIsLiked() {
        return isLiked;
    }
    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }
    public List<String> getNews_image_path() {
        return news_image_path;
    }
    public void setNews_image_path(List<String> news_image_path) {
        this.news_image_path = news_image_path;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }


}
