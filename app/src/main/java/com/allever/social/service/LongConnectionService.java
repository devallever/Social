package com.allever.social.service;

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
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.receiver.LongConnectionAlarmReceiver;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
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
 * Created by XM on 2016/4/28.
 */
public class LongConnectionService extends Service {
    private Handler handler;
    private int count;
    private final String TAG = "LongConnectionService";

    private NetService mNetService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mNetService = new OkHttpService();

        Toast.makeText(this,"服务已被重启",Toast.LENGTH_LONG).show();
        count=0;
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //每隔一分钟自动登录一次
        Intent intentLocationService =new Intent(LongConnectionService.this,BDLocationService.class);
        startService(intentLocationService);

        count++;
        //Toast.makeText(this,"每隔1分钟登录一次\n已登录" + count + "次.",Toast.LENGTH_LONG).show();
        Log.d(TAG,"每隔1分钟登录一次\n已登录" + count + "次.");

        //Log.d("StartService", "登陆聊天服务器成功！");
        if (SharedPreferenceUtil.getUserName()!=null && SharedPreferenceUtil.getPassword()!=null){
            if (!SharedPreferenceUtil.getUserName().equals("") && !SharedPreferenceUtil.getPassword().equals("")){
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
            }
        }


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_POLL_SERVICE:
                        handlePollService(msg);
                        break;
                }
            }
        };

        OkhttpUtil.pollServive(handler);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int ten_min = 60*1000*1;//1分钟;
        //int ten_min = 30*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + ten_min;
        Intent i = new Intent(this,LongConnectionAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }



    private void handlePollService(Message msg){
        Log.d(TAG, "handlePollService: ");
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);
        Log.d("LongConnection",result + "TTTT");

        if (root!=null || !root.success){
            if(root.message.equals("未登录")){
                Log.d(TAG, "handlePollService: AAAAAAAAAAAA");
                //断线自动重连
                //OkhttpUtil.autoLogin(handler);
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
                return;
            }else {
                Log.d(TAG, "handlePollService: BBBBBBBBBBB");
            }
            return ;
        }else{
            Log.d(TAG, "handlePollService: CCCCCCCCC");
            SharedPreferenceUtil.setVip(root.is_vip+"");
            SharedPreferenceUtil.setRecommend(root.is_recommended+"");
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LongConnectionWas killed!!!!!!1111");
        Toast.makeText(this,"LongConnectionWas killed!!!!!!1111",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,LongConnectionService.class);
        startService(intent);
    }

    private void handleAutoLogin(NetResponse netResponse){
        Log.d(TAG, "handleAutoLogin: ");
        String result = netResponse.getString();
        Log.d(TAG, "handleAutoLogin: result = " + result);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Response<com.allever.social.bean.User>>() {}.getType();
        com.allever.social.bean.Response<com.allever.social.bean.User> root = gson.fromJson(result,type);

        if (root.isSuccess()){
            Log.d(TAG, "handleAutoLogin: autoLogin success");
            SharedPreferenceUtil.setSessionId(root.getSession_id());
            Log.d(TAG, "handleAutoLogin: set session_id = " + root.getSession_id());
            SharedPreferenceUtil.setState("1");
        }else {
            Log.d(TAG, "handleAutoLogin: autoLogin fail");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("Social");
        builder.setContentText("已重新登录...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(this, root.getData().getUsername(), new TagAliasCallback() {
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
