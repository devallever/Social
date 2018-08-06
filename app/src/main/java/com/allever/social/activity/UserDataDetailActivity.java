package com.allever.social.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.PhotoWallImgBaseAdapter;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MyGridView;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;


/**
 * Created by XM on 2016/5/27.
 * 用户资料界面
 */
public class UserDataDetailActivity extends BaseActivity implements View.OnClickListener,RippleView.OnRippleCompleteListener {

    private static final int REQUEST_CODE_SECOND_NAME = 1000;
    private static final int REQUEST_CODE_CHOOSE_FRIEND_GROUP = 1001;

    private String friend_id;//username对应的id

    private Toolbar toolbar;
   // private String friend_id;
    private Handler handler;
    private Root root;
    private Gson gson;

    //private TextView tv_nickname;
    private TextView tv_username;
    private TextView tv_city;
    private TextView tv_signature;
    private TextView tv_hight;
    private TextView tv_weight;
    private TextView tv_figure;
    private TextView tv_emotion;
    private TextView tv_distance;
    private TextView tv_follow;
    private TextView tv_unfollow;

    private ImageView iv_news_img_1;
    private ImageView iv_news_img_2;
    private ImageView iv_news_img_3;
    private ImageView iv_vip_logo;

    //private ImageView iv_sex;
   // private CircleImageView iv_head;
    private ButtonRectangle btn_add;
    private ButtonRectangle btn_chat;
    private ButtonRectangle btn_delet;
    private ButtonRectangle btn_video_call;

    // private TableRow tableRow_news;
    // private TableRow tableRow_photo;

    private RippleView rv_news;
    private RippleView rv_photo;
    private RippleView rv_second_name;
    private RippleView rv_friendgroup;
    private TextView tv_secondname;
    private TextView tv_friendgroup_name;

    private TextView tv_sex;
    private TextView tv_age;
    private LinearLayout ll_sex;

    private TextView tv_constellation;
    private LinearLayout ll_constellation;

    private TextView tv_occupation;
    private LinearLayout ll_occupation;

    private List<String> list_news_img;

    private MyGridView gridview;
    private List<String> list_photo_wall;
    private PhotoWallImgBaseAdapter photoWallImgBaseAdapter;

    private String username;
    private ImageView iv_bg;

    private RippleView rv_follow;
    private RippleView rv_fans;
    private TextView tv_follow_count;
    private TextView tv_fans_count;

