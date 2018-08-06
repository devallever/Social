package com.allever.social.pojo;

import java.util.List;

/**
 * Created by XM on 2016/5/18.
 */
public class NearByRecruitItem {

    private String id;
    private String commanyname;
    private String head_img;
    private double distance;
    private int is_owner;
    private String requirement;
    private List<PostItem> list_post;
    private List<String> list_recruit_img;
    private String link;
    private String phone;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCommanyname() {
        return commanyname;
    }
    public void setCommanyname(String commanyname) {
        this.commanyname = commanyname;
    }
    public String getHead_img() {
        return head_img;
    }
    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRequirement() {
        return requirement;
    }
    public void setRequirement(String requrement) {
        this.requirement = requrement;
    }

    public void setListPostItem(List<PostItem> list_post){
        this.list_post = list_post;
    }
    public List<PostItem> getListPostItem(){
        return this.list_post;
    }

    public void setList_recruit_img(List<String> list_recruit_img){
        this.list_recruit_img = list_recruit_img;
    }

    public List<String> getList_recruit_img(){
        return this.list_recruit_img;
    }

    public void setLink(String link){
        this.link = link;
    }
    public String getLink(){
        return this.link;
    }

    public void setIs_owner(int isowner){
        this.is_owner = isowner;
    }
    public int getIs_owner(){
        return this.is_owner;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getPhone(){
        return this.phone;
    }

}
