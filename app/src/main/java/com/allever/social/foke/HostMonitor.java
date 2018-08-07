package com.allever.social.foke;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.bean.Response;
import com.allever.social.bean.User;
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.lang.reflect.Type;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/7/8.
 */
public class HostMonitor extends Service {
    private Handler handler;
    private int count;
    private final String TAG = "ConnectionService";


    private NetService mNetService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count=0;

        mNetService = new OkHttpService();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //每隔一分钟自动登录一次
        count++;
        Log.d(TAG,"每隔1分钟登录一次\n已登录" + count + "次.");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_POLL_SERVICE:
                        handlePollService(msg);
                        break;
                }
            }
        };

        if (OkhttpUtil.checkLogin()){
            OkhttpUtil.pollServive(handler);
        }

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }



    private void handlePollService(Message msg){
        Log.d(TAG, "handlePollService: ");
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);
        Log.d(TAG, "handlePollService: result = " + result);


        if (root==null) return;

        if (!root.success){
            mNetService.autoLogin(new NetCallback() {
                @Override
                public void onSuccess(NetResponse response) {
                    Log.d(TAG, "autoLogin onSuccess: ");
                    handleAutoLogin(response);
                }
                @Override
                public void onFail(String msg) {
                    Log.d(TAG, "autoLogin onFail: ");
                }
            });
        }else{
            //登录自己服务器：
            SharedPreferenceUtil.setVip(root.is_vip+"");
            SharedPreferenceUtil.setRecommend(root.is_recommended+"");
            //登录环信和极光推送
            loginHuanxinAndJpush();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LongConnectionWas killed!!!!!!1111");
        Toast.makeText(this,"LongConnectionWas killed!!!!!!1111",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,HostMonitor.class);
        startService(intent);
    }

    private void handleAutoLogin(NetResponse netResponse) {
        String result = netResponse.getString();
        Log.d(TAG, "handleAutoLogin: result = " + result);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Response<User>>() {}.getType();
        com.allever.social.bean.Response<com.allever.social.bean.User> root = gson.fromJson(result, type);

        if (root==null) return;
        if (root.isSuccess()==false) return;

        SharedPreferenceUtil.setSessionId(root.getSession_id());
        SharedPreferenceUtil.setState("1");

        loginHuanxinAndJpush();//登录环信和极光推送

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("Social");
        builder.setContentText("已重新登录...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());
    }


    private void loginHuanxinAndJpush(){
        //登录环信
        //登录app服务器成功后登录环信服务器
        EMClient.getInstance().login(SharedPreferenceUtil.getUserName(), SharedPreferenceUtil.getPassword(), new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d(TAG, "登陆聊天服务器成功！");
                    }
                }).start();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "登陆聊天服务器失败！\n" + message);
            }
        });


        JPushInterface.setAlias(this, SharedPreferenceUtil.getUserName(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }




    class Root{
        boolean success;
        int code;
        String message;
        int is_vip;
        int is_recommended;
    }
}