package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonRectangle;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by XM on 2016/5/14.
 */
public class FriendRequestDialogActivity extends BaseActivity implements View.OnClickListener{
    private String friend_id;  //用户名
    private String reason;//理由
    private TextView tv_nickname;
    private TextView tv_reason;
    private ImageView iv_head;

    private CheckBox cb_share_location;
    private String is_share = "1";

    private ButtonRectangle btn_accept;
    private ButtonRectangle btn_reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request_dialog_activity_layout);

        friend_id = getIntent().getStringExtra("friend_id");
        reason = getIntent().getStringExtra("reason");

        tv_nickname = (TextView)this.findViewById(R.id.id_friend_request_dialog_activity_tv_nickname);
        tv_reason = (TextView)this.findViewById(R.id.id_friend_request_dialog_activity_tv_reason);
        tv_nickname.setText(SharedPreferenceUtil.getUserNickname(friend_id));
        tv_reason.setText(reason);

        btn_accept = (ButtonRectangle)this.findViewById(R.id.id_friend_request_dialog_activity_btn_accept);
        btn_reject = (ButtonRectangle)this.findViewById(R.id.id_friend_request_dialog_activity_btn_reject);
        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);

        iv_head = (ImageView)this.findViewById(R.id.id_friend_request_dialog_activity_iv_head);
        Glide.with(this).load( SharedPreferenceUtil.getUserHeadPath(friend_id)).into(iv_head);
        iv_head.setOnClickListener(this);

        cb_share_location = (CheckBox)this.findViewById(R.id.id_friend_request_dialog_activity_cb_share_location);

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
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_friend_request_dialog_activity_btn_accept:
                Toast.makeText(this, "接受", Toast.LENGTH_LONG).show();
                try{
                    EMClient.getInstance().contactManager().acceptInvitation(friend_id);
                    if (cb_share_location.isChecked()){
                        is_share = "1";
                    }else{
                        is_share = "0";
                    }
                    OkhttpUtil.addFriend(new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what){
                                case OkhttpUtil.MESSAGE_ADD_FRIEND:
                                    Toast.makeText(FriendRequestDialogActivity.this,"已添加",Toast.LENGTH_LONG).show();
                                    Intent broadIntent = new Intent("com.allever.updateFriend");
                                    sendBroadcast(broadIntent);//更新我的群组列表
                                    break;
                            }
                        }
                    }, friend_id,is_share);
                    this.finish();
                } catch (HyphenateException e){
                    e.printStackTrace();
                }
                break;
            case R.id.id_friend_request_dialog_activity_btn_reject:
                try {
                    EMClient.getInstance().contactManager().declineInvitation(friend_id);
                    Toast.makeText(this, "已拒绝", Toast.LENGTH_LONG).show();
                    this.finish();
                }catch (HyphenateException e){
                    e.printStackTrace();
                }
                break;
            case R.id.id_friend_request_dialog_activity_iv_head:
                intent = new Intent(this,UserDataDetailActivity.class);
                intent.putExtra("username", friend_id);
                startActivity(intent);
                break;
        }
    }
}
