package com.allever.social.pojo;

/**
 * Created by XM on 2016/7/30.
 */
public class WebCollectionItem {
    private String id;
    private String title;
    private String url;

    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }
}
