package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.ui.activity.LoginActivity;
import com.allever.social.activity.ModifyUserDataActivity;
import com.allever.social.activity.SettingActivity;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/23.
 */
public class SettingFragment extends Fragment implements View.OnClickListener,View.OnTouchListener,RippleView.OnRippleCompleteListener{
    private LinearLayout ll_modify_user_data;
    private CircleImageView iv_head;
    private TextView tv_nickname;
    private TextView tv_username;
    private RelativeLayout rl_general_setting;
    private RelativeLayout rl_private;
    private RelativeLayout rl_feedback;
    private RelativeLayout rl_about;
    private ButtonRectangle btn_logout;
    private Handler handler;
    private RippleView rv_account_and_secure;


    private AfterModifyUserDataReceiver afterModifyUserDataReceiver;
    private AfterLoginaReceiver afterLoginReceiver;
    private IntentFilter intentFilter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_layout,container,false);
        ll_modify_user_data = (LinearLayout)view.findViewById(R.id.id_setting_fg_ll_modify_user_data);
        ll_modify_user_data.setOnClickListener(this);
        ll_modify_user_data.setOnTouchListener(this);
        rl_general_setting = (RelativeLayout)view.findViewById(R.id.id_setting_fg_rl_gegeral_setting);
        rl_general_setting.setOnClickListener(this);
        rl_general_setting.setOnTouchListener(this);
        rl_private = (RelativeLayout)view.findViewById(R.id.id_setting_fg_rl_private);
        rl_private.setOnClickListener(this);
        rl_private.setOnTouchListener(this);
        rl_feedback = (RelativeLayout)view.findViewById(R.id.id_setting_fg_rl_feedback);
        rl_feedback.setOnClickListener(this);
        rl_feedback.setOnTouchListener(this);
        rl_about = (RelativeLayout)view.findViewById(R.id.id_setting_fg_rl_about);
        rl_about.setOnClickListener(this);
        rl_about.setOnTouchListener(this);


        rv_account_and_secure = (RippleView)view.findViewById(R.id.id_setting_fg_rv_account_and_souce);
        rv_account_and_secure.setOnRippleCompleteListener(this);

        iv_head = (CircleImageView)view.findViewById(R.id.id_setting_fg_iv_head);
        tv_nickname = (TextView)view.findViewById(R.id.id_setting_fg_tv_nickname);
        tv_username = (TextView)view.findViewById(R.id.id_setting_fg_tv_username);
        if (OkhttpUtil.checkLogin()){
            tv_username.setText("账号：" + SharedPreferenceUtil.getUserName());
            tv_nickname.setText(SharedPreferenceUtil.getNickname());
            //Picasso.with(getActivity()).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath()).into(iv_head);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.modifyUserHead");
        intentFilter.addAction("com.allever.modifyUserData");
        intentFilter.addAction("com.allever.afterlogin");
        afterModifyUserDataReceiver = new AfterModifyUserDataReceiver();
        afterLoginReceiver = new AfterLoginaReceiver();
        getActivity().registerReceiver(afterLoginReceiver,intentFilter);
        getActivity().registerReceiver(afterModifyUserDataReceiver,intentFilter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGOUT:
                        handleLogout(msg);
                        break;
                }
            }
        };

        btn_logout = (ButtonRectangle)view.findViewById(R.id.id_setting_fg_btn_logout);
        btn_logout.setOnClickListener(this);

        if (OkhttpUtil.checkLogin()){
            btn_logout.setVisibility(View.VISIBLE);
        }else{
            btn_logout.setVisibility(View.INVISIBLE);
        }

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
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        switch (id){
            case R.id.id_setting_fg_rv_account_and_souce:
                Toast.makeText(getActivity(),"账号与安全",Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(afterModifyUserDataReceiver);
        getActivity().unregisterReceiver(afterLoginReceiver);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int actionType = motionEvent.getActionMasked();
        int id = view.getId();
        switch (id){
            case R.id.id_setting_fg_rl_gegeral_setting:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        rl_general_setting.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        rl_general_setting.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;
            case R.id.id_setting_fg_ll_modify_user_data:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        ll_modify_user_data.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        ll_modify_user_data.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;
            case R.id.id_setting_fg_rl_account_and_secure:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        //rl_account_and_secure.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        //rl_account_and_secure.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;
            case R.id.id_setting_fg_rl_private:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        rl_private.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        rl_private.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;
            case R.id.id_setting_fg_rl_feedback:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        rl_feedback.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        rl_feedback.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;
            case R.id.id_setting_fg_rl_about:
                switch (actionType){
                    case MotionEvent.ACTION_DOWN:
                        rl_about.setBackgroundColor(getResources().getColor(R.color.divider_grey));
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        rl_about.setBackgroundColor(getResources().getColor(R.color.standard_white));
                        break;
                }
                break;

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_setting_fg_ll_modify_user_data:
                if (OkhttpUtil.checkLogin()){
                    intent = new Intent(getActivity(), ModifyUserDataActivity.class);
                    startActivity(intent);
                }else{
                        Dialog dialog = new Dialog(getActivity(),"Tips","请登录");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                }
                break;
            case R.id.id_setting_fg_rl_gegeral_setting://通用设置
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.id_setting_fg_rl_about:
                Toast.makeText(getActivity(),"关于",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_setting_fg_rl_private:
                Toast.makeText(getActivity(),"隐私",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_setting_fg_rl_feedback:
                Toast.makeText(getActivity(),"反馈",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_setting_fg_rl_account_and_secure:
                Toast.makeText(getActivity(),"账号与安全",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_setting_fg_btn_logout:
                logout();
                cleanLocation();
                getActivity().finish();
                intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void cleanLocation(){
        SharedPreferenceUtil.setState("0");
        SharedPreferenceUtil.setSessionId("");
    }

    private void logout(){
        OkhttpUtil.logout(handler);
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void handleAutoLogin(Message msg){
        //发广播通知MainActivity修改界面
        String result = msg.obj.toString();
        Log.d("Setting", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);
        JPushInterface.setAlias(getActivity(), root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

        Intent intent = new Intent("com.allever.autologin");
        getActivity().sendBroadcast(intent);

        tv_nickname.setText(SharedPreferenceUtil.getNickname());
        tv_username.setText(SharedPreferenceUtil.getUserName());

        Intent i = new Intent(getActivity(),ModifyUserDataActivity.class);
        startActivity(i);
    }

    private class  AfterModifyUserDataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action = intent.getAction();
            if(action.equals("com.allever.modifyUserData")){
                String nickname = intent.getStringExtra("nickname");
                tv_nickname.setText(nickname+ "(" + SharedPreferenceUtil.getUserName() + ")");
            }else if (action.equals("com.allever.modifyUserHead")){
                String head_path = intent.getStringExtra("head_path");
            }
        }
    }

    private class  AfterLoginaReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getActivity(),"收到广播",Toast.LENGTH_LONG).show();
            String  action = intent.getAction();
            if(action.equals("com.allever.afterlogin")){
                getActivity().finish();
            }
        }
    }

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }

    class User{
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
    }

    private void handleLogout(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LogoutRoot  root = gson.fromJson(result, LogoutRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){

        }else{
            logoutIMService();
        }
    }

    class LogoutRoot{
        public Boolean success;
        public String message;
    }

    private void logoutIMService(){
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("SocialActivity", "成功退出环信服务器");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.d("SocialActivity", "还没退出环信服务器");
            }
        });
    }
}
