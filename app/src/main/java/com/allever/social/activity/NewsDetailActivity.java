package com.allever.social.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.CommentItemAdapter;
import com.allever.social.adapter.CommentItemBaseAdapter;
import com.allever.social.pojo.CommentItem;
import com.allever.social.pojo.News;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MyListView;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/19.
 * 动态详情
 */
public class NewsDetailActivity extends BaseActivity implements View.OnClickListener,
                                                                View.OnFocusChangeListener ,
                                                                AdapterView.OnItemClickListener{
    private String state;
    private Root root;
    private Gson gson;
    private Toolbar toolbar;
    private MyListView listView;
   // private CommentItemAdapter ad;
    private CommentItemBaseAdapter commentItemBaseAdapter;
    private List<CommentItem> list_comment;

    private String longitude;
    private String latitude;
    //private News news;

    private CircleImageView iv_head;
    private TextView tv_nickname;
    private TextView tv_time;
    private TextView tv_content;
    private TextView tv_distance;
    private TextView tv_like_count;
    private TextView tv_comment_count;
    private ImageView iv_like;
    //private GridView gridView;
    //private NewsImgAdapter newsImgAdapter;
    private EditText et_comment_content;
    //private ImageButton ibtn_add_comment;
    private RippleView rv_add_comment;
    private String content;

    private int position;//点击评论列表获取索引

    private Handler handler;
    private boolean isLike;

    private int from_position;


    private RelativeLayout rl_img_container_1;
    private RelativeLayout rl_img_container_2;
    private RelativeLayout rl_img_container_3;
    private RelativeLayout rl_img_container_4;
    private RelativeLayout rl_img_container_5;
    private RelativeLayout rl_img_container_6;

    private String news_id;
    private News news;

    private ProgressDialog progressDialog;

    private RippleView rv_audio;//动态语音
    private TextView tv_play_audio;
    private String news_voice_url;
    private String news_voice_local_path;


    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private String audio_path = "";//语音文件保存路径
    private Handler handler_two;

    private RippleView rv_choose_audio_comment;
    private RippleView rv_choose_text_comment;
    private RelativeLayout rl_comment_audio_container;

    private RippleView rv_record;
    private RippleView rv_play_audio;//播放评论语音
    private TextView tv_play_audio_comment;
    private ImageView iv_delete_record;
    private TextView tv_audio_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);

        handler_two = new Handler();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEWS_COMMENT:
                        handleCommentList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LIKE:
                        handleLikeResult(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_COMMENT:
                        handleAddComment(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_NEWS_DETAIL_DETAIL:
                        handleGetNewsDetail(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DOWNLOAD_NEWS_VOICE:
                        handleDownloadNewsVoice(msg);
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

        //news = (News)getIntent().getSerializableExtra("news");
        news_id = getIntent().getStringExtra("news_id");

        from_position = getIntent().getIntExtra("position", 0);
       // toolbar = (Toolbar)this.findViewById(R.id.id_news_detail_toolbar);
       // CommentUtil.initToolbar(this, toolbar, "详情");
        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("详情");
        initData();
        //CommentUtil.showProgressDialog(progressDialog, this);
        getNewsDetail();


    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer!=null){
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void downloadNewsVoice(){
        OkhttpUtil.downloadNewsVoice(handler, news_voice_url);
    }

    private void handleDownloadNewsVoice(Message msg){
        byte[] b = (byte[])msg.obj;
        FileOutputStream fos;
        String filePath= "";
        File file = null;
        try{
            filePath = Environment.getExternalStorageDirectory().getPath() + "/social/";
            file = new File(filePath);
            if  (!file .exists()  && !file .isDirectory()) {
                System.out.println("//不存在");
                file .mkdir();
            } else{
                System.out.println("//目录存在");
            }

            filePath = filePath + "/voice/";
            file = new File(filePath);
            if  (!file .exists()  && !file .isDirectory()) {
                System.out.println("//不存在");
                file .mkdir();
            } else{
                System.out.println("//目录存在");
            }

            filePath = filePath + "/news_voice/";
            file = new File(filePath);
            if  (!file .exists()  && !file .isDirectory()) {
                System.out.println("//不存在");
                file .mkdir();
            } else{
                System.out.println("//目录存在");
            }

            filePath = news_voice_local_path;

            System.out.println("path = " + filePath);
            fos = new FileOutputStream(filePath);
            fos.write(b);
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getNewsDetail(){
        OkhttpUtil.getNewsDetail(handler, news_id);
    }

    private void handleGetNewsDetail(Message msg){
        String result = msg.obj.toString();
        Log.d("LikeNews", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        NewsDetailRoot root = gson.fromJson(result, NewsDetailRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if(root.message.equals("未登录")){
            OkhttpUtil.autoLogin(handler);
            return;
        }

       //CommentUtil.closeProgressDialog(progressDialog);

        news = root.news;
        updateVisitedNews();//获取newsdetail后调用
        getNewsComment();

        tv_content.setText(news.content);
        tv_nickname.setText(news.nickname);
        tv_time.setText(news.date);
        tv_distance.setText("距离 " + news.distance + " km");
        tv_distance.setText(news.city);

        if (news.news_voice_path==null || news.news_voice_path.equals("")) rv_audio.setVisibility(View.GONE);
        else rv_audio.setVisibility(View.VISIBLE);

        news_voice_url = news.news_voice_path;
        //news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social" + apk_version_name+ ".apk";
        news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.id +".arm";


        FileInputStream fin = null;
        String filename = news_voice_local_path;
        Log.d("Mainactivity", filename);
        File file = new File(filename);
        //判断文件是否存在，不存在就去下载
        if (!file.exists()) {
            //下载语音
            downloadNewsVoice();
        }else{
            //不干嘛
        }


//        if(news.distance != null){
//            Log.d("NewsDetail", "distance = " + news.getDistance());
//            if(news.getDistance().equals("-1.0")){
//                tv_distance.setText("距离未知");
//            }else{
//                tv_distance.setText("距离 " + news.getDistance() + " km");
//            }
//        }else{
//            if(!news.city.equals("")){
//                tv_distance.setText(news.getCity());
//            }else{
//                tv_distance.setText("距离未知");
//            }
//        }

        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + news.user_head_path)
                .into(iv_head);
        // Picasso.with(this).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).into(iv_head);

        tv_comment_count.setText(news.commentcount + "");
        tv_like_count = (TextView)this.findViewById(R.id.id_news_detail_tv_like_count);
        tv_like_count.setText(news.lickcount + "");


        if (news.isLiked==0){
            iv_like.setImageResource(R.mipmap.like_48);
            isLike = false;
        }else{
            iv_like.setImageResource(R.mipmap.liked_48);
            isLike = true;
        }

        //-----------------------------------------------------------------------------------------
        switch (news.news_image_path.size()){
            case 0:
                break;
            case 1:
                rl_img_container_1 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_1);
                rl_img_container_1.setVisibility(View.VISIBLE);
                ImageView container_1_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_1_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_1_iv_1);
                container_1_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                break;
            case 2:
                rl_img_container_2 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_2);
                rl_img_container_2.setVisibility(View.VISIBLE);
                ImageView container_2_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_2_iv_1);
                ImageView container_2_iv_2 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_2_iv_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_2_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(1)).into(container_2_iv_2);
                container_2_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                container_2_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }
                });
                break;
            case 3:
                rl_img_container_3 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_3);
                rl_img_container_3.setVisibility(View.VISIBLE);
                ImageView container_3_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_3_iv_1);
                ImageView container_3_iv_2 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_3_iv_2);
                ImageView container_3_iv_3 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_3_iv_3);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_3_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(1)).into(container_3_iv_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(2)).into(container_3_iv_3);
                container_3_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                container_3_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }
                });
                container_3_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }
                });
                break;
            case 4:
                rl_img_container_4 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_4);
                rl_img_container_4.setVisibility(View.VISIBLE);
                ImageView container_4_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_4_iv_1);
                ImageView container_4_iv_2 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_4_iv_2);
                ImageView container_4_iv_3 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_4_iv_3);
                ImageView container_4_iv_4 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_4_iv_4);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_4_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(1)).into(container_4_iv_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(2)).into(container_4_iv_3);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(3)).into(container_4_iv_4);

                container_4_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                container_4_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }
                });
                container_4_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }
                });
                container_4_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 3);
                        startActivity(intent);
                    }
                });
                break;
            case 5:
                rl_img_container_5 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_5);
                rl_img_container_5.setVisibility(View.VISIBLE);
                ImageView container_5_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_5_iv_1);
                ImageView container_5_iv_2 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_5_iv_2);
                ImageView container_5_iv_3 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_5_iv_3);
                ImageView container_5_iv_4 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_5_iv_4);
                ImageView container_5_iv_5 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_5_iv_5);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_5_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(1)).into(container_5_iv_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(2)).into(container_5_iv_3);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(3)).into(container_5_iv_4);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(4)).into(container_5_iv_5);

                container_5_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                container_5_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }
                });
                container_5_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }
                });
                container_5_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 3);
                        startActivity(intent);
                    }
                });
                container_5_iv_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 4);
                        startActivity(intent);
                    }
                });
                break;
            case 6:
                rl_img_container_6 = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_img_6);
                rl_img_container_6.setVisibility(View.VISIBLE);
                ImageView container_6_iv_1 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_1);
                ImageView container_6_iv_2 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_2);
                ImageView container_6_iv_3 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_3);
                ImageView container_6_iv_4 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_4);
                ImageView container_6_iv_5 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_5);
                ImageView container_6_iv_6 = (ImageView)this.findViewById(R.id.id_news_detail_rl_img_6_iv_6);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(0)).into(container_6_iv_1);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(1)).into(container_6_iv_2);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(2)).into(container_6_iv_3);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(3)).into(container_6_iv_4);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(4)).into(container_6_iv_5);
                Glide.with(this).load(WebUtil.HTTP_ADDRESS + news.news_image_path.get(5)).into(container_6_iv_6);

                container_6_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
                container_6_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }
                });
                container_6_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }
                });
                container_6_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 3);
                        startActivity(intent);
                    }
                });
                container_6_iv_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 4);
                        startActivity(intent);
                    }
                });
                container_6_iv_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NewsDetailActivity.this,ShowNewsImageActivity.class);
                        String[] arr = new String[news.news_image_path.size()];
                        for (int j = 0; j<news.news_image_path.size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.news_image_path.get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 5);
                        startActivity(intent);
                    }
                });
                break;
        }
        //-----------------------------------------------------------------------------------------

    }

    private void updateVisitedNews(){
        OkhttpUtil.updateVisitedNews(news.id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_share:
                //showShare();
                getShareInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void initData(){
        listView = (MyListView)this.findViewById(R.id.id_news_detail_listView);
        list_comment = new ArrayList<>();
        iv_head = (CircleImageView)this.findViewById(R.id.id_news_detail_circle_iv_userhead);
        iv_head.setOnClickListener(this);
        tv_content = (TextView)this.findViewById(R.id.id_news_detail_tv_content);
        tv_nickname = (TextView)this.findViewById(R.id.id_news_detail_tv_nickname);
        tv_distance = (TextView)this.findViewById(R.id.id_news_detail_tv_distance);
        tv_time = (TextView)this.findViewById(R.id.id_news_detail_tv_time);
        tv_comment_count = (TextView)this.findViewById(R.id.id_news_detail_tv_comment_count);
        iv_like = (ImageView)this.findViewById(R.id.id_news_detail_iv_like);

        iv_like.setOnClickListener(this);
        et_comment_content = (EditText)this.findViewById(R.id.id_news_detail_et_content);
        et_comment_content.setOnClickListener(this);
        et_comment_content.setOnFocusChangeListener(this);

        tv_play_audio = (TextView)this.findViewById(R.id.id_news_detail_tv_play_audio);
        rv_audio = (RippleView)this.findViewById(R.id.id_news_detail_rv_audio);

        rv_choose_audio_comment = (RippleView)this.findViewById(R.id.id_news_detail_rv_choose_audio_comment);
        rv_choose_text_comment = (RippleView)this.findViewById(R.id.id_news_detail_rv_choose_text_comment);
        rl_comment_audio_container = (RelativeLayout)this.findViewById(R.id.id_news_detail_rl_comment_audio_container);
        rv_add_comment = (RippleView)this.findViewById(R.id.id_news_detail_rv_add_comment);
        rv_record = (RippleView)this.findViewById(R.id.id_news_detail_rv_audio_record);
        rv_play_audio = (RippleView)this.findViewById(R.id.id_news_detail_rv_play_audio);
        tv_play_audio_comment = (TextView)this.findViewById(R.id.id_news_detail_tv_play_audio_comment);
        iv_delete_record = (ImageView)this.findViewById(R.id.id_news_detail_iv_delete_record);
        tv_audio_record = (TextView)this.findViewById(R.id.id_news_detail_tv_audio_record);

        rv_record.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(tv_audio_record.getText().toString().equals("点我开始")){
                    tv_audio_record.setText("停止");
                    rv_record.setBackgroundColor(NewsDetailActivity.this.getResources().getColor(R.color.colorAccent));
                    //录音
                    audio_path = Environment.getExternalStorageDirectory().getPath();
                    audio_path += "/audio_temp.arm";
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mRecorder.setOutputFile(audio_path);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("NewsDetailActivity", "prepare() failed");
                    }
                    mRecorder.start();


                }else if (tv_audio_record.getText().toString().equals("停止")){
                    tv_audio_record.setText("点我开始");
                    rv_record.setBackgroundColor(NewsDetailActivity.this.getResources().getColor(R.color.colorPrimary));
                    rv_record.setVisibility(View.GONE);
                    rv_play_audio.setVisibility(View.VISIBLE);
                    //停止录音
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                }
            }
        });
        rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (tv_play_audio_comment.getText().toString().equals("播放")){
                    //播放录音
                    tv_play_audio_comment.setText("停止");
                    mPlayer = new MediaPlayer();
                    try{
                        mPlayer.setDataSource(audio_path);
                        mPlayer.prepare();
                        mPlayer.start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean flag = true;
                                try {
                                    while (flag){
                                        Thread.sleep(1000);
                                        if (mPlayer !=null){
                                            if (!mPlayer.isPlaying()) flag = false;
                                        }
                                    }

                                    handler_two.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_play_audio_comment.setText("播放");
                                        }
                                    });
                                }catch (InterruptedException e){
                                }
                            }
                        }).start();


                    }catch(IOException e){
                        Log.e("AddNewsActivity",audio_path);
                        Log.e("AddNewsActivity","播放失败");
                    }


                }else if (tv_play_audio_comment.getText().toString().equals("停止")){
                    //停止播放录音
                    tv_play_audio_comment.setText("播放");
                    mPlayer.stop();
                }
            }
        });


        iv_delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_play_audio.setVisibility(View.GONE);
                rv_record.setVisibility(View.VISIBLE);
                audio_path = "";
                if (mPlayer!=null){
                    if (mPlayer.isPlaying()){
                        tv_play_audio_comment.setText("播放");
                        mPlayer.release();
                        mPlayer = null;
                    }
                }
            }
        });


        //ibtn_add_comment = (ImageButton)this.findViewById(R.id.id_news_detail_btn_add_comment);
        //ibtn_add_comment.setOnClickListener(this);

        rv_add_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                content  = et_comment_content.getText().toString();
                if (audio_path.equals("") && content.equals("")){
                    if(content.equals("") || content.equals("评论")){
                        new Dialog(NewsDetailActivity.this,"Tips","请输入评论内容").show();
                        return;
                    }
                }

                addComment();
            }
        });

        rv_choose_audio_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                rv_choose_audio_comment.setVisibility(View.GONE);
                rv_choose_text_comment.setVisibility(View.VISIBLE);
                et_comment_content.setVisibility(View.GONE);
                rl_comment_audio_container.setVisibility(View.VISIBLE);
            }
        });
        rv_choose_text_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                rv_choose_text_comment.setVisibility(View.GONE);
                rv_choose_audio_comment.setVisibility(View.VISIBLE);
                et_comment_content.setVisibility(View.VISIBLE);
                rl_comment_audio_container.setVisibility(View.GONE);
            }
        });

        listView.setOnItemClickListener(this);

        rv_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //播放录音
                if (tv_play_audio.getText().toString().equals("播放")){
                    //播放录音
                    tv_play_audio.setText("停止");
                    mPlayer = new MediaPlayer();
                    try{
                        mPlayer.setDataSource(news_voice_local_path);
                        mPlayer.prepare();
                        mPlayer.start();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean flag = true;
                                try {
                                    while (flag){
                                        Thread.sleep(1000);
                                        if (mPlayer !=null){
                                            if (!mPlayer.isPlaying()) flag = false;
                                        }
                                    }

                                    handler_two.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_play_audio.setText("播放");
                                        }
                                    });
                                }catch (InterruptedException e){
                                }
                            }
                        }).start();


                    }catch(IOException e){
                        Log.e("AddNewsActivity",news_voice_local_path);
                        Log.e("AddNewsActivity","播放失败");
                    }
                }else if (tv_play_audio.getText().toString().equals("停止")){
                    //停止播放
                    tv_play_audio.setText("播放");
                    mPlayer.stop();
                    //mPlayer = null;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_news_detail_et_content:
                if(et_comment_content.getText().toString().equals("评论")){
                    et_comment_content.setText("");
                }
                break;
            case R.id.id_news_detail_circle_iv_userhead:
                intent = new Intent(this, UserDataDetailActivity.class);
                intent.putExtra("username", news.username);
                startActivity(intent);
                break;
            case R.id.id_news_detail_iv_like:
                likeNews();
//                if(OkhttpUtil.checkLogin()){
//                    likeNews();
//                }else{
//                    new Dialog(this,"提示","您还没登录").show();
//                }
                break;
//            case R.id.id_news_detail_btn_add_comment:
//                content  = et_comment_content.getText().toString();
//                if(content.equals("") || content.equals("评论")){
//                    new Dialog(this,"Tips","请输入评论内容").show();
//                    return;
//                }
//                addComment();
//                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        position = i;
        et_comment_content.setFocusable(true);
        et_comment_content.setFocusableInTouchMode(true);
        et_comment_content.requestFocus();
        et_comment_content.setText("回复_" + list_comment.get(position).getNickname() + ":");
        et_comment_content.setSelection(et_comment_content.getText().toString().length());
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        int id = view.getId();
        switch (id){
            case R.id.id_news_detail_et_content:
                if(et_comment_content.getText().toString().equals("评论")){
                    et_comment_content.setText("");
                }
                break;
        }
    }



    /**
     * 使用自定义的Listview不用调用该方法
     * */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private void addComment(){
        if(content.split("_")[0].equals("回复")){
            OkhttpUtil.addComment(handler,content,news.id,list_comment.get(position).getId(),audio_path);//回复评论
        }else{
            OkhttpUtil.addComment(handler,content,news.id,null,audio_path);//发表评论
        }

    }

    private void likeNews(){
        OkhttpUtil.likeNews(handler, news.id);
    }

    private void handleLikeResult(Message msg){

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



        tv_like_count.setText(root.likeCount + "");
        if (root.success == true){
            if(isLike){
                iv_like.setImageResource(R.mipmap.like_48);
                isLike = false;
            }else{
                iv_like.setImageResource(R.mipmap.liked_48);
                isLike = true;
            }

            Intent data = new Intent();
            data.putExtra("result_type","like");
            data.putExtra("position",from_position);
            data.putExtra("like_count",root.likeCount);
            data.putExtra("islike",isLike);
            setResult(RESULT_OK, data);
        }
    }

    private void handleAutoLogin(Message msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("已自动登录");
        builder.setContentText("请重新操作...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());

        //发广播通知MainActivity修改界面
        Intent intent = new Intent("com.allever.autologin");
        sendBroadcast(intent);

        String result = msg.obj.toString();
        Log.d("Setting", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });


    }

    private void handleAddComment(Message msg){
        String result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddCommentRoot root = gson.fromJson(result, AddCommentRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == true){
            list_comment.clear();

            Toast.makeText(this,"评论成功",Toast.LENGTH_LONG).show();
            getNewsComment();
            et_comment_content.setText("");

            tv_comment_count.setText(root.comment_count + "");
            Intent data = new Intent();
            data.putExtra("result_type","comment");
            data.putExtra("position", from_position);
            data.putExtra("comment_count", root.comment_count);
            setResult(RESULT_OK, data);


        }else if(root.success == false){
            if(root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }
    }

    private void getNewsComment(){
        OkhttpUtil.getNewsComment(handler,news.id);
    }

    private void handleCommentList(Message msg){
        String result = msg.obj.toString();
        Log.d("NewsDetail", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == true){
            //new Dialog(this,"错误",root.message).show();
            list_comment.clear();
            CommentItem commentItem;
            for (Comment comment : root.list_comment){
                commentItem = new CommentItem();
                commentItem.setId(comment.id);
                commentItem.setTime(comment.date);
                commentItem.setNickname(comment.nickname);
                commentItem.setUsername(comment.username);
                commentItem.setUser_head_path(comment.user_head_path);
                commentItem.setContent(comment.content);
                commentItem.setUser_id(comment.user_id);
                commentItem.setComment_id(comment.comment_id);
                commentItem.setComment_voice(comment.comment_voice_path);
                list_comment.add(commentItem);
            }
            //ad = new CommentItemAdapter(this,R.layout.comment_item,list_comment);
            //listView.setAdapter(ad);
            //setListViewHeightBasedOnChildren(listView);

            commentItemBaseAdapter = new CommentItemBaseAdapter(this,list_comment);
            listView.setAdapter(commentItemBaseAdapter);
            CommentUtil.setListViewHeightBasedOnChildren(listView);
            ScrollView scrollView = (ScrollView)this.findViewById(R.id.id_news_detail_scrollview);
            scrollView.smoothScrollTo(0, 0);
        }
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
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        showShare(root.content, root.url, root.img_url);

    }

    //调用shareSDK分享代码
    private void showShare(String content,String url,String img_url) {
        ShareSDK.initSDK(NewsDetailActivity.this);
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
        oks.setComment(Constants.SHARE_TITLE + SharedPreferenceUtil.getUserName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("sharecallback", "成功" + platform.getName());
                Toast.makeText(NewsDetailActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                addShareRecord();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("sharecallback", "错误，失败");
                Toast.makeText(NewsDetailActivity.this, "失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("sharecallback", "取消");
                Toast.makeText(NewsDetailActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
// 启动分享GUI
        oks.show(NewsDetailActivity.this);
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
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;


    }

    class AddShareRecordRoot{
        boolean success;
        String message;
    }



    class Root{
        boolean success;
        String message;
        List<Comment> list_comment;
    }
    class Comment{
        String id;
        String content;
        String user_id;
        String nickname;
        String username;
        String user_head_path;
        String date;
        String comment_id;
        String comment_voice_path;
    }

    class LikeRoot{
        boolean success;
        String message;
        int likeCount;
    }

    class AddCommentRoot{
        boolean success;
        String message;
        int comment_count;
        Comment comment;
    }


    class NewsDetailRoot{
        boolean success;
        String message;
        News news;
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

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }
    class User{
        String id;
        String username;
        String nickname;
        String imagepath;
        double longitude;
        double latiaude;
        String phone;
        String email;
        String user_head_path;
        String signature;
        String city;
        String sex;
    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }



}
