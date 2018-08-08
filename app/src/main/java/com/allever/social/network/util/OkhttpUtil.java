package com.allever.social.network.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.allever.social.MyApplication;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.Constants;
import com.allever.social.utils.FileUtil;
import com.allever.social.utils.ImageUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by XM on 2016/4/20.
 */
public class OkhttpUtil {

    private static final String TAG = "OkhttpUtil";

    public static final int MESSAGE_FRIEND_LIST = 0;
    public static final int MESSAGE_USER_DATA = 1;
    public static final int MESSAGE_NEARBY_NEWS = 2;
    public static final int MESSAGE_HOT_NEWS = 3;
    public static final int MESSAGE_LOGIN = 4;
    public static final int MESSAGE_NEWS_DETAIL = 5;
    public static final int MESSAGE_REGIST = 6;
    public static final int MESSAGE_NEWS_COMMENT = 7;
    public static final int MESSAGE_USER_NEWS = 8;
    public static final int MESSAGE_LIKE = 9;
    public static final int MESSAGE_ADD_COMMENT= 10;
    public static final int MESSAGE_ADD_NEWS = 11;
    public static final int MESSAGE_ADD_FRIEND = 12;
    public static final int MESSAGE_DELETE_FRIEND = 13;
    public static final int MESSAGE_DELETE_NEWS = 14;
    public static final int MESSAGE_MODIFY_HEAD = 15;
    public static final int MESSAGE_MODIFY_USER_DATA = 16;
    public static final int MESSAGE_POLL_SERVICE = 17;
    public static final int MESSAGE_DOWNLOAD = 19;
    public static final int MESSAGE_AUTO_LOGIN = 20;
    public static final int MESSAGE_AD_SETTING = 21;
    public static final int MESSAGE_LOGOUT = 22;
    public static final int MESSAGE_DOWNLOAD_IMAGE = 23;
    public static final int MESSAGE_NEARBY_USER = 24;
    public static final int MESSAGE_NEARBY_GROUP = 25;
    public static final int MESSAGE_MY_GROUP_LIST = 26;
    public static final int MESSAGE_GROUP_DATA = 27;
    public static final int MESSAGE_ADD_GROUP = 28;
    public static final int MESSAGE_JOIN_GROUP = 29;
    public static final int MESSAGE_DROP_GROUP = 30;
    public static final int MESSAGE_DELETE_GROUP = 31;
    public static final int MESSAGE_NEARBY_RECRUIT = 32;
    public static final int MESSAGE_ADD_RECRUIT = 33;
    public static final int MESSAGE_ADD_POST = 34;
    public static final int MESSAGE_DELETE_RECRUIT = 35;
    public static final int MESSAGE_NEARBY_POST = 36;
    public static final int MESSAGE_MY_RECRUIT_LIST = 37;
    public static final int MESSAGE_RECRUIT_DATA = 38;
    public static final int MESSAGE_POST_LIST = 39;
    public static final int MESSAGE_POST_DATA = 40;
    public static final int MESSAGE_MODIFY_POST = 41;
    public static final int MESSAGE_MODIFY_RECRUIT = 42;
    public static final int MESSAGE_DELETE_POST = 43;
    public static final int MESSAGE_FRIEND_NEWS = 44;
    public static final int MESSAGE_ADD_PHOTO_WALL = 45;
    public static final int MESSAGE_PHOTO_WALL_LIST = 46;
    public static final int MESSAGE_DELETE_PHOTO_WALL = 47;
    public static final int MESSAGE_GROUP_MEMBER_LIST = 48;
    public static final int MESSAGE_SEARCH_USER = 49;
    public static final int MESSAGE_CHECK_PHONE = 50;
    public static final int MESSAGE_AD_DETAIL = 51;
    public static final int MESSAGE_VISITED_USER_LIST = 52;
    public static final int MESSAGE_VISITED_FOR_USER_LIST = 53;
    public static final int MESSAGE_VISITED_FOR_NEWS_LIST = 54;
    public static final int MESSAGE_GET_CREDIT = 55;
    public static final int MESSAGE_GET_VIP = 56;
    public static final int MESSAGE_GET_SIGN = 57;
    public static final int MESSAGE_SIGN = 58;
    public static final int MESSAGE_GET_VIDEO_FEE_SETTING = 59;
    public static final int MESSAGE_SAVE_VIDEO_FEE_SETTING = 60;
    public static final int MESSAGE_GET_VIDEO_CALL_INCOME = 61;
    public static final int MESSAGE_ADD_WITHDRAW = 62;
    public static final int MESSAGE_GET_WITHDRAW_LOG = 63;
    public static final int MESSAGE_FRIEND_GROUP_LIST = 64;
    public static final int MESSAGE_FRIEND_GROUP_NAME_LIST = 65;
    public static final int MESSAGE_ADD_FRIEND_GROUP = 66;
    public static final int MESSAGE_MODIFY_FRIEND_GROUP = 67;
    public static final int MESSAGE_MODIFY_SECOND_NAME = 68;
    public static final int MESSAGE_MODIFY_USER_FRIEND_GROUP = 69;
    public static final int MESSAGE_NEWS_DETAIL_DETAIL = 70;
    public static final int MESSAGE_DOWNLOAD_NEWS_VOICE = 71;
    public static final int MESSAGE_CHAT_RANK = 72;
    public static final int MESSAGE_GET_MY_CHAT_RANK = 73;
    public static final int MESSAGE_CHECK_VIDEO_CALL = 74;
    public static final int MESSAGE_KICK_GROUP_MEMBER = 75;
    public static final int MESSAGE_INVITE_FRIEND_TO_GROUP = 76;
    public static final int MESSAGE_CHOOSE_FRIEND_LIST = 77;
    public static final int MESSAGE_REFRESH_NEARBY_USER = 78;
    public static final int MESSAGE_PULL_REFRESH_USER = 79;
    public static final int MESSAGE_SOCIAL_COUNT = 80;
    public static final int MESSAGE_FOLLOW_USER = 81;//关注数 MineFragment
    public static final int MESSAGE_GET_FOLLOW_USER = 82;//关注列表
    public static final int MESSAGE_GET_FANS_USER = 83;
    public static final int MESSAGE_ADD_SHARE_RECORD = 84;
    public static final int MESSAGE_SHARE_RANK = 85;
    public static final int MESSAGE_GET_MY_SHARE_RANK = 86;
    public static final int MESSAGE_SAVE_WEB_COLLECTION = 87;
    public static final int MESSAGE_GET_WEB_COLLECTION_LIST = 88;
    public static final int MESSAGE_GET_ONLINE_STATE = 89;
    public static final int MESSAGE_MODIFY_ONLINE_STATE = 90;
    public static final int MESSAGE_GET_AUTO_REACTION = 91;
    public static final int MESSAGE_SAVE_AUTO_REACTION = 92;
    public static final int MESSAGE_DELETE_WEB_COLLECTION = 93;
    public static final int MESSAGE_ADD_FEEDBACK = 94;
    public static final int MESSAGE_GET_VIDEO_CALL_MIN_COUNT = 95;
    public static final int MESSAGE_GET_MESSAGE_COUNT = 96;
    public static final int MESSAGE_UPDATE_MESSAGE_COUNT = 97;
    public static final int MESSAGE_GIVE_VIP = 98;
    public static final int MESSAGE_CHECK_USERNAME = 100;
    public static final int MESSAGE_DOWNLOAD_QQ_HEAD = 101;
    public static final int MESSAGE_REGIST_WITH_QQ_OPEN_ID = 102;
    public static final int MESSAGE_LOGIN_WITH_QQ_OPEN_ID = 103;
    public static final int MESSAGE_RED_POCKET_GROUP =104;
    public static final int MESSAGE_ADD_RED_POCKET_GROUP_COUNT = 105;
    public static final int MESSAGE_GET_SHARE_INFO = 106;
    public static final int MESSAGE_GET_SHUASHUA_USER_LIST = 107;
    public static final int MESSAGE_UPDATE_SHUASHUA = 108;
    public static final int MESSAGE_DIS_FOLLOW_USER = 109;
    public static final int MESSAGE_GET_RECOMMEND_GROUP = 110;
    public static final int MESSAGE_FRIEND_LOCATION_LIST = 111;
    public static final int MESSAGE_FRIEND_NOT_LOCATION_LIST = 112;
    public static final int MESSAGE_REQUEST_FRIEND_LOCATION = 113;
    public static final int MESSAGE_ACCEPT_FRIEND_LOCATION = 114;
    public static final int MESSAGE_USER_LIST = 115;
    public static final int MESSAGE_NEWS_LIST = 116;


