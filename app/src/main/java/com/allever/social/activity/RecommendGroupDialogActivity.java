package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by XM on 2016/10/21.
 */
public class RecommendGroupDialogActivity extends BaseActivity {

    private TextView tv_groupname;
    private TextView tv_distance;
    private TextView tv_group_member_count;
    private ImageView iv_head;
    private CheckBox cb_join;
    private RippleView rv_enter;

    private String group_json_data;
    private Group group;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_group_dialog_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_JOIN_GROUP:
                        handleJoinGroup(msg);
                        break;
                }
            }
        };

        group_json_data = getIntent().getStringExtra("group_data");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        RecommendGroupRoot root = gson.fromJson(group_json_data, RecommendGroupRoot.class);
        group = root.group;
        initView();



    }

    private void initView(){
        tv_groupname = (TextView)this.findViewById(R.id.id_recommend_group_dialog_activity_tv_group_name);
        tv_distance = (TextView)this.findViewById(R.id.id_recommend_group_dialog_activity_tv_distance);
        tv_group_member_count = (TextView)this.findViewById(R.id.id_recommend_group_dialog_activity_tv_group_member_count);
        iv_head = (ImageView)this.findViewById(R.id.id_recommend_group_dialog_activity_iv_head);
        cb_join = (CheckBox)this.findViewById(R.id.id_recommend_group_dialog_checkbox_join_group);
        rv_enter = (RippleView)this.findViewById(R.id.id_recommend_group_dialog_activity_rv_enter);

        tv_groupname.setText(group.groupname);
        tv_distance.setText(group.distance + " km");
        tv_group_member_count.setText("本群共" + group.member_count + "人(女生" + group.women_count + "人)");

        Glide.with(this).load(WebUtil.HTTP_ADDRESS  + group.group_img).into(iv_head);

        rv_enter.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (cb_join.isChecked()) {
                    joinGroup();

                } else {
                    RecommendGroupDialogActivity.this.setResult(RESULT_OK);
                    RecommendGroupDialogActivity.this.finish();
                }
            }
        });

    }

    private void joinGroup(){
        //OkhttpUtil.joinGroup(handler,group.id, SharedPreferenceUtil.getUserName());
        //直接加入群组
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                    EMClient.getInstance().groupManager().joinGroup(group.hx_group_id);//需异步处理
                    //添加服务器群组成员记录
                    OkhttpUtil.joinGroup(handler, group.id,SharedPreferenceUtil.getUserName());
                }catch (HyphenateException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleJoinGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JoinGroupRoot root  = gson.fromJson(result, JoinGroupRoot.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        //发送消息
        EMMessage message = EMMessage.createTxtSendMessage("大家好，我是新人 " + SharedPreferenceUtil.getNickname(), group.hx_group_id);
        message.setChatType(EMMessage.ChatType.GroupChat);
        //sendMessage(message);
        EMClient.getInstance().chatManager().sendMessage(message);
        setResult(RESULT_OK);
        this.finish();


    }

    @Override
    public void onBackPressed() {
        //不允许Back
    }

    class RecommendGroupRoot{
        boolean success;
        String message;
        int is_first_login;
        Group group;
    }

    class Group{
        String id;
        String groupname;
        String group_img;
        double distance;
        int member_count;
        int women_count;
        String hx_group_id;
    }


    class JoinGroupRoot {
        boolean success;
        String message;
        Group group;
    }

}
