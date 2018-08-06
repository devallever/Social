package com.allever.social.pojo;

/**
 * Created by XM on 2016/4/16.
 */
import java.util.List;

public class NewsItem {
    private String id;
    private String nickname;
    private String username;
    private String time;
    private String content;
    private List<String> newsimg_list;
    private String distance;
    private String user_head_path;
    private String lickCount;
    private String city;
    private String sex;
    private int age;
    private String commentCount;
    private String user_id;
    private String isLiked;
    private String news_from;
    private String news_voice;


    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<String> getNewsimg_list() {
        return newsimg_list;
    }
    public void setNewsimg_list(List<String> newsimg_list) {
        this.newsimg_list = newsimg_list;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getLickCount() {
        return lickCount;
    }
    public void setLickCount(String lickCount) {
        this.lickCount = lickCount;
    }
    public String getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }
    public String getIsLiked() {
        return isLiked;
    }
    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public String getUser_head_path(){
        return this.user_head_path;
    }
    public void setUser_head_path(String user_head_path){
        this.user_head_path = user_head_path;
    }
    public void setCity(String city){
        this.city  =city;
    }
    public String getCity(){
        return this.city;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public String getUser_id(){
        return this.user_id;
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
    public void setNews_from(String from){
        this.news_from = from;
    }
    public String getNews_from(){
        return this.news_from;
    }

    public String getNews_voice() {
        return news_voice;
    }
    public void setNews_voice(String news_voice) {
        this.news_voice = news_voice;
    }
}
