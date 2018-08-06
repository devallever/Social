package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.ForwardUserBaseAdapter;
import com.allever.social.pojo.ForwardUserItem;
import com.allever.social.utils.ListViewUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/8/10.
 */
public class ChooseForwardUserActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ForwardUserBaseAdapter forwardUserBaseAdapter;
    private List<ForwardUserItem> list_forward_user_item = new ArrayList<>();
    private Handler handler;
    private String forward_msg_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_forward_user_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_LIST:
                        handleGetFriendList(msg);
                        break;
                }
            }
        };

        forward_msg_id = getIntent().getStringExtra("forward_msg_id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择用户");

        initView();

        getFriendList();


    }

    private void getFriendList(){
        OkhttpUtil.getFriendList(handler);
    }

    private void handleGetFriendList(Message msg){

        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        list_forward_user_item.clear();
        ForwardUserItem forwardUserItem;
        for (Friend friend: root.friends_list){
            forwardUserItem = new ForwardUserItem();
            forwardUserItem.setUsername(friend.username);
            forwardUserItem.setUser_id(friend.id);
            forwardUserItem.setUser_head_path(friend.head_path);
            forwardUserItem.setNickname(friend.nickname);
            list_forward_user_item.add(forwardUserItem);
        }

        forwardUserBaseAdapter = new ForwardUserBaseAdapter(this,list_forward_user_item);
        listView.setAdapter(forwardUserBaseAdapter);

    }

    private  void initView(){
        listView = (ListView)this.findViewById(R.id.id_choose_forward_user_activity_listview);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("friend_id", list_forward_user_item.get(i).getUsername());
        intent.putExtra("chatType", EaseConstant.CHATTYPE_SINGLE);
        intent.putExtra("forward_msg_id",forward_msg_id);
        startActivity(intent);
        Toast.makeText(this,"已转发",Toast.LENGTH_LONG).show();
        finish();
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
    class Root{
        boolean success;
        String message;
        List<Friend> friends_list;
    }

    class Friend{
        String id;
        String nickname;
        String username;
        String head_path;
        String signature;
    }
}
