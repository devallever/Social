package com.allever.social.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allever.social.R;
import com.allever.social.activity.FriendLocationActivity;
import com.allever.social.activity.FriendNewsActivity;
import com.allever.social.activity.NewsDetailActivity;
import com.allever.social.activity.RequestFriendLocationDialogActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by XM on 2016/6/19.
 */
public class MyJPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyJPushReceiver";

    public static final String MESSAGE_TYPE_REFRESH_USER = "refresh_user";
    public static final String MESSAGE_TYPE_ADD_NEWS = "add_news";
    public static final String MESSAGE_TYPE_LIKE_NEWS = "like_news";
    public static final String MESSAGE_TYPE_COMMENT_NEWS = "comment_news";
    public static final String MESSAGE_TYPE_FOLLOW = "follow";
    public static final String MESSAGE_TYPE_REGIST_USER = "regist_user";
    public static final String MESSAGE_TYPE_REQUEST_FRIEND_LOCATION = "request_friend_location";
    public static final String MESSAGE_TYPE_ACCEPT_FRIEND_LOCATION = "accept_friend_location";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("MyJpushReceiver", "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            SharedPreferenceUtil.setRegistrationId(JPushInterface.getRegistrationID(context));
        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            String json_result = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            CustomeMessage  message = gson.fromJson(json_result, CustomeMessage.class);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setTicker(message.content);
            builder.setContentTitle(message.title);
            builder.setContentText(message.content);
            builder.setSmallIcon(R.mipmap.logo);
            //long[] viberate = {0,1000,1000,1000};//震动两次
            long[] viberate = {0,1000,0,0};
            builder.setVibrate(viberate);
            builder.setLights(0xff00ff00, 300, 1000);
            //默认系统设置
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            //builder.setContentInfo("This is content info");
            builder.setAutoCancel(true);

            if (message.msg_type.equals(MESSAGE_TYPE_REFRESH_USER)){
                Log.d("RefreshUser","刷新附近人");
                Intent i = new Intent(context,UserDataDetailActivity.class);
                i.putExtra("username", message.username);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (!message.username.equals(SharedPreferenceUtil.getUserName())){
                    notificationManager.notify(4, builder.build());
                }


//                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                builder.setTicker(message.content);
//                builder.setContentTitle(message.title);
//                builder.setContentText(message.content);
//                builder.setSmallIcon(R.mipmap.logo);
//                Intent i = new Intent(context,UserDataDetailActivity.class);
//                i.putExtra("username",message.username);
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                long[] viberate = {0,1000,0,0};
//                builder.setVibrate(viberate);
//                builder.setLights(0xff00ff00,300,1000);
//                //builder.setContentInfo("This is content info");
//                builder.setAutoCancel(true);
//
//                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(4, builder.build());
            }else if (message.msg_type.equals(MESSAGE_TYPE_ADD_NEWS)){
                Intent i = new Intent(context,NewsDetailActivity.class);
                i.putExtra("news_id",message.news_id);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(5, builder.build());


                int msg_count = SharedPreferenceUtil.getMsgCount();
                msg_count ++;
                SharedPreferenceUtil.saveMsgCount(message.username, msg_count);
                Intent broadintent = new Intent("com.allever.social.receiver_msg");
                broadintent.putExtra("username",message.username);
                broadintent.putExtra("msg_type",message.msg_type);
                context.sendBroadcast(broadintent);

            }else if (message.msg_type.equals(MESSAGE_TYPE_LIKE_NEWS)){
                Intent i = new Intent(context,NewsDetailActivity.class);
                i.putExtra("news_id",message.news_id);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(6, builder.build());
            }else if (message.msg_type.equals(MESSAGE_TYPE_COMMENT_NEWS)){
                Intent i = new Intent(context,NewsDetailActivity.class);
                i.putExtra("news_id",message.news_id);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(7, builder.build());
            }else if (message.msg_type.equals(MESSAGE_TYPE_FOLLOW)){
                Intent i = new Intent(context,UserDataDetailActivity.class);
                i.putExtra("username", message.username);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(8, builder.build());


            }else if (message.msg_type.equals(MESSAGE_TYPE_REGIST_USER)){
                Intent i = new Intent(context,UserDataDetailActivity.class);
                i.putExtra("username", message.username);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(9, builder.build());
            }else if (message.msg_type.equals(MESSAGE_TYPE_REQUEST_FRIEND_LOCATION)){
                Intent i = new Intent(context,RequestFriendLocationDialogActivity.class);
                i.putExtra("username", message.username);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(10, builder.build());
            }else if (message.msg_type.equals(MESSAGE_TYPE_ACCEPT_FRIEND_LOCATION)){
                Intent i = new Intent(context,FriendLocationActivity.class);
                i.putExtra("username", message.username);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(10, builder.build());
                //发送广播通知修改界面
                Intent intentBroadcast = new Intent("com.allever.social.update_friend_location");
                context.sendBroadcast(intentBroadcast);
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了通知");

            String json_result = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            if (json_result==null) return;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            MyMsg  msg = gson.fromJson(json_result, MyMsg.class);
            if (msg==null) return;
            String username = msg.username;
            String msg_type = msg.msg_type;

            if (msg_type!=null){
                if (msg_type.equals("add_news")){
                    int msg_count = SharedPreferenceUtil.getMsgCount();
                    msg_count ++;
                    SharedPreferenceUtil.saveMsgCount(username, msg_count);
                }
            }


            Intent broadintent = new Intent("com.allever.social.receiver_msg");
            broadintent.putExtra("username",username);
            broadintent.putExtra("msg_type",msg_type);
            context.sendBroadcast(broadintent);


            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            String json_result = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            if (json_result==null) return;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            MyMsg  msg = gson.fromJson(json_result, MyMsg.class);
            if (msg==null) return;
            String username = msg.username;
            String msg_type = msg.msg_type;

            if (msg_type!=null){
                if (msg_type.equals("add_news")){
                    Intent i = new Intent(context, FriendNewsActivity.class);  //自定义打开的界面
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    SharedPreferenceUtil.saveMsgCount("",0);
                }
                if (msg_type.equals("like_news")){
                    Intent i = new Intent(context, NewsDetailActivity.class);  //自定义打开的界面
                    i.putExtra("news_id", msg.news_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                if (msg_type.equals("comment_news")){
                    Intent i = new Intent(context, NewsDetailActivity.class);  //自定义打开的界面
                    i.putExtra("news_id",msg.news_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                if (msg_type.equals("refresh_user")){
                    Intent i = new Intent(context, UserDataDetailActivity.class);
                    i.putExtra("username", msg.username);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }

            System.out.println("用户点击打开了通知");


        } else {
            Log.d("MyJpushReceiver", "Unhandled intent - " + intent.getAction());
        }
    }

    private class MyMsg{
        String username;
        String msg_type;
        String news_id;
    }


    class CustomeMessage{
        //Common
        String username;
        String title;
        String content;
        String msg_type;

        //发布动态
        String news_id;
    }
}
