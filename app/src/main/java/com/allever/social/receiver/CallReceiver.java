package com.allever.social.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.activity.VideoCallActivity;
import com.allever.social.activity.VoiceeCallActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

/**
 * Created by XM on 2016/6/11.
 */
public class CallReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( !EMClient.getInstance().isLoggedInBefore()) return;
        //拨打方username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");
        if("video".equals(type)){ //视频通话
            Toast.makeText(context,"收到视频聊天请求",Toast.LENGTH_LONG).show();
            Log.d("CallReceiver","收到视频聊天请求");
            context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else if ("voice".equals(type)){ //音频通话
            context.startActivity(new Intent(context, VoiceeCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        EMLog.d("CallReceiver", "app received a incoming call");
    }
}
