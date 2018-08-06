package com.allever.social.activity;

import android.os.Bundle;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.fragment.MyEaseChatFragment;
import com.baidu.mobstat.StatService;
import com.hyphenate.easeui.EaseConstant;

/**
 * Created by XM on 2016/5/14.
 * 群聊界面
 */
public class GroupChatActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_activity_layout);

        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        String hx_group_id = getIntent().getStringExtra("hx_group_id");

        // new出EaseChatFragment或其子类的实例
        MyEaseChatFragment chatFragment = new MyEaseChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, hx_group_id);
        args.putString("hx_group_id",hx_group_id);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.id_group_chat_activity_chat_fg_container, chatFragment).commit();
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

}
