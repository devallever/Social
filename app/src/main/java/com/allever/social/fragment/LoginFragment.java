package com.allever.social.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.RegistFirstAcrivity;
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
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/4/18.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private static final int  LOGIN = 0;
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
    private LoginFragmentCallback loginFragmentCallback;
    private  String result;

    private ImageView iv_qq_login;

    private Tencent mTencent;
    private IUiListener loginListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loginFragmentCallback = (LoginFragmentCallback)activity;
        mTencent = Tencent.createInstance("1105431865", MyApplication.mContext);

        loginListener = new QQIUiListener(){
            @Override
            public void onComplete(Object response) {
                //super.onComplete(response);
                Log.d("QQLogin","QQ登录成功");
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("QQLogin","回调111111");
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout,container,false);
        btn_login = (ButtonFlat)view.findViewById(R.id.id_login_fg_btn_login);
        btn_login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_regist = (ButtonFlat)view.findViewById(R.id.id_login_fg_btn_regist);
        btn_regist.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_forget = (ButtonFlat)view.findViewById(R.id.id_login_fg_btn_forget);
        btn_forget.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        et_password = (MaterialEditText)view.findViewById(R.id.id_login_fg_et_password);
        et_username = (MaterialEditText)view.findViewById(R.id.id_login_fg_et_username);

        iv_qq_login = (ImageView)view.findViewById(R.id.id_login_fg_iv_qq_login);
        iv_qq_login.setOnClickListener(this);

        btn_regist.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", getActivity().MODE_PRIVATE);
        if (sharedPreferences != null){
            password = SharedPreferenceUtil.getPassword();
            username = SharedPreferenceUtil.getUserName();
            et_password.setText(password);
            et_username.setText(username);
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_LOGIN:
                        handleLogin(msg);
                        break;
                    default:
                        break;
                }
            }
        };

        return view;
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_login_fg_btn_regist:
                //Intent intent = new Intent(getActivity(), RegistActivity.class);
                Intent intent = new Intent(getActivity(), RegistFirstAcrivity.class);
                startActivity(intent);
                break;
            case R.id.id_login_fg_btn_login:
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                Log.d("LoginFragment", "RegistrationID = " + JPushInterface.getRegistrationID(getActivity()));
                login();
                break;
            case R.id.id_login_fg_iv_qq_login:
                mTencent.login(getActivity(),"all",loginListener);
                break;
            default:
                break;
        }
    }

    private void handleLogin(Message msg){
        result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            new Dialog(getActivity(),"错误",root.message).show();
            return ;
        }

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(getActivity(), root.user.username, new TagAliasCallback() {
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
        SharedPreferenceUtil.setAutoReaction(root.user.autoreaction);
        SharedPreferenceUtil.setOnlineState(root.user.onlinestate);

        loginFragmentCallback.loginSuccessCallback();
        SharedPreferenceUtil.getUserName();


    }

    private void login(){
        OkhttpUtil.login(handler,username,password);
    }
//


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
        String autoreaction;
        String onlinestate;
    }

    public interface LoginFragmentCallback{
        public void loginSuccessCallback();
    }


    private class QQIUiListener implements  IUiListener{
        @Override
        public void onComplete(Object response) {
            Log.d("QQLogin","QQ登录成功");
            Log.d("QQLogin",response.toString());
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

//    private class BaseUiListener implements IUiListener {
//        @Override
//        public void onComplete(JSONObject response) {
//            //mBaseMessageText.setText("onComplete:");
//            //mMessageText.setText(response.toString());
//            Log.d("qqLogin","QQ授权登录成功");
//            doComplete(response);
//        }
//        protected void doComplete(JSONObject values) {
//
//        }
//        @Override
//        public void onError(UiError e) {
//            //showResult("onError:", "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
//        }
//        @Override
//        public void onCancel() {
//            //showResult("onCancel", "");
//        }
//    }

}
