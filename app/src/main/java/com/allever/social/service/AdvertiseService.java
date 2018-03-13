package com.allever.social.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.allever.social.MyApplication;
import com.allever.social.receiver.AdvertiseReciever;
import com.allever.social.receiver.LongConnectionAlarmReceiver;
import com.allever.social.utils.SharedPreferenceUtil;

/**
 * Created by XM on 2016/4/29.
 */
public class AdvertiseService extends Service {
    private int dayCount;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceOnCreate3", "进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Log.d("AdvertiseService", "进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务进入广告定时服务");


        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences("com.allever.social_preferences", Context.MODE_PRIVATE);
        String str_day = sharedPreferences.getString("pref_setting_ad_day_key", "1");

        int day = 1;
        if (str_day.equals("1")) day = 1;
        else if (str_day.equals("2")) day = 2;
        else if (str_day.equals("3")) day = 3;



        int ten_min = 60*1000*60*24*day;//n天
        //int ten_min = 60*1000;//60s测试
        long triggerAtTime = SystemClock.elapsedRealtime() + ten_min;
        Intent i = new Intent(this,AdvertiseReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);

        if(SharedPreferenceUtil.getADReceiver()){

        }else{
            Log.d("AdvertiseService", day + "天后从新设置了闹钟");
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
            SharedPreferenceUtil.updateADReceiver(true);
        }


       stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
