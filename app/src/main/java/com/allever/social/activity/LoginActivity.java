package com.allever.social.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.fragment.LoginFragment;
import com.allever.social.service.BDLocationService;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/4/17.
 */
public class LoginActivity extends BaseActivity implements LoginFragment.LoginFragmentCallback ,View.OnClickListener{
//    private ButtonFlat btn_login;
//    private ButtonFlat btn_forget;
//    private ButtonFlat btn_regist;

    private boolean isFromFirstActivity = false;

    private static final String HX_DEFAULT_PASSWORD = "123456";
    private final static int REQUEST_CODE_SET_ACCOUNT = 1000;
    private final static int REQUEST_CODE_REGIST = 1001;

    private Toolbar toolbar;
    private String force_logout;

    private MaterialEditText et_username;
    private MaterialEditText et_password;
    private ButtonFlat btn_login;
    private ButtonFlat btn_forget;
    private ButtonFlat btn_regist;
    private String username;
    private String password;
    private Handler handler;
    private Gson gson;
    private Root root;
    private  String result;

    private ImageView iv_qq_login;

    private Tencent mTencent;
    private IUiListener loginListener;

    private static final String QQ_APP_ID = "1105431865";
    private QQToken qqToken;
    private UserInfo userInfo;
    private String openid;
    private String access_token;
    private String expires;

