package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
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
public class GroupRequestDialogActivity extends BaseActivity implements View.OnClickListener {
    private String hx_group_id;  //环信群组id
    private String groupName;
    private String applyer;//申请者的账号username
    private String reason;//理由
    private TextView tv_nickname;
    private TextView tv_reason;
    private ImageView iv_head;

    private ButtonRectangle btn_accept;
    private ButtonRectangle btn_reject;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_request_dialog_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_JOIN_GROUP:
                        GroupRequestDialogActivity.this.finish();
                        break;
                }
            }
        };

        hx_group_id = getIntent().getStringExtra("hx_group_id");
        reason = getIntent().getStringExtra("reason");
        applyer = getIntent().getStringExtra("applyer");
        groupName = getIntent().getStringExtra("groupName");

        tv_nickname = (TextView)this.findViewById(R.id.id_group_request_dialog_activity_tv_nickname);
        tv_reason = (TextView)this.findViewById(R.id.id_group_request_dialog_activity_tv_reason);
        tv_nickname.setText(SharedPreferenceUtil.getUserNickname(applyer));
        tv_reason.setText(reason+":\n(" + groupName + ")");

        btn_accept = (ButtonRectangle)this.findViewById(R.id.id_group_request_dialog_activity_btn_accept);
        btn_reject = (ButtonRectangle)this.findViewById(R.id.id_group_request_dialog_activity_btn_reject);
        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);

        iv_head = (ImageView)this.findViewById(R.id.id_group_request_dialog_activity_iv_head);
        Glide.with(this).load( SharedPreferenceUtil.getUserHeadPath(applyer)).into(iv_head);
        iv_head.setOnClickListener(this);
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
            case R.id.id_group_request_dialog_activity_btn_accept:
                Toast.makeText(this, "已同意", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().addUsersToGroup(hx_group_id, new String[]{applyer});//需异步处理

                            }catch (HyphenateException e){
                                e.printStackTrace();
                            }

                        }
                    }).start();
                OkhttpUtil.joinGroup(handler, SharedPreferenceUtil.getGroupid(hx_group_id),applyer);
                break;
            case R.id.id_friend_request_dialog_activity_btn_reject:
                finish();
                break;
            case R.id.id_friend_request_dialog_activity_iv_head:
                intent = new Intent(this,UserDataActivity.class);
                intent.putExtra("friend_id", applyer);
                startActivity(intent);
                break;
        }
    }
}
