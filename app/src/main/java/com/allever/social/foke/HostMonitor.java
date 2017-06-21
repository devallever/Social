package com.allever.social.foke;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatService;
import com.hyphenate.chat.EMClient;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.service.PushService;

/**
 * Created by XM on 2016/7/8.
 */
public class HostMonitor extends Service {
    private Handler handler;
    private int count;
    private final String TAG = "ConnectionService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        //Toast.makeText(this, "服务已被重启", Toast.LENGTH_LONG).show();
        count=0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(HostMonitor.this).clearDiskCache();
            }
        }).start();

//        Intent intentEMService = new Intent(this, EMChatService.class);
//        startService(intentEMService);
//
//        Intent intentJPushService = new Intent(this, PushService.class);
//        startService(intentJPushService);

        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //每隔一分钟自动登录一次
        count++;
        //Toast.makeText(this,"每隔1分钟登录一次\n已登录" + count + "次.",Toast.LENGTH_LONG).show();
        Log.d(TAG,"每隔1分钟登录一次\n已登录" + count + "次.");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_POLL_SERVICE:
                        handlePollService(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                }
            }
        };

        if (OkhttpUtil.checkLogin()){
            OkhttpUtil.pollServive(handler);
        }

        //模拟信息-----------------------------------------------------------------------------
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("This is ticker text");
        builder.setContentTitle("Social");
        builder.setContentText("已开启长连接服务");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setContentInfo("This is content info");
        builder.setAutoCancel(true);
////        Intent intent = new Intent(this, NotificationPendingIntentActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
////        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.notify(1, builder.build());
        //--------------------------------------------------------------------------------------


        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int ten_min = 60*1000*1;//1分钟;
        //int ten_min = 30*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + ten_min;
        //Intent i = new Intent(this,LongConnectionAlarmReceiver.class);
        Intent i = new Intent(this,PhoneStatReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }



    private void handlePollService(Message msg){
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);
        Log.d("LongConnection",result);


        if (root==null) return;

        if (!root.success){
            if(root.message.equals("未登录")){
                //断线自动重连自己的服务器
                OkhttpUtil.autoLogin(handler);
                return;
            }
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//            builder.setTicker("登录失败");
//            builder.setContentTitle("Social");
//            builder.setContentText("请重新登录");
//            builder.setSmallIcon(R.mipmap.logo);
//            builder.setContentInfo("");
//            builder.setAutoCancel(true);
//////        Intent intent = new Intent(this, NotificationPendingIntentActivity.class);
//////        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//////        builder.setContentIntent(pendingIntent);
//            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            //notificationManager.notify(1, builder.build());
            return ;
        }else{
            //登录自己服务器：
            SharedPreferenceUtil.setVip(root.is_vip+"");
            SharedPreferenceUtil.setRecommend(root.is_recommended+"");
            //登录环信和极光推送
            loginHuanxinAndJpush();
        }
        //this.stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LongConnectionWas killed!!!!!!1111");
        Toast.makeText(this,"LongConnectionWas killed!!!!!!1111",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,HostMonitor.class);
        startService(intent);
    }



    private void handleAutoLogin(Message msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("Social");
        builder.setContentText("已重新登录...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());

        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);

        if (root==null) return;
        if (root.seccess==false) return;

        SharedPreferenceUtil.setSessionId(root.session_id);
        SharedPreferenceUtil.setState("1");

        loginHuanxinAndJpush();//登录环信和极光推送

//        //登录成功后为每个用户设置别名：username
//        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
//            @Override
//            public void gotResult(int i, String s, Set<String> set) {
//
//            }
//        });

        Log.d("LongConnection", result);
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

    public class LoginRoot{
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
    }


}