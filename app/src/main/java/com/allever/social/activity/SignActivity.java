package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/7.
 * 签到界面//新手人民
 */
public class SignActivity extends BaseActivity {
    private ImageView iv_head;
    private TextView tv_nickname;
    private Handler handler;

    private ProgressBarDeterminate progressBarDeterminate;
    private TextView tv_already_sign;

//    private RelativeLayout rl_progress_1;
//    private RelativeLayout rl_progress_2;
//    private RelativeLayout rl_progress_3;
//    private RelativeLayout rl_progress_4;
//    private RelativeLayout rl_progress_5;
//    private RelativeLayout rl_progress_6;
//    private RelativeLayout rl_progress_7;
//    private RelativeLayout rl_progress_8;
//    private RelativeLayout rl_progress_9;
//    private RelativeLayout rl_progress_10;
//
//    private TextView tv_day_count_1;
//    private TextView tv_day_count_2;
//    private TextView tv_day_count_3;
//    private TextView tv_day_count_4;
//    private TextView tv_day_count_5;
//    private TextView tv_day_count_6;
//    private TextView tv_day_count_7;
//    private TextView tv_day_count_8;
//    private TextView tv_day_count_9;
//    private TextView tv_day_count_10;

    private RippleView rv_sign;
    private RippleView rv_already_sign;
    private TextView tv_day_count;
    private int day_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_SIGN:
                        handleSignData(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SIGN:
                        handleSign(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("签到");

        initData();

        getSingData();

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
        tv_nickname = (TextView)this.findViewById(R.id.id_sign_activity_tv_nickname);
        tv_nickname.setText(SharedPreferenceUtil.getNickname());

        iv_head = (ImageView)this.findViewById(R.id.id_sign_activity_iv_head);
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .into(iv_head);

        progressBarDeterminate = (ProgressBarDeterminate)this.findViewById(R.id.id_sign_activity_progress_bar);
//        progressBarDeterminate.setMax(100);
//        progressBarDeterminate.setMin(0);
        progressBarDeterminate.setBackgroundColor(getResources().getColor(R.color.colorIndigo_700));

        tv_already_sign = (TextView)this.findViewById(R.id.id_sign_activity_tv_already_sign);


        rv_sign = (RippleView)this.findViewById(R.id.id_sign_activity_rv_sign);
        rv_already_sign = (RippleView)this.findViewById(R.id.id_sign_activity_rv_already_sign);
        tv_day_count = (TextView)this.findViewById(R.id.id_sign_activity_tv_day_count);

        rv_sign.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                sign();
            }
        });

    }

    private void sign(){
        OkhttpUtil.sign(handler);
    }

    private void handleSign(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        day_count = root.sign.day_count;
        tv_day_count.setText(day_count + "");
        rv_sign.setVisibility(View.INVISIBLE);
        rv_already_sign.setVisibility(View.VISIBLE);

        progressBarDeterminate.setProgress(day_count);
        tv_already_sign.setText("已签到 " + day_count + " 天");


    }

    private void getSingData(){
        OkhttpUtil.getSign(handler);
    }

    private void handleSignData(Message msg){
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

        if(root.sign.is_sign==0){
            rv_already_sign.setVisibility(View.INVISIBLE);
            rv_sign.setVisibility(View.VISIBLE);
        }else if(root.sign.is_sign==1){
            rv_already_sign.setVisibility(View.VISIBLE);
            rv_sign.setVisibility(View.INVISIBLE);
        }

        day_count = root.sign.day_count;
        tv_day_count.setText(day_count+"");

        progressBarDeterminate.setProgress(day_count);
        tv_already_sign.setText("已签到 " + day_count + " 天");


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
        Sign sign;

    }

    class Sign{
        String username;
        int is_sign;
        int day_count;
    }
}
