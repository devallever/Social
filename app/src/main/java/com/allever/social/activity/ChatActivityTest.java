package com.allever.social.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.ChatItemBaseAdapter;
import com.allever.social.pojo.Msg;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by XM on 2016/5/3.
 */
public class ChatActivityTest extends BaseActivity {
    private ListView listView;
    private List<Msg> list_msg;
    private String friend_id;
    private String friend_head_path;
    private ChatItemBaseAdapter chatItemBaseAdapter;
    private String friend_nickname;

    private EMConversation conversation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_test_layout);

        listView = (ListView)this.findViewById(R.id.id_chat_activity_test_lisview);

        friend_head_path = getIntent().getStringExtra("friend_head_path");
        friend_id = getIntent().getStringExtra("friend_id");
        friend_nickname = getIntent().getStringExtra("friend_nickname");

        getSupportActionBar().setTitle("与" + friend_nickname + "聊天");

        conversation = EMClient.getInstance().chatManager().getConversation(friend_id);
        chatItemBaseAdapter = new ChatItemBaseAdapter(this,conversation,friend_id,friend_head_path);
        listView.setAdapter(chatItemBaseAdapter);

        //注册广播接收新消息
        NewsMessageReceiver newsMessageReceiver = new NewsMessageReceiver();
        IntentFilter intentFilter = new IntentFilter("unknow");
        intentFilter.setPriority(3);
        registerReceiver(newsMessageReceiver, intentFilter);
        //EMClient.getInstance(


    }

    private class NewsMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            //meseage id
            String message_id = intent.getStringExtra("msgid");
            String username = intent.getStringExtra("from");
            EMMessage emMessage = EMClient.getInstance().chatManager().getMessage(message_id);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
//            if(emMessage.getType() == EMMessage.ChatType.GroupChat){
//
//            }
            if(!username.equals(username)){
                return;
            }
            conversation.insertMessage(emMessage);
            chatItemBaseAdapter.notifyDataSetChanged();
            listView.setAdapter(chatItemBaseAdapter);
            listView.setSelection(listView.getCount() - 1);
        }

    }

    private void initData(){

    }
}
