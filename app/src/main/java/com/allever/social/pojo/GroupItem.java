package com.allever.social.pojo;

import java.util.List;

/**
 * Created by XM on 2016/5/12.
 */
public class GroupItem  {
    private String id;
    private String groupname;
    private String group_img;
    private String group_bulider_path;
    private double distance;
    private String point;
    private int is_member;
    private int member_count;
    private int women_count;
    private String attention;
    private String[] list_members_head_path;
    private String hx_group_id;
    private int group_type;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getGroupname() {
        return groupname;
    }
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
    public String getGroup_img() {
        return group_img;
    }
    public void setGroup_img(String group_img) {
        this.group_img = group_img;
    }
    public String getGroup_bulider_path() {
        return group_bulider_path;
    }
    public void setGroup_bulider_path(String group_bulider_path) {
        this.group_bulider_path = group_bulider_path;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getPoint() {
        return point;
    }
    public void setPoint(String point) {
        this.point = point;
    }
    public int getMember_count() {
        return member_count;
    }
    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }
    public int getWomen_count() {
        return women_count;
    }
    public void setWomen_count(int women_count) {
        this.women_count = women_count;
    }
    public String getAttention() {
        return attention;
    }
    public void setAttention(String attention) {
        this.attention = attention;
    }
    public String[] getList_members_path() {
        return list_members_head_path;
    }
    public void setList_members_path(String[] list_members_path) {
        this.list_members_head_path = list_members_path;
    }
    public void setIs_member(int is_member){
        this.is_member = is_member;
    }
    public int getIs_member(){
        return this.is_member;
    }

    public String getHx_group_id() {
        return hx_group_id;
    }
    public void setHx_group_id(String hx_group_id) {
        this.hx_group_id = hx_group_id;
    }

    public int getGroup_type(){
        return this.group_type;
    }
    public void setGroup_type(int group_type){
        this.group_type = group_type;;
    }
}