    private int video_fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        setContentView(R.layout.user_data_detail_activity_layout);
        username = getIntent().getStringExtra("username");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_USER_DATA:
                        if (msg!=null) handleUserData(msg);
                        else return ;
                        break;
                    case OkhttpUtil.MESSAGE_ADD_FRIEND:
                        //handleAddFriend(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_FRIEND:
                        handleDeleteFriend(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_PHOTO_WALL_LIST:
                        handlePhotoWallList(msg);
                        break;
                    case 1000:
                        new Dialog(UserDataDetailActivity.this,"提示","您的信用不足，请充值。").show();
                        break;
                    case OkhttpUtil.MESSAGE_FOLLOW_USER:
                        handleFollowUser(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DIS_FOLLOW_USER:
                        handleDisFollowUser(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_SHARE_RECORD:
                        handleAddShareRecord(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SOCIAL_COUNT:
                        handleGetSocialCount(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GET_SHARE_INFO:
                        handleGetShareInfo(msg);
                        break;
                }

            }
        };

        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("详细信息");

        initData();
        //OkhttpUtil.getUserData(friend_id, handler);

        getPhotoWallList();

        getUserData();

        getSocialCount();

    }

    private void getSocialCount(){
        OkhttpUtil.getSocialCount(handler, username);
    }

    private void handleGetSocialCount(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SocialCountRoot root = gson.fromJson(result, SocialCountRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        tv_fans_count.setText(root.fans_count + "");
        tv_follow_count.setText(root.follow_count + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SECOND_NAME:
                //getUserData();
                if (resultCode == RESULT_OK){
                    tv_secondname.setText(data.getStringExtra("second_name"));
                }
                break;
            case REQUEST_CODE_CHOOSE_FRIEND_GROUP:
                if(resultCode == RESULT_OK){
                    tv_friendgroup_name.setText(data.getStringExtra("selected_friendgroup_name"));
                }
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        db.close();
    }

    private void handleAutoLogin(Message msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("已自动登录");
        builder.setContentText("请重新操作...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());

        //发广播通知MainActivity修改界面
        Intent intent = new Intent("com.allever.autologin");
        sendBroadcast(intent);

        String result = msg.obj.toString();
        Log.d("Setting", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });


    }


    /**
     * 删除好友
     * **/
    private void deleteFriend(){
        OkhttpUtil.deleteFriend(handler, username);
    }

    private void handleDeleteFriend(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataActivity", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FriendRoot root = gson.fromJson(result, FriendRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
            return;
        }else{
            new Dialog(this,"提示","删除成功").show();
            //finish();
            btn_delet.setVisibility(View.INVISIBLE);
            btn_add.setVisibility(View.VISIBLE);

            Intent intent = new Intent("com.allever.updateFriend");
            sendBroadcast(intent);
        }

    }


    /**
    * 获取照片墙图片地址
    * **/
    private void getPhotoWallList(){
        OkhttpUtil.getPhotoWallList(handler, username);
    }

    private void handlePhotoWallList(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PhotoWallListRoot root = gson.fromJson(result, PhotoWallListRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                return;
            }
            if(root.message.equals("未登录")){
                new Dialog(this,"Tips","未登录").show();
                return;
            }
        }

        //to do
        list_photo_wall = new ArrayList<>();
        for(String path: root.photowalllist){
            list_photo_wall.add(WebUtil.HTTP_ADDRESS + path);
        }
        if(root.photowalllist.size()==0){
            list_photo_wall.add(SharedPreferenceUtil.getUserHeadPath(username));
        }
        photoWallImgBaseAdapter = new PhotoWallImgBaseAdapter(this,list_photo_wall);
        gridview.setAdapter(photoWallImgBaseAdapter);


    }



    private void initData(){
        //tv_nickname = (TextView)this.findViewById(R.id.id_user_detail_tv_nickname);
        tv_username = (TextView)this.findViewById(R.id.id_user_detail_tv_username);
        tv_city = (TextView)this.findViewById(R.id.id_user_detail_tv_city);
        tv_signature = (TextView)this.findViewById(R.id.id_user_detail_tv_signature);
        tv_hight = (TextView)this.findViewById(R.id.id_user_detail_tv_hight);
        tv_weight = (TextView)this.findViewById(R.id.id_user_detail_tv_weight);
        tv_figure = (TextView)this.findViewById(R.id.id_user_detail_tv_figure);
        tv_emotion = (TextView)this.findViewById(R.id.id_user_detail_tv_emotion);
        tv_distance = (TextView)this.findViewById(R.id.id_user_detail_tv_discance);
        tv_follow = (TextView)this.findViewById(R.id.id_user_detail_tv_follow);
        tv_unfollow = (TextView)this.findViewById(R.id.id_user_detail_tv_unfollow);

        tv_follow.setOnClickListener(this);
        tv_unfollow.setOnClickListener(this);

        //iv_head = (CircleImageView)this.findViewById(R.id.id_user_detail_iv_userhead);
        //iv_sex = (ImageView)this.findViewById(R.id.id_user_data_iv_sex);
        iv_news_img_1 = (ImageView)this.findViewById(R.id.id_user_detail_iv_news_img_1);
        iv_news_img_2 = (ImageView)this.findViewById(R.id.id_user_detail_iv_news_img_2);
        iv_news_img_3 = (ImageView)this.findViewById(R.id.id_user_detail_iv_news_img_3);
        iv_vip_logo = (ImageView)this.findViewById(R.id.id_user_detail_iv_vip_logo);

        btn_add = (ButtonRectangle)this.findViewById(R.id.id_user_detail_btn_add);
        btn_chat = (ButtonRectangle)this.findViewById(R.id.id_user_detail_btn_chat);
        btn_delet = (ButtonRectangle)this.findViewById(R.id.id_user_detail_btn_delete);
        btn_video_call = (ButtonRectangle)this.findViewById(R.id.id_user_detail_btn_video_call);

        btn_add.setOnClickListener(this);
        btn_delet.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_video_call.setOnClickListener(this);
        //iv_head.setOnClickListener(this);
        iv_bg = (ImageView)this.findViewById(R.id.id_user_detail_iv_bg);

        tv_sex = (TextView)this.findViewById(R.id.id_user_detail_tv_sex);
        tv_age = (TextView)this.findViewById(R.id.id_user_detail_tv_age);
        ll_sex = (LinearLayout)this.findViewById(R.id.id_user_detail_ll_sex);

        tv_constellation = (TextView)this.findViewById(R.id.id_user_detail_tv_constellation);
        //ll_constellation = (LinearLayout)this.findViewById(R.id.id_user_detail_ll_constellation);

        tv_occupation = (TextView)this.findViewById(R.id.id_user_detail_tv_occupation);
        //ll_occupation = (LinearLayout)this.findViewById(R.id.id_user_detail_ll_occupation);

        rv_news = (RippleView)this.findViewById(R.id.id_user_detail_rv_news);
        rv_news.setOnRippleCompleteListener(this);
        rv_second_name = (RippleView)this.findViewById(R.id.id_user_detail_rv_second_name);
        rv_second_name.setOnRippleCompleteListener(this);
        rv_friendgroup = (RippleView)this.findViewById(R.id.id_user_detail_rv_friendgroup);
        rv_friendgroup.setOnRippleCompleteListener(this);
//        rv_photo = (RippleView)this.findViewById(R.id.id_user_detail_rv_photo);
//        rv_photo.setOnRippleCompleteListener(this);

        rv_follow = (RippleView)this.findViewById(R.id.id_user_detail_rv_follow);
        rv_fans = (RippleView)this.findViewById(R.id.id_user_detail_rv_fans);
        rv_follow.setOnRippleCompleteListener(this);
        rv_fans.setOnRippleCompleteListener(this);
        tv_follow_count = (TextView)this.findViewById(R.id.id_user_detail_tv_follow_count);
        tv_fans_count = (TextView)this.findViewById(R.id.id_user_detail_tv_fans_count);

        tv_friendgroup_name = (TextView)this.findViewById(R.id.id_user_detail_tv_friendgroup_name);
        tv_secondname = (TextView)this.findViewById(R.id.id_user_detail_tv_second_name);

        gridview = (MyGridView)this.findViewById(R.id.id_id_user_detail_gv_photowall);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(UserDataDetailActivity.this, ShowNewsImageActivity.class);
                String[] list_path = new String[list_photo_wall.size()];
                for (int i=0; i<list_photo_wall.size();i++){
                    list_path[i] =  list_photo_wall.get(i);
                }
                intent.putExtra("listpath",list_path);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        if (username.equals(SharedPreferenceUtil.getUserName())){
            btn_add.setVisibility(View.GONE);
            btn_delet.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_video_call.setVisibility(View.GONE);
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        Intent intent;
        switch (id){
            case R.id.id_user_detail_rv_news:
                intent = new Intent(this, UserNewsActivity.class);
                intent.putExtra("user_id",root.user.id);
                intent.putExtra("nickname",root.user.nickname);
                intent.putExtra("username",root.user.username);
                intent.putExtra("user_head_path",root.user.user_head_path);
                startActivity(intent);
                break;
            case R.id.id_user_detail_rv_second_name:
                intent = new Intent(this, SecondNameActivity.class);
                intent.putExtra("friend_id",friend_id);
                intent.putExtra("old_second_name",tv_secondname.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_SECOND_NAME);
                break;
            case R.id.id_user_detail_rv_friendgroup:
                intent  = new Intent(this,ChooseFriendGroupActivity.class);
                intent.putExtra("friend_id", friend_id);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_FRIEND_GROUP);
                break;
//            case R.id.id_user_data_rv_photo:
//                String[] arr_news_img = new String[list_news_img.size()];
//                for (int i=0;i<list_news_img.size();i++){
//                    arr_news_img[i] = WebUtil.HTTP_ADDRESS + list_news_img.get(i);
//                }
//                intent = new Intent(this,PhotoActivity.class);
//                intent.putExtra("arr_news_img", arr_news_img);
//                startActivity(intent);
//                break;
            case R.id.id_user_detail_rv_follow:
                intent = new Intent(this, FollowFansCountActivity.class);
                intent.putExtra("page_position", 0);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.id_user_detail_rv_fans:
                intent = new Intent(this, FollowFansCountActivity.class);
                intent.putExtra("page_position", 1);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
//            case R.id.id_user_data_tablerow_news:
//                intent = new Intent(this, UserNewsActivity.class);
//                intent.putExtra("user_id",root.user.id);
//                intent.putExtra("nickname",root.user.nickname);
//                intent.putExtra("username",root.user.username);
//                intent.putExtra("user_head_path",root.user.user_head_path);
//                startActivity(intent);
//                //tableRow.setBackgroundColor(getResources().getColor(R.color.divider_grey));
//                break;
//            case R.id.id_user_data_tablerow_photo:
//                String[] arr_news_img = new String[list_news_img.size()];
//                for (int i=0;i<list_news_img.size();i++){
//                    arr_news_img[i] = WebUtil.HTTP_ADDRESS + list_news_img.get(i);
//                }
//                intent = new Intent(this,PhotoActivity.class);
//                intent.putExtra("arr_news_img", arr_news_img);
//                startActivity(intent);
//                break;
            case R.id.id_user_detail_btn_add:
                try {
                    if (SharedPreferenceUtil.getUserName().equals(username)) {
                        btn_add.setClickable(false);
                        Toast.makeText(this, "不能添加自己.", Toast.LENGTH_LONG).show();
                        return ;
                    }
                    EMClient.getInstance().contactManager().addContact(username, "请求添加好友.");
                    Toast.makeText(this, "发送成功.", Toast.LENGTH_LONG).show();
                }catch (HyphenateException e){
                    e.printStackTrace();
                }

                //addFriend();
                break;
            case R.id.id_user_detail_btn_delete:
                final Dialog dialog = new Dialog(this, "提示", "你确定要删除好友吗？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //deleteNews();
                        Dialog dialog = new Dialog(UserDataDetailActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    EMClient.getInstance().contactManager().deleteContact(username);
                                    deleteFriend();
                                }catch (HyphenateException e){
                                    e.printStackTrace();
                                }
                                //deleteFriend();
                                //new Dialog(UserNewsActivity.this, "提示", "删除成功").show();
                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
                break;
            case R.id.id_user_detail_btn_chat:
                if (SharedPreferenceUtil.getUserName().equals(username)) {
                    Toast.makeText(this, "不能跟自己聊天.", Toast.LENGTH_LONG).show();
                    return ;
                }
                intent = new Intent(this,ChatActivity.class);
                intent.putExtra("friend_id", username);
                startActivity(intent);
                break;
            case R.id.id_user_detail_btn_video_call:
                final Dialog dialog_video = new Dialog(this,"提示","发起视频聊天\n需要向 " + SharedPreferenceUtil.getUserNickname(username) + " \n支付 " + video_fee + " 信用/分钟");
                dialog_video.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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
                                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                                    CreditRoot root = gson.fromJson(result, CreditRoot.class);

                                    if(root==null){
                                        //new Dialog(context,"错误","链接服务器失败").show();
                                        return ;
                                    }

                                    if (!root.success){
                                        //new Dialog(context,"Tips",root.message).show();
                                        return;
                                    }

                                    if (root.credit >= video_fee){
                                        startActivity(new Intent(UserDataDetailActivity.this, VideoCallActivity.class).putExtra("username", username)
                                                .putExtra("isComingCall", false));
                                    }else{
                                        Message message = new Message();
                                        message.what = 1000;
                                        message.obj = result;
                                        message.arg1 = -1;
                                        handler.sendMessage(message);
                                        System.out.println(result);
                                    }

                                }
                            }
                        });

                    }
                });
                dialog_video.show();
                //视频聊天
                break;
            case R.id.id_user_detail_tv_unfollow:
                if (OkhttpUtil.checkLogin()) disfollowUser();
                else Toast.makeText(this,"请登录", Toast.LENGTH_LONG).show();
                break;
            case R.id.id_user_detail_tv_follow:
                if (OkhttpUtil.checkLogin()) followUser();
                else Toast.makeText(this,"请登录", Toast.LENGTH_LONG).show();
                break;
