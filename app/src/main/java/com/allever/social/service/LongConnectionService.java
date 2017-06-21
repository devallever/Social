package com.allever.social.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
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

//import com.hyphenate.chat.EMClient;

import com.allever.social.R;
import com.allever.social.receiver.LongConnectionAlarmReceiver;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/4/28.
 */
public class LongConnectionService extends Service {
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
        Toast.makeText(this,"服务已被重启",Toast.LENGTH_LONG).show();
        count=0;
        //开一个线程每隔一分钟发一次广播
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //Intent intentbroadcast = new Intent("com.allever.social.longconnection");
                        //intentbroadcast.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        //LongConnectionService.this.sendBroadcast(intentbroadcast);
                        //Log.d(TAG, "服务进行中。已发第" + (count + 1) + "个广播");
                        //Toast.makeText(LongConnectionService.this,"服务进行中。已发第" + (count + 1) + "个广播",Toast.LENGTH_LONG).show();
                        //Log.d("StartService", "登陆聊天服务器成功！");
                        //Toast.makeText(LongConnectionService.this,"服务进行中。",Toast.LENGTH_LONG).show();
                        Thread.sleep(1000*60);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).start();
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

        OkhttpUtil.pollServive(handler);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //服务该干嘛的干嘛
                //执行耗时的操作
                //stopSelf();

            }
        }).start();
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
        Intent i = new Intent(this,LongConnectionAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }



    private void handlePollService(Message msg){
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);
        Log.d("LongConnection",result);

//        if (SharedPreferenceUtil.getUserName()!=null && SharedPreferenceUtil.getPassword()!=null){
//            if (!SharedPreferenceUtil.getUserName().equals("") && !SharedPreferenceUtil.getPassword().equals("")){
//                //登录环信
//                //登录app服务器成功后登录环信服务器
//                EMClient.getInstance().login(SharedPreferenceUtil.getUserName(), SharedPreferenceUtil.getPassword(), new EMCallBack() {//回调
//                    @Override
//                    public void onSuccess() {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                EMClient.getInstance().groupManager().loadAllGroups();
//                                EMClient.getInstance().chatManager().loadAllConversations();
//                                Log.d("LoninFragment", "登陆聊天服务器成功！");
//                            }
//                        }).start();
//
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String status) {
//
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        Log.d("LoninFragment", "登陆聊天服务器失败！");
//                    }
//                });
//            }
//        }



        if (root!=null || !root.success){
            if(root.message.equals("未登录")){
                //断线自动重连
                OkhttpUtil.autoLogin(handler);
                return;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setTicker("登录失败");
            builder.setContentTitle("Social");
            builder.setContentText("请重新登录");
            builder.setSmallIcon(R.mipmap.logo);
            builder.setContentInfo("");
            builder.setAutoCancel(true);
////        Intent intent = new Intent(this, NotificationPendingIntentActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
////        builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.notify(1, builder.build());

            return ;
        }else{
            SharedPreferenceUtil.setVip(root.is_vip+"");
            SharedPreferenceUtil.setRecommend(root.is_recommended+"");
//            //登录环信
//            //登录app服务器成功后登录环信服务器
//            EMClient.getInstance().login(SharedPreferenceUtil.getUserName(), SharedPreferenceUtil.getPassword(), new EMCallBack() {//回调
//                @Override
//                public void onSuccess() {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            EMClient.getInstance().groupManager().loadAllGroups();
//                            EMClient.getInstance().chatManager().loadAllConversations();
//                            Log.d("LoninFragment", "登陆聊天服务器成功！");
//                        }
//                    }).start();
//
//                }
//
//                @Override
//                public void onProgress(int progress, String status) {
//
//                }
//
//                @Override
//                public void onError(int code, String message) {
//                    Log.d("LoninFragment", "登陆聊天服务器失败！");
//                }
//            });
            //Toast.makeText(this,result,Toast.LENGTH_LONG).show();
        }
        //this.stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LongConnectionWas killed!!!!!!1111");
        Toast.makeText(this,"LongConnectionWas killed!!!!!!1111",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,LongConnectionService.class);
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

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

//        //登录app服务器成功后登录环信服务器
//        EMClient.getInstance().login(root.user.username, SharedPreferenceUtil.getPassword(), new EMCallBack() {//回调
//            @Override
//            public void onSuccess() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EMClient.getInstance().groupManager().loadAllGroups();
//                        EMClient.getInstance().chatManager().loadAllConversations();
//                        Log.d("LoninFragment", "登陆聊天服务器成功！");
//                    }
//                }).start();
//
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                Log.d("LoninFragment", "登陆聊天服务器失败！");
//            }
//        });

        Log.d("LongConnection", result);


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
