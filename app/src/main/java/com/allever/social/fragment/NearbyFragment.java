package com.allever.social.fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.AddCommentDialogActivity;
import com.allever.social.activity.NewsDetailActivity;
import com.allever.social.adapter.NewsItemAdapter;
import com.allever.social.adapter.NewsItemAdapterOne;
import com.allever.social.adapter.NewsItemBaseAdapter;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/4/15.
 */
public class NearbyFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2{
    private final static int REQUEST_CODE_UPDATE = 1000;
    private final static int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;

    private PullToRefreshListView listView;
    private List<NewsItem> list_newsItem;
    private int page = 1;
    private NewsItemAdapter ad;
    private NewsItemAdapterOne ad_one;
    private NewsItemBaseAdapter ad_base;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;
    private Handler handler;
    private Gson gson;
    private Root root;
    private ProgressDialog progressDialog;
    //private SocialDBAdapter db;

    private MyReceiver myReceiver;
    private IntentFilter intentFilter;

//    private ADBarFragment adBarFragment;
//    private FragmentManager fragmentManager;
//    private FragmentTransaction fragmentTransaction;

//    private CloseADBarReceiver closeADBarReceiver;

    private int selected_position;//点赞记录所选位置


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_fragment_layout, container, false);
//        db= new SocialDBAdapter(getActivity());
//        db.open();

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.refresh_nearby_news");
        intentFilter.addAction("com.allever.action_update_like_news");
        intentFilter.addAction("com.allever.action_update_comment_news");
        //intentFilter.addAction("com.allever.social.broadcast_close_ad_bar");
        myReceiver = new MyReceiver();
        getActivity().registerReceiver(myReceiver, intentFilter);

