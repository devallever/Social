package com.allever.social.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allever.social.service.LongConnectionService;

import java.util.List;

/**
 * Created by XM on 2016/4/28.
 */
public class LongConnectionAlarmReceiver extends BroadcastReceiver {
    private final String TAG = "ConnectionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到了广播");
        Log.d(TAG, "收到了 com.allever.social.longconnection 的广播");
        Intent i = new Intent(context, LongConnectionService.class);
        context.startService(i);


    }
}
