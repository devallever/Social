package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/5.
 * 开通会员
 */
public class GetVipActivity extends BaseActivity implements View.OnClickListener {
    private String month_count;
    private String type;
    private RadioButton rb_count_1;
    private RadioButton rb_count_3;
    private RadioButton rb_count_6;
    private RadioButton rb_count_12;
    private RadioButton rb_type_1;
    private RadioButton rb_type_2;
    private RippleView rv_pay;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_vip_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_VIP:
                        handleGetVip(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("开通会员");

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
        rb_count_1 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_1);
        rb_count_3 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_3);
        rb_count_6 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_6);
        rb_count_12 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_12);

        rb_type_1 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_zhifubao);
        rb_type_2 = (RadioButton)this.findViewById(R.id.id_get_vip_activity_rb_credit);

        rb_count_1.setOnClickListener(this);
        rb_count_3.setOnClickListener(this);
        rb_count_6.setOnClickListener(this);
        rb_count_12.setOnClickListener(this);
        rb_type_1.setOnClickListener(this);
        rb_type_2.setOnClickListener(this);

        rv_pay = (RippleView)this.findViewById(R.id.id_get_vip_activity_rv_pay);
        rv_pay.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (month_count == null || month_count.length() == 0) {
                    Toast.makeText(GetVipActivity.this, "请选择开通时长", Toast.LENGTH_LONG).show();
                    return;
                }
                if (type == null || month_count.length() == 0) {
                    Toast.makeText(GetVipActivity.this, "请选择支付方式", Toast.LENGTH_LONG).show();
                    return;
                }
                if (type.equals("1")) {
                    Intent intent = new Intent(GetVipActivity.this,PayActivity.class);
                    startActivity(intent);
                    //Toast.makeText(GetVipActivity.this, "暂不支持支付宝付款", Toast.LENGTH_LONG).show();
                    return;
                } else if (type.equals("2")) {
                    getVip();
                }

            }
        });

    }

    private void getVip(){
        OkhttpUtil.getVip(handler,month_count,type);
    }
    private void handleGetVip(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        SharedPreferenceUtil.setVip("1");
        Dialog dialog = new Dialog(this,"Tips","恭喜，您已成为会员");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetVipActivity.this.setResult(RESULT_OK);
                GetVipActivity.this.finish();
            }
        });
        dialog.show();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_get_vip_activity_rb_1:
                month_count = "1";
                break;
            case R.id.id_get_vip_activity_rb_3:
                month_count = "3";
                break;
            case R.id.id_get_vip_activity_rb_6:
                month_count = "6";
                break;
            case R.id.id_get_vip_activity_rb_12:
                month_count = "12";
                break;
            case R.id.id_get_vip_activity_rb_zhifubao:
                type = "1";
                break;
            case R.id.id_get_vip_activity_rb_credit:
                type = "2";
                break;

        }
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
    }
}
