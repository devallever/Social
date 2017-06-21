package com.allever.social.foundModule.bean;

/**
 * Created by Allever on 2016/12/2.
 */

public class UserBeen {
    private String username;
    private String nickname;
    private String head_path;
    private String sex;
    private int age;
    private String occupation;
    private int is_accept_video;
    private String login_time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_path() {
        return head_path;
    }

    public void setHead_path(String head_path) {
        this.head_path = head_path;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getIs_accept_video() {
        return is_accept_video;
    }

    public void setIs_accept_video(int is_accept_video) {
        this.is_accept_video = is_accept_video;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }
}
