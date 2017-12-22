package com.allever.social.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.SharedPreferenceUtil;

/**
 * Created by XM on 2016/10/9.
 */
public class ShareRemindDateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int rest_count = SharedPreferenceUtil.getShareRemindCount();
        //Toast.makeText(context,"收到广播",Toast.LENGTH_LONG).show();
        if (rest_count == 0){
            //Toast.makeText(context,"剩余：" + "rest_count = " + rest_count + "天提醒", Toast.LENGTH_LONG).show();
        }else{
            rest_count -- ;
            SharedPreferenceUtil.setShareRemindRestCount(SharedPreferenceUtil.getShareRemindDate(),rest_count);
            //Toast.makeText(context,"上一次提醒日期；" + SharedPreferenceUtil.getShareRemindDate() + "\n" + "今天：" + CommentUtil.getDate() + "\n剩余：" + rest_count + "天", Toast.LENGTH_LONG).show();
        }
        return;
    }
}
