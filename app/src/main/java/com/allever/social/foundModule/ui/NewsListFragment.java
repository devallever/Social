package com.allever.social.foundModule.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.AddCommentDialogActivity;
import com.allever.social.activity.NewsDetailActivity;
import com.allever.social.foundModule.adapter.NewsListBaseAdapter;
import com.allever.social.foundModule.bean.NewsBeen;
import com.allever.social.fragment.NearbyFragment;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.listener.RecyclerViewScrollListener;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allever on 2016/12/3.
 */

public class NewsListFragment extends Fragment implements  RecyclerViewScrollListener.OnRecycleRefreshListener {

    private final static int REQUEST_CODE_UPDATE = 1000;
    private final static int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;

    private ProgressDialog progressDialog;

    private List<NewsBeen> list_news = new ArrayList<>();
    private NewsListBaseAdapter newsListBaseAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewScrollListener recyclerViewScrollListener;

    private Handler handler;
    private int page = 1;

    private int selected_position;//点赞记录所选位置
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list_fragment_layout,container,false);

        createNewsImageDir();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEWS_LIST:
                        handleNewsList(msg);
                        break;
                }

            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.refresh_nearby_news");
        intentFilter.addAction("com.allever.action_update_like_news");
        intentFilter.addAction("com.allever.action_update_comment_news");
        //intentFilter.addAction("com.allever.social.broadcast_close_ad_bar");
        myReceiver = new MyReceiver();
        getActivity().registerReceiver(myReceiver, intentFilter);

        initView(view);
        getNewsList();
        return view;
    }

    private void createNewsImageDir(){
        String dirPath = Environment.getExternalStorageDirectory() + "/social/news/";
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_UPDATE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (data.getStringExtra("result_type").equals("like")) {
                        int position = data.getIntExtra("position", 0);
                        int like_count = data.getIntExtra("like_count", 0);
                        boolean islike = data.getBooleanExtra("islike", false);
                        list_news.get(position).setLickCount(like_count);
                        if (islike) list_news.get(position).setIsLiked(1);
                        else list_news.get(position).setIsLiked(0);
                        newsListBaseAdapter.notifyDataSetChanged();
                    }
                    //可有可无
                    if (data.getStringExtra("result_type").equals("comment")) {
                        int position = data.getIntExtra("position", 0);
                        int comment_count = data.getIntExtra("comment_count", 0);
                        list_news.get(position).setCommentCount(comment_count + "");
                        newsListBaseAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    private void initDate(){
        NewsBeen newsBeen = new NewsBeen();
        newsBeen.setContent("content");
        List<String> list_img = new ArrayList<>();
        list_img.add("/images/head/baobao.jpg");
        newsBeen.setNewsimg_list(list_img);
        newsBeen.setIsLiked(1);
        newsBeen.setLickCount(3);
        newsBeen.setUser_head_path(WebUtil.HTTP_ADDRESS + "/images/head/yc_been.jpg");
        newsBeen.setNickname("Baobao");
        newsBeen.setTime("2分钟前");
        list_news.add(newsBeen);
    }

    private void initView(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.id_news_list_fg_recycler_view);
        newsListBaseAdapter = new NewsListBaseAdapter(getActivity(), list_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(newsListBaseAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("news_id",list_news.get(position).getId());
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
                switch (view.getId()){
                    case R.id.id_news_item_tv_content:
                        Toast.makeText(getActivity(),list_news.get(position).getContent(),Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_news_list_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        recyclerViewScrollListener = new RecyclerViewScrollListener(this);

        recyclerView.addOnScrollListener(recyclerViewScrollListener);
        swipeRefreshLayout.setOnRefreshListener(recyclerViewScrollListener);

    }

    private void getNewsList(){
        OkhttpUtil.getNewsList(handler, page+"");
    }

    private void handleNewsList(Message msg){
        String result = msg.obj.toString();
        Log.d("NewsListFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        NewsListRoot root = gson.fromJson(result, NewsListRoot.class);
        swipeRefreshLayout.setRefreshing(false);
        dismissProgressDialog();
        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            Toast.makeText(getActivity(), root.message, Toast.LENGTH_LONG).show();
            return;
        }

        if (page == 1){
            list_news.clear();
        }
        NewsBeen newsBeen;
        for (News news: root.news_list){
            newsBeen = new NewsBeen();
            newsBeen.setId(news.id);
            newsBeen.setContent(news.content);
            newsBeen.setIsLiked(news.isLiked);
            newsBeen.setLickCount(news.lickcount);
            newsBeen.setNewsimg_list(news.news_image_path);
            newsBeen.setUser_head_path(WebUtil.HTTP_ADDRESS + news.user_head_path);
            newsBeen.setNickname(news.nickname);
            newsBeen.setTime(news.date);
            list_news.add(newsBeen);
            SharedPreferenceUtil.saveUserData(news.username,news.nickname,WebUtil.HTTP_ADDRESS + news.user_head_path);
        }
        recyclerViewScrollListener.setLoadDataStatus(false);
        newsListBaseAdapter.notifyDataSetChanged();
    }

    private void likeNews(int position){
        OkhttpUtil.likeNews(handler,list_news.get(position).getId());
    }

    private void handleLikeNews(Message msg){
        String result = msg.obj.toString();
        Log.d("LikeNews", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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
                list_news.get(selected_position).setIsLiked(1);
            }else{
                list_news.get(selected_position).setIsLiked(0);
            }
            list_news.get(selected_position).setLickCount(root.likeCount);
        }
        newsListBaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        page = 1;
        getNewsList();
    }

    @Override
    public void loadMore() {
        showProgressDialog("正在加载...");
        //Toast.makeText(getActivity(),"正在加载...",Toast.LENGTH_LONG).show();
        page ++;
        getNewsList();
    }

    private void showProgressDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.refresh_nearby_news":
                    //Toast.makeText(getActivity(),"s收到刷新动态广播",Toast.LENGTH_LONG).show();
                    page = 1;
                    getNewsList();
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
                            intent_activity.putExtra("news_id", list_news.get(selected_position).getId());
                            intent_activity.putExtra("position", selected_position);
                            startActivityForResult(intent_activity, REQUEST_CODE_UPDATE_COMMENT_COUNT);
                        }
                    }
                    break;
            }

        }
    }


    class NewsListRoot {
        boolean success;
        String message;
        List<News> news_list;
    }

    class News {
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
        double distance;
        String user_head_path;
        int commentcount;
        int lickcount;
        int isLiked;
        String news_voice_path;
        List<String> news_image_path;
    }

    class LikeRoot {
        boolean success;
        String message;
        int likeCount;
        int islike;
    }

}
