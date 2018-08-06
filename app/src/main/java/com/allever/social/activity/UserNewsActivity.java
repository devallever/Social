package com.allever.social.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.adapter.NewsItemAdapter;
import com.allever.social.adapter.NewsItemBaseAdapter;
import com.allever.social.pojo.News;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.BlurTransformation;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.ImageUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.NestedScrollView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/22.
 * 用户动态界面
 */
public class UserNewsActivity extends BaseActivity implements AdapterView.OnItemClickListener ,
        AdapterView.OnItemLongClickListener{
    private final int REQUEST_CODE_UPDATE = 1000;
    private final int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private CircleImageView iv_head;

    private NestedScrollView listView;
    private List<NewsItem> list_newsItem;
    private NewsItemAdapter ad;
    private NewsItemBaseAdapter ad_base;
    private Handler handler;
    private Gson gson;
    private Root root;

    private String user_id;
    private String nickname;
    private String username;
    private String user_head_path;

    private ImageView iv_bg;
    private int seleced_position;//点赞记录所选位置
    private MyReciever myReciever;
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_news_layout);
        user_id = getIntent().getStringExtra("user_id");
        nickname = getIntent().getStringExtra("nickname");
        username = getIntent().getStringExtra("username");
        user_head_path = getIntent().getStringExtra("user_head_path");


        toolbar = (Toolbar) findViewById(R.id.id_user_news_toolbar);
        CommentUtil.initToolbar(this, toolbar, nickname + "的资料");
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.id_user_news_toolbar_layout);
        toolBarLayout.setTitle(nickname + "的动态");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_USER_NEWS:
                        handleUserNews(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_NEWS:
                        handleDeleteNews(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LIKE:
                        handleLikeNews(msg);
                        break;
                }
            }
        };

        initData();

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.action_update_like_news");
        intentFilter.addAction("com.allever.action_update_comment_news");
        myReciever = new MyReciever();
        registerReceiver(myReciever, intentFilter);

        getUserNews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReciever);
    }

    private void initData(){
        listView = (NestedScrollView)this.findViewById(R.id.id_user_news_listview);
        list_newsItem = new ArrayList<>();
        listView.setOnItemClickListener(this);
        iv_head = (CircleImageView)this.findViewById(R.id.id_user_news_iv_head);


        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS +user_head_path)
                .into(iv_head);
        if(user_id.equals(SharedPreferenceUtil.getUserId())){
            listView.setOnItemLongClickListener(this);
        }

        iv_bg = (ImageView)this.findViewById(R.id.id_user_news_iv_bg);
        Glide.with(this).load(WebUtil.HTTP_ADDRESS + user_head_path)
                .transform(new BlurTransformation(this, 100))
                .crossFade()
                .into(iv_bg);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final int position = i;
        if (user_id.equals(SharedPreferenceUtil.getUserId())){
            final Dialog dialog = new Dialog(this, "提示", "你确定要删除该动态吗？次操作不可恢复噢！");
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //deleteNews();
                    Dialog dialog = new Dialog(UserNewsActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteNews(position);
                        }
                    });
                    dialog.show();
                }
            });
            dialog.show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_UPDATE:
                if (resultCode == RESULT_OK){
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
                if (resultCode== RESULT_OK){
                    if (data.getStringExtra("result_type").equals("comment")){
                        int position = data.getIntExtra("position",0);
                        int comment_count = data.getIntExtra("comment_count",0);
                        list_newsItem.get(position).setCommentCount(comment_count+"");
                        ad_base.notifyDataSetChanged();
                        Toast.makeText(this, "评论成功", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("position",i);
        intent.putExtra("news_id",list_newsItem.get(i).getId());
        startActivityForResult(intent,REQUEST_CODE_UPDATE);
    }

    private void deleteNews(int position){
        OkhttpUtil.deleteNews(handler,list_newsItem.get(position).getId());
    }

    private void handleDeleteNews(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataActivity", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        DeleteNewsRoot root = gson.fromJson(result, DeleteNewsRoot.class);

        if (root == null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            new Dialog(this,"Tips",root.messgae).show();
        }else{
            getUserNews();
        }
    }

    private void getUserNews(){
        OkhttpUtil.getUserNews(handler, user_id);
    }

    private void handleUserNews(Message msg){
        String result = msg.obj.toString();
        Log.d("UserNewsActivity", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }


        list_newsItem.clear();
        NewsItem newsItem;
        for (News news : root.news_list){
            newsItem = new NewsItem();
            newsItem.setId(news.id);
            newsItem.setLickCount(String.valueOf(news.lickcount));
            newsItem.setCommentCount(String.valueOf(news.commentcount));
            newsItem.setTime(news.date);
            newsItem.setCity(news.city);
            newsItem.setNickname(news.nickname);
            newsItem.setNewsimg_list(news.news_image_path);
            newsItem.setUser_head_path(news.user_head_path);
            newsItem.setUser_id(news.user_id);
            newsItem.setUsername(news.username);
            newsItem.setSex(news.sex);
            newsItem.setAge(news.age);
            newsItem.setContent(news.content);
            newsItem.setDistance(news.distance);
            newsItem.setIsLiked(String.valueOf(news.isLiked));
            newsItem.setNews_from("user_news");//用户的动态
            newsItem.setNews_voice(news.news_voice_path);
            list_newsItem.add(newsItem);
        }
        ad_base = new NewsItemBaseAdapter(this,list_newsItem,WebUtil.NEWS_TYPE_HOT);
        listView.setAdapter(ad_base);


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
        String date;
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

    class DeleteNewsRoot{
        boolean success;
        String messgae;
    }


    private class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.action_update_like_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("user_news")){
                            seleced_position = intent.getIntExtra("position", 0);
                            likeNews(seleced_position);
                        }
                    }
                    break;
                case "com.allever.action_update_comment_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("user_news")){
                            seleced_position = intent.getIntExtra("position", 0);
                            Intent intent_activity = new Intent(UserNewsActivity.this,AddCommentDialogActivity.class);
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
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
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