//        fragmentManager = this.getChildFragmentManager();
//        //测试用---------------
////        adBarFragment = new ADBarFragment();
////        fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.add(R.id.id_nearby_fg_fragment_ad_bar_container, adBarFragment);
////        fragmentTransaction.commit();
//        //----------------
//        closeADBarReceiver = new CloseADBarReceiver();
//        getActivity().registerReceiver(closeADBarReceiver,intentFilter);



        listView = (PullToRefreshListView)view.findViewById(R.id.id_nearby_fg_listview_nearby);
        listView.setOnItemClickListener(this);
        list_newsItem = new ArrayList<>();
        Log.d("NearbyFragment", WebUtil.HTTP_ADDRESS + "/NearbyNewsListServlet");

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel(
                getString(R.string.pull_to_load));
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
                getString(R.string.loading));
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel(
                getString(R.string.release_to_load));
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        Log.i("ListScroll", "用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                        Glide.with(MyApplication.mContext).pauseRequests();
                        //刷新
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.i("ListScroll", "视图已经停止滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.i("ListScroll", "手指没有离开屏幕，视图正在滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        //showProgressDialog();

//        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_nearby_fg_refresh);
//        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
//                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEARBY_NEWS:
                        handleNearbyNews(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        handleADSetting(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LIKE:
                        handleLikeNews(msg);
                        break;
                }
            }
        };
        //getADSetting();

        getNearbyNews();

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

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        getNearbyNews();
    }
    //上啦
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getNearbyNews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_UPDATE:
                if (resultCode == getActivity().RESULT_OK){
                    if (data.getStringExtra("result_type").equals("like")){
                        int position = data.getIntExtra("position",0);
                        int like_count = data.getIntExtra("like_count",0);
                        boolean islike = data.getBooleanExtra("islike",false);
                        list_newsItem.get(position).setLickCount(like_count + "");
                        if (islike) list_newsItem.get(position).setIsLiked("1");
                        else list_newsItem.get(position).setIsLiked("0");
                        ad_base.notifyDataSetChanged();
                    }
                    //可有可无
                    if (data.getStringExtra("result_type").equals("comment")){
                        int position = data.getIntExtra("position",0);
                        int comment_count = data.getIntExtra("comment_count",0);
                        list_newsItem.get(position).setCommentCount(comment_count+"");
                        ad_base.notifyDataSetChanged();
                    }

                }
                break;
            case REQUEST_CODE_UPDATE_COMMENT_COUNT:
                if (resultCode== getActivity().RESULT_OK){
                    if (data.getStringExtra("result_type").equals("comment")){
                        int position = data.getIntExtra("position",0);
                        int comment_count = data.getIntExtra("comment_count",0);
                        list_newsItem.get(position).setCommentCount(comment_count+"");
                        ad_base.notifyDataSetChanged();
                        Toast.makeText(getActivity(),"评论成功",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        db.close();
        getActivity().unregisterReceiver(myReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Intent intent = new
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra("position",i-1);
        intent.putExtra("news_id",list_newsItem.get(i-1).getId());
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

//    @Override
//    public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
//                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
//                    getNearbyNews();
//                    isloading = false;
//
//                } else {
//                    Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
//                            Toast.LENGTH_SHORT).show();
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        }, 1000);
//    }

    private void getNearbyNews(){
        OkhttpUtil.getNearbyNews(handler,page+"");
    }

    private void handleNearbyNews(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            closeProgressDialog();
            listView.onRefreshComplete();
            new Dialog(getActivity(),"Tips","无法获取附近动态").show();
            return;
        }else{
            closeProgressDialog();
            if (page == 1) list_newsItem.clear();
            NewsItem newsItem;
            for (News news : root.news_list){
                newsItem = new NewsItem();
                newsItem.setId(news.id);
                newsItem.setLickCount(String.valueOf(news.lickcount));
                newsItem.setCommentCount(String.valueOf(news.commentcount));
                newsItem.setTime(news.date);
                newsItem.setNickname(news.nickname);
                newsItem.setUsername(news.username);
                newsItem.setSex(news.sex);
                newsItem.setAge(news.age);
                newsItem.setNewsimg_list(news.news_image_path);
                newsItem.setUser_head_path(news.user_head_path);
                newsItem.setUser_id(news.user_id);
                newsItem.setContent(news.content);
                newsItem.setDistance(news.distance);
                newsItem.setIsLiked(String.valueOf(news.isLiked));
                newsItem.setNews_from("nearby_news");
                newsItem.setNews_voice(news.news_voice_path);
                list_newsItem.add(newsItem);
                SharedPreferenceUtil.saveUserData(news.username,news.nickname,WebUtil.HTTP_ADDRESS + news.user_head_path);

            }
            //ad = new NewsItemAdapter(MyApplication.getContext(),R.layout.news_item,list_newsItem,WebUtil.NEWS_TYPE_NEARBY);
            if (page == 1){
                ad_base = new NewsItemBaseAdapter(getActivity(),list_newsItem,WebUtil.NEWS_TYPE_NEARBY);
                listView.setAdapter(ad_base);
                listView.onRefreshComplete();
            }else{
                ad_base.notifyDataSetChanged();
                listView.onRefreshComplete();
            }


        }

    }


    class Root{
        boolean success;
        String message;
        List<News> news_list;
    }

    class News implements Serializable{
        String id;
        String content;
        String user_id;
        String nickname;
        String username;
        String sex;
        int age;
        String date;
        String longitude;
        String latitude;
        String city;
        String distance;
        String user_head_path;
        int commentcount;
        int lickcount;
        int isLiked;
        String news_voice_path;
        List<String> news_image_path;
    }


    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.refresh_nearby_news":
                    page = 1;
                    getNearbyNews();
                    break;
                case "com.allever.action_update_like_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("nearby_news")){
                            selected_position = intent.getIntExtra("position", 0);
                            likeNews(selected_position);
                        }
                    }
                    break;
                case "com.allever.action_update_comment_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("nearby_news")){
                            selected_position = intent.getIntExtra("position", 0);
                            Intent intent_activity = new Intent(getActivity(),AddCommentDialogActivity.class);
                            intent_activity.putExtra("news_id", list_newsItem.get(selected_position).getId());
                            intent_activity.putExtra("position", selected_position);
                            startActivityForResult(intent_activity, REQUEST_CODE_UPDATE_COMMENT_COUNT);
                        }
                    }
                    break;
            }

        }
    }

    private void likeNews(int position){
        OkhttpUtil.likeNews(handler,list_newsItem.get(position).getId());
    }

    private void handleLikeNews(Message msg){
        String result = msg.obj.toString();
        Log.d("LikeNews", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LikeRoot root = gson.fromJson(result, LikeRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if(root.message.equals("未登录")){
            OkhttpUtil.autoLogin(handler);
            return;
        }
        if (root.success == true){
            if(root.islike==1){
                list_newsItem.get(selected_position).setIsLiked("1");
            }else{
                list_newsItem.get(selected_position).setIsLiked("0");
            }
            list_newsItem.get(selected_position).setLickCount(root.likeCount+"");
        }
        ad_base.notifyDataSetChanged();
    }

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

    class LikeRoot {
        boolean success;
        String message;
        int likeCount;
        int islike;
    }


//    class CloseADBarReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(adBarFragment);
//            fragmentTransaction.commit();
//        }
//    }

}
