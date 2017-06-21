package com.allever.social.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/4/16.
 */
public class ChatFragment extends Fragment {
    private Handler handler;
    private FrameLayout.LayoutParams lp_Left_Bottom;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        handleADSetting(msg);
                        break;
                }
            }
        };



        getADSetting();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }

    /**
     * 获取广告设置
     * **/
    private void getADSetting(){
        OkhttpUtil.getADSetting(handler);
    }


    private void handleADSetting(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADSettingRoot  root = gson.fromJson(result, ADSettingRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }

        int count = SharedPreferenceUtil.getADcount("ad_bar");
        //联网后
        boolean isshow = SharedPreferenceUtil.getADshow("ad_bar");
        if((root.ad_setting.isshow==1) && isshow){
            if(count != 0){
                SharedPreferenceUtil.updateADcount((count - 1), "ad_bar");
            }else{
                SharedPreferenceUtil.updateADshow(false,"ad_bar");
            }
        }



    }

    class ADSettingRoot{
        boolean success;
        String message;
        ADSetting ad_setting;
    }

    class ADSetting{
        String id;
        int day_space;
        int count;
        int isshow;
    }

}
