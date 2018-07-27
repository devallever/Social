package com.allever.social.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.allever.social.R;
import com.allever.social.receiver.AdvertiseReciever;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;


/**
 * Created by XM on 2016/4/29.
 */
public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference lp_ad_day;
    private ListPreference lp_ad_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.seeting_pref);

        lp_ad_day = (ListPreference)this.findPreference("pref_setting_ad_day_key");
        lp_ad_count = (ListPreference)this.findPreference("pref_setting_ad_count_key");


    }



    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String show_day = sharedPreferences.getString("pref_setting_ad_day_key","选择弹出时间") + "天";
        lp_ad_day.setSummary(show_day);

        String show_count = sharedPreferences.getString("pref_setting_ad_count_key","选择弹出次数") + "次";
        lp_ad_count.setSummary(show_count);

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //设置了系统的生成的配置文件
        String  str_daycount = sharedPreferences.getString("pref_setting_ad_day_key", "1");
        String show_day = sharedPreferences.getString("pref_setting_ad_day_key","1") + "天";
        lp_ad_day.setSummary(show_day);

        String str_count = sharedPreferences.getString("pref_setting_ad_count_key","1");
        String show_count = sharedPreferences.getString("pref_setting_ad_count_key","1") + "次";
        lp_ad_count.setSummary(show_count);

        //设置自己的配置文件
        SharedPreferenceUtil.setADComment(Integer.parseInt(str_daycount),Integer.parseInt(str_count));



       // SharedPreferenceUtil.setAdvertiseDatacannotshow();
        int dayCount;
        if(str_daycount.equals("1")){
            dayCount= 1;
        }else if (str_daycount.equals("2")){
            dayCount = 2;
        }else if (str_daycount.equals("3")){
            dayCount = 3;
        }else{
            dayCount = 1;
        }
        int ten_min = 60*1000*60*24*dayCount;//n天
        //int ten_min = 60*1000;//60s测试
        long triggerAtTime = SystemClock.elapsedRealtime() + ten_min;
        Intent i = new Intent(this,AdvertiseReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
    }
}
