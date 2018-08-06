package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.FriendGroupNameBaseAdapter;
import com.allever.social.pojo.FriendGroupNameItem;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/15.
 * 选择分组界面
 */
public class ChooseFriendGroupActivity extends BaseActivity {
    private ListView listView;
    private Handler handler;
    private FriendGroupNameBaseAdapter friendGroupNameBaseAdapter;
    private List<FriendGroupNameItem> list_friendgroupnameItem = new ArrayList<>();

    private String friend_id;
    private String friendgroup_id;
    private String selected_friendgroup_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_friendgroup_activity_layout);

        friend_id = getIntent().getStringExtra("friend_id");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_GROUP_NAME_LIST:
                        handleGetFriendGroupNameList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_MODIFY_USER_FRIEND_GROUP:
                        handleModifyUserFriendGroup(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择分组");

        listView = (ListView)this.findViewById(R.id.id_choose_friendgroup_activity_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                friendgroup_id =  list_friendgroupnameItem.get(position).getId();
                selected_friendgroup_name = list_friendgroupnameItem.get(position).getFriendgroup_name();
                modifyUserFriendGroup();
            }
        });

        getFriendGroupNameList();
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



    private void modifyUserFriendGroup(){
        OkhttpUtil.modifyUserFriendGroup(handler, friend_id, friendgroup_id);
    }

    private void handleModifyUserFriendGroup(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("selected_friendgroup_name", selected_friendgroup_name);
        setResult(RESULT_OK, intent);
        Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();

        //发广播通知修改联系人列表
        Intent broadcastIntent = new Intent("com.allever.social.friendgroup_data_changed");
        sendBroadcast(broadcastIntent);
        finish();



    }

    /**
     * 获取分组列表
     * **/
    private void getFriendGroupNameList(){
        OkhttpUtil.getFriendGroupNameList(handler);
    }

    /**
     * 处理获取分组列表
     * **/
    private void handleGetFriendGroupNameList(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        list_friendgroupnameItem.clear();
        FriendGroupNameItem friendGroupNameItem;
        for(FriendGroup friendGroup : root.list_friendgroup){
            friendGroupNameItem = new FriendGroupNameItem();
            friendGroupNameItem.setId(friendGroup.id);
            friendGroupNameItem.setFriendgroup_name(friendGroup.friendgroup_name);
            list_friendgroupnameItem.add(friendGroupNameItem);
        }

        friendGroupNameBaseAdapter = new FriendGroupNameBaseAdapter(this,list_friendgroupnameItem);
        listView.setAdapter(friendGroupNameBaseAdapter);

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
        List<FriendGroup> list_friendgroup;
    }

    class FriendGroup{
        String id;
        String friendgroup_name;
    }
}
