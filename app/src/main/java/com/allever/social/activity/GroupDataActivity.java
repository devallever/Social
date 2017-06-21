package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/13.
 */
public class GroupDataActivity extends BaseActivity implements View.OnClickListener ,RippleView.OnRippleCompleteListener{
    private Handler handler;
    private String group_id;
    private Gson gson;
    private Root root;

    private CircleImageView iv_group_img;

    private TextView tv_group_name;
    private TextView tv_point;
    private TextView tv_group_id;
    private TextView tv_description;
    private TextView tv_attention;
    private TextView tv_levle;
    private TextView tv_build_date;
    private TextView tv_member_count;
    private TextView tv_group_type;

    private CircleImageView iv_member_1;
    private CircleImageView iv_member_2;
    private CircleImageView iv_member_3;
    private CircleImageView iv_builder;

    private ButtonRectangle btn_chat;
    private ButtonRectangle btn_join;
    private ButtonRectangle btn_drop;
    private ButtonRectangle btn_delete;

    private String hx_group_id;//环信群组id

    private RippleView rv_group_member;
    private RippleView rv_group_owner;
    private RippleView rv_manage_member;
    private RippleView rv_invite_member;


    private String owner_username;
    private int group_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_data_activity_layout);

        group_id = getIntent().getStringExtra("group_id");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GROUP_DATA:
                        handleGroupData(msg);
                        break;
                    case OkhttpUtil.MESSAGE_JOIN_GROUP:
                        handleJoinGroup(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DROP_GROUP:
                        handleDropGroup(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_GROUP:
                        handleDeleteGroup(msg);
                        break;
                }
            }
        };

        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("群资料");

        initData();

        getGroupDate();
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
        iv_group_img = (CircleImageView)this.findViewById(R.id.id_group_data_iv_group_img);
        iv_member_1 = (CircleImageView)this.findViewById(R.id.id_group_data_iv_member_img_1);
        iv_member_2 = (CircleImageView)this.findViewById(R.id.id_group_data_iv_member_img_2);
        iv_member_3 = (CircleImageView)this.findViewById(R.id.id_group_data_iv_member_img_3);
        iv_builder = (CircleImageView)this.findViewById(R.id.id_group_data_iv_group_builder);

        tv_group_name = (TextView)this.findViewById(R.id.id_group_data_tv_groupname);
        tv_point = (TextView)this.findViewById(R.id.id_group_data_tv_point);
        tv_group_id = (TextView)this.findViewById(R.id.id_group_data_tv_group_id);
        tv_description = (TextView)this.findViewById(R.id.id_group_data_tv_group_description);
        tv_attention = (TextView)this.findViewById(R.id.id_group_data_tv_group_attention);
        tv_levle = (TextView)this.findViewById(R.id.id_group_data_tv_group_level);
        tv_build_date = (TextView)this.findViewById(R.id.id_group_data_tv_build_date);
        tv_member_count = (TextView)this.findViewById(R.id.id_group_data_tv_member_count);
        tv_group_type = (TextView)this.findViewById(R.id.id_group_data_tv_group_type);

        btn_join = (ButtonRectangle)this.findViewById(R.id.id_group_data_btn_join);
        btn_chat = (ButtonRectangle)this.findViewById(R.id.id_group_data_btn_chat);
        btn_drop = (ButtonRectangle)this.findViewById(R.id.id_group_data_btn_drop);
        btn_delete = (ButtonRectangle)this.findViewById(R.id.id_group_data_btn_delete);

        btn_join.setOnClickListener(this);
        btn_drop.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        rv_group_member = (RippleView)this.findViewById(R.id.id_group_data_rv_group_member);
        rv_group_owner= (RippleView)this.findViewById(R.id.id_group_data_rv_group_owner);
        rv_manage_member= (RippleView)this.findViewById(R.id.id_group_data_rv_manage_member);
        rv_invite_member= (RippleView)this.findViewById(R.id.id_group_data_rv_invite_member);

        rv_group_member.setOnRippleCompleteListener(this);
        rv_group_owner.setOnRippleCompleteListener(this);
        rv_manage_member.setOnRippleCompleteListener(this);
        rv_invite_member.setOnRippleCompleteListener(this);

    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        Intent intent;
        switch (id){
            case R.id.id_group_data_rv_manage_member:
            case R.id.id_group_data_rv_group_member:
                intent = new Intent(this, GroupMemberActivity.class);
                intent.putExtra("group_id",group_id);
                intent.putExtra("hx_group_id", hx_group_id);
                startActivity(intent);
                break;
            case R.id.id_group_data_rv_group_owner:
                intent = new Intent(this,UserDataDetailActivity.class);
                intent.putExtra("username", owner_username);
                startActivity(intent);
                break;
            case R.id.id_group_data_rv_invite_member:
                intent = new Intent(GroupDataActivity.this,ChooseFriendActivity.class);
                intent.putExtra("group_id", group_id);
                intent.putExtra("hx_group_id",hx_group_id);
                intent.putExtra("owner_username", owner_username);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id  = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_group_data_btn_join:
                switch (group_type){
                    case 1:
                    case 2:
                        //siyouqun
                        Toast.makeText(this,"私有群只能群主邀请",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(this,"已申请",Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
                                    EMClient.getInstance().groupManager().applyJoinToGroup(hx_group_id, "请求加入群组");//需异步处理
                                }catch (HyphenateException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        //joinGroup();先发送环信加群申请，带群主收到请求，同意或拒绝，同意就执行同意加群操作
                        break;
                    case 4:
                        //直接加入群组
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                                    EMClient.getInstance().groupManager().joinGroup(hx_group_id);//需异步处理
                                    //添加服务器群组成员记录
                                    OkhttpUtil.joinGroup(handler, SharedPreferenceUtil.getGroupid(hx_group_id),SharedPreferenceUtil.getUserName());
                                }catch (HyphenateException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                }

                break;
            case R.id.id_group_data_btn_drop:
                dropGroup();
                break;
            case R.id.id_group_data_btn_delete:
                deleteGroup();
                break;
            case R.id.id_group_data_btn_chat:
                //new GroupChatActivity;
                if(root.group.is_member==1){
                    intent = new Intent(this,GroupChatActivity.class);
                    intent.putExtra("hx_group_id", hx_group_id);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"你还没进群呢.",Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void deleteGroup(){
        if (OkhttpUtil.checkLogin()){
            Dialog dialog = new Dialog(this, "提示", "你确定要删除群组吗？");
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //deleteNews();
                    Dialog dialog = new Dialog(GroupDataActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OkhttpUtil.deleteGroup(handler, group_id);
                            //new Dialog(UserNewsActivity.this, "提示", "删除成功").show();
                        }
                    });
                    dialog.show();
                }
            });
            dialog.show();

        }
    }

    private void handleDeleteGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
             try {
                 EMClient.getInstance().groupManager().destroyGroup(hx_group_id);//需异步处理
             }catch (HyphenateException e){
                 e.printStackTrace();
             }
            }
        }).start();

        Dialog dialog = new Dialog(this,"Tips","删除成功");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.allever.social.refresh_group_list");
                sendBroadcast(intent);
                GroupDataActivity.this.finish();
            }
        });
        dialog.show();
    }

    private void dropGroup(){
        if (OkhttpUtil.checkLogin()){
            OkhttpUtil.dropGroup(handler,group_id);
        }
    }

    private void handleDropGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        //退出环信群组
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().leaveGroup(hx_group_id);//需异步处理
                }catch (HyphenateException e){
                    e.printStackTrace();
                }
            }
        }).start();

        Dialog dialog = new Dialog(this,"Tips","退群成功");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.allever.social.refresh_group_list");
                sendBroadcast(intent);
                btn_join.setVisibility(View.VISIBLE);
                btn_drop.setVisibility(View.INVISIBLE);
            }
        });
        dialog.show();


    }


    private void joinGroup(){
        if (OkhttpUtil.checkLogin()){
            //OkhttpUtil.joinGroup(handler,group_id,);
        }
    }

    private void handleJoinGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }



        Dialog dialog = new Dialog(this,"Tips","加群成功");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.allever.social.refresh_group_list");
                sendBroadcast(intent);
                btn_join.setVisibility(View.INVISIBLE);
                btn_drop.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();


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

    private void getGroupDate(){
        OkhttpUtil.getGroupData(group_id,handler);
    }

    private void handleGroupData(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }
        Group group = root.group;
        tv_group_name.setText(group.groupname);
        tv_point.setText(group.point + " "+group.distance + " km");
        tv_group_id.setText(group.id);
        tv_description.setText(group.description);
        tv_attention.setText(group.attention);
        tv_build_date.setText(group.date);
        hx_group_id = group.hx_group_id;
        tv_member_count.setText("共 "+group.member_count + "人");
        group_type = group.group_type;
        switch (group.group_type){
            case 1:
                tv_group_type.setText("私有群，群成员也能邀请人进群；");
                break;
            case 2:
                tv_group_type.setText("私有群，只能群主邀请人进群；");
                break;
            case 3:
                tv_group_type.setText("公开群，加入此群除了群主邀请，只能通过申请加入此群；；");
                break;
            case 4:
                tv_group_type.setText("公开群，任何人都能加入此群。；");
                break;

        }

        owner_username = group.group_bulider.username;

        SharedPreferenceUtil.saveGroupDataFromHXgroupid(group.id,group.groupname,group.hx_group_id,WebUtil.HTTP_ADDRESS + group.group_img);

        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + group.group_bulider.headpath)
                .into(iv_builder);
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + group.group_img)
                .into(iv_group_img);
        switch (root.group.list_members.size()){
            case 0:
                break;
            case 1:
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(0).headpath)
                        .into(iv_member_1);
                break;
            case 2:
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(0).headpath)
                        .into(iv_member_1);
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(1).headpath)
                        .into(iv_member_2);
                break;
            case 3:
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(0).headpath)
                        .into(iv_member_1);
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(1).headpath)
                        .into(iv_member_2);
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(2).headpath)
                        .into(iv_member_3);
                break;
            default:
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(0).headpath)
                        .into(iv_member_1);
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(1).headpath)
                        .into(iv_member_2);
                Glide.with(this)
                        .load(WebUtil.HTTP_ADDRESS + group.list_members.get(2).headpath)
                        .into(iv_member_3);
                break;
        }

        if (group.is_member==1){
            btn_join.setVisibility(View.INVISIBLE);
            if(group.group_bulider.username.equals(SharedPreferenceUtil.getUserName())){
                btn_drop.setVisibility(View.INVISIBLE);
                btn_delete.setVisibility(View.VISIBLE);
                rv_manage_member.setVisibility(View.VISIBLE);
                rv_invite_member.setVisibility(View.GONE);
            }else{
                btn_drop.setVisibility(View.VISIBLE);
                btn_delete.setVisibility(View.INVISIBLE);
                rv_manage_member.setVisibility(View.GONE);
                rv_invite_member.setVisibility(View.VISIBLE);
            }

        }else{
            btn_join.setVisibility(View.VISIBLE);
            btn_drop.setVisibility(View.INVISIBLE);
            btn_delete.setVisibility(View.INVISIBLE);
            rv_manage_member.setVisibility(View.GONE);
            rv_invite_member.setVisibility(View.GONE);
        }


        if (group.group_bulider.username.equals(SharedPreferenceUtil.getUserName())){
            //rv_manage_member.setVisibility(View.VISIBLE);
            rv_invite_member.setVisibility(View.VISIBLE);
        }else{
            //rv_manage_member.setVisibility(View.GONE);
            rv_invite_member.setVisibility(View.GONE);
        }


    }


    class Root {
        boolean success;
        String message;
        Group group;
    }

    class Group {
        String id;
        String groupname;
        String group_img;
        User group_bulider;
        String description;
        double distance;
        String point;
        int is_member;
        int member_count;
        int women_count;
        int level;
        String date;
        String attention;
        String hx_group_id;
        int group_type;
        List<User> list_members;
    }

    class User{
        String username;
        String nickname;
        String headpath;
    }
}
