package com.allever.social.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.allever.social.MyApplication;

/**
 * Created by XM on 2016/4/20.
 */
public class SharedPreferenceUtil {

    public static boolean setSelectedNearbyUserDistance(String distance){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE).edit();
        if(checkNull(distance)) editor.putString("distance",distance);
        editor.commit();
        return true;
    }

    public static String getSelectedNearbyUserDistance(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE);
        String distance = sharedPreferences.getString("distance", "1000");
        return distance;
    }

    public static boolean setSelectedNearbyUserMaxage(String max_age){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE).edit();
        if(checkNull(max_age)) editor.putString("max_age",max_age);
        editor.commit();
        return true;
    }

    public static String getSelectedNearbyUserMaxage(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE);
        String max_age = sharedPreferences.getString("max_age", "99");
        return max_age;
    }

    public static boolean setSelectedNearbyUserMinage(String min_age){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE).edit();
        if(checkNull(min_age)) editor.putString("min_age",min_age);
        editor.commit();
        return true;
    }

    public static String getSelectedNearbyUserMinage(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE);
        String min_age = sharedPreferences.getString("min_age", "0");
        return min_age;
    }

    public static boolean setSelectedNearbyUserSex(String sex){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE).edit();
        if(checkNull(sex)) editor.putString("sex",sex);
        editor.commit();
        return true;
    }


    public static String getSelectedNearbyUserSex(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("selected_nearby_user", Context.MODE_PRIVATE);
        String sex = sharedPreferences.getString("sex", "全部");
        return sex;
    }

    public static String getLongitude(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        String longitude = sharedPreferences.getString("longitude", "");
        return longitude;
    }

    public static String getLatitude(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "");
        return latitude;
    }

    public static String getAddress(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        String address = sharedPreferences.getString("address", "");
        return address;
    }

    public static String getCity(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        String city = sharedPreferences.getString("city", "");
        return city;
    }

    public static boolean setShareRemindRestCount(String date,int rest_count){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("share_remind", Context.MODE_PRIVATE).edit();
        editor.putString("date",date);
        editor.putInt("rest_count", rest_count);
        editor.commit();
        return true;
    }


    public static String getShareRemindDate(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("share_remind", Context.MODE_PRIVATE);
        String date = sharedPreferences.getString("date", "");
        return date;
    }

    public static int getShareRemindCount(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("share_remind", Context.MODE_PRIVATE);
        int rest_count = sharedPreferences.getInt("rest_count", -1);
        return rest_count;
    }

    public static boolean setLocation(String longitude, String latitude, String city,String address){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        editor.putString("longitude",longitude);
        editor.putString("latitude", latitude);
        editor.putString("city", city);
        editor.putString("address", address);
        editor.commit();
        return true;
    }

    //程序丢一次启动调用
    public static boolean setADReceiver(){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("ad_receiver", Context.MODE_PRIVATE).edit();
        editor.putBoolean("iswaiting", false);
        editor.commit();
        return true;
    }


    //闹钟唤起广播之后设置为false
    //开启服务后设置为true//表示已经设置了闹钟，不必再设置
    public static boolean updateADReceiver(boolean iswaiting){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("ad_receiver", Context.MODE_PRIVATE).edit();
        editor.putBoolean("iswaiting", iswaiting);
        editor.commit();
        return true;
    }

    public static boolean getADReceiver(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("ad_receiver", Context.MODE_PRIVATE);
        boolean iswaiting = sharedPreferences.getBoolean("iswaiting", true);
        return iswaiting;
    }

    public static void setPoints(String points){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        editor.putString("points",points);
        editor.commit();
    }

    public static String getPoints(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        String points = sharedPreferences.getString("points", "");
        return points;
    }

    public static boolean setLocation(String longitude, String latitude){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        if(checkNull(longitude)) editor.putString("longitude",longitude);
        if (checkNull(latitude)) editor.putString("latitude", latitude);
        editor.commit();
        return true;
    }

    public static String getRegistration_id(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String registration_id = sharedPreferences.getString("registration_id", "");
        return registration_id;
    }

    public static String getUserId(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        return user_id;
    }

    public static String getUserName(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        return username;
    }

    public static String getOpenid(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String openid = sharedPreferences.getString("openid", "");
        return openid;
    }

    public static String getAutoReaction(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String autoreaction = sharedPreferences.getString("autoreaction", "");
        return autoreaction;
    }

    public static String getOnlineState(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String onlinestate = sharedPreferences.getString("onlinestate", "");
        return onlinestate;
    }

    public static String getIsOffline(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String  isOffline = sharedPreferences.getString("isoffline", "0");
        return isOffline;
    }

    public static String getSessionId(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String session_id = sharedPreferences.getString("session_id", "");
        return session_id;
    }

    public static String getPassword(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String password = sharedPreferences.getString("password", "");
        return password;
    }

    public static String getHeadpath(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String head_path = sharedPreferences.getString("head_path", "");
        return head_path;
    }

    public static String getNickname(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        return nickname;
    }

    public static String getSignature(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String signature = sharedPreferences.getString("signature", "");
        return signature;
    }

    public static String getState(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String state = sharedPreferences.getString("state", "");
        return state;
    }
    public static String getSex(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String sex = sharedPreferences.getString("sex", "");
        return sex;
    }
    public static int  getAge(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int age = sharedPreferences.getInt("age", 0);
        return age;
    }

    public static String getOccupation(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String occupation = sharedPreferences.getString("occupation", "学生");
        return occupation;
    }

    public static String getConstellation(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String constellation = sharedPreferences.getString("constellation", "白羊座");
        return constellation;
    }
    public static String getPhone(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        return phone;
    }
    public static String getEmail(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        return email;
    }

    public static String getHight(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String hight = sharedPreferences.getString("hight", "");
        return hight;
    }

    public static String getWeight(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String weight = sharedPreferences.getString("weight", "");
        return weight;
    }

    public static String getFigure(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String figure = sharedPreferences.getString("figure", "");
        return figure;
    }

    public static String getEmotion(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String emotion = sharedPreferences.getString("emotion", "");
        return emotion;
    }
    public static String getVip(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String is_vip = sharedPreferences.getString("vip", "");
        return is_vip;
    }

    public static String getRecommended(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String is_recommended = sharedPreferences.getString("recommend", "");
        return is_recommended;
    }

    public static String getHeadPath(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String head_path = sharedPreferences.getString("head_path", "");
        return head_path;
    }


    public static boolean setUserDate(String userId,String username, String password,String head_path, String nickname, String signature, String state, String sessionId){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(userId)) editor.putString("user_id",userId);
        if(checkNull(username)) editor.putString("username", username);
        if(checkNull(password)) editor.putString("password", password);
        if(checkNull(head_path)) editor.putString("head_path", head_path);
        if(checkNull(nickname)) editor.putString("nickname", nickname);
        if(checkNull(signature)) editor.putString("signature", signature);
        if(checkNull(state)) editor.putString("state", state);
        if(checkNull(sessionId)) editor.putString("session_id", sessionId);
        editor.commit();
        return true;
    }

    public static boolean setRegistrationId(String registration_id){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(registration_id)) editor.putString("registration_id",registration_id);
        editor.commit();
        return true;
    }

    public static boolean setUserId(String user_id){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(user_id)) editor.putString("user_id",user_id);
        editor.commit();
        return true;
    }
    public static boolean setUsername(String username){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(username)) editor.putString("username",username);
        editor.commit();
        return true;
    }
    public static boolean setOpenid(String openid){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(openid)) editor.putString("openid",openid);
        editor.commit();
        return true;
    }

    public static boolean setPassword(String password){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(password)) editor.putString("password",password);
        editor.commit();
        return true;
    }
    public static boolean setHeadpath(String head_path){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(head_path)) editor.putString("head_path",head_path);
        editor.commit();
        return true;
    }
    public static boolean setNickname(String nickname){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(nickname)) editor.putString("nickname",nickname);
        editor.commit();
        return true;
    }
    public static boolean setSignature(String signature){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(signature)) editor.putString("signature",signature);
        editor.commit();
        return true;
    }
    public static boolean setSessionId(String session_id){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(session_id)) editor.putString("session_id",session_id);
        editor.commit();
        return true;
    }
    public static boolean setState(String state){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(state)) editor.putString("state",state);
        editor.commit();
        return true;
    }
    public static boolean setCity(String city){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(city)) editor.putString("city",city);
        editor.commit();
        return true;
    }
    public static boolean setEmail(String email){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(email)) editor.putString("email",email);
        editor.commit();
        return true;
    }
    public static boolean setAge(int age){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putInt("age", age);
        editor.commit();
        return true;
    }
    public static boolean setOccupation(String occupation){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putString("occupation", occupation);
        editor.commit();
        return true;
    }
    public static boolean setConstellation(String constellation){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putString("constellation", constellation);
        editor.commit();
        return true;
    }
    public static boolean setPhone(String phone){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(phone)) editor.putString("phone",phone);
        editor.commit();
        return true;
    }
    public static boolean setSex(String sex){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(sex)) editor.putString("sex",sex);
        editor.commit();
        return true;
    }
    public static boolean setHihgt(String hight){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(hight)) editor.putString("hight",hight);
        editor.commit();
        return true;
    }

    public static boolean setWeight(String weight){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(weight)) editor.putString("weight",weight);
        editor.commit();
        return true;
    }

    public static boolean setFigure(String figure){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(figure)) editor.putString("figure",figure);
        editor.commit();
        return true;
    }

    public static boolean setEmotion(String emotion){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(emotion)) editor.putString("emotion",emotion);
        editor.commit();
        return true;
    }
    public static boolean setVip(String is_vip){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(is_vip)) editor.putString("vip", is_vip);
        editor.commit();
        return true;
    }

    public static boolean setRecommend(String is_recommended){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(is_recommended)) editor.putString("recommend", is_recommended);
        editor.commit();
        return true;
    }

    public static boolean setAutoReaction(String autoreaction){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(autoreaction)) editor.putString("autoreaction", autoreaction);
        editor.commit();
        return true;
    }

    public static boolean setOnlineState(String onlinestate){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(onlinestate)) editor.putString("onlinestate", onlinestate);
        editor.commit();
        return true;
    }

    public static boolean setIsOffline(String isOffline){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        if(checkNull(isOffline)) editor.putString("isoffline", isOffline);
        editor.commit();
        return true;
    }


    private static boolean checkNull(String arg){
        if(arg == null){
            return false;
        }else{
            return true;
        }
    }

    public static void initADSharepreference(){
        SharedPreferences.Editor sharedPreferences_ad_screen_editor = MyApplication.getContext().getSharedPreferences("ad_screen", Context.MODE_PRIVATE).edit();
        sharedPreferences_ad_screen_editor.putBoolean("isshow",true);
        sharedPreferences_ad_screen_editor.putInt("day", 3);
        sharedPreferences_ad_screen_editor.putInt("count", 1);
        sharedPreferences_ad_screen_editor.commit();

        SharedPreferences.Editor sharedPreferences_ad_bar_editor = MyApplication.getContext().getSharedPreferences("ad_bar",  Context.MODE_PRIVATE).edit();
        sharedPreferences_ad_bar_editor.putBoolean("isshow",true);
        sharedPreferences_ad_bar_editor.putInt("day", 3);
        sharedPreferences_ad_bar_editor.putInt("count", 1);
        sharedPreferences_ad_bar_editor.commit();

        SharedPreferences.Editor sharedPreferences_ad_exit_editor = MyApplication.getContext().getSharedPreferences("ad_exit", Context. MODE_PRIVATE).edit();
        sharedPreferences_ad_exit_editor.putBoolean("isshow",true);
        sharedPreferences_ad_exit_editor.putInt("day", 3);
        sharedPreferences_ad_exit_editor.putInt("count", 1);
        sharedPreferences_ad_exit_editor.commit();
    }

    public static void setADComment(int day, int count){
        SharedPreferences.Editor sharedPreferences_ad_screen_editor = MyApplication.getContext().getSharedPreferences("ad_screen", Context.MODE_PRIVATE).edit();
        sharedPreferences_ad_screen_editor.putBoolean("isshow",true);
        sharedPreferences_ad_screen_editor.putInt("day", day);
        sharedPreferences_ad_screen_editor.putInt("count", count);
        sharedPreferences_ad_screen_editor.commit();

        SharedPreferences.Editor sharedPreferences_ad_bar_editor = MyApplication.getContext().getSharedPreferences("ad_bar",  Context.MODE_PRIVATE).edit();
        sharedPreferences_ad_bar_editor.putBoolean("isshow",true);
        sharedPreferences_ad_bar_editor.putInt("day", day);
        sharedPreferences_ad_bar_editor.putInt("count", count);
        sharedPreferences_ad_bar_editor.commit();

        SharedPreferences.Editor sharedPreferences_ad_exit_editor = MyApplication.getContext().getSharedPreferences("ad_exit", Context. MODE_PRIVATE).edit();
        sharedPreferences_ad_exit_editor.putBoolean("isshow",true);
        sharedPreferences_ad_exit_editor.putInt("day", day);
        sharedPreferences_ad_exit_editor.putInt("count", count);
        sharedPreferences_ad_exit_editor.commit();
    }

    public static void updateADdata(int day, int count, String perf_name){
        SharedPreferences.Editor sharedPreferences_ad_exit_editor = MyApplication.getContext().getSharedPreferences(perf_name, Context.MODE_PRIVATE).edit();
        sharedPreferences_ad_exit_editor.putBoolean("isshow",true);
        sharedPreferences_ad_exit_editor.putInt("day", day);
        sharedPreferences_ad_exit_editor.putInt("count", count);
        sharedPreferences_ad_exit_editor.commit();
    }

    public static int getADcount(String pref_name){
        SharedPreferences sharedPreference = MyApplication.getContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        int count = sharedPreference.getInt("count",0);
        return count;
    }

    public static void updateADcount(int count,String pref_name){
        MyApplication.getContext().getSharedPreferences(pref_name,Context.MODE_PRIVATE).edit().putInt("count",count).commit();
    }

    public static boolean getADshow(String pref_name){
        SharedPreferences sharedPreference = MyApplication.getContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        boolean isshow = sharedPreference.getBoolean("isshow",true);
        return isshow;
    }

    public static void updateADshow(boolean isshow, String pref_name){
        MyApplication.getContext().getSharedPreferences(pref_name,Context.MODE_PRIVATE).edit().putBoolean("isshow",isshow).commit();
    }


    public static boolean saveGroupDataFromHXgroupid(String group_id, String group_name, String hx_group_id,String group_head_path){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(hx_group_id, Context.MODE_PRIVATE).edit();
        editor.putString("group_id",group_id);
        editor.putString("group_name", group_name);
        editor.putString("hx_group_id", hx_group_id);
        editor.putString("group_head_path",group_head_path);
        editor.commit();
        return true;
    }

    public static String getGroupid(String hx_group_id){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences(hx_group_id, Context.MODE_PRIVATE);
        String group_id = sharedPreferences.getString("group_id", "");
        return group_id;
    }

    public static String getGroupName(String hx_group_id){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences(hx_group_id, Context.MODE_PRIVATE);
        String groupname = sharedPreferences.getString("group_name", "");
        return groupname;
    }
    public static String getGroupImgPath(String hx_group_id){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences(hx_group_id, Context.MODE_PRIVATE);
        String groupimgpath = sharedPreferences.getString("group_head_path", "");
        return groupimgpath;
    }


    public static boolean setRefreshUserRefreshingState(int isRefreshing){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("refresh_user", Context.MODE_PRIVATE).edit();
        editor.putInt("isRefreshing", isRefreshing);
        editor.commit();
        return true;
    }

    public static int getRefreshUserRefreshingState(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("refresh_user", Context.MODE_PRIVATE);
        int isRefreshing = sharedPreferences.getInt("isRefreshing",0);
        return isRefreshing;
    }

    public static boolean saveUserData(String username, String nickname, String head_path){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(username, Context.MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.putString("nickname", nickname);
        editor.putString("head_path", head_path);
        editor.commit();
        return true;
    }

    public static boolean saveMsgCount(String username, int msg_count){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("msg_count", Context.MODE_PRIVATE).edit();
        editor.putString("msg_username",username);
        editor.putInt("msg_count", msg_count);
        editor.commit();
        return true;
    }

    public static int getMsgCount(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("msg_count", Context.MODE_PRIVATE);
        int msg_count = sharedPreferences.getInt("msg_count",0);
        return msg_count;
    }

    public static String getMsgUserName(){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences("msg_count", Context.MODE_PRIVATE);
        String msg_username = sharedPreferences.getString("msg_username", "");
        return msg_username;
    }






    public static String getUserNickname(String usernmae){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences(usernmae, Context.MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        return nickname;
    }

    public static String getUserHeadPath(String usernmae){
        SharedPreferences sharedPreferences  = MyApplication.getContext().getSharedPreferences(usernmae, Context.MODE_PRIVATE);
        String head_path = sharedPreferences.getString("head_path", "");
        return head_path;
    }



}
