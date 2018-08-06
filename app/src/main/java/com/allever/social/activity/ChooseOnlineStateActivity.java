package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.OnlineStateItemBaseAdapter;
import com.allever.social.pojo.OnlineStateItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/8/2.
 */
public class ChooseOnlineStateActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private ListView listView;
    private OnlineStateItemBaseAdapter onlineStateItemBaseAdapter;
    private List<OnlineStateItem> list_online_item;

    private String onlinestate;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_onlice_state_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_ONLINE_STATE:
                        handleModifyOnlineState(msg);
                        break;
                }
            }
        };

        ininView();

    }

    private void ininView(){
        listView = (ListView)this.findViewById(R.id.id_choose_online_state_activity_listview);
        OnlineStateItem onlineStateItem_online = new OnlineStateItem();
        OnlineStateItem onlineStateItem_offline = new OnlineStateItem();
        OnlineStateItem onlineStateItem_busy = new OnlineStateItem();

        onlineStateItem_online.setState("在线");
        onlineStateItem_offline.setState("离线");
        onlineStateItem_busy.setState("忙碌");

        list_online_item = new ArrayList<>();
        list_online_item.add(onlineStateItem_online);
        list_online_item.add(onlineStateItem_offline);
        list_online_item.add(onlineStateItem_busy);

        onlineStateItemBaseAdapter = new OnlineStateItemBaseAdapter(this,list_online_item);
        listView.setAdapter(onlineStateItemBaseAdapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onlinestate = list_online_item.get(i).getState();
        modifyOnlineState();
    }

    private void modifyOnlineState(){
        OkhttpUtil.modifyOnlineState(handler, onlinestate);
    }

    private void handleModifyOnlineState(Message msg){

        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        OnlineStateRoot root = gson.fromJson(result, OnlineStateRoot.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return ;
        }

        SharedPreferenceUtil.setOnlineState(root.onlinestate);

        //发广播通知修改MineFragment
        Intent broadIntent = new Intent("com.allever.social.UPDATE_ONLINE_STATE");
        sendBroadcast(broadIntent);
        finish();

    }

    class OnlineStateRoot{
        boolean success;
        String message;
        String onlinestate;
    }

}
