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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MyListView;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/28.
 * 群成员界面
 */
public class GroupMemberActivity extends BaseActivity implements AdapterView.OnItemLongClickListener{
    private String group_id;
    private String hx_group_id;
    private String owner_username;
    private String kicked_username;

    private CircleImageView iv_owner_head;

    private TextView tv_nickname;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_constellation;
    private TextView tv_occupation;
    private TextView tv_signature;
    private TextView tv_diatance;

    private LinearLayout ll_sex;
    private LinearLayout ll_constellation;
    private LinearLayout ll_occupation;

    private MyListView listView;

    private Handler handler;

    private NearbyUserItemAdapter nearbyUserItemAdapter;

    private List<NearByUserItem> list_user = new ArrayList<>();

    private RippleView rv_owner_container;
    private RippleView rv_invite_member;

    private ScrollView scrollview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member_activity_layout);

        group_id = getIntent().getStringExtra("group_id");
        hx_group_id = getIntent().getStringExtra("hx_group_id");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GROUP_MEMBER_LIST:
                        handleGroupMemberList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_KICK_GROUP_MEMBER:
                        handleKickGroupMember(msg);
                        break;
                }
            }
        };


        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("群成员");

        initData();

        getGroupMember();

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


    private void  initData(){
        iv_owner_head = (CircleImageView)this.findViewById(R.id.id_group_member_activity_iv_owner_head);

        tv_nickname = (TextView)this.findViewById(R.id.id_group_member_activity_tv_nickname);
        tv_sex = (TextView)this.findViewById(R.id.id_group_member_activity_tv_sex);
        tv_age = (TextView)this.findViewById(R.id.id_group_member_activity_tv_age);
        tv_constellation = (TextView)this.findViewById(R.id.id_group_member_activity_tv_constellation);
        tv_occupation = (TextView)this.findViewById(R.id.id_group_member_activity_tv_occupation);
        tv_signature = (TextView)this.findViewById(R.id.id_group_member_activity_tv_signature);
        tv_diatance = (TextView)this.findViewById(R.id.id_group_member_activity_tv_distance);

        ll_constellation = (LinearLayout)this.findViewById(R.id.id_group_member_activity_ll_constellation);
        ll_sex = (LinearLayout)this.findViewById(R.id.id_group_member_activity_ll_sex);
        ll_occupation = (LinearLayout)this.findViewById(R.id.id_group_member_activity_ll_occupation);

        scrollview = (ScrollView)this.findViewById(R.id.id_group_member_scrollview);

        rv_owner_container = (RippleView)this.findViewById(R.id.id_group_member_activity_rv_owner_container);
        rv_owner_container.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intetnt = new Intent(GroupMemberActivity.this,UserDataDetailActivity.class);
                intetnt.putExtra("username",owner_username);
                startActivity(intetnt);
            }
        });
        rv_invite_member = (RippleView)this.findViewById(R.id.id_group_member_activity_rv_invite_member);
        rv_invite_member.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //邀请好友
                //打开好友列表,选中好友
                //inviteFriend();
                Intent intent = new Intent(GroupMemberActivity.this,ChooseFriendActivity.class);
                intent.putExtra("group_id",group_id);
                intent.putExtra("hx_group_id",hx_group_id);
                intent.putExtra("owner_username",owner_username);
                startActivity(intent);
            }
        });

        listView = (MyListView)this.findViewById(R.id.id_group_member_activity_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intetnt = new Intent(GroupMemberActivity.this,UserDataDetailActivity.class);
                intetnt.putExtra("username",list_user.get(i).getUsername());
                startActivity(intetnt);
            }
        });


    }

    private void inviteFriend(){
        //OkhttpUtil.inviteFriendToGroup(handler,group_id,applyer,SharedPreferenceUtil.getUserName());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //list_user.get(i).getUsername();
        kicked_username = list_user.get(i).getUsername();
        final int position = i;
        Dialog dialog = new Dialog(this, "删除成员","删除该成员?");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除该成员
                kickGroupMember(list_user.get(position).getUsername());
            }
        });
        dialog.show();
        return false;
    }

    private void kickGroupMember(String username){
        OkhttpUtil.kickGroupMember(handler, group_id, username);
    }

    private void handleKickGroupMember(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        KickGroupMemberRoot root = gson.fromJson(result, KickGroupMemberRoot.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        //踢人
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().removeUserFromGroup(hx_group_id,kicked_username );//需异步处理
                }catch (HyphenateException e){
                    e.printStackTrace();
                }
            }
        }).start();

        getGroupMember();



    }

    /**
     * 获取群成员列表
     * **/
    private void getGroupMember(){
        OkhttpUtil.getGroupMemberList(group_id, handler);
    }

    private void handleGroupMemberList(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        GroupMemberRoot root = gson.fromJson(result, GroupMemberRoot.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        User owner = root.group_owner;
        owner_username = owner.username;


        if (root.group_owner.username.equals(SharedPreferenceUtil.getUserName())){
            listView.setOnItemLongClickListener(this);
        }

        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + owner.user_head_path)
                .into(iv_owner_head);
        tv_nickname.setText(owner.nickname);
        tv_sex.setText(owner.sex);
        tv_age.setText(owner.age+"");
        tv_constellation.setText(owner.constellation);
        tv_occupation.setText(owner.occupation);
        tv_signature.setText(owner.signature);
        tv_diatance.setText(owner.distance + " km");

        if (root.group_owner.username.equals(SharedPreferenceUtil.getUserName())){
            rv_invite_member.setVisibility(View.VISIBLE);
        }else{
            rv_invite_member.setVisibility(View.GONE);
        }

        if (owner.sex.equals("男")){
            ll_sex.setBackgroundColor(getResources().getColor(R.color.colorSexBlue));
        }else{
            ll_sex.setBackgroundColor(getResources().getColor(R.color.colorSexPink));
        }

        switch (owner.occupation){
            case "学生":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorRed_300));
                tv_occupation.setText("学");
                break;
            case "信息技术":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_occupation.setText("IT");
                break;
            case "保险":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorGray_300));
                tv_occupation.setText("保");
                break;
            case "工程制造":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorGreen_300));
                tv_occupation.setText("工");
                break;
            case "商业服务":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorBlue_300));
                tv_occupation.setText("商");
                break;
            case "交通运输":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorIndigo_300));
                tv_occupation.setText("交");
                break;
            case "文化传媒":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorPurple_300));
                tv_occupation.setText("文");
                break;
            case "教育":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorRed_300));
                tv_occupation.setText("教");
                break;
            case "娱乐":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorSexPink));
                tv_occupation.setText("娱");
                break;
            case "公共事业":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorGreen_300));
                tv_occupation.setText("公");
                break;
            case "金融":
                ll_occupation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_occupation.setText("金");
                break;
        }


        switch (owner.constellation){
            case "白羊座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorGray_300));
                tv_constellation.setText("白羊");
                break;
            case "金牛座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("金牛");
                break;
            case "双子座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorRed_300));
                tv_constellation.setText("双子");
                break;
            case "巨蟹座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("巨蟹");
                break;
            case "狮子座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("狮子");
                break;
            case "处女座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorPink_300));
                tv_constellation.setText("处女");
                break;
            case "天秤座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorGreen_300));
                tv_constellation.setText("天秤");
                break;
            case "天蝎座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorPurple_300));
                tv_constellation.setText("天蝎");
                break;
            case "射手座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorBlue_300));
                tv_constellation.setText("射手");
                break;
            case "魔蝎座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorIndigo_300));
                tv_constellation.setText("魔蝎");
                break;
            case "水瓶座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorBlue_300));
                tv_constellation.setText("水瓶");
                break;
            case "双鱼座":
                ll_constellation.setBackgroundColor(getResources().getColor(R.color.colorOrange_300));
                tv_constellation.setText("双鱼");
                break;
        }


        list_user.clear();
        NearByUserItem nearByUserItem;
        for (User user : root.member_list){
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

        nearbyUserItemAdapter = new NearbyUserItemAdapter(this,R.layout.near_by_user_item,list_user);
        listView.setAdapter(nearbyUserItemAdapter);
        scrollview.smoothScrollTo(0,0);





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

    class GroupMemberRoot{
        boolean success;
        String message;
        User group_owner;
        int is_member;
        List<User> member_list;
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

    class KickGroupMemberRoot{
        boolean success;
        String message;
    }

}
