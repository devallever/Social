package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.HotNewsActivity;
import com.allever.social.activity.ShowBigImageActvity;
import com.allever.social.activity.ShowNewsImageActivity;
import com.allever.social.activity.UserDataActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/11.
 * 附近动态列表适配器
 */
public class NewsItemBaseAdapter extends BaseAdapter {
    private List<NewsItem> list_newsItem;
    private Context context;
    private OkHttpClient okHttpClient;
    private String type;
    private int position;

    private Handler handler;
    private Gson gson;
    private String result;
    private boolean isliked;
    //private Root root;
    private int likeCount;

    LayoutInflater inflater;

    //private MediaPlayer mPlayer = null;
    private Handler handler_two;

    public NewsItemBaseAdapter(Context context, List<NewsItem> newsItem_list,String newsType){
        this.list_newsItem = newsItem_list;
        this.context = context;
        okHttpClient = new OkHttpClient();
        this.type = newsType;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list_newsItem.size();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        handler_two = new Handler();

        handler = new Handler();
        Log.d("NewsItemBaseAdater", position+"");
        final NewsItem news = (NewsItem)getItem(position);
        if(news.getIsLiked().equals("1")){
            isliked = true;
        }else{
            isliked = false;
        }
        likeCount = Integer.valueOf(news.getLickCount());

        ViewHolder viewHolder = null;
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        ViewHolder3 holder3 = null;
        ViewHolder4 holder4 = null;
        ViewHolder5 holder5 = null;
        ViewHolder6 holder6 = null;

        int item_type = getItemViewType(position);
        View view;
        if(convertView == null) {
            switch (item_type){
                case 0:
                    view = inflater.inflate(R.layout.news_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    viewHolder.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    viewHolder.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    viewHolder.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    viewHolder.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    viewHolder.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    viewHolder.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    viewHolder.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    viewHolder.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    viewHolder.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    viewHolder.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    viewHolder.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    viewHolder.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    viewHolder.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    viewHolder.mPlayer = new MediaPlayer();

                    view.setTag(viewHolder);
                    break;
                case 1:
                    view = inflater.inflate(R.layout.news_item_one, parent, false);
                    holder1 = new ViewHolder1();
                    holder1.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder1.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder1.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder1.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder1.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder1.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder1.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder1.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder1.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder1.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder1.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder1.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder1.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder1.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder1.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder1.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder1.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);

                    holder1.mPlayer = new MediaPlayer();
                    view.setTag(holder1);
                    break;

                case 2:
                    view = inflater.inflate(R.layout.news_item_two, parent, false);
                    holder2 = new ViewHolder2();
                    holder2.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder2.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder2.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder2.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder2.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder2.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder2.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder2.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder2.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder2.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder2.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder2.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder2.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder2.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder2.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder2.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder2.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
                    holder2.iv_news_img_iv_2 = (ImageView)view.findViewById(R.id.id_news_item_iv_2);

                    holder2.mPlayer = new MediaPlayer();
                    view.setTag(holder2);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.news_item_three, parent, false);
                    holder3 = new ViewHolder3();
                    holder3.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder3.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder3.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder3.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder3.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder3.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder3.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder3.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder3.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder3.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder3.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder3.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder3.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder3.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder3.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder3.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder3.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
                    holder3.iv_news_img_iv_2 = (ImageView)view.findViewById(R.id.id_news_item_iv_2);
                    holder3.iv_news_img_iv_3 = (ImageView)view.findViewById(R.id.id_news_item_iv_3);

                    holder3.mPlayer = new MediaPlayer();
                    view.setTag(holder3);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.news_item_four, parent, false);
                    holder4 = new ViewHolder4();
                    holder4.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder4.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder4.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder4.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder4.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder4.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder4.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder4.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder4.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder4.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder4.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder4.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder4.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder4.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder4.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder4.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder4.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
                    holder4.iv_news_img_iv_2 = (ImageView)view.findViewById(R.id.id_news_item_iv_2);
                    holder4.iv_news_img_iv_3 = (ImageView)view.findViewById(R.id.id_news_item_iv_3);
                    holder4.iv_news_img_iv_4 = (ImageView)view.findViewById(R.id.id_news_item_iv_4);

                    holder4.mPlayer = new MediaPlayer();
                    view.setTag(holder4);
                    break;
                case 5:
                    view = inflater.inflate(R.layout.news_item_five, parent, false);
                    holder5 = new ViewHolder5();
                    holder5.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder5.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder5.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder5.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder5.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder5.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder5.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder5.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder5.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder5.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder5.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder5.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder5.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder5.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder5.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder5.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder5.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
                    holder5.iv_news_img_iv_2 = (ImageView)view.findViewById(R.id.id_news_item_iv_2);
                    holder5.iv_news_img_iv_3 = (ImageView)view.findViewById(R.id.id_news_item_iv_3);
                    holder5.iv_news_img_iv_4 = (ImageView)view.findViewById(R.id.id_news_item_iv_4);
                    holder5.iv_news_img_iv_5 = (ImageView)view.findViewById(R.id.id_news_item_iv_5);

                    holder5.mPlayer = new MediaPlayer();
                    view.setTag(holder5);
                    break;
                case 6:
                    view = inflater.inflate(R.layout.news_item_six, parent, false);
                    holder6 = new ViewHolder6();
                    holder6.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    holder6.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    holder6.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    holder6.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    holder6.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    holder6.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    holder6.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    holder6.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    holder6.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    holder6.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    holder6.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    holder6.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    holder6.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    holder6.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    holder6.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    holder6.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    holder6.iv_news_img_iv_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
                    holder6.iv_news_img_iv_2 = (ImageView)view.findViewById(R.id.id_news_item_iv_2);
                    holder6.iv_news_img_iv_3 = (ImageView)view.findViewById(R.id.id_news_item_iv_3);
                    holder6.iv_news_img_iv_4 = (ImageView)view.findViewById(R.id.id_news_item_iv_4);
                    holder6.iv_news_img_iv_5 = (ImageView)view.findViewById(R.id.id_news_item_iv_5);
                    holder6.iv_news_img_iv_6 = (ImageView)view.findViewById(R.id.id_news_item_iv_6);

                    holder6.mPlayer = new MediaPlayer();
                    view.setTag(holder6);
                    break;
                default:
                    view = inflater.inflate(R.layout.news_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
                    viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
                    viewHolder.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
                    viewHolder.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);
                    viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
                    viewHolder.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
                    viewHolder.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
                    viewHolder.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);
                    viewHolder.tv_show_all = (TextView)view.findViewById(R.id.id_news_item_tv_show_all);
                    viewHolder.iv_comment = (ImageView)view.findViewById(R.id.id_news_item_iv_comment);
                    viewHolder.tv_audio = (TextView)view.findViewById(R.id.id_news_item_tv_audio);
                    viewHolder.rv_play_audio = (RippleView)view.findViewById(R.id.id_news_item_rv_play_audio);
                    viewHolder.tv_play_audio = (TextView)view.findViewById(R.id.id_news_item_tv_play_audio);

