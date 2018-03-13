package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.allever.social.adapter.NewsItemBaseAdapter;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/4/15.
 * 好友动态界面
 */
public class FriendNewsFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2{
    private final int NEARBY_NEWS_LIST = 0;
    private final int REQUEST_CODE_UPDATE = 1000;
    private final int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;


    private PullToRefreshListView listView;
    private List<NewsItem> list_newsItem;
    private NewsItemAdapter ad;
    private NewsItemBaseAdapter ad_base;

    private boolean isloading;

    private OkHttpClient okHttpClient;
    private Handler handler;

    private Gson gson;
    private Root root;

    private SharedPreferences userShpf;
    private String session_id;
    private String state;

    private int seleced_position;//点赞记录所选位置
    private MyReciever myReciever;
    private IntentFilter intentFilter;

    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.hot_fragment_layout, container, false);
        listView = (PullToRefreshListView)view.findViewById(R.id.id_hot_fg_listview_hot);
        listView.setOnItemClickListener(this);
        list_newsItem = new ArrayList<>();

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
                switch (scrollState){
                    case SCROLL_STATE_FLING:
                        Log.i("ListScroll","用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                        Glide.with(MyApplication.mContext).pauseRequests();
                        //刷新
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.i("ListScroll", "视图已经停止滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.i("ListScroll","手指没有离开屏幕，视图正在滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        Log.d("FriendNewsList", WebUtil.HTTP_ADDRESS + "/FriendNewsServlet");

        userShpf = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        session_id = userShpf.getString("session_id", null);
        state = userShpf.getString("state", "0");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_NEWS:
                        handleFriendNews(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LIKE:
                        handleLikeNews(msg);
                        break;
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.action_update_like_news");
        intentFilter.addAction("com.allever.action_update_comment_news");
        myReciever = new MyReciever();
        getActivity().registerReceiver(myReciever, intentFilter);

        getFriendNewsList();
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
        getFriendNewsList();
    }
    //上啦
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++;
        getFriendNewsList();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReciever);
    }

    public void getFriendNewsList(){
        if(OkhttpUtil.checkLogin())  OkhttpUtil.getFriendNews(handler,page+"");
        else new Dialog(getActivity(),"Tips","未登录").show();
    }
    private void handleFriendNews(Message msg){
        String result = msg.obj.toString();
        Log.d("FrinedNewsFragment", result);
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

        if (page == 1) list_newsItem.clear();
        NewsItem newsItem;
        if(root.news_list==null){
            return;
        }
        for (News news : root.news_list){
            newsItem = new NewsItem();
            newsItem.setId(news.id);
            newsItem.setLickCount(String.valueOf(news.lickcount));
            newsItem.setCommentCount(String.valueOf(news.commentcount));
            newsItem.setTime(news.date);
            newsItem.setCity(news.city);
            newsItem.setSex(news.sex);
            newsItem.setAge(news.age);
            newsItem.setUsername(news.username);
            newsItem.setNickname(news.nickname);
            newsItem.setNewsimg_list(news.news_image_path);
            newsItem.setUser_head_path(news.user_head_path);
            newsItem.setContent(news.content);
            newsItem.setDistance(news.distance);
            newsItem.setUser_id(news.user_id);
            newsItem.setIsLiked(String.valueOf(news.isLiked));
            newsItem.setNews_from("friend_news");//好友动态
            newsItem.setNews_voice(news.news_voice_path);
            list_newsItem.add(newsItem);

            SharedPreferenceUtil.saveUserData(news.username,news.nickname,WebUtil.HTTP_ADDRESS +news.user_head_path);

        }
        if (page == 1){
            ad_base = new NewsItemBaseAdapter(getActivity(),list_newsItem,WebUtil.NEWS_TYPE_NEARBY);
            listView.setAdapter(ad_base);
            listView.onRefreshComplete();
        }else{
            ad_base.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra("position",i-1);
        intent.putExtra("news_id",list_newsItem.get(i-1).getId());
        startActivityForResult(intent,REQUEST_CODE_UPDATE);
    }

    class Root{
        boolean success;
        String message;
        List<News> news_list;
    }

    class News{
        String id;
        String content;
        String user_id;
        String username;
        String date;
        String nickname;
        String sex;
        int age;
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

    private class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.action_update_like_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("friend_news")){
                            seleced_position = intent.getIntExtra("position", 0);
                            likeNews(seleced_position);
                        }
                    }
                    break;
                case "com.allever.action_update_comment_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("friend_news")){
                            seleced_position = intent.getIntExtra("position", 0);
                            Intent intent_activity = new Intent(getActivity(),AddCommentDialogActivity.class);
                            intent_activity.putExtra("news_id", list_newsItem.get(seleced_position).getId());
                            intent_activity.putExtra("position", seleced_position);
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
                list_newsItem.get(seleced_position).setIsLiked("1");
            }else{
                list_newsItem.get(seleced_position).setIsLiked("0");
            }
            list_newsItem.get(seleced_position).setLickCount(root.likeCount+"");
        }
        ad_base.notifyDataSetChanged();
    }
    class LikeRoot {
        boolean success;
        String message;
        int likeCount;
        int islike;
    }

}
