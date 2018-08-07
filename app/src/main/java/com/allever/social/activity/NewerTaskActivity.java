package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/7.
 */
public class NewerTaskActivity extends BaseActivity {

    private Handler handler;
    private TextView tv_credit;
    private ImageView iv_head;
    private TextView tv_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newer_task_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_CREDIT:
                        handleCredit(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新手任务");

        initData();
        getCredit();

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


    private void getCredit(){
        OkhttpUtil.getCredit(handler);
    }


    private void handleCredit(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        tv_credit.setText("当前信用：" + root.credit);



    }

    private void initData(){
        tv_nickname = (TextView)this.findViewById(R.id.id_newer_task_activity_tv_nickname);
        tv_nickname.setText(SharedPreferenceUtil.getNickname());

        tv_credit = (TextView)this.findViewById(R.id.id_newer_task_activity_tv_credit);
        iv_head = (ImageView)this.findViewById(R.id.id_newer_task_activity_iv_head);
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .into(iv_head);
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
        int credit;
    }
}