    //--------------------------------------------------------------------------------------------
    private OkHttpClient mClient;
    private OkhttpUtil(){
        mClient = new OkHttpClient();
    }

    private static class Holder{
        private static final OkhttpUtil INS = new OkhttpUtil();
    }

    public static OkhttpUtil getIns(){
        return Holder.INS;
    }

    public void login(String username, String password, Callback callback){
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude",SharedPreferenceUtil.getLatitude())
                .add("address",SharedPreferenceUtil.getAddress())
                .add("jpush_registration_id", JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginServlet")
                .post(formBody)
                .build();
        mClient.newCall(request).enqueue(callback);
    }

    public void checkQQOpenId(String open_id, Callback callback){
        RequestBody formBody = new FormEncodingBuilder()
                .add("open_id", open_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/CheckQQOpenIdServlet")
                .post(formBody)
                .build();
        mClient.newCall(request).enqueue(callback);
    }


    public void loginWithQQopenid(String qq_open_id, Callback callback){
        RequestBody formBody = new FormEncodingBuilder()
                .add("qq_open_id", qq_open_id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("jpush_registration_id", JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginWithQQopenidServlet")
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(callback);

    }

    public void autoLogin(Callback callback){
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", SharedPreferenceUtil.getUserName())
                .add("password", SharedPreferenceUtil.getPassword())
                .add("qq_open_id", SharedPreferenceUtil.getOpenid())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("jpush_registration_id",JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginServlet")
                .post(formBody)
                .build();
        mClient.newCall(request).enqueue(callback);
    }

    //----------------------------------------------------------------------------------------------


    public static void acceptRequestFriendLocation(final Handler handler,String to_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("to_username",to_username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AcceptFriendLocationServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ACCEPT_FRIEND_LOCATION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void requestFriendLocation(final Handler handler,String to_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("to_username",to_username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/RequestFriendLocationServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_REQUEST_FRIEND_LOCATION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendNotLocationList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FriendNotLocationListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_NOT_LOCATION_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendLocationList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FriendLocationListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_LOCATION_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getRecommendGroup(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetRecommendGroupInfo")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_RECOMMEND_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void updateShuashua(final Handler handler,String other_username,String flag){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("other_username",other_username)
                .add("flag", flag)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/UpdateShuashuaServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_UPDATE_SHUASHUA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getShuashuaUserList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetShuaShuaUserServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_SHUASHUA_USER_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getShareInfo(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetShareInfoServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_SHARE_INFO;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addRedPocketGroupCount(final Handler handler,String hx_group_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("hx_group_id", hx_group_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddRedPocketGroupCountServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_RED_POCKET_GROUP_COUNT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void loginWithQQopenid(final Handler handler,String qq_open_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("qq_open_id", qq_open_id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("jpush_registration_id", JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginWithQQopenidServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_LOGIN_WITH_QQ_OPEN_ID;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void registWithByteWithQQopenid(final Handler handler, String username,String nickname, byte[] bytes,String sex,String city,String qq_open_id){
        OkHttpClient okHttpClient = new OkHttpClient();
//        File file = new File(str_user_head_path);
//        Log.d("ResgistActivity", "path = " + str_user_head_path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"username\""),
                        RequestBody.create(null, username))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"city\""),
                        RequestBody.create(null, city))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"nickname\""),
                        RequestBody.create(null, nickname))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"qq_open_id\""),
                        RequestBody.create(null, qq_open_id))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"sex\""),
                        RequestBody.create(null, sex))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"jpush_registration_id\""),
                        RequestBody.create(null, JPushInterface.getRegistrationID(MyApplication.mContext)))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"photo\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/RegistWithQQopenidServlet")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_REGIST_WITH_QQ_OPEN_ID;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void downloadQQhead(final Handler handler, String head_url){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .build();
        Request request = new Request.Builder()
                .url(head_url)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("DownloadQQhead", "失败");

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                Log.d("DownloadQQhead", "成功");
                //if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();
                    Message message = new Message();
                    message.what = MESSAGE_DOWNLOAD_QQ_HEAD;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                    Log.d("DownloadQQhead", "成功2");
                //}
            }
        });
    }

    public static void checkUsername(final Handler handler,String username){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/CheckUserNameServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_CHECK_USERNAME;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }



//    public static void checkQQOpenId(final Handler handler,String open_id){
//        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody formBody = new FormEncodingBuilder()
//                .add("open_id", open_id)
//                .build();
//        Request request = new Request.Builder()
//                .url(WebUtil.HTTP_ADDRESS + "/CheckQQOpenIdServlet")
//                .post(formBody)
//                .build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                //NOT UI Thread
//                if (response.isSuccessful()) {
//                    System.out.println(response.code());
//                    String result = response.body().string();
//                    Message message = new Message();
//                    message.what = MESSAGE_CHECK_QQ_OPEN_ID;
//                    message.obj = result;
//                    handler.sendMessage(message);
//                    System.out.println(result);
//                }
//            }
//        });
//
//    }

    public static void giveVip(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GiveVipServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GIVE_VIP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void updateMessageCount(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/UpdateMessageCountServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_UPDATE_MESSAGE_COUNT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getMessageCount(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetMessageCountServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_MESSAGE_COUNT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getVideoCallMinCount(final Handler handler,String to_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("to_username", to_username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetVideoCallMinCountServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_VIDEO_CALL_MIN_COUNT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addFeedback(final Handler handler,String content){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("content", content)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddFeedbackServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_FEEDBACK;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void deleteWebCollection(final Handler handler,String webcollection_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("webcollection_id", webcollection_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteWebCollectionServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_WEB_COLLECTION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void saveAutoReaction(final Handler handler,String content){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("content", content)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/SaveAutoReactionServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_SAVE_AUTO_REACTION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getAutoReaction(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AutoReactionServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_AUTO_REACTION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modifyOnlineState(final Handler handler,String onlinestate){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("onlinestate", onlinestate)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyOnlineStateServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_ONLINE_STATE;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getOnlineState(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetOnlineStateServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_ONLINE_STATE;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getWebcollectionList(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("page", page)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/WebCollectionListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_WEB_COLLECTION_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void saveWebCollection(final Handler handler,String title,String url){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("title", title)
                .add("url", url)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddWebCollectionServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_SAVE_WEB_COLLECTION;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addShareRecord(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddShareRecordServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_SHARE_RECORD;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getFansUser(final Handler handler,String page,String username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .add("username", username)
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FansUserServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_GET_FANS_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFollowUser(final Handler handler,String page,String username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .add("username", username)
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FollowUserServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_GET_FOLLOW_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void disfollowUser(final Handler handler,String dis_follow_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("dis_follow_username", dis_follow_username)
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DisFollowUserServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_DIS_FOLLOW_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void followUser(final Handler handler, final String follow_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("follow_username", follow_username)
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FollowServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_FOLLOW_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                    //关注后自动发送问候。发送消息
                    EMMessage em_message = EMMessage.createTxtSendMessage(Constants.AUTO_FOLLOW_GREETING, follow_username);
                    //sendMessage(message);
                    EMClient.getInstance().chatManager().sendMessage(em_message);
                }
            }
        });
    }

    public static void getSocialCount(final Handler handler,String username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("username", username)
                .build();
        Request request;
        request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FansCountServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_SOCIAL_COUNT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void pullRefreshUser(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/PullRefresherServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_PULL_REFRESH_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FriendListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getChooseFriendList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ChooseFriendListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_CHOOSE_FRIEND_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendGroupList(final Handler handler,String phone_json){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("phone_json", phone_json)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FriendGroupListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_GROUP_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendGroupNameList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/FriendGroupNameListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_GROUP_NAME_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getCredit(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetCreditServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_CREDIT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getMyShareRank(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/MyShareRankServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_MY_SHARE_RANK;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getMyCharRank(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/MyChatRankServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_MY_CHAT_RANK;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addFriendGroup(final Handler handler,String friendgroup_name){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("friendgroup_name", friendgroup_name)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddFriendGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_FRIEND_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void modifyFriendGroup(final Handler handler,String friendgroup_id, String friendgroup_name){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("friendgroup_name", friendgroup_name)
                .add("friendgroup_id", friendgroup_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyFriendGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_FRIEND_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modifySecondName(final Handler handler,String friend_id, String second_name){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("friend_id", friend_id)
                .add("second_name", second_name)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifySecondNameServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_SECOND_NAME;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modifyUserFriendGroup(final Handler handler,String friend_id, String friendgroup_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("friend_id", friend_id)
                .add("friendgroup_id", friendgroup_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyUserFriendGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_USER_FRIEND_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getWithdrawlog(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/WithdrawLogServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_WITHDRAW_LOG;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addWithdraw(final Handler handler,String money,String account){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("money", money)
                .add("account", account)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddWithdrawServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_WITHDRAW;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getVideoFeeSetting(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetVideoFeeSettingServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_VIDEO_FEE_SETTING;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getVideoCallIncome(final Handler handler,String min_count,String from_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("from_username", from_username)
                .add("min_count", min_count)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetVideoCallIncomeServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_VIDEO_CALL_INCOME;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void saveVideoFeeSetting(final Handler handler,String accept_video,String video_fee){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("accept_video", accept_video)
                .add("video_fee", video_fee)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/SaveVideoFeeSettingServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_SAVE_VIDEO_FEE_SETTING;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void checkVideoCall(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/CheckVideoCallServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_CHECK_VIDEO_CALL;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getSign(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("username", SharedPreferenceUtil.getUserName())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetSignDataServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_SIGN;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void sign(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("username", SharedPreferenceUtil.getUserName())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/SignServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_SIGN;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getVip(final Handler handler, String month_count,String type){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("month_count", month_count)
                .add("type", type)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/GetVipServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_GET_VIP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getPhotoWallList(final Handler handler,String username){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/PhotoWallListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_PHOTO_WALL_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getMyGroupList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/MyGroupListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MY_GROUP_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getVisitedUserList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/VisitedUserListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_VISITED_USER_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getVisitedForUserList(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/VisitedForUserServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_VISITED_FOR_USER_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getVisitedForNewsList(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/VisitedForNewsServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_VISITED_FOR_NEWS_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getMyRecruitList(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/MyRecruitListServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MY_RECRUIT_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void updateVisitedNews(String news_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("news_id", news_id)
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UpdateVisitedNews")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UpdateVisitedNews")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
//                    //String result = response.body().string();
//                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
//                    Message message = new Message();
//                    message.what = MESSAGE_USER_DATA;
//                    message.obj = result;
//                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getUserData(String id,final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("check_user_id", id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UserDataServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UserDataServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_USER_DATA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void searchUser (String key,final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("key", key)
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/SearchUserServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/SearchUserServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_SEARCH_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getPostData(final Handler handler, String post_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("post_id", post_id)
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/PostDetailServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/PostDetailServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_POST_DATA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getGroupData(String group_id,final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/GroupDataServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/GroupDataServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_GROUP_DATA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getGroupMemberList(String group_id,final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/GroupMemberListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/GroupMemberListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_GROUP_MEMBER_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getRecruitData(final Handler handler,String recruit_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("recruit_id", recruit_id)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/RecruitDataServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/RecruitDataServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_RECRUIT_DATA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getPostList(final Handler handler,String recruit_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("recruit_id", recruit_id)
                .build();
        Request request;
        if(checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/PostListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/PostListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_POST_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getHotNews(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/HostNewsListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/HostNewsListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_HOT_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getFriendNews(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/FriendNewsServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/FriendNewsServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_FRIEND_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getNewsList(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NewsListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NewsListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEWS_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNearbyNews(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyNewsListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyNewsListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEARBY_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNearbyRecruit(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyRecruitListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyRecruitListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEARBY_RECRUIT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNearbyPost(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyPostListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyPostListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEARBY_POST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNearbyGroup(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyGroupListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyGroupListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEARBY_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getRedPocketGroup(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/RedPocketGroupListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/RedPocketGroupListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_RED_POCKET_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void refreshNearbyUser(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .add("selected_sex", SharedPreferenceUtil.getSelectedNearbyUserSex())
                .add("selected_min_age", SharedPreferenceUtil.getSelectedNearbyUserMinage())
                .add("selected_max_age", SharedPreferenceUtil.getSelectedNearbyUserMaxage())
                .add("selected_distance", SharedPreferenceUtil.getSelectedNearbyUserDistance())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyUserListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyUserListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_REFRESH_NEARBY_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getUserList(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .add("selected_sex", SharedPreferenceUtil.getSelectedNearbyUserSex())
                .add("selected_min_age", SharedPreferenceUtil.getSelectedNearbyUserMinage())
                .add("selected_max_age", SharedPreferenceUtil.getSelectedNearbyUserMaxage())
                .add("selected_distance", SharedPreferenceUtil.getSelectedNearbyUserDistance())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UserListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/UserListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_USER_LIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNearbyUser(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("page", page)
                .add("selected_sex", SharedPreferenceUtil.getSelectedNearbyUserSex())
                .add("selected_min_age", SharedPreferenceUtil.getSelectedNearbyUserMinage())
                .add("selected_max_age", SharedPreferenceUtil.getSelectedNearbyUserMaxage())
                .add("selected_distance", SharedPreferenceUtil.getSelectedNearbyUserDistance())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyUserListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NearbyUserListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_NEARBY_USER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getShareRank(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/ShareRankServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/ShareRankServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_SHARE_RANK;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getChatRank(final Handler handler,String page){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("page", page)
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/ChatRankListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/ChatRankListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_CHAT_RANK;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void getUserNews(final Handler handler, String user_id){//要查看的用户的id
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("friend_id", user_id)
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request;
        if(OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/FriendNewsListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/FriendNewsListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_USER_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void login(final Handler handler,String username, String password){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .add("longitude",SharedPreferenceUtil.getLongitude())
                .add("latitude",SharedPreferenceUtil.getLatitude())
                .add("address",SharedPreferenceUtil.getAddress())
                .add("jpush_registration_id", JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_LOGIN;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void checkPhone(final Handler handler,String phone){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("phone", phone)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/CheckPhoneServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_CHECK_PHONE;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void autoLogin(final Handler handler){
        Log.d(TAG, "autoLogin: ");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", SharedPreferenceUtil.getUserName())
                .add("password", SharedPreferenceUtil.getPassword())
                .add("qq_open_id", SharedPreferenceUtil.getOpenid())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .add("jpush_registration_id",JPushInterface.getRegistrationID(MyApplication.mContext))
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/LoginServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }
            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    System.out.println(result);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    LoginRoot root = gson.fromJson(result,LoginRoot.class);
                    if (root.seccess){
                        SharedPreferenceUtil.setSessionId(root.session_id);
                        Log.d(TAG, "onResponse: session id = " + SharedPreferenceUtil.getSessionId());
                        SharedPreferenceUtil.setState("1");
                        Message message = new Message();
                        message.what = MESSAGE_AUTO_LOGIN;
                        message.obj = result;
                        handler.sendMessage(message);
                        System.out.println(result);
                    }
                }
            }
        });
    }

    public static void logout(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request;
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/LogoutServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_LOGOUT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void getNewsComment(final Handler handler, String news_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("news_id", news_id)
                .build();
        Request request;
        if (OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/CommentListServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/CommentListServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_NEWS_COMMENT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }


    public static void getNewsDetail(final Handler handler, String news_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("news_id", news_id)
                .add("latitude",SharedPreferenceUtil.getLongitude())
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .build();
        Request request;
        if (OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NewsDetailServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/NewsDetailServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_NEWS_DETAIL_DETAIL;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void registWithByte(final Handler handler, String username, String password,String recommend_name,String phone, byte[] bytes,String age,String sex){
        OkHttpClient okHttpClient = new OkHttpClient();
//        File file = new File(str_user_head_path);
//        Log.d("ResgistActivity", "path = " + str_user_head_path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"username\""),
                        RequestBody.create(null, username))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"password\""),
                        RequestBody.create(null, password))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"phone\""),
                        RequestBody.create(null, phone))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"age\""),
                        RequestBody.create(null, age))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"sex\""),
                        RequestBody.create(null, sex))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"recommend_name\""),
                        RequestBody.create(null, recommend_name))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"jpush_registration_id\""),
                        RequestBody.create(null, JPushInterface.getRegistrationID(MyApplication.mContext)))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"photo\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/RegistServlet")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_REGIST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addGroup(final Handler handler, String groupname, String description,String point, byte[] bytes,String hx_group_id,String group_type){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"groupname\""),
                        RequestBody.create(null, groupname))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"hx_group_id\""),
                        RequestBody.create(null, hx_group_id))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"group_type\""),
                        RequestBody.create(null, group_type))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"description\""),
                        RequestBody.create(null, description))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"point\""),
                        RequestBody.create(null, point))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"longitude\""),
                        RequestBody.create(null, SharedPreferenceUtil.getLongitude()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"latitude\""),
                        RequestBody.create(null, SharedPreferenceUtil.getLatitude()))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"group_img_part\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddGroupServlet")
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }


    public static void joinGroup(final Handler handler,String group_id,String applyer){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .add("applyer", applyer)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/JoinGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_JOIN_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void inviteFriendToGroup(final Handler handler,String group_id,String applyer,String invited_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .add("applyer", applyer)
                .add("invited_username",invited_username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/InviteFriendToGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_INVITE_FRIEND_TO_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void dropGroup(final Handler handler,String group_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DropGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DROP_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void kickGroupMember(final Handler handler,String group_id,String kicked_username){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .add("kicked_username", kicked_username)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/KickGroupMemberServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_KICK_GROUP_MEMBER;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void deleteGroup(final Handler handler,String group_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("group_id", group_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteGroupServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_GROUP;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void deletePost(final Handler handler,String post_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("post_id", post_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeletePostServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_POST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addPost(final Handler handler,String recruit_id,String postname,String salary,String requirement,String description){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("recruit_id", recruit_id)
                .add("postname", postname)
                .add("salary", salary)
                .add("requirement", requirement)
                .add("description", description)
                .add("longitude", SharedPreferenceUtil.getLongitude())
                .add("latitude", SharedPreferenceUtil.getLatitude())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddPostServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_POST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modifyPost(final Handler handler,String post_id,String postname,String salary,String requirement,String description){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("post_id", post_id)
                .add("postname", postname)
                .add("salary", salary)
                .add("requirement", requirement)
                .add("description", description)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyPostServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_POST;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modifyRecruit(final Handler handler,String recruit_id,String companyname,String link,String phone,String address, String requirement){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("recruit_id", recruit_id)
                .add("companyname", companyname)
                .add("link", link)
                .add("requirement", requirement)
                .add("phone", phone)
                .add("address", address)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyRecruitServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_RECRUIT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void modityHead(final Handler handler, byte[] bytes){

        OkHttpClient okHttpClient = new OkHttpClient();
//        File file = new File(user_head_path);
//        Log.d("ModifyUserDataActivity", "okhttp  path = " + user_head_path);


        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"head_img\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyHeadServlet")
                .post(requestBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_HEAD;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void modifyUserData(final Handler handler,
                                      String nickname,
                                      String sex,
                                      String city,
                                      String signature,
                                      String email,
                                      String age,
                                      String occupation,
                                      String constellation,
                                      String hight,
                                      String weight,
                                      String figure,
                                      String emotion){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("nickname", nickname)
                .add("sex", sex)
                .add("city", city)
                .add("signature", signature)
                .add("email", email)
                .add("age",age)
                .add("occupation", occupation)
                .add("constellation", constellation)
                .add("hight", hight)
                .add("weight", weight)
                .add("figure", figure)
                .add("emotion", emotion)
                .build();
        Request request  = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ModifyUserDataServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_MODIFY_USER_DATA;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addPhotoWall(final Handler handler,int position, byte[] bytes){

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"position\""),
                        RequestBody.create(null, position + ""))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"part\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddPhotoWallServlet")
                .post(requestBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_PHOTO_WALL;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }


    public static void likeNews(final Handler handler,String news_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        RequestBody formBody = new FormEncodingBuilder()
                .add("news_id", news_id)
                .build();
        Request request;
        if (OkhttpUtil.checkLogin()){
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/LikeServlet")
                    .post(formBody)
                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                    .build();
        }else{
            request = new Request.Builder()
                    .url(WebUtil.HTTP_ADDRESS + "/LikeServlet")
                    .post(formBody)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    //String result = response.body().string();
                    //Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
                    Message message = new Message();
                    message.what = MESSAGE_LIKE;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }



    public static void addComment(final Handler handler, String content, String news_id, String comment_id,String comment_voice_path) {
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = null;
        Headers header;
        RequestBody fileBody;
        // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
        if (comment_id == null) comment_id = "";
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM).type(MultipartBuilder.FORM);
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"content\""),
                RequestBody.create(null, content));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"news_id\""),
                RequestBody.create(null,news_id));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"comment_id\""),
                RequestBody.create(null,comment_id));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"longitude\""),
                RequestBody.create(null, SharedPreferenceUtil.getLongitude()));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"latitude\""),
                RequestBody.create(null, SharedPreferenceUtil.getLatitude()));
        if (!comment_voice_path.equals("")){
            byte[] bytes;
            bytes = FileUtil.getBytes(comment_voice_path);

            fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            header = Headers.of(
                    "Content-Disposition",
                    "form-data; name=\"audio_part\"");
            builder.addPart(header, fileBody);
        }

        RequestBody requestBody = builder.build();
//        RequestBody formBody = new FormEncodingBuilder()
//                .add("news_id", news_id)
//                .add("comment_id", comment_id)
//                .add("content", content)
//                .add("longitude", SharedPreferenceUtil.getLongitude())
//                .add("latitude", SharedPreferenceUtil.getLatitude())
//                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddCommentServlet")
                .post(requestBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_COMMENT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void addFriend(final Handler handler,String friend_id,String is_share){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("friend_id", friend_id)
                .add("is_share",is_share)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddFriendServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_FRIEND;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addRankRecord(String chatwith_name){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("chatwith_name", chatwith_name)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddRankRecordServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    System.out.println(result);
                }
            }
        });
    }

    public static void addFriendWithoutHandler(String friend_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("friend_id", friend_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddFriendServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
//                if (response.isSuccessful()) {
//                    System.out.println(response.code());
//                    String result = response.body().string();
//                    Message message = new Message();
//                    message.what = MESSAGE_ADD_FRIEND;
//                    message.obj = result;
//                    handler.sendMessage(message);
//                    System.out.println(result);
//                }
            }
        });
    }

    public static void deleteFriend(final Handler handler, String friend_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("friend_id", friend_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteFriendServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_FRIEND;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void deletePhotoWall(final Handler handler, int position){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("position", position+"")
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeletePhotoWallServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_PHOTO_WALL;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void deleteRecruit(final Handler handler, String recruit_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("recruit_id", recruit_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteRecruitServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_RECRUIT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void deleteFriendWithoutHandler( String friend_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("friend_id", friend_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteFriendServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
//                if (response.isSuccessful()) {
//                    System.out.println(response.code());
//                    String result = response.body().string();
//                    Message message = new Message();
//                    message.what = MESSAGE_DELETE_FRIEND;
//                    message.obj = result;
//                    handler.sendMessage(message);
//                    System.out.println(result);
//                }
            }
        });
    }

    public static void deleteNews(final Handler handler, String news_id){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("news_id", news_id)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/DeleteNewsServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_DELETE_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void addNews(final Handler handler,String content, String longitude, String latitude, String city, List<String> list_user_head_path,String news_voice_path){
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = null;
        Headers header;
        RequestBody fileBody;
        if(longitude.equals("") || longitude==null){
            longitude = "0";
        }else{
            longitude = SharedPreferenceUtil.getLongitude();
        }
        if(latitude.equals("") || latitude==null){
            latitude = "0";
        }else{
            latitude = SharedPreferenceUtil.getLatitude();
        }
        if(city.equals("") || city==null){
            city = "未知";
        }else{
            city = SharedPreferenceUtil.getCity();
        }

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM).type(MultipartBuilder.FORM);
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"content\""),
                RequestBody.create(null, content));
            builder.addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"city\""),
                    RequestBody.create(null,city));
            builder.addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"longitude\""),
                    RequestBody.create(null, longitude));
            builder.addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"latitude\""),
                    RequestBody.create(null, latitude));

        for (int i=0; i<list_user_head_path.size(); i++){
            String path = list_user_head_path.get(i);
            Log.d("AddNewsActivity", "form-data; name=\"part" + (i + 1) + "\"");
            Bitmap bitmap;
            byte[] bytes;
            try {
                bitmap = ImageUtil.revitionImageSize(path);

                bytes = CommentUtil.Bitmap2Bytes(bitmap);
                fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
                header = Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"part" + (i + 1) + "\"");
                builder.addPart(header, fileBody);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if (!news_voice_path.equals("")){
            byte[] bytes;
            bytes = FileUtil.getBytes(news_voice_path);

            fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            header = Headers.of(
                    "Content-Disposition",
                    "form-data; name=\"audio_part\"");
            builder.addPart(header, fileBody);
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddNewsServlet")
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_NEWS;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });



    }

    public static void addRecruit(final Handler handler,String companyname, String link, String phone,String requirement,String address, List<String> list_image_path){
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = null;
        Headers header;
        RequestBody fileBody;

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM).type(MultipartBuilder.FORM);
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"companyname\""),
                RequestBody.create(null, companyname));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"requirement\""),
                RequestBody.create(null,requirement));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"link\""),
                RequestBody.create(null,link));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"phone\""),
                RequestBody.create(null,phone));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"address\""),
                RequestBody.create(null,address));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"longitude\""),
                RequestBody.create(null, SharedPreferenceUtil.getLongitude()));
        builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"latitude\""),
                RequestBody.create(null, SharedPreferenceUtil.getLatitude()));

        for (int i=0; i<list_image_path.size(); i++){
            String path = list_image_path.get(i);
            Log.d("AddRecruitActivity", "form-data; name=\"part" + (i+1) + "\"");

            Bitmap bitmap;
            byte[] bytes;
            try {
                bitmap = ImageUtil.revitionImageSize(path);

                bytes = CommentUtil.Bitmap2Bytes(bitmap);
                fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
                header = Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"part" + (i + 1) + "\"");
                builder.addPart(header, fileBody);
            }catch (IOException e){
                e.printStackTrace();
            }


//            file = new File(path);
//            Log.d("AddRecruitActivity", "path = " + path);
//            fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//            header = Headers.of(
//                    "Content-Disposition",
//                    "form-data; name=\"part" + (i+ 1) + "\"");
//            builder.addPart(header,fileBody);
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AddRecruitServlet")
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_ADD_RECRUIT;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });



    }


    public static boolean checkLogin(){
        Log.d(TAG, "checkLogin: session id = " + SharedPreferenceUtil.getSessionId());
        Log.d(TAG, "checkLogin: ");
        if(SharedPreferenceUtil.getSessionId().equals("") ||
                SharedPreferenceUtil.getState().equals("")){
            return false;
        }
        return true;
    }

    public static void pollServive(final Handler handler){
        Log.d(TAG, "pollServive: session_id = " + SharedPreferenceUtil.getSessionId());
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ConnectionServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_POLL_SERVICE;
                    message.obj = result;
                    handler.sendMessage(message);
                }
            }
        });

    }

    public static void getAdDdtail(final Handler handler,String type){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("type", type)
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/AdDetailServlet")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_AD_DETAIL;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });

    }

    public static void getADSetting(final Handler handler){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + "/ADSettingServlet")
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    String result = response.body().string();
                    Message message = new Message();
                    message.what = MESSAGE_AD_SETTING;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void downloadUpdateVersion(final Handler handler, String app_path){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + app_path)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        Log.d("Download apk", WebUtil.HTTP_ADDRESS+app_path);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();
                    Message message = new Message();
                    message.what = MESSAGE_DOWNLOAD;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }


    public static void downloadNewsVoice(final Handler handler, String news_voice_url){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        Log.d("Download NewsVoice", WebUtil.HTTP_ADDRESS + news_voice_url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();
                    Message message = new Message();
                    message.what = MESSAGE_DOWNLOAD_NEWS_VOICE;
                    message.obj = result;
                    handler.sendMessage(message);
                    System.out.println(result);
                }
            }
        });
    }

    public static void downloadHeadImage(final Handler handler, final String path){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        Log.d("Download Image", path);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();

                    System.out.println(result);

                    //Bitmap bm = BitmapFactory.decodeByteArray(byte[] data, int offset,int length);//别忘了判断数组是不是为空。
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                    String filename = path.split("head/")[1];
                    System.out.println("filename =  "+filename);
                    saveFile(bitmap, filename);

                    Message message = new Message();
                    message.what = MESSAGE_DOWNLOAD_IMAGE;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static void downloadNewsImage(final Handler handler, final String path){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        Log.d("Download Image", path);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();

                    System.out.println(result);

                    //Bitmap bm = BitmapFactory.decodeByteArray(byte[] data, int offset,int length);//别忘了判断数组是不是为空。
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result,0,result.length);
                    String filename = path.split("newsimg/")[1];
                    System.out.println("filename =  "+filename);
                    saveFile(bitmap, filename);

                    Message message = new Message();
                    message.what = MESSAGE_DOWNLOAD_IMAGE;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static void saveFile(Bitmap bm, String fileName) throws IOException {
        String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/social/";

        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File imageFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    private class LoginRoot{
        private boolean seccess;
        private String message;
        private String session_id;
        private User user;
    }
    private class User{
        private String id;
        private String username;
        private String nickname;
        private String imagepath;
        private double longitude;
        private double latiaude;
        private String phone;
        private String email;
        private String user_head_path;
        private String signature;
        private String city;
        private String sex;
    }

}
