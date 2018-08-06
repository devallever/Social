package com.allever.social.foke;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by XM on 2016/7/20.
 */
public class FokeService extends Service {

    private int count = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentHostMonitorService = new Intent(this,HostMonitor.class);
        startService(intentHostMonitorService);
        count ++ ;
        Log.d("FokeService","已启动HostMonitorService" + count + "次");
        return super.onStartCommand(intent, flags, startId);
    }
}
