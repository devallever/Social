package com.allever.social.modules.main.nearByUser.bean;

/**
 * Created by allever on 17-6-19.
 */
public class User_list {
    private String id;

    private String username;

    private String nickname;

    private String sex;

    private double distance;

    private String user_head_path;

    private String signature;

    private int age;

    private String constellation;

    private String occupation;

    private int is_vip;

    private int video_fee;

    private int accetp_video;

    private String login_time;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public String getSex(){
        return this.sex;
    }
    public void setDistance(double distance){
        this.distance = distance;
    }
    public double getDistance(){
        return this.distance;
    }
    public void setUser_head_path(String user_head_path){
        this.user_head_path = user_head_path;
    }
    public String getUser_head_path(){
        return this.user_head_path;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    public String getSignature(){
        return this.signature;
    }
    public void setAge(int age){
        this.age = age;
    }
    public int getAge(){
        return this.age;
    }
    public void setConstellation(String constellation){
        this.constellation = constellation;
    }
    public String getConstellation(){
        return this.constellation;
    }
    public void setOccupation(String occupation){
        this.occupation = occupation;
    }
    public String getOccupation(){
        return this.occupation;
    }
    public void setIs_vip(int is_vip){
        this.is_vip = is_vip;
    }
    public int getIs_vip(){
        return this.is_vip;
    }
    public void setVideo_fee(int video_fee){
        this.video_fee = video_fee;
    }
    public int getVideo_fee(){
        return this.video_fee;
    }
    public void setAccetp_video(int accetp_video){
        this.accetp_video = accetp_video;
    }
    public int getAccetp_video(){
        return this.accetp_video;
    }
    public void setLogin_time(String login_time){
        this.login_time = login_time;
    }
    public String getLogin_time(){
        return this.login_time;
    }

}