//            case R.id.id_user_data_iv_userhead:
//                intent = new Intent(this,ShowBigImageActvity.class);
//                intent.putExtra("image_path",WebUtil.HTTP_ADDRESS + root.user.user_head_path);
//                startActivity(intent);
//                break;
        }
    }

    private void disfollowUser(){
        OkhttpUtil.disfollowUser(handler, username);
    }

    private void handleDisFollowUser(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FollowRoot root = gson.fromJson(result, FollowRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.is_follow==1){
            tv_follow.setVisibility(View.GONE);
            tv_unfollow.setVisibility(View.VISIBLE);
        }else{
            tv_follow.setVisibility(View.VISIBLE);
            tv_unfollow.setVisibility(View.GONE);
        }

        //发广播通知修改MineFragment
        Intent intentBroadcast = new Intent("com.allever.social.UPDATE_SOCIAL_COUNT");
        sendBroadcast(intentBroadcast);

    }

    private void followUser(){
        OkhttpUtil.followUser(handler, username);
    }


    private void handleFollowUser(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FollowRoot root = gson.fromJson(result, FollowRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.is_follow==1){
            tv_follow.setVisibility(View.GONE);
            tv_unfollow.setVisibility(View.VISIBLE);
        }else{
            tv_follow.setVisibility(View.VISIBLE);
            tv_unfollow.setVisibility(View.GONE);
        }

        //发广播通知修改MineFragment
        Intent intentBroadcast = new Intent("com.allever.social.UPDATE_SOCIAL_COUNT");
        sendBroadcast(intentBroadcast);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_share:
                if (OkhttpUtil.checkLogin()){
                    //showShare();
                    getShareInfo();
                }
                else Toast.makeText(this,"未登录", Toast.LENGTH_LONG).show();
                break;
//            case R.id.id_menu_share_sina:
//                shareSina();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getShareInfo(){
        OkhttpUtil.getShareInfo(handler);
    }
    private void handleGetShareInfo(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ShareInfoRoot root = gson.fromJson(result, ShareInfoRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        showShare(root.content, root.url, root.img_url);

    }

    private void shareSina(){
        Platform.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText("测试分享的文本");
        sp.setImageUrl(WebUtil.HTTP_ADDRESS + "/images/logo.png");
        //sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("TAG","Success");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("TAG", "Error \n" + throwable.getMessage());
                Toast.makeText(UserDataDetailActivity.this,"Error \n" + throwable.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("TAG","Cancle");
            }
        }); // 设置分享事件回调
