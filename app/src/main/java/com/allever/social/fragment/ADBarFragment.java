package com.allever.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.WebViewActivity;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/1.
 * 广告条
 */
public class ADBarFragment extends Fragment {
    private ImageView iv_ad_bar;
    private ImageView iv_close;

    private Handler handler;
    private List<AdDetail> list_addetail = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ad_bar_fragment_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AD_DETAIL:
                        handleADDetail(msg);
                        break;
                }
            }
        };

        iv_ad_bar = (ImageView)view.findViewById(R.id.id_ad_bar_fg_iv_ad_bar);
        iv_close = (ImageView)view.findViewById(R.id.id_ad_bar_fg_iv_close);

        iv_ad_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "点击广告条", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url",list_addetail.get(0).url);
                if(list_addetail.size()>0) startActivity(intent);
                Intent broadIntent = new Intent("com.allever.social.broadcast_close_ad_bar");
                getActivity().sendBroadcast(broadIntent);
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent broadIntent = new Intent("com.allever.social.broadcast_close_ad_bar");
                getActivity().sendBroadcast(broadIntent);
            }
        });

        getADBar();


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

    private void getADBar(){
        OkhttpUtil.getAdDdtail(handler, "2");
    }

    private void handleADDetail(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADDetailRoot  root = gson.fromJson(result, ADDetailRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }

        list_addetail = root.addetail_list;

        if (list_addetail.size()>0) Glide.with(this).load(WebUtil.HTTP_ADDRESS+list_addetail.get(0).ad_path).into(iv_ad_bar);
        else iv_ad_bar.setImageResource(R.mipmap.ic_ad_bar);


    }


    class ADDetailRoot{
        boolean success;
        String message;
        List<AdDetail> addetail_list;
    }

    class AdDetail{
        String id;
        String ad_path;
        String url;
    }
}
