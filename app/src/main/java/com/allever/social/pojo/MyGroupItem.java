package com.allever.social.pojo;

/**
 * Created by XM on 2016/5/12.
 */
public class MyGroupItem {
    private String id;
    private String groupname;
    private String group_img;
    private int state;
    private int is_my_group;
    private String description;

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
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getIs_my_group() {
        return is_my_group;
    }
    public void setIs_my_group(int is_my_group) {
        this.is_my_group = is_my_group;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
