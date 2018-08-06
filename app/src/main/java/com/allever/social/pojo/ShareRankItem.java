package com.allever.social.pojo;

/**
 * Created by XM on 2016/7/28.
 */
public class ShareRankItem {

    private String user_head_path;
    private String username;
    private String nickname;
    private int share_count;

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
    public String getUser_head_path() {
        return user_head_path;
    }
    public void setUser_head_path(String user_head_path) {
        this.user_head_path = user_head_path;
    }

    public int getShare_count(){
        return this.share_count;
    }
    public void setShare_count(int share_count){
        this.share_count = share_count;
    }
}