    private String nickname;
    private String head_url;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        isFromFirstActivity = getIntent().getBooleanExtra("isFromFirstActivity",false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_LOGIN:
                        handleLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_CHECK_QQ_OPEN_ID:
                        handleCheckQQOpenId(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGIN_WITH_QQ_OPEN_ID:
                        //handleLoginWithQQopenid(msg);
                        handleLogin(msg);
                        break;
                    default:
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("登录");

        force_logout = getIntent().getStringExtra("force_logout");

        if (force_logout!=null){
            Dialog dialog = new Dialog(this,"提示","该账号在其他设备登录");
            dialog.show();
        }

        loginListener = new QQLoginIUiListener();

        initView();

        startLocationService();

    }

    private void startLocationService(){
        Intent intent = new Intent(this, BDLocationService.class);
        startService(intent);
    }

    private void initView(){
        btn_login = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_login);
        btn_login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_regist = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_regist);
        btn_regist.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_forget = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_forget);
        btn_forget.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        et_password = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_password);
        et_username = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_username);

        iv_qq_login = (ImageView)this.findViewById(R.id.id_login_activity_iv_qq_login);
        iv_qq_login.setOnClickListener(this);

        btn_regist.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        if (sharedPreferences != null){
            password = SharedPreferenceUtil.getPassword();
            username = SharedPreferenceUtil.getUserName();
            et_password.setText(password);
            et_username.setText(username);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_login_activity_btn_regist:
                Intent intent = new Intent(this, RegistActivity.class);
                //Intent intent = new Intent(this, RegistFirstAcrivity.class);
                startActivityForResult(intent,REQUEST_CODE_REGIST);
                break;
            case R.id.id_login_activity_btn_login:
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                Log.d("LoginActivity", "RegistrationID = " + JPushInterface.getRegistrationID(this));
                login();
                break;
            case R.id.id_login_activity_iv_qq_login:
                qqLogin();

                break;
            default:
                break;
        }
    }

    private void qqLogin(){
        mTencent = Tencent.createInstance(QQ_APP_ID, MyApplication.mContext);
        mTencent.login(this,"all",loginListener);
    }


    private void login(){
        OkhttpUtil.login(handler, username, password);
    }

    private void handleLogin(Message msg){
        result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            new Dialog(this,"错误",root.message).show();
            return ;
        }

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("JPush", i + "");
            }
        });

        //登录app服务器成功后登录环信服务器
        EMClient.getInstance().login(root.user.username, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("LoninFragment", "登陆聊天服务器成功！");
                    }
                }).start();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("LoninFragment", "登陆聊天服务器失败！");
            }
        });


        SharedPreferenceUtil.setUserId(root.user.id);
        SharedPreferenceUtil.setUsername(root.user.username);
        SharedPreferenceUtil.setNickname(root.user.nickname);
        SharedPreferenceUtil.setPassword(password);
        SharedPreferenceUtil.setHeadpath(root.user.user_head_path);
        SharedPreferenceUtil.setSignature(root.user.signature);
        SharedPreferenceUtil.setState("1");
        SharedPreferenceUtil.setSessionId(root.session_id);
        SharedPreferenceUtil.setCity(root.user.city);
        SharedPreferenceUtil.setSex(root.user.sex);
        SharedPreferenceUtil.setPhone(root.user.phone);
        SharedPreferenceUtil.setEmail(root.user.email);
        SharedPreferenceUtil.setAge(root.user.age);
        SharedPreferenceUtil.setOccupation(root.user.occupation);
        SharedPreferenceUtil.setConstellation(root.user.constellation);
        SharedPreferenceUtil.setHihgt(root.user.hight);
        SharedPreferenceUtil.setWeight(root.user.weight);
        SharedPreferenceUtil.setFigure(root.user.figure);
        SharedPreferenceUtil.setEmotion(root.user.emotion);
        SharedPreferenceUtil.setVip(root.user.is_vip + "");
        SharedPreferenceUtil.setRecommend(root.user.is_recommended + "");
        SharedPreferenceUtil.setAutoReaction(root.user.autoreaction);
        SharedPreferenceUtil.setOnlineState(root.user.onlinestate);
        SharedPreferenceUtil.setOpenid(root.user.qq_open_id);


        if (isFromFirstActivity){
            Intent intent = new Intent("com.allever.afterlogin");
            sendBroadcast(intent);
            setResult(RESULT_OK);
            finish();
        }else{
            Intent intent = new Intent("com.allever.afterlogin");
            //sendBroadcast(intent);
            setResult(RESULT_OK);
            Intent intent1 = new Intent(this,ShuaShuaActivity.class);
            intent1.putExtra("is_first",true);
            startActivity(intent1);
            finish();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("QQLogin", "回调111111");
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        if (requestCode==REQUEST_CODE_SET_ACCOUNT && resultCode==RESULT_OK){
            //登录
            loginWithQQopenid();
        }

        if (requestCode == REQUEST_CODE_REGIST && resultCode ==RESULT_OK){
            Intent intent = new Intent(this,ShuaShuaActivity.class);
            intent.putExtra("is_first",true);
            startActivity(intent);
            finish();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithQQopenid(){
        OkhttpUtil.loginWithQQopenid(handler, openid);
    }

    private void handleLoginWithQQopenid(Message msg){

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


    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    @Override
    public void loginSuccessCallback() {

        Intent intent = new Intent("com.allever.afterlogin");
        sendBroadcast(intent);
        Intent intent1 = new Intent(this, SocialMainActivity.class);
        startActivity(intent1);
        finish();
    }


    private class QQLoginIUiListener implements  IUiListener{
        @Override
        public void onComplete(Object response) {
            Log.d("QQLogin", "QQ登录成功0000");
            System.out.println(response);
            password = HX_DEFAULT_PASSWORD;
            //看服务器是否有该
            try {
                openid = ((JSONObject)response).getString("openid");
                access_token = ((JSONObject)response).getString("access_token");
                expires = ((JSONObject)response).getString("expires_in");
                Log.d("QQLogin","openid = " + openid);
                Log.d("QQLogin", "access_token = " + access_token);
                Log.d("QQLogin", "expires_in = " + expires);
                mTencent.setOpenId(openid);
                mTencent.setAccessToken(access_token, expires);

                SharedPreferenceUtil.setOpenid(openid);

                checkExistOpenId();

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onCancel() {
            Log.d("QQLogin","取消登录");
        }

        @Override
        public void onError(UiError uiError) {
            Log.d("QQLogin","QQ登录出错\n" + uiError);
        }
    }


    private void checkExistOpenId(){
        OkhttpUtil.checkQQOpenId(handler,openid);
    }

    private void handleCheckQQOpenId(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CheckOpenIdRoot root = gson.fromJson(result, CheckOpenIdRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"错误",root.message).show();
            return ;
        }

        if (root.isExist==0){
            //无记录
            // 注册操作
            // 获取QQ用户信息 昵称 地址 头像url
            //设置互信号
            Intent intent = new Intent(this,SetAccountActivity.class);
            intent.putExtra("openid",openid);
            intent.putExtra("access_token",access_token);
            intent.putExtra("expires",expires);
            startActivityForResult(intent,REQUEST_CODE_SET_ACCOUNT);

        }else{
            //有记录 登录操作
            loginWithQQopenid();
        }
    }


    public class Root{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }

    public class User{
        String id;
        String username;
        String nickname;
        String imagepath;
        double longitude;
        double latiaude;
        String phone;
        String email;
        String user_head_path;
        String signature;
        String city;
        String sex;
        int age;
        String occupation;
        String constellation;
        String hight;
        String weight;
        String figure;
        String emotion;
        int is_vip;
        int is_recommended;
        String autoreaction;
        String onlinestate;
        String qq_open_id;
    }

    class CheckOpenIdRoot{
        boolean success;
        String message;
        int isExist;
    }
}