// 执行图文分享
        weibo.share(sp);
    }




    private void getUserData(){
        //
        OkhttpUtil.getUserData(username, handler);
        //OkhttpUtil.getUserData(db.get);
    }


    private void handleUserData(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.seccess == false){
            new Dialog(this,"错误",root.message).show();
        }

        friend_id = root.user.id;

        list_news_img = root.user.list_news_img;
        SharedPreferenceUtil.saveUserData(root.user.username, root.user.nickname, WebUtil.HTTP_ADDRESS + root.user.user_head_path);


        username = root.user.username;
        //tv_nickname.setText(root.user.nickname);
        tv_username.setText(root.user.username);
        tv_city.setText(root.user.city);
        tv_signature.setText(root.user.signature);
        tv_distance.setText(root.user.distance+" km");

        video_fee = root.user.video_fee;

        if (SharedPreferenceUtil.getVip().equals("1")){
            tv_hight.setText(root.user.hight);
            tv_weight.setText(root.user.weight);
            tv_figure.setText(root.user.figure);
            tv_emotion.setText(root.user.emotion);
        }

        if (root.user.is_vip==1){
            tv_username.setTextColor(getResources().getColor(R.color.colorRed_500));
            iv_vip_logo.setVisibility(View.VISIBLE);
        }else{
            tv_username.setTextColor(getResources().getColor(R.color.black_deep));
            iv_vip_logo.setVisibility(View.GONE);
        }

        if(root.user.accept_video==1){
            if (!SharedPreferenceUtil.getUserName().equals(username)) btn_video_call.setVisibility(View.VISIBLE);
            else btn_video_call.setVisibility(View.GONE);
        }else {
            btn_video_call.setVisibility(View.GONE);
        }

        if (SharedPreferenceUtil.getUserName().equals(username)){
            tv_follow.setVisibility(View.GONE);
            tv_unfollow.setVisibility(View.GONE);
        }else{
            if (root.user.is_follow==1){
                tv_follow.setVisibility(View.GONE);
                tv_unfollow.setVisibility(View.VISIBLE);
            }else{
                tv_follow.setVisibility(View.VISIBLE);
                tv_unfollow.setVisibility(View.GONE);
            }
        }



        switch (root.user.list_news_img.size()){
            case 0:
                break;
            case 1:
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(0)).into(iv_news_img_1);
                break;
            case 2:
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(0)).into(iv_news_img_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(1)).into(iv_news_img_2);
                break;
            case 3:
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(0)).into(iv_news_img_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(1)).into(iv_news_img_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(2)).into(iv_news_img_3);
                break;
            default:
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(0)).into(iv_news_img_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(1)).into(iv_news_img_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(2)).into(iv_news_img_3);
                break;
        }



        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + root.user.user_head_path)
                .into(iv_bg);

        tv_age.setText(root.user.age+"");
        tv_sex.setText(root.user.sex);
        if(root.user.sex.equals("男")){
            ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
        }else{
            ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);
        }

        tv_constellation.setText(root.user.constellation);
        tv_occupation.setText(root.user.occupation);

        if (root.user.second_name==null) {
            rv_second_name.setVisibility(View.GONE);
        }else{
            rv_second_name.setVisibility(View.VISIBLE);
            tv_secondname.setText(root.user.second_name);
        }

        if (root.user.friendgroup_name==null) {
            rv_friendgroup.setVisibility(View.GONE);
        }else{
            rv_friendgroup.setVisibility(View.VISIBLE);
            tv_friendgroup_name.setText(root.user.friendgroup_name);
        }






        getSupportActionBar().setTitle(root.user.nickname);

