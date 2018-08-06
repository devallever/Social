package com.allever.social.activity;

import android.os.Bundle;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.fragment.MyEaseChatFragment;
import com.baidu.mobstat.StatService;
import com.hyphenate.easeui.EaseConstant;

/**
 * Created by XM on 2016/5/3.
 * 聊天界面
 */
public class ChatActivity extends BaseActivity {
    //private SocialDBAdapter db;
    private String forward_msg_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        forward_msg_id = getIntent().getStringExtra("forward_msg_id");

        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        String friend_id = getIntent().getStringExtra("friend_id");


       // new出EaseChatFragment或其子类的实例
        MyEaseChatFragment chatFragment = new MyEaseChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, friend_id);
        args.putString("forward_msg_id",forward_msg_id);
        chatFragment.setArguments(args);
        //chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.id_chat_activity_chat_fg_container, chatFragment).commit();


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
    protected void onDestroy() {
        super.onDestroy();
    }
}
