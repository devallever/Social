package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.ManageFriendGroupActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.FriendGroupItemExpandableBaseAdapter;
import com.allever.social.adapter.FriendItemAdapter;
import com.allever.social.pojo.FriendGroupItem;
import com.allever.social.pojo.FriendItem;
import com.allever.social.pojo.MyRecruitItem;
import com.allever.social.utils.ListViewUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.view.MyGridView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/14.
 */
public class ContactFragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final int MANAGE_FRIEND_GROUP = 1000;

    private static final int ITEM1 = Menu.FIRST;
    private static final int ITEM2_MODIFY = Menu.FIRST + 1;
    private static final int ITEM3_DELETE = Menu.FIRST + 2;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;

    private ExpandableListView expandableListView;
    private FriendGroupItemExpandableBaseAdapter friendGroupItemExpandableBaseAdapter;
    private List<FriendGroupItem> list_friendgroupItem = new ArrayList<>();
    private Handler handler;

    private MyReceiver myReceiver;
    private IntentFilter intentFilter;

    private String[] phone;
    private String phone_json;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment_2_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_GROUP_LIST:
                        handleGetFriendGroupList(msg);
                        break;
                }
            }
        };

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_contact_fg2_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        expandableListView = (ExpandableListView)view.findViewById(R.id.id_contact_fg2_expandable_listview);
        expandableListView.setGroupIndicator(null);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPosition, int childPosition, long l) {
                Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
                intent.putExtra("username", list_friendgroupItem.get(parentPosition).getList_friend().get(childPosition).getUsername());
                startActivity(intent);
                return false;
            }
        });

        registerForContextMenu(expandableListView);

        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.friendgroup_data_changed");
        intentFilter.addAction("com.allever.updateFriend");
        getActivity().registerReceiver(myReceiver,intentFilter);

        readContact();

        getFriendGroupList();

        return view;
    }

    private void readContact(){
        Cursor cursor = null;
        try {
            //查询联系人
            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            List<String> list_phone = new ArrayList<>();
            String number;
            while (cursor.moveToNext()){
                number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                list_phone.add(number);
                Log.d("PhoneNumber","number = " + number);
            }
            phone = new String[list_phone.size()];
            for (int i=0; i<list_phone.size();i++){
                phone[i] = list_phone.get(i);
            }

            Data data = new Data();
            data.phone = phone;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            phone_json = gson.toJson(data);
            Log.d("PhoneNumber","phone_json = " + phone_json);

        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    private void getFriendGroupList(){
        OkhttpUtil.getFriendGroupList(handler,phone_json);
    }

    private void handleGetFriendGroupList(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(getActivity(),"错误","链接服务器失败").show();
            return ;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                list_friendgroupItem.clear();
                return;
            }
            if(root.message.equals("未登录")){
                new Dialog(getActivity(),"Tips","未登录").show();
                return;
            }

        }

        list_friendgroupItem.clear();
        FriendGroupItem friendGroupItem;
        for (FriendGroup friendGroup : root.list_friendgroup){
            friendGroupItem = new FriendGroupItem();
            friendGroupItem.setId(friendGroup.id);
            friendGroupItem.setFriendgroup_name(friendGroup.friendgroup_name);
            FriendItem friendItem;
            List<FriendItem> list_friendItem = new ArrayList<>();
            for(Friend friend: friendGroup.list_friend){
                friendItem = new FriendItem();
                friendItem.setUser_id(friend.id);
                friendItem.setSignature(friend.signature);
                friendItem.setNickname(friend.nickname);
                friendItem.setUser_head_path(friend.head_path);
                friendItem.setUsername(friend.username);
                list_friendItem.add(friendItem);
            }
            friendGroupItem.setList_friend(list_friendItem);
            list_friendgroupItem.add(friendGroupItem);
        }

        friendGroupItemExpandableBaseAdapter = new FriendGroupItemExpandableBaseAdapter(getActivity(),list_friendgroupItem);
        expandableListView.setAdapter(friendGroupItemExpandableBaseAdapter);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (expandableListView.getFirstVisiblePosition() == 0 && !isloading) {
                    // Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    getFriendGroupList();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, ITEM1, 0, "分组管理");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM1:
                Intent intent = new Intent(getActivity(),ManageFriendGroupActivity.class);
                startActivityForResult(intent,MANAGE_FRIEND_GROUP);
                break;
            case ITEM2_MODIFY:
                break;
            case ITEM3_DELETE:
                break;
        }
        return true;
    }

    class Root{
        boolean success;
        String message;
        List<FriendGroup> list_friendgroup;
    }

    class FriendGroup{
        String id;
        String friendgroup_name;
        List<Friend> list_friend;
    }

    class Friend{
        String id;
        String nickname;
        String username;
        String head_path;
        String signature;
    }

    class Data{
        String[] phone;
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.updateFriend":
                case "com.allever.social.friendgroup_data_changed":
                    getFriendGroupList();
                    break;
            }
        }
    }
}
