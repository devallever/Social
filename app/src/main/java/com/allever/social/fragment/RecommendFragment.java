package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.ChatRankActivity;
import com.allever.social.activity.FriendLocationActivity;
import com.allever.social.activity.FriendNewsActivity;
import com.allever.social.activity.GetVipActivity;
import com.allever.social.activity.HotNewsActivity;
import com.allever.social.activity.NearbyGroupWithRedPocketMapViewActivity;
import com.allever.social.activity.NearbyUserActivity;
import com.allever.social.activity.NewerTaskActivity;
import com.allever.social.activity.SignActivity;
import com.allever.social.activity.WebViewActivity;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.flashview.FlashView;
import com.gc.flashview.listener.FlashViewListener;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/24.
 * 发现 推荐界面
 */
public class RecommendFragment extends Fragment implements View.OnClickListener , RippleView.OnRippleCompleteListener{
    private RippleView rv_hot;
    private RippleView rv_friend;
    private RippleView rv_newer_task;
    private RippleView rv_sign_in;
    private RippleView rv_rank;
    private RippleView rv_share;

    private RippleView rv_friend_location;

    private FlashView flashView_ad;

    private Handler handler;

    private List<AdDetail> list_addetail = new ArrayList<>();
    private List<String> imageUrls;

    private RelativeLayout rl_msg_container;

    private TextView tv_msg_count;

    private CircleImageView iv_msg_userhead;

    private int msg_count = 0;

