package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.adapter.RequestFriendLocationItemRecyclerViewAdapter;
import com.allever.social.pojo.RequestFriendLocationItem;
import com.allever.social.utils.OkhttpUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allever on 2016/11/5.
 */

public class ChooseRequestFriendLocationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private RequestFriendLocationItemRecyclerViewAdapter requestFriendLocationItemVecyclerViewAdapter;
    private List<RequestFriendLocationItem> requestFriendLocationItemList = new ArrayList<>();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_request_friend_location_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择好友");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_NOT_LOCATION_LIST:
                        handleFriendNotLocationList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_REQUEST_FRIEND_LOCATION:
                        handleRequestFriendLocation(msg);
                        break;
                }
            }
        };

        initView();

        getFriendNotLocationList();
    }

    private void initView(){
        recyclerView = (RecyclerView)this.findViewById(R.id.id_choose_request_friend_location_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //发送请求
                requestFriendLocation(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void requestFriendLocation(int position){
        OkhttpUtil.requestFriendLocation(handler,requestFriendLocationItemList.get(position).getUsername());
    }

    private void handleRequestFriendLocation(Message msg){
        Toast.makeText(this,"发送成功", Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void getFriendNotLocationList(){
        OkhttpUtil.getFriendNotLocationList(handler);
    }

    private void handleFriendNotLocationList(Message msg){
        String result = msg.obj.toString();
        Log.d("FriendLocationActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        requestFriendLocationItemList.clear();
        RequestFriendLocationItem requestFriendLocationItem;
        for (Friend friend: root.friends_list){
            requestFriendLocationItem = new RequestFriendLocationItem();
            requestFriendLocationItem.setUsername(friend.username);
            requestFriendLocationItem.setNickname(friend.nickname);
            requestFriendLocationItem.setHead_path(friend.head_path);
            requestFriendLocationItemList.add(requestFriendLocationItem);
        }

        requestFriendLocationItemVecyclerViewAdapter  = new RequestFriendLocationItemRecyclerViewAdapter(this,requestFriendLocationItemList);
        recyclerView.setAdapter(requestFriendLocationItemVecyclerViewAdapter);
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
