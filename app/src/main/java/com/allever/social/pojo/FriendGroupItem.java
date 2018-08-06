package com.allever.social.pojo;

import java.util.List;

/**
 * Created by XM on 2016/6/14.
 */
public class FriendGroupItem {
    private String id;
    private String friendgroup_name;
    private List<FriendItem> list_friend;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFriendgroup_name() {
        return friendgroup_name;
    }
    public void setFriendgroup_name(String friendgroup_name) {
        this.friendgroup_name = friendgroup_name;
    }
    public List<FriendItem> getList_friend() {
        return list_friend;
    }
    public void setList_friend(List<FriendItem> list_friend) {
        this.list_friend = list_friend;
    }

}
