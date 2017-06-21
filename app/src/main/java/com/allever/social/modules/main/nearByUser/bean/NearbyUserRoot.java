package com.allever.social.modules.main.nearByUser.bean;

import java.util.List;

/**
 * Created by allever on 17-6-19.
 */

public class NearbyUserRoot {
    private boolean success;

    private String message;

    private List<User_list> user_list ;

    public void setSuccess(boolean success){
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setUser_list(List<User_list> user_list){
        this.user_list = user_list;
    }
    public List<User_list> getUser_list(){
        return this.user_list;
    }

}