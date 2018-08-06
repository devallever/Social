package com.allever.social.pojo;

/**
 * Created by XM on 2016/5/3.
 */
public class Msg {
    private String head_path;
    private String content;

    public void setHead_path(String path){
        this.head_path = path;
    }
    public String getHead_path(){
        return this.head_path;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
}
