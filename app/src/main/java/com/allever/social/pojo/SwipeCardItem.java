package com.allever.social.pojo;

import java.util.List;

/**
 * Created by XM on 2016/10/14.
 */
public class SwipeCardItem  {

    private String username;
    private String nickname;
    private String sex;
    private String occupation;
    private int age;
    private String distance;
    private String signature;
    private List<String> list_imgs;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<String> getList_imgs() {
        return list_imgs;
    }

    public void setList_imgs(List<String> list_imgs) {
        this.list_imgs = list_imgs;
    }
}