                    viewHolder.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
                    viewHolder.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
                    viewHolder.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

                    viewHolder.mPlayer = new MediaPlayer();
                    view.setTag(viewHolder);
                    break;
                }
        }else{
            view = convertView;
            switch (item_type){
                case 0:
                    viewHolder = (ViewHolder)view.getTag();
                    break;
                case 1:
                    holder1 = (ViewHolder1)view.getTag();
                    break;
                case 2:
                    holder2 = (ViewHolder2)view.getTag();
                    break;
                case 3:
                    holder3 = (ViewHolder3)view.getTag();
                    break;
                case 4:
                    holder4 = (ViewHolder4)view.getTag();
                    break;
                case 5:
                    holder5 = (ViewHolder5)view.getTag();
                    break;
                case 6:
                    holder6 = (ViewHolder6)view.getTag();
                    break;
                default:
                    viewHolder = (ViewHolder)view.getTag();
                    break;
            }
        }

        //设置资源

        //news.setContent(content);
        String content = news.getContent();
        if (content.length()>100){
            content = content.substring(0,100).toString() + "... ";
        }
        switch (item_type){
            case 0:
                if (content.length()>100){
                    viewHolder.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.tv_show_all.setVisibility(View.GONE);
                }
                viewHolder.tv_nickname.setText(news.getNickname());
                viewHolder.tv_time.setText(news.getTime());
                viewHolder.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    viewHolder.tv_audio.setVisibility(View.GONE);
                    viewHolder.rv_play_audio.setVisibility(View.GONE);
                }else{
                    viewHolder.tv_audio.setVisibility(View.VISIBLE);
                    viewHolder.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder finalViewHolder = viewHolder;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }


                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    viewHolder.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        viewHolder.tv_distance.setText("未知距离");
                    }else{
                        viewHolder.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                viewHolder.tv_likeCount.setText(news.getLickCount());
                viewHolder.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(viewHolder.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    viewHolder.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    viewHolder.iv_like.setImageResource(R.mipmap.liked_48);

                }
                viewHolder.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                viewHolder.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                viewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                viewHolder.tv_age.setText(news.getAge()+"");
                viewHolder.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) viewHolder.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else viewHolder.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);
                break;
            case 1:
                if (content.length()>100){
                    holder1.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder1.tv_show_all.setVisibility(View.GONE);
                }
                holder1.tv_nickname.setText(news.getNickname());
                holder1.tv_time.setText(news.getTime());
                holder1.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder1.tv_audio.setVisibility(View.GONE);
                    holder1.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder1.tv_audio.setVisibility(View.VISIBLE);
                    holder1.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder1 finalViewHolder = holder1;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder1.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder1.tv_distance.setText("未知距离");
                    }else{
                        holder1.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder1.tv_likeCount.setText(news.getLickCount());
                holder1.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder1.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder1.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder1.iv_like.setImageResource(R.mipmap.liked_48);

                }
                holder1.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder1.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder1.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder1.tv_age.setText(news.getAge()+"");
                holder1.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder1.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder1.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder1.iv_news_img_iv_1);

                holder1.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context,ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j<news.getNewsimg_list().size();j++){
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath",arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                break;
            case 2:
                if (content.length()>100){
                    holder2.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder2.tv_show_all.setVisibility(View.GONE);
                }
                holder2.tv_nickname.setText(news.getNickname());
                holder2.tv_time.setText(news.getTime());
                holder2.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder2.tv_audio.setVisibility(View.GONE);
                    holder2.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder2.tv_audio.setVisibility(View.VISIBLE);
                    holder2.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder2 finalViewHolder = holder2;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder2.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder2.tv_distance.setText("未知距离");
                    }else{
                        holder2.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder2.tv_likeCount.setText(news.getLickCount());
                holder2.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder2.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder2.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder2.iv_like.setImageResource(R.mipmap.liked_48);

                }
                holder2.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder2.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder2.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder2.tv_age.setText(news.getAge()+"");
                holder2.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder2.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder2.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder2.iv_news_img_iv_1);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(1)).into(holder2.iv_news_img_iv_2);
                holder2.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                holder2.iv_news_img_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 1);
                        context.startActivity(intent);
                    }
                });
                break;
            case 3:
                if (content.length()>100){
                    holder3.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder3.tv_show_all.setVisibility(View.GONE);
                }
                holder3.tv_nickname.setText(news.getNickname());
                holder3.tv_time.setText(news.getTime());
                holder3.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder3.tv_audio.setVisibility(View.GONE);
                    holder3.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder3.tv_audio.setVisibility(View.VISIBLE);
                    holder3.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder3 finalViewHolder = holder3;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder3.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder3.tv_distance.setText("未知距离");
                    }else{
                        holder3.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder3.tv_likeCount.setText(news.getLickCount());
                holder3.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder3.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder3.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder3.iv_like.setImageResource(R.mipmap.liked_48);

                }
                holder3.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder3.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder3.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder3.tv_age.setText(news.getAge()+"");
                holder3.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder3.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder3.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder3.iv_news_img_iv_1);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(1)).into(holder3.iv_news_img_iv_2);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(2)).into(holder3.iv_news_img_iv_3);
                holder3.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                holder3.iv_news_img_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 1);
                        context.startActivity(intent);
                    }
                });
                holder3.iv_news_img_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 2);
                        context.startActivity(intent);
                    }
                });
                break;
            case 4:
                if (content.length()>100){
                    holder4.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder4.tv_show_all.setVisibility(View.GONE);
                }
                holder4.tv_nickname.setText(news.getNickname());
                holder4.tv_time.setText(news.getTime());
                holder4.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder4.tv_audio.setVisibility(View.GONE);
                    holder4.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder4.tv_audio.setVisibility(View.VISIBLE);
                    holder4.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder4 finalViewHolder = holder4;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder4.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder4.tv_distance.setText("未知距离");
                    }else{
                        holder4.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder4.tv_likeCount.setText(news.getLickCount());
                holder4.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder4.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder4.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder4.iv_like.setImageResource(R.mipmap.liked_48);
                }
                holder4.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder4.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder4.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder4.tv_age.setText(news.getAge() + "");
                holder4.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder4.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder4.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder4.iv_news_img_iv_1);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(1)).into(holder4.iv_news_img_iv_2);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(2)).into(holder4.iv_news_img_iv_3);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(3)).into(holder4.iv_news_img_iv_4);
                holder4.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                holder4.iv_news_img_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 1);
                        context.startActivity(intent);
                    }
                });
                holder4.iv_news_img_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 2);
                        context.startActivity(intent);
                    }
                });
                holder4.iv_news_img_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 3);
                        context.startActivity(intent);
                    }
                });
                break;
            case 5:
                if (content.length()>100){
                    holder5.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder5.tv_show_all.setVisibility(View.GONE);
                }
                holder5.tv_nickname.setText(news.getNickname());
                holder5.tv_time.setText(news.getTime());
                holder5.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder5.tv_audio.setVisibility(View.GONE);
                    holder5.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder5.tv_audio.setVisibility(View.VISIBLE);
                    holder5.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder5 finalViewHolder = holder5;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder5.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder5.tv_distance.setText("未知距离");
                    }else{
                        holder5.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder5.tv_likeCount.setText(news.getLickCount());
                holder5.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder5.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder5.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder5.iv_like.setImageResource(R.mipmap.liked_48);

                }
                holder5.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder5.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder5.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder5.tv_age.setText(news.getAge() + "");
                holder5.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder5.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder5.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder5.iv_news_img_iv_1);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(1)).into(holder5.iv_news_img_iv_2);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(2)).into(holder5.iv_news_img_iv_3);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(3)).into(holder5.iv_news_img_iv_4);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(4)).into(holder5.iv_news_img_iv_5);
                holder5.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                holder5.iv_news_img_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 1);
                        context.startActivity(intent);
                    }
                });
                holder5.iv_news_img_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 2);
                        context.startActivity(intent);
                    }
                });
                holder5.iv_news_img_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 3);
                        context.startActivity(intent);
                    }
                });
                holder5.iv_news_img_iv_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 4);
                        context.startActivity(intent);
                    }
                });
                break;
            case 6:
                if (content.length()>100){
                    holder6.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    holder6.tv_show_all.setVisibility(View.GONE);
                }
                holder6.tv_nickname.setText(news.getNickname());
                holder6.tv_time.setText(news.getTime());
                holder6.tv_content.setText(content);

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    holder6.tv_audio.setVisibility(View.GONE);
                    holder6.rv_play_audio.setVisibility(View.GONE);
                }else{
                    holder6.tv_audio.setVisibility(View.VISIBLE);
                    holder6.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder6 finalViewHolder = holder6;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    holder6.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        holder6.tv_distance.setText("未知距离");
                    }else{
                        holder6.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                holder6.tv_likeCount.setText(news.getLickCount());
                holder6.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(holder6.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    holder6.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    holder6.iv_like.setImageResource(R.mipmap.liked_48);

                }
                holder6.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder6.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                holder6.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                holder6.tv_age.setText(news.getAge() + "");
                holder6.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) holder6.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else holder6.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);

                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder6.iv_news_img_iv_1);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(1)).into(holder6.iv_news_img_iv_2);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(2)).into(holder6.iv_news_img_iv_3);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(3)).into(holder6.iv_news_img_iv_4);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(4)).into(holder6.iv_news_img_iv_5);
                Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(5)).into(holder6.iv_news_img_iv_6);
                holder6.iv_news_img_iv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                });
                holder6.iv_news_img_iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 1);
                        context.startActivity(intent);
                    }
                });
                holder6.iv_news_img_iv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 2);
                        context.startActivity(intent);
                    }
                });
                holder6.iv_news_img_iv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 3);
                        context.startActivity(intent);
                    }
                });
                holder6.iv_news_img_iv_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 4);
                        context.startActivity(intent);
                    }
                });
                holder6.iv_news_img_iv_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ShowNewsImageActivity.class);
                        String[] arr = new String[news.getNewsimg_list().size()];
                        for (int j = 0; j < news.getNewsimg_list().size(); j++) {
                            arr[j] = WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(j);
                        }
                        intent.putExtra("listpath", arr);
                        intent.putExtra("position", 5);
                        context.startActivity(intent);
                    }
                });
                break;
            default:
                if (content.length()>100){
                    viewHolder.tv_show_all.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.tv_show_all.setVisibility(View.GONE);
                }
                viewHolder.tv_nickname.setText(news.getNickname());
                viewHolder.tv_time.setText(news.getTime());
                viewHolder.tv_content.setText(news.getContent());

                if (news.getNews_voice()==null || news.getNews_voice().equals("")){
                    viewHolder.tv_audio.setVisibility(View.GONE);
                    viewHolder.rv_play_audio.setVisibility(View.GONE);
                }else{
                    viewHolder.tv_audio.setVisibility(View.VISIBLE);
                    viewHolder.rv_play_audio.setVisibility(View.VISIBLE);
                    //有录音的动态

                    final ViewHolder finalViewHolder = viewHolder;
                    final String news_voice_url = news.getNews_voice();
                    final String news_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/news_voice/"+ news.getId() +".arm";
                    FileInputStream fin = null;
                    String filename = news_voice_local_path;
                    Log.d("CommentItemBase", filename);
                    File file = new File(filename);
                    //判断文件是否存在，不存在就去下载
                    if (!file.exists()) {
                        //下载语音
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("user_id", SharedPreferenceUtil.getUserId())
                                .build();
                        Request request = new Request.Builder()
                                .url(WebUtil.HTTP_ADDRESS + news_voice_url)
                                .post(formBody)
                                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                .build();
                        Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + news_voice_url);
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    System.out.println(response.code());
                                    byte[] result = response.body().bytes();

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
                                        fos.write(result);
                                        fos.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        //不干嘛
                    }

                    finalViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {
                            if (finalViewHolder.tv_play_audio.getText().toString().equals("播放")){
                                //播放录音
                                finalViewHolder.tv_play_audio.setText("停止");
                                finalViewHolder.mPlayer = new MediaPlayer();
                                try{
                                    //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                                    finalViewHolder.mPlayer.setDataSource(news_voice_local_path);
                                    finalViewHolder.mPlayer.prepare();
                                    finalViewHolder.mPlayer.start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean flag = true;
                                            try {
                                                while (flag){
                                                    Thread.sleep(1000);
                                                    if (finalViewHolder.mPlayer !=null){
                                                        if (!finalViewHolder.mPlayer.isPlaying()) flag = false;
                                                    }
                                                }
                                                handler_two.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalViewHolder.tv_play_audio.setText("播放");
                                                    }
                                                });
                                            }catch (InterruptedException e){
                                            }
                                        }
                                    }).start();


                                }catch(IOException e){
                                    Log.e("NewsItemBase",news_voice_local_path);
                                    Log.e("CommentItemBase","播放失败");
                                }
                            }else if (finalViewHolder.tv_play_audio.getText().toString().equals("停止")){
                                //停止播放
                                finalViewHolder.tv_play_audio.setText("播放");
                                finalViewHolder.mPlayer.stop();
                                //mPlayer = null;
                            }
                        }
                    });





                    //有语音动态介绍位置
                }

                if(type.equals(WebUtil.NEWS_TYPE_NEARBY)){
                    viewHolder.tv_distance.setText("距离 " + news.getDistance()+" km");
                }else if(type.equals(WebUtil.NEWS_TYPE_HOT)){
                    if (news.getDistance().equals("-1.0")){
                        viewHolder.tv_distance.setText("未知距离");
                    }else{
                        viewHolder.tv_distance.setText(news.getCity()+" " + news.getDistance()+" km");
                    }

                }
                viewHolder.tv_likeCount.setText(news.getLickCount());
                viewHolder.tv_commentCount.setText(news.getCommentCount());
                Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                        .into(viewHolder.iv_userhead);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + news.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);

                if (news.getIsLiked().equals("0")){
                    viewHolder.iv_like.setImageResource(R.mipmap.like_48);

                }else{
                    viewHolder.iv_like.setImageResource(R.mipmap.liked_48);

                }
                viewHolder.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_like_news");
                        broadintent.putExtra("position",position);
                        if (news.getIsLiked().equals("0")){
                            //holder1.iv_like.setImageResource(R.mipmap.like_48);
                            broadintent.putExtra("islike",1);
                        }else{
                            //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                            broadintent.putExtra("islike",0);
                        }
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                viewHolder.iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent broadintent = new Intent("com.allever.action_update_comment_news");
                        broadintent.putExtra("position",position);
                        broadintent.putExtra("news_from", list_newsItem.get(position).getNews_from());
                        context.sendBroadcast(broadintent);
                    }
                });
                viewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        Intent intent;
                        switch (id) {
                            case R.id.id_news_item_circle_iv_userhead:
                                intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                                intent.putExtra("username", list_newsItem.get(position).getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                                break;
                        }
                    }
                });

                viewHolder.tv_age.setText(news.getAge()+"");
                viewHolder.tv_sex.setText(news.getSex());
                if (news.getSex().equals("男")) viewHolder.ll_sex.setBackgroundResource(R.drawable.color_blue_bg_round);
                else viewHolder.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);
                break;
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_newsItem.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        switch (list_newsItem.get(position).getNewsimg_list().size()){
            case 0:return 0;
            case 1:return 1;
            case 2:return 2;
            case 3:return 3;
            case 4:return 4;
            case 5:return 5;
            case 6:return 6;
            default: return 0;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    private static class ViewHolder{
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        TextView tv_show_all;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        MediaPlayer mPlayer;
    }

    private static class ViewHolder1{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;

        MediaPlayer mPlayer;
    }

    private static class ViewHolder2{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;
        ImageView iv_news_img_iv_2;

        MediaPlayer mPlayer;
    }

    private static class ViewHolder3{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;
        ImageView iv_news_img_iv_2;
        ImageView iv_news_img_iv_3;

        MediaPlayer mPlayer;
    }

    private static class ViewHolder4{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;
        ImageView iv_news_img_iv_2;
        ImageView iv_news_img_iv_3;
        ImageView iv_news_img_iv_4;

        MediaPlayer mPlayer;

    }

    private static class ViewHolder5{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;
        ImageView iv_news_img_iv_2;
        ImageView iv_news_img_iv_3;
        ImageView iv_news_img_iv_4;
        ImageView iv_news_img_iv_5;

        MediaPlayer mPlayer;
    }

    private static class ViewHolder6{
        TextView tv_show_all;
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        TextView tv_distance;
        TextView tv_commentCount;
        TextView tv_likeCount;
        ImageView iv_like;
        TextView tv_sex;
        TextView tv_age;
        LinearLayout ll_sex;
        ImageView iv_comment;
        TextView tv_audio;
        RippleView rv_play_audio;
        TextView tv_play_audio;

        ImageView iv_news_img_iv_1;
        ImageView iv_news_img_iv_2;
        ImageView iv_news_img_iv_3;
        ImageView iv_news_img_iv_4;
        ImageView iv_news_img_iv_5;
        ImageView iv_news_img_iv_6;

        MediaPlayer mPlayer;
    }

    class LikeRoot{
        boolean success;
        String message;
        int likeCount;
    }
}
