package com.allever.social.mvp.presenter;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.allever.social.MyApplication;
import com.allever.social.mvp.base.BasePresenter;
import com.allever.social.mvp.view.ISocialMainView;
import com.allever.social.service.BDLocationService;
import com.baidu.mobstat.StatService;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;

public class SocialMainPresenter extends BasePresenter<ISocialMainView> {


    //百度移动统计
    public void  initMTJ(){
        StatService.setLogSenderDelayed(10);
        StatService.setSessionTimeOut(30);
    }

    public void createSocialDir(){
        String dirPath = Environment.getExternalStorageDirectory() + "/social/";
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
    }

    public void initXGPush() {
        //信鸽调试。发布时注释
        XGPushConfig.enableDebug(MyApplication.mContext, true);
        //信鸽推送------------------------------------------------------------------------------
        XGPushManager.registerPush(MyApplication.mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.d("SocialMain","注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.d("SocialMain","注册失败");
            }
        });
        //信鸽推送------------------------------------------------------------------------------
    }

    public void startLocationService() {
        Intent intent = new Intent(MyApplication.mContext, BDLocationService.class);
        MyApplication.mContext.startService(intent);
    }

}
