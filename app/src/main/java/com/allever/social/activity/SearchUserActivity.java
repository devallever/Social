package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.GroupItemArrayAdapter;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.pojo.GroupItem;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MyListView;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/29.
 * 搜索界面
 */
public class SearchUserActivity extends BaseActivity {

    private RippleView rv_search;
    private EditText et_search;
    private MyListView listView_user;
    private MyListView listView_group;
    private String key;
    private Handler handler;

    private RelativeLayout rl_user;
    private RelativeLayout rl_group;

    private NearbyUserItemAdapter nearbyUserItemAdapter;
    private List<NearByUserItem> list_user = new ArrayList<>();

    private GroupItemArrayAdapter groupItemArrayAdapter;
    private List<GroupItem> list_group = new ArrayList<GroupItem>();

    //输入框改变时向服务器服务器搜索
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            key = et_search.getText().toString();
            if(key.length()!=0){
                searchUser();
            }else if(key.length()==0){
                list_user.clear();
                nearbyUserItemAdapter = new NearbyUserItemAdapter(SearchUserActivity.this,R.layout.near_by_user_item,list_user);
                listView_user.setAdapter(nearbyUserItemAdapter);
                rl_user.setVisibility(View.GONE);

                list_group.clear();
                groupItemArrayAdapter = new GroupItemArrayAdapter(SearchUserActivity.this,R.layout.nearby_group_item,list_group);
                listView_group.setAdapter(groupItemArrayAdapter);
                rl_group.setVisibility(View.GONE);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_SEARCH_USER:
                        handleSearchUser(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("搜索");

        initData();

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


    private void initData(){
        rv_search  = (RippleView)this.findViewById(R.id.id_search_user_activity_rv_search);
        et_search = (EditText)this.findViewById(R.id.id_search_user_activity_et_search);
        listView_user= (MyListView)this.findViewById(R.id.id_search_user_activity_listview_user);
        listView_group = (MyListView)this.findViewById(R.id.id_search_user_activity_listview_group);

        rl_group = (RelativeLayout)this.findViewById(R.id.id_search_user_activity_rl_group);
        rl_user = (RelativeLayout)this.findViewById(R.id.id_search_user_activity_rl_user);

        //et_search.addTextChangedListener(watcher);
        rv_search.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                key = et_search.getText().toString();
                if (key.length() != 0) {
                    searchUser();
                }
            }
        });

        listView_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchUserActivity.this, UserDataDetailActivity.class);
                intent.putExtra("username", list_user.get(i).getUsername());
                startActivity(intent);
            }
        });

        listView_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchUserActivity.this, GroupDataActivity.class);
                intent.putExtra("group_id",list_group.get(i).getId());
                startActivity(intent);
            }
        });

    }

    private void searchUser(){
        OkhttpUtil.searchUser(key, handler);
    }

    private void handleSearchUser(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(this,"错误","链接服务器失败").show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }


        list_user.clear();
        NearByUserItem nearByUserItem;
        for (User user : root.user_list){
            nearByUserItem = new NearByUserItem();
            nearByUserItem.setUser_id(user.id);
            nearByUserItem.setNickname(user.nickname);
            nearByUserItem.setUsername(user.username);
            nearByUserItem.setSex(user.sex);
            nearByUserItem.setAge(user.age);
            nearByUserItem.setDistance(user.distance+"");
            nearByUserItem.setSignature(user.signature);
            nearByUserItem.setUser_head_path(user.user_head_path);
            nearByUserItem.setOccupation(user.occupation);
            nearByUserItem.setConstellation(user.constellation);
            list_user.add(nearByUserItem);
            SharedPreferenceUtil.saveUserData(user.username, user.nickname, WebUtil.HTTP_ADDRESS + user.user_head_path);
        }

        if (list_user.size()==0){
            rl_user.setVisibility(View.GONE);
        }else{
            rl_user.setVisibility(View.VISIBLE);
        }
        nearbyUserItemAdapter = new NearbyUserItemAdapter(this,R.layout.near_by_user_item,list_user);
        listView_user.setAdapter(nearbyUserItemAdapter);

        ///----------------------
        list_group.clear();
        GroupItem groupItem;
        for(Group group : root.group_list){
            groupItem = new GroupItem();
            groupItem.setId(group.id);
            groupItem.setWomen_count(group.women_count);
            groupItem.setPoint(group.point);
            groupItem.setMember_count(group.member_count);
            groupItem.setGroupname(group.groupname);
            groupItem.setAttention(group.attention);
            groupItem.setIs_member(group.is_member);
            groupItem.setHx_group_id(group.hx_group_id);
            groupItem.setGroup_bulider_path(group.group_bulider.user_head_path);
            groupItem.setGroup_img(group.group_img);
            groupItem.setGroup_type(group.group_type);
            String[] arr_member_head_path = new String[group.list_members.size()];
            for(int i=0;i<group.list_members.size();i++){
                arr_member_head_path[i] = group.list_members.get(i).user_head_path;
            }
            groupItem.setList_members_path(arr_member_head_path);
            groupItem.setDistance(group.distance);
            list_group.add(groupItem);

            SharedPreferenceUtil.saveGroupDataFromHXgroupid(group.id,group.groupname,group.hx_group_id,WebUtil.HTTP_ADDRESS + group.group_img);
        }

        if (list_group.size()==0){
            rl_group.setVisibility(View.GONE);
        }else{
            rl_group.setVisibility(View.VISIBLE);
        }
        groupItemArrayAdapter = new GroupItemArrayAdapter(this,R.layout.nearby_group_item,list_group);
        listView_group.setAdapter(groupItemArrayAdapter);
        //listView_group.setOnItemClickListener(this);


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
        List<User> user_list;
        List<Group> group_list;
    }

    class User{
        String id;
        String username;
        String nickname;
        String sex;
        double distance;
        String user_head_path;
        String signature;
        int age;
        String constellation;
        String occupation;
    }

    class Group {
        String id;
        String groupname;
        String group_img;
        User group_bulider;
        double distance;
        String point;
        int is_member;
        int member_count;
        int women_count;
        String hx_group_id;
        String attention;
        int group_type;
        List<User> list_members;
    }

}
