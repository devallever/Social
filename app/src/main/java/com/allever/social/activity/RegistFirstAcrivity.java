package com.allever.social.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.pojo.PostItem;
import com.allever.social.service.RegisterCodeTimerService;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.RegisterCodeTimer;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.ConcurrentHashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by XM on 2016/5/31.
 * 获取验证码界面
 */
public class RegistFirstAcrivity extends BaseActivity {

    private static final int REQUEST_CODE_REGIST_FIRST = 1000;

    private RippleView rv_check_code;
    private RippleView rv_next;
    private TextView tv_get_check_code;

    private String phone;
    private MaterialEditText et_phone;

    private String checkCode;
    private MaterialEditText et_checkCode;
//    private Intent checkCodeIntent;
    private Handler timeHandler;
    private Handler handler;
    private EventHandler eh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_first_activity_layout);

        timeHandler = new Handler();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_CHECK_PHONE:
                        handleCheckPhone(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("注册");

        initData();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_REGIST_FIRST:
                if (resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
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

        tv_get_check_code = (TextView)this.findViewById(R.id.id_regist_first_activity_tv_check_code);
        rv_check_code = (RippleView)this.findViewById(R.id.id_regist_first_activity_rv_check_code);
        rv_next = (RippleView)this.findViewById(R.id.id_regist_first_activity_rv_next);

        et_phone = (MaterialEditText)this.findViewById(R.id.id_regist_first_activity_et_phone);
        et_checkCode = (MaterialEditText)this.findViewById(R.id.id_regist_first_activity_et_check_code);

        SMSSDK.initSDK(RegistFirstAcrivity.this, "154e99832bd92", "a9b2031d048b2b46e7469f491c627e54");
        eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.d("RegistFirst", "提交验证码成功!!!!!");
                        Intent intent = new Intent(RegistFirstAcrivity.this,RegistActivity.class);
                        intent.putExtra("phone",phone);
                        startActivityForResult(intent,REQUEST_CODE_REGIST_FIRST);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        //Toast.makeText(RegistFirstAcrivity.this,"获取验证码成功",Toast.LENGTH_LONG).show();
                        Log.d("RegistFirst","获取验证码成功!!!!!");
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //发布时注释
                            Toast.makeText(RegistFirstAcrivity.this, "验证码错误或重新获取", Toast.LENGTH_LONG).show();
                        }
                    });

                            ((Throwable) data).printStackTrace();
                }
            }
        };

        rv_next.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                checkCode = et_checkCode.getText().toString();
                if (checkCode.length() == 0) {
                    Toast.makeText(RegistFirstAcrivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
                    return;
                }

                SMSSDK.submitVerificationCode("86", phone, checkCode);
            }
        });

        rv_check_code.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //
                checkPhone();
                //-------------------



//                rv_check_code.setEnabled(false);
//                rv_check_code.setBackgroundResource(R.drawable.btn_background_round_gray);
//                SMSSDK.initSDK(RegistFirstAcrivity.this, "136211fca2f9e", "1b4f8f9599f93e81845c6f3e555a2683");
//                eh=new EventHandler(){
//                    @Override
//                    public void afterEvent(int event, int result, Object data) {
//
//                        if (result == SMSSDK.RESULT_COMPLETE) {
//                            //回调完成
//                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                                //提交验证码成功
//                                Log.d("RegistFirst", "提交验证码成功!!!!!");
//                                Intent intent = new Intent(RegistFirstAcrivity.this,RegistActivity.class);
//                                intent.putExtra("phone",phone);
//                                startActivity(intent);
//                               // et_checkCode.setText("");
//                                RegistFirstAcrivity.this.finish();
//                            }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
//                                //获取验证码成功
//                                //Toast.makeText(RegistFirstAcrivity.this,"获取验证码成功",Toast.LENGTH_LONG).show();
//                                Log.d("RegistFirst","获取验证码成功!!!!!");
//                            }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
//                                //返回支持发送验证码的国家列表
//                            }
//                        }else{
//                            ((Throwable)data).printStackTrace();
//                        }
//                    }
//                };
//                SMSSDK.registerEventHandler(eh); //注册短信回调
//                SMSSDK.getVerificationCode("86", phone);
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        for (int i=60; i>=0; i--){
//                            final int j=i;
//                            timeHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv_get_check_code.setText(j+"s重新获取");
//                                    if(j==0){
//                                        rv_check_code.setEnabled(true);
//                                        rv_check_code.setBackgroundResource(R.drawable.btn_background_round_green);
//                                        tv_get_check_code.setText("获取验证码");
//                                    }
//                                }
//                            });
//                            try {
//                                Thread.sleep(1000);
//                            }catch (InterruptedException e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });


    }

    private void checkPhone(){
        phone = et_phone.getText().toString();
        if(phone.length()==0){
            Toast.makeText(this,"请输入手机号码",Toast.LENGTH_LONG).show();
            return;
        }
        OkhttpUtil.checkPhone(handler, phone);
    }

    private void handleCheckPhone(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        final Root  root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"提示",root.message).show();
            return ;
        }else{
            //new Dialog(this,"提示","该手机号可以注册").show();
                rv_check_code.setEnabled(false);
                rv_check_code.setBackgroundResource(R.drawable.btn_background_round_gray);

                SMSSDK.registerEventHandler(eh); //注册短信回调
                SMSSDK.getVerificationCode("86", phone);


                //即时线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        for (int i=60; i>=0; i--){
                            final int j=i;
                            timeHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_get_check_code.setText(j+"s重新获取");
                                    if(j==0){
                                        rv_check_code.setEnabled(true);
                                        rv_check_code.setBackgroundResource(R.drawable.btn_background_round_green);
                                        tv_get_check_code.setText("获取验证码");
                                    }
                                }
                            });
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }


    class Root{
        boolean success;
        String message;
    }
}
