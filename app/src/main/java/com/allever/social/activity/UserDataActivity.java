package com.allever.social.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.PhotoWallImgBaseAdapter;
import com.allever.social.adapter.RecruitItemBaseAdapter;
import com.allever.social.db.SocialDBAdapter;
import com.allever.social.utils.CommentUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/21.
 */
public class UserDataActivity extends BaseActivity implements View.OnClickListener,RippleView.OnRippleCompleteListener {
    private Toolbar toolbar;
    private String friend_id;
    private Handler handler;
    private Root root;
    private Gson gson;

    private TextView tv_nickname;
    private TextView tv_username;
    private TextView tv_city;
    private TextView tv_signature;
    private ImageView iv_news_img_1;
    private ImageView iv_news_img_2;
    private ImageView iv_news_img_3;
    //private ImageView iv_sex;
    private CircleImageView iv_head;
    private ButtonRectangle btn_add;
    private ButtonRectangle btn_chat;
    private ButtonRectangle btn_delet;
   // private TableRow tableRow_news;
   // private TableRow tableRow_photo;

    private RippleView rv_news;
    private RippleView rv_photo;

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


    //private SocialDBAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data_layout);

//        db = new SocialDBAdapter(this);
//        db.open();

        friend_id = getIntent().getStringExtra("friend_id");//需要username

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_USER_DATA:
                        handleUserData(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_FRIEND:
                        handleAddFriend(msg);
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
                }

            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("详细信息");

        //toolbar = (Toolbar)this.findViewById(R.id.id_user_data_toolbar);
        //CommentUtil.initToolbar(this, toolbar, "详细资料");

        initData();
        //OkhttpUtil.getUserData(friend_id, handler);

        getPhotoWallList();

        getUserData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPhotoWallList(){
        OkhttpUtil.getPhotoWallList(handler,friend_id);
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
            list_photo_wall.add(SharedPreferenceUtil.getUserHeadPath(friend_id));
        }
        photoWallImgBaseAdapter = new PhotoWallImgBaseAdapter(this,list_photo_wall);
        gridview.setAdapter(photoWallImgBaseAdapter);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        db.close();
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

        list_news_img = root.user.list_news_img;
        SharedPreferenceUtil.saveUserData(root.user.username,root.user.nickname,WebUtil.HTTP_ADDRESS + root.user.user_head_path);


