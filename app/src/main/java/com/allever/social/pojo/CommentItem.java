package com.allever.social.pojo;

/**
 * Created by XM on 2016/4/19.
 */
public class CommentItem {
    private String id;
    private String nickname;
    private String username;
    private String user_head_path;
    private String time;
    private String user_id;
    private String content;
    private String comment_id;
    private String comment_voice;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getComment_id() {
        return comment_id;
    }
    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
    public String getUser_id(){
        return this.user_id;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public void  setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setComment_voice(String comment_voice){
        this.comment_voice = comment_voice;
    }
    public String getComment_voice(){
        return this.comment_voice;
    }
}