//        switch (root.user.constellation){
//            case "白羊座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorGray_300));
//                tv_constellation.setText("白羊");
//                break;
//            case "金牛座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_constellation.setText("金牛");
//                break;
//            case "双子座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
//                tv_constellation.setText("双子");
//                break;
//            case "巨蟹座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_constellation.setText("巨蟹");
//                break;
//            case "狮子座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_constellation.setText("狮子");
//                break;
//            case "处女座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorPink_300));
//                tv_constellation.setText("处女");
//                break;
//            case "天秤座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
//                tv_constellation.setText("天秤");
//                break;
//            case "天蝎座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorPurple_300));
//                tv_constellation.setText("天蝎");
//                break;
//            case "射手座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
//                tv_constellation.setText("射手");
//                break;
//            case "魔蝎座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorIndigo_300));
//                tv_constellation.setText("魔蝎");
//                break;
//            case "水瓶座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
//                tv_constellation.setText("水瓶");
//                break;
//            case "双鱼座":
//                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_constellation.setText("双鱼");
//                break;
//
//        }

//        switch (root.user.occupation){
//            case "学生":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
//                tv_occupation.setText("学");
//                break;
//            case "信息技术":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_occupation.setText("IT");
//                break;
//            case "保险":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGray_300));
//                tv_occupation.setText("保");
//                break;
//            case "工程制造":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
//                tv_occupation.setText("工");
//                break;
//            case "商业服务":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
//                tv_occupation.setText("商");
//                break;
//            case "交通运输":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorIndigo_300));
//                tv_occupation.setText("交");
//                break;
//            case "文化传媒":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorPurple_300));
//                tv_occupation.setText("文");
//                break;
//            case "教育":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
//                tv_occupation.setText("教");
//                break;
//            case "娱乐":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorSexPink));
//                tv_occupation.setText("娱");
//                break;
//            case "公共事业":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
//                tv_occupation.setText("公");
//                break;
//            case "金融":
//                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
//                tv_occupation.setText("金");
//                break;
//        }


        if(root.user.is_friend==1){
            btn_add.setVisibility(View.INVISIBLE);
            btn_delet.setVisibility(View.VISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
        }else if(root.user.is_friend == 0){

        }
        if(root.user.id == SharedPreferenceUtil.getUserId()){

        }

    }

    //调用shareSDK分享代码
    private void showShare(String content,String url,String img_url) {
        ShareSDK.initSDK(UserDataDetailActivity.this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(Constants.SHARE_TITLE + SharedPreferenceUtil.getUserName());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath();//确保SDcard下面存在此张图片
        oks.setImageUrl(img_url);//新增参数
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(Constants.SHARE_TITLE + SharedPreferenceUtil.getUserName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("sharecallback", "成功" + platform.getName());
                Toast.makeText(UserDataDetailActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                addShareRecord();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("sharecallback", "错误，失败");
                Toast.makeText(UserDataDetailActivity.this, "失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("sharecallback", "取消");
                Toast.makeText(UserDataDetailActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
// 启动分享GUI
        oks.show(UserDataDetailActivity.this);
    }

    private void addShareRecord(){
        OkhttpUtil.addShareRecord(handler);
    }

    private void handleAddShareRecord(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataDetail", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddShareRecordRoot root = gson.fromJson(result, AddShareRecordRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;


    }

    class AddShareRecordRoot{
        boolean success;
        String message;
    }

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }


    class Root{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }

    class User{
        String id;
        String username;
        String nickname;
        double longitude;
        double latiaude;
        double distance;
        int is_friend;
        String phone;
        String user_head_path;
        String email;
        String signature;
        String city;
        String sex;
        int age;
        String occupation;
        String constellation;
        String hight;
        String weight;
        String figure;
        String emotion;
        int is_vip;
        int accept_video;
        int video_fee;
        int is_follow;
        String second_name;
        String friendgroup_name;
        List<String> list_news_img;
    }

    class FriendRoot{
        boolean success;
        String message;
    }


    class PhotoWallListRoot{
        boolean success;
        String message;
        List<String> photowalllist;
    }

    class CreditRoot{
        boolean success;
        String message;
        int credit;
    }

    class FollowRoot{
        boolean success;
        String message;
        int is_follow;
    }

    class SocialCountRoot{
        boolean success;
        String message;
        int fans_count;//粉丝数
        int follow_count;//关注数
        int news_count;//动态数
    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }

}