    private IntentFilter intentFilter;
    private MyMsgReceiver myMsgReceiver;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_fragment_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AD_DETAIL:
                        handleADDetail(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_SHARE_RECORD:
                        handleAddShareRecord(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GET_SHARE_INFO:
                        handleGetShareInfo(msg);
                        break;
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.receiver_msg");
        myMsgReceiver = new MyMsgReceiver();
        getActivity().registerReceiver(myMsgReceiver, intentFilter);

        rv_hot = (RippleView)view.findViewById(R.id.id_found_fg_rv_hot_news);
        rv_friend = (RippleView)view.findViewById(R.id.id_found_fg_rv_friend_news);
        rv_newer_task = (RippleView)view.findViewById(R.id.id_found_fg_rv_newer_task);
        rv_sign_in = (RippleView)view.findViewById(R.id.id_found_fg_rv_sign_in);
        rv_rank = (RippleView)view.findViewById(R.id.id_found_fg_rv_rank);
        rv_friend_location = (RippleView)view.findViewById(R.id.id_found_fg_rv_friend_location);

        tv_msg_count = (TextView)view.findViewById(R.id.id_found_fg_tv_msg_count);
        iv_msg_userhead = (CircleImageView)view.findViewById(R.id.id_found_fg_iv_msg_userhead);
        rl_msg_container = (RelativeLayout)view.findViewById(R.id.id_found_fg_rl_msg_container);
        msg_count = SharedPreferenceUtil.getMsgCount();
        if (msg_count!=0){
            rl_msg_container.setVisibility(View.VISIBLE);
            tv_msg_count.setText(msg_count + "");
            Glide.with(getActivity()).load(SharedPreferenceUtil.getUserHeadPath(SharedPreferenceUtil.getMsgUserName())).into(iv_msg_userhead);
        }

        rv_hot.setOnClickListener(this);
        rv_hot.setOnRippleCompleteListener(this);

        rv_friend.setOnClickListener(this);
        rv_friend.setOnRippleCompleteListener(this);
        rv_friend_location.setOnRippleCompleteListener(this);

        rv_newer_task.setOnRippleCompleteListener(this);
        rv_sign_in.setOnRippleCompleteListener(this);
        rv_rank.setOnRippleCompleteListener(this);



        imageUrls = new ArrayList<>();
        flashView_ad = (FlashView)view.findViewById(R.id.id_found_fg_flashview);
        flashView_ad.setOnPageClickListener(new FlashViewListener() {
            @Override
            public void onClick(int position) {
                //Toast.makeText(getActivity(),list_addetail.get(position).url,Toast.LENGTH_LONG).show();
                if(list_addetail.size()>0) {
                    //Toast.makeText(WelcomeActivity.this,list_addetail.get(0).url,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("url",list_addetail.get(position).url);
                    if(list_addetail.size()>0) startActivity(intent);
                }
            }
        });

        rv_share = (RippleView)view.findViewById(R.id.id_found_fg_rv_share);
        rv_share.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //showShare();
                getShareInfo();
            }
        });

        getADurl();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myMsgReceiver);
    }

    private void  getADurl(){
        OkhttpUtil.getAdDdtail(handler, "2");
    }

    private void handleADDetail(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADDetailRoot  root = gson.fromJson(result, ADDetailRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }

        list_addetail = root.addetail_list;
        for(AdDetail adDetail : list_addetail){
            imageUrls.add(WebUtil.HTTP_ADDRESS + adDetail.ad_path);
        }
        flashView_ad.setImageUris(imageUrls);

//        if (list_addetail.size()>0) Glide.with(this).load(WebUtil.HTTP_ADDRESS+list_addetail.get(0).ad_path).into(iv_ad_bar);
//        else iv_ad_bar.setImageResource(R.mipmap.ic_ad_bar);


    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        Intent intent;
        switch (id){
            case R.id.id_found_fg_rv_hot_news:
                intent = new Intent(getActivity(), HotNewsActivity.class);
                startActivity(intent);
                break;
            case R.id.id_found_fg_rv_friend_news:
                msg_count = 0;
                rl_msg_container.setVisibility(View.GONE);
                SharedPreferenceUtil.saveMsgCount("", msg_count);
                intent = new Intent(getActivity(), FriendNewsActivity.class);
                startActivity(intent);

                Intent broadIntent = new Intent("com.allever.social.update_msg_count");
                getActivity().sendBroadcast(broadIntent);
                break;
            case R.id.id_found_fg_rv_newer_task:
                intent = new Intent(getActivity(), NewerTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.id_found_fg_rv_sign_in:
                intent = new Intent(getActivity(), SignActivity.class);
                startActivity(intent);
                break;
            case R.id.id_found_fg_rv_rank:
                //Toast.makeText(getActivity(),"该功能未开启",Toast.LENGTH_LONG).show();
                //intent = new Intent(getActivity(), ChatRankActivity.class);
                //startActivity(intent);

                intent = new Intent(getActivity(), NearbyUserActivity.class);
                startActivity(intent);
                break;
            case R.id.id_found_fg_rv_friend_location:
                //intent  = new Intent(getActivity(), NearbyGroupWithRedPocketMapViewActivity.class);
                //startActivity(intent);
                intent= new Intent(getActivity(), FriendLocationActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view) {

    }


    private void getShareInfo(){
        OkhttpUtil.getShareInfo(handler);
    }

    private void handleGetShareInfo(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ShareInfoRoot root = gson.fromJson(result, ShareInfoRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        showShare(root.content,root.url,root.img_url);

    }

    //调用shareSDK分享代码
    private void showShare(String content,String url,String img_url) {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(Constants.SHARE_TITLE + SharedPreferenceUtil.getUserName());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath();//确保SDcard下面存在此张图片
        oks.setImageUrl(img_url);//新增参数
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(Constants.SHARE_TITLE  + SharedPreferenceUtil.getUserName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("sharecallback", "成功" + platform.getName());
                Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_LONG).show();
                addShareRecord();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("sharecallback", "错误，失败");
                Toast.makeText(getActivity(), "失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("sharecallback", "取消");
                Toast.makeText(getActivity(), "取消", Toast.LENGTH_LONG).show();
            }
        });
// 启动分享GUI
        oks.show(getActivity());
    }


    private void addShareRecord(){
        OkhttpUtil.addShareRecord(handler);
    }

    private void handleAddShareRecord(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataDetail", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddShareRecordRoot root = gson.fromJson(result, AddShareRecordRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success==false) return;

    }

    class AddShareRecordRoot{
        boolean success;
        String message;
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


    private class MyMsgReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.receiver_msg":
                    //Toast.makeText(getActivity(),"收到更新",Toast.LENGTH_LONG).show();
                    String msg_type = intent.getStringExtra("msg_type");
                    if (msg_type==null) return;
                    if (msg_type.equals("add_news")){
                        rl_msg_container.setVisibility(View.VISIBLE);
                        String username = intent.getStringExtra("username");
                        Glide.with(getActivity()).load( SharedPreferenceUtil.getUserHeadPath(username)).into(iv_msg_userhead);
                        msg_count++;
                        tv_msg_count.setText(msg_count + "");
                    }
                    if (msg_type.equals("like_news")){
                    }
                    if (msg_type.equals("comment_news"))
                    break;
            }
        }
    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }
}