        friend_id = root.user.username;
        tv_nickname.setText(root.user.nickname);
        tv_username.setText("账号：" + root.user.username);
        tv_city.setText(root.user.city);
        tv_signature.setText(root.user.signature);
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

//        if (root.user.list_news_img.size()>0){
//            if (root.user.list_news_img.get(0) != null){
//                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(0)).into(iv_news_img_1);
//            }
//        }
//        if (root.user.list_news_img.size()>1){
//            if (root.user.list_news_img.get(1) != null){
//                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(1)).into(iv_news_img_2);
//            }
//        }
//        if (root.user.list_news_img.size()>2){
//            if (root.user.list_news_img.get(2) != null){
//                Glide.with(this).load(WebUtil.HTTP_ADDRESS + root.user.list_news_img.get(2)).into(iv_news_img_3);
//            }
//        }


        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + root.user.user_head_path)
                .into(iv_head);

        tv_age.setText(root.user.age+"");
        tv_sex.setText(root.user.sex);
        if(root.user.sex.equals("男")){
            ll_sex.setBackgroundColor(getResources().getColor(R.color.colorSexBlue));
        }else{
            ll_sex.setBackgroundColor(getResources().getColor(R.color.colorSexPink));
        }

        switch (root.user.constellation){
            case "白羊座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorGray_300));
                tv_constellation.setText("白羊");
                break;
            case "金牛座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("金牛");
                break;
            case "双子座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
                tv_constellation.setText("双子");
                break;
            case "巨蟹座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("巨蟹");
                break;
            case "狮子座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("狮子");
                break;
            case "处女座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorPink_300));
                tv_constellation.setText("处女");
                break;
            case "天秤座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
                tv_constellation.setText("天秤");
                break;
            case "天蝎座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorPurple_300));
                tv_constellation.setText("天蝎");
                break;
            case "射手座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
                tv_constellation.setText("射手");
                break;
            case "魔蝎座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorIndigo_300));
                tv_constellation.setText("魔蝎");
                break;
            case "水瓶座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
                tv_constellation.setText("水瓶");
                break;
            case "双鱼座":
                ll_constellation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("双鱼");
                break;

        }

        switch (root.user.occupation){
            case "学生":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
                tv_occupation.setText("学");
                break;
            case "信息技术":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_occupation.setText("IT");
                break;
            case "保险":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGray_300));
                tv_occupation.setText("保");
                break;
            case "工程制造":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
                tv_occupation.setText("工");
                break;
            case "商业服务":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorBlue_300));
                tv_occupation.setText("商");
                break;
            case "交通运输":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorIndigo_300));
                tv_occupation.setText("交");
                break;
            case "文化传媒":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorPurple_300));
                tv_occupation.setText("文");
                break;
            case "教育":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorRed_300));
                tv_occupation.setText("教");
                break;
            case "娱乐":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorSexPink));
                tv_occupation.setText("娱");
                break;
            case "公共事业":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorGreen_300));
                tv_occupation.setText("公");
                break;
            case "金融":
                ll_occupation.setBackgroundColor(this.getResources().getColor(R.color.colorOrange_300));
                tv_occupation.setText("金");
                break;
        }


        if(root.user.is_friend==1){
            btn_add.setVisibility(View.INVISIBLE);
            btn_delet.setVisibility(View.VISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
        }else if(root.user.is_friend == 0){

        }
        if(root.user.id == SharedPreferenceUtil.getUserId()){

        }

    }

    private void initData(){
        tv_nickname = (TextView)this.findViewById(R.id.id_user_data_tv_nickname);
        tv_username = (TextView)this.findViewById(R.id.id_user_data_tv_username);
        tv_city = (TextView)this.findViewById(R.id.id_user_data_tv_city);
        tv_signature = (TextView)this.findViewById(R.id.id_user_data_tv_signature);
        iv_head = (CircleImageView)this.findViewById(R.id.id_user_data_iv_userhead);
        //iv_sex = (ImageView)this.findViewById(R.id.id_user_data_iv_sex);
        iv_news_img_1 = (ImageView)this.findViewById(R.id.id_user_data_iv_news_img_1);
        iv_news_img_2 = (ImageView)this.findViewById(R.id.id_user_data_iv_news_img_2);
        iv_news_img_3 = (ImageView)this.findViewById(R.id.id_user_data_iv_news_img_3);
        btn_add = (ButtonRectangle)this.findViewById(R.id.id_user_data_btn_add);
        btn_chat = (ButtonRectangle)this.findViewById(R.id.id_user_data_btn_chat);
        btn_delet = (ButtonRectangle)this.findViewById(R.id.id_user_data_btn_delete);
//        tableRow_news = (TableRow)this.findViewById(R.id.id_user_data_tablerow_news);
//        tableRow_news.setOnClickListener(this);
//        tableRow_news.setOnTouchListener(this);
//        tableRow_photo = (TableRow)this.findViewById(R.id.id_user_data_tablerow_photo);
//        tableRow_photo.setOnClickListener(this);
//        tableRow_photo.setOnTouchListener(this);
        btn_add.setOnClickListener(this);
        btn_delet.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        iv_head.setOnClickListener(this);

        tv_sex = (TextView)this.findViewById(R.id.id_user_data_tv_sex);
        tv_age = (TextView)this.findViewById(R.id.id_user_data_tv_age);
        ll_sex = (LinearLayout)this.findViewById(R.id.id_user_data_ll_sex);

        tv_constellation = (TextView)this.findViewById(R.id.id_user_data_tv_constellation);
        ll_constellation = (LinearLayout)this.findViewById(R.id.id_user_data_ll_constellation);

        tv_occupation = (TextView)this.findViewById(R.id.id_user_data_tv_occupation);
        ll_occupation = (LinearLayout)this.findViewById(R.id.id_user_data_ll_occupation);

        rv_news = (RippleView)this.findViewById(R.id.id_user_data_rv_news);
        rv_news.setOnRippleCompleteListener(this);
        rv_photo = (RippleView)this.findViewById(R.id.id_user_data_rv_photo);
        rv_photo.setOnRippleCompleteListener(this);

        gridview = (MyGridView)this.findViewById(R.id.id_id_user_data_gv_photowall);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(UserDataActivity.this, ShowNewsImageActivity.class);
                String[] list_path = new String[list_photo_wall.size()];
                for (int i=0; i<list_photo_wall.size();i++){
                    list_path[i] =  list_photo_wall.get(i);
                }
                intent.putExtra("listpath",list_path);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        Intent intent;
        switch (id){
            case R.id.id_user_data_rv_news:
                intent = new Intent(this, UserNewsActivity.class);
                intent.putExtra("user_id",root.user.id);
                intent.putExtra("nickname",root.user.nickname);
                intent.putExtra("username",root.user.username);
                intent.putExtra("user_head_path",root.user.user_head_path);
                startActivity(intent);
                break;
            case R.id.id_user_data_rv_photo:
                String[] arr_news_img = new String[list_news_img.size()];
                for (int i=0;i<list_news_img.size();i++){
                    arr_news_img[i] = WebUtil.HTTP_ADDRESS + list_news_img.get(i);
                }
                intent = new Intent(this,PhotoActivity.class);
                intent.putExtra("arr_news_img", arr_news_img);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_user_data_tablerow_news:
                intent = new Intent(this, UserNewsActivity.class);
                intent.putExtra("user_id",root.user.id);
                intent.putExtra("nickname",root.user.nickname);
                intent.putExtra("username",root.user.username);
                intent.putExtra("user_head_path",root.user.user_head_path);
                startActivity(intent);
                //tableRow.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                break;
            case R.id.id_user_data_tablerow_photo:
                String[] arr_news_img = new String[list_news_img.size()];
                for (int i=0;i<list_news_img.size();i++){
                    arr_news_img[i] = WebUtil.HTTP_ADDRESS + list_news_img.get(i);
                }
                intent = new Intent(this,PhotoActivity.class);
                intent.putExtra("arr_news_img", arr_news_img);
                startActivity(intent);
                break;
            case R.id.id_user_data_btn_add:
                try {
                    Toast.makeText(this,"发送成功.",Toast.LENGTH_LONG).show();
                    EMClient.getInstance().contactManager().addContact(friend_id, "请求添加好友.");
                }catch (HyphenateException e){
                    e.printStackTrace();
                }

                //addFriend();
                break;
            case R.id.id_user_data_btn_delete:
                final Dialog dialog = new Dialog(this, "提示", "你确定要删除好友吗？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //deleteNews();
                        Dialog dialog = new Dialog(UserDataActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    EMClient.getInstance().contactManager().deleteContact(friend_id);
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
            case R.id.id_user_data_btn_chat:
                intent = new Intent(this,ChatActivity.class);
                intent.putExtra("friend_id", friend_id);
                startActivity(intent);
                break;
            case R.id.id_user_data_iv_userhead:
                intent = new Intent(this,ShowBigImageActvity.class);
                intent.putExtra("image_path",WebUtil.HTTP_ADDRESS + root.user.user_head_path);
                startActivity(intent);
                break;
        }
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

    private void deleteFriend(){
        OkhttpUtil.deleteFriend(handler, friend_id);
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

    private void addFriend(){
        OkhttpUtil.addFriend(handler,friend_id,"0");
    }

    private void handleAddFriend(Message msg){
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
            if(root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"Tips",root.message).show();
        }else{
            new Dialog(this,"Tips","发送成功").show();
            //finish();
            btn_add.setVisibility(View.INVISIBLE);
            btn_delet.setVisibility(View.VISIBLE);

            Intent intent = new Intent("com.allever.updateFriend");
            sendBroadcast(intent);

        }
    }


    private void getUserData(){
        //
        OkhttpUtil.getUserData(friend_id,handler);
        //OkhttpUtil.getUserData(db.get);
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
        List<String> list_news_img;
    }

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
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

}
