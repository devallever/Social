package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.GroupDataActivity;
import com.allever.social.adapter.MyGroupItemArrayAdapter;
import com.allever.social.pojo.MyGroupItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/12.
 */
public class MyGroupFragment extends Fragment implements AdapterView.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private MyGroupItemArrayAdapter myGroupItemArrayAdapter;
    private Handler handler;
    private Root root;
    private Gson gson;
    private List<MyGroupItem> list_mygroup;

    private IntentFilter intentFilter;
    private AddGroupReceiver receiver;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_group_fragment_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MY_GROUP_LIST:
                        handleMyGroupList(msg);
                        break;
                }
            }
        };

        listView = (ListView)view.findViewById(R.id.id_my_group_fg_listview);
        listView.setOnItemClickListener(this);
        list_mygroup = new ArrayList<>();
        getMyGroupList();

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.refresh_group_list");
        intentFilter.addAction("com.allever.social.refresh_my_group_group_list");
        receiver = new AddGroupReceiver();
        getActivity().registerReceiver(receiver, intentFilter);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_my_group_fg_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    getMyGroupList();
                    isloading = false;

                } else {
                    Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                            Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), GroupDataActivity.class);
        intent.putExtra("group_id",list_mygroup.get(i).getId());
        startActivity(intent);
    }

    private void getMyGroupList(){
        OkhttpUtil.getMyGroupList(handler);
    }

    private void handleMyGroupList(Message msg){
        String result = msg.obj.toString();
        Log.d("MyGroupList", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                return;
            }
            if(root.message.equals("未登录")){
                //OkhttpUtil.autoLogin(handler);
                new Dialog(getActivity(),"Tips","未登录").show();
                return;
                //new Dialog(getActivity(),"提示",root.message).show();
            }
        }

        list_mygroup.clear();
        if(root.group_list== null || root.group_list.size()==0){
            //new Dialog(getActivity(),"",root.message).show();
            Log.d("MyGroupList->for()", "FAILFFFFFFFFFFFFFFFFFF");
            return ;
        }

        MyGroupItem myGroupItem;
        //Toast.makeText(getActivity(),root.group_list.size()+"",Toast.LENGTH_LONG).show();
        for (Group group : root.group_list){
            Log.d("MyGroupList->for()", group.groupname);
            myGroupItem = new MyGroupItem();
            myGroupItem.setId(group.id);
            myGroupItem.setGroup_img(group.group_img);
            myGroupItem.setGroupname(group.groupname);
            myGroupItem.setDescription(group.description);
            myGroupItem.setIs_my_group(group.is_my_group);
            myGroupItem.setState(group.state);
            list_mygroup.add(myGroupItem);
            SharedPreferenceUtil.saveGroupDataFromHXgroupid(group.id, group.groupname, group.hx_group_id, WebUtil.HTTP_ADDRESS + group.group_img);
        }

        myGroupItemArrayAdapter = new MyGroupItemArrayAdapter(getActivity(),R.layout.my_group_item,list_mygroup);
        listView.setAdapter(myGroupItemArrayAdapter);

    }


    class Root {
        boolean success;
        String message;
        List<Group> group_list;
    }

    class Group {
        String id;
        String groupname;
        String group_img;
        String hx_group_id;
        int state;
        int is_my_group;
        String description;
    }

    private class AddGroupReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.refresh_group_list":
                    getMyGroupList();
                    break;
                case "com.allever.social.refresh_my_group_group_list":
                    getMyGroupList();
                    break;
            }

        }
    }
}
