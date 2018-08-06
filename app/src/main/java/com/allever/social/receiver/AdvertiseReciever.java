package com.allever.social.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.allever.social.MyApplication;
import com.allever.social.service.AdvertiseService;
import com.allever.social.utils.SharedPreferenceUtil;

/**
 * Created by XM on 2016/4/29.
 */
public class AdvertiseReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences("com.allever.social_preferences",Context.MODE_PRIVATE);
        String str_day = sharedPreferences.getString("pref_setting_ad_day_key", "1");
        String str_count = sharedPreferences.getString("pref_setting_ad_count_key","1");

        Log.d("count before", str_count);

        int day = 1;
        if (str_day.equals("1")) day = 1;
        else if (str_day.equals("2")) day = 2;
        else if (str_day.equals("3")) day = 3;

        int count = 1;
        if (str_count.equals("1")) count = 1;
        else if (str_count.equals("2")) count = 2;
        else if (str_count.equals("3")) count = 3;

        Log.d("count after", count + "");

        SharedPreferenceUtil.updateADdata(day,count,"ad_screen");
        SharedPreferenceUtil.updateADdata(day,count,"ad_bar");
        SharedPreferenceUtil.updateADdata(day, count, "ad_exit");

        Intent i = new Intent(context, AdvertiseService.class);
        context.startService(i);
        SharedPreferenceUtil.updateADReceiver(false);
    }
}
