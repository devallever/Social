package com.allever.social.foke;

/**
 * Created by XM on 2016/7/8.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allever.social.utils.FileUtils;

public class PhoneStatReceiver extends BroadcastReceiver {

    private String TAG = "tag";
    private int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到了广播");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            //Log.i(TAG, "手机开机了~~");
            NativeRuntime.getInstance().startService(context.getPackageName() + "/com.allever.social.foke.FokeService", FileUtils.createRootPath());
        } else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
        }
    }
}
