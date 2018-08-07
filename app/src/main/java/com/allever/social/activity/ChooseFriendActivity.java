package com.allever.social.activity;

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
import com.allever.social.adapter.ChooseFriendItemBaseAdapter;
import com.allever.social.pojo.ChooseFriendItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/7/18.
 */
public class ChooseFriendActivity extends BaseActivity {
    private ListView listView;
    private ChooseFriendItemBaseAdapter chooseFriendItemBaseAdapter;
    private List<ChooseFriendItem> list_chooseFriendItem;

    private Handler handler;

    private String group_id;
    private String hx_group_id;
    private String owner_username;
    private String applyer_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_friend_activity_layout);

        group_id = getIntent().getStringExtra("group_id");
        hx_group_id = getIntent().getStringExtra("hx_group_id");
        owner_username = getIntent().getStringExtra("owner_username");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_CHOOSE_FRIEND_LIST:
                        handleGetChooseFriendList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_INVITE_FRIEND_TO_GROUP:
                        handleInviteFriendToGroup(msg);
                        break;

                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择好友");

        initView();

        getChooseFriendList();


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

    private void initView(){
        listView = (ListView)this.findViewById(R.id.id_choose_friend_activity_listview);
        list_chooseFriendItem = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //邀请
                applyer_username = list_chooseFriendItem.get(i).getUsername();
                inviteFriend();
            }
        });
    }

    private void inviteFriend(){
        OkhttpUtil.inviteFriendToGroup(handler, group_id, applyer_username, SharedPreferenceUtil.getUserName());
    }


    private void getChooseFriendList(){
        OkhttpUtil.getChooseFriendList(handler);
    }

    private void handleInviteFriendToGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("ChooseFriendActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"提示",root.message).show();
            return;
        }

        //成功之后邀请
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (owner_username.equals(SharedPreferenceUtil.getUserName())){
                        //群主邀请
                        EMClient.getInstance().groupManager().addUsersToGroup(hx_group_id, new String[]{applyer_username});//需异步处理
                        Log.d("GroupMember","群主邀请");
                    }else{
                        //群成员邀请
                        Log.d("GroupMember", "成员邀请之前");
                        EMClient.getInstance().groupManager().inviteUser(hx_group_id, new String[]{applyer_username},"");//需异步处理
                        Log.d("GroupMember", "成员邀请之后");
                    }
                }catch (HyphenateException e){
                    Log.d("GroupMember", "邀请失败");
                    e.printStackTrace();
                }
            }
        }).start();

        Toast.makeText(this,"邀请成功",Toast.LENGTH_LONG).show();
        this.finish();


    }

    private void handleGetChooseFriendList(Message msg){
        String result = msg.obj.toString();
        Log.d("ChooseFriendActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"提示",root.message).show();
            return;
        }

        list_chooseFriendItem.clear();
        ChooseFriendItem chooseFriendItem;
        for (Friend friend:root.friends_list){
            chooseFriendItem = new ChooseFriendItem();
            chooseFriendItem.setUsername(friend.username);
            chooseFriendItem.setNickname(friend.nickname);
            chooseFriendItem.setUser_head_path(friend.head_path);
            list_chooseFriendItem.add(chooseFriendItem);
        }

        chooseFriendItemBaseAdapter = new ChooseFriendItemBaseAdapter(this,list_chooseFriendItem);
        listView.setAdapter(chooseFriendItemBaseAdapter);


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
