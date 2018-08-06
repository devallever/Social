package com.allever.social.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Switch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.OnClick;

/**
 * Created by Allever on 2016/11/5.
 */

public class RequestFriendLocationDialogActivity extends BaseActivity implements View.OnClickListener {
    private String username;  //用户名
    private TextView tv_nickname;
    private TextView tv_reason;
    private ImageView iv_head;

    private ButtonRectangle btn_accept;
    private ButtonRectangle btn_reject;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_friend_location_dialog_activity_layout);

        username = getIntent().getStringExtra("username");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ACCEPT_FRIEND_LOCATION:
                        handleAcceptFriendLocation(msg);
                        break;
                }
            }
        };

        initView();
    }
    private void  initView(){
        tv_nickname = (TextView)this.findViewById(R.id.id_request_friend_location_dialog_activity_tv_nickname);
        tv_reason = (TextView)this.findViewById(R.id.id_request_friend_location_dialog_activity_tv_reason);
        tv_nickname.setText(SharedPreferenceUtil.getUserNickname(username));

        btn_accept = (ButtonRectangle)this.findViewById(R.id.id_request_friend_location_dialog_activity_btn_accept);
        btn_reject = (ButtonRectangle)this.findViewById(R.id.id_request_friend_location_dialog_activity_btn_reject);
        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);

        iv_head = (ImageView)this.findViewById(R.id.id_request_friend_location_dialog_activity_iv_head);
        Glide.with(this).load( SharedPreferenceUtil.getUserHeadPath(username)).into(iv_head);
        iv_head.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_request_friend_location_dialog_activity_btn_accept:
                acceptRequestFriendLocation();
                break;
            case R.id.id_friend_request_dialog_activity_btn_reject:
                break;
            case R.id.id_request_friend_location_dialog_activity_iv_head:
                Intent intent = new Intent(this,UserDataDetailActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
                break;
        }
    }

    private void acceptRequestFriendLocation(){
        OkhttpUtil.acceptRequestFriendLocation(handler,username);
    }

    private void handleAcceptFriendLocation(Message msg){
        String result = msg.obj.toString();
        Log.d("RequestFriendLocation", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ChooseRequestFriendLocationActivity.Root root = gson.fromJson(result, ChooseRequestFriendLocationActivity.Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        this.finish();
    }

    class Root{
        boolean success;
        String message;
    }
}
