package com.allever.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.ChatRankItemBaseAdapter;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.adapter.ShareRankItemBaseAdapter;
import com.allever.social.pojo.ChatRankItem;
import com.allever.social.pojo.ShareRankItem;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by XM on 2016/7/11.
 */
public class ShareRankFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2{

    private PullToRefreshListView listView;

    private List<ShareRankItem> list_share_rank_item;

    private ShareRankItemBaseAdapter shareRankItemBaseAdapter;

    private int page = 1;

    private Handler handler;

    private RippleView rv_invite_user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_rank_fragment_lalyout, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_SHARE_RANK:
                        handleShareRank(msg);
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

        listView = (PullToRefreshListView)view.findViewById(R.id.id_share_rank_fg_listview);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel(
                getString(R.string.pull_to_load));
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
                getString(R.string.loading));
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel(
                getString(R.string.release_to_load));
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);

        list_share_rank_item = new ArrayList<>();

        rv_invite_user = (RippleView)view.findViewById(R.id.id_share_rank_fg_rv_invite_member);
        rv_invite_user.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //showShare();
                getShareInfo();
            }
        });

        getShareRank();//不显示数据

        return view;
    }

    private void getShareInfo(){
        OkhttpUtil.getShareInfo(handler);
    }

    private void handleGetShareInfo(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ShareInfoRoot root = gson.fromJson(result, ShareInfoRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        showShare(root.content, root.url, root.img_url);

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
        oks.setTitle(Constants.SHARE_TITLE  + SharedPreferenceUtil.getUserName());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath();//确保SDcard下面存在此张图片
        //oks.setImageUrl(WebUtil.HTTP_ADDRESS + "/images/logo.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(content);
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
                Log.d("sharecallback","错误，失败");
                Toast.makeText(getActivity(),"失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("sharecallback","取消");
                Toast.makeText(getActivity(),"取消",Toast.LENGTH_LONG).show();
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

        //成功 刷新 排位
        page = 1;
        getShareRank();

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

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page=1;
        getShareRank();
        //listView.onRefreshComplete();
    }

    //上拉
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getShareRank();
        //listView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
        intent.putExtra("username", list_share_rank_item.get(i-1).getUsername());
        startActivity(intent);
    }

    private void getShareRank(){
        OkhttpUtil.getShareRank(handler, page + "");
    }

    private void handleShareRank(Message msg){
        String result = msg.obj.toString();
        Log.d("ShareRankFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }


        if (page == 1) list_share_rank_item.clear();
        ShareRankItem shareRankItem;
        for (UserRank userRank: root.list_userrank){
            shareRankItem = new ShareRankItem();
            shareRankItem.setUsername(userRank.username);
            shareRankItem.setUser_head_path(userRank.user_head_path);
            shareRankItem.setNickname(userRank.nickname);
            shareRankItem.setShare_count(userRank.sharecount);
            list_share_rank_item.add(shareRankItem);
        }

        if (page==1){
            shareRankItemBaseAdapter = new ShareRankItemBaseAdapter(getActivity(),list_share_rank_item);
            listView.setAdapter(shareRankItemBaseAdapter);
            listView.onRefreshComplete();
        }else{
            shareRankItemBaseAdapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
    }


    class Root{
        boolean success;
        String message;
        List<UserRank> list_userrank;
    }
    class UserRank{
        String username;
        String nickname;
        int rank;
        String user_head_path;
        int sharecount;
    }

    class AddShareRecordRoot{
        boolean success;
        String message;
    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }

}
