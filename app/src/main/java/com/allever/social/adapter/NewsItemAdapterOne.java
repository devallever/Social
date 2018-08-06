package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.UserDataActivity;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/11.
 */
public class NewsItemAdapterOne extends ArrayAdapter<NewsItem> {
    private int news_item_res_id;
    private List<NewsItem> list_newsItem;
    private Context context;
    private OkHttpClient okHttpClient;
    private String type;
    private int position;

    private Handler handler;
    private Gson gson;
    private String result;
    private boolean isliked;
    private Root root;
    private int likeCount;
    public NewsItemAdapterOne(Context context, int newsItemResId, List<NewsItem> newsItem_list,String newsType){
       super(context, newsItemResId,newsItem_list);
        this.list_newsItem = newsItem_list;
        news_item_res_id = newsItemResId;
        this.context = context;
        okHttpClient = new OkHttpClient();
        this.type = newsType;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final NewsItem news = (NewsItem)getItem(position);
        if(news.getIsLiked().equals("1")){
            isliked = true;
        }else{
            isliked = false;
        }
        likeCount = Integer.valueOf(news.getLickCount());
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(news_item_res_id, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_news_item_circle_iv_userhead);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_news_item_tv_nickname);
            viewHolder.tv_time = (TextView)view.findViewById(R.id.id_news_item_tv_time);
            viewHolder.tv_content = (TextView)view.findViewById(R.id.id_news_item_tv_content);

            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_news_item_tv_distance);
            viewHolder.tv_likeCount = (TextView)view.findViewById(R.id.id_news_item_tv_like_count);
            viewHolder.tv_commentCount = (TextView)view.findViewById(R.id.id_news_item_tv_comment_count);
            viewHolder.iv_like = (ImageView)view.findViewById(R.id.id_news_item_iv_like);

            viewHolder.ll_sex = (LinearLayout)view.findViewById(R.id.id_news_item_ll_sex);
            viewHolder.tv_sex = (TextView)view.findViewById(R.id.id_news_item_tv_sex);
            viewHolder.tv_age = (TextView)view.findViewById(R.id.id_news_item_tv_age);

            viewHolder.iv_news_img_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        //viewHolder.iv_userhead.setImageResource(R.mipmap.winchen);//静态

        viewHolder.tv_nickname.setText(news.getNickname());
        viewHolder.tv_time.setText(news.getTime());
        viewHolder.tv_content.setText(news.getContent());
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
        viewHolder.tv_commentCount.setText("评论|" + news.getCommentCount());
        Log.d("NewsItemAdapter2", WebUtil.HTTP_ADDRESS + news.getUser_head_path());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + news.getUser_head_path())
                .into(viewHolder.iv_userhead);

        if (news.getIsLiked().equals("0")){
            viewHolder.iv_like.setImageResource(R.mipmap.like_48);

        }else{
            viewHolder.iv_like.setImageResource(R.mipmap.liked_48);

        }
        viewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                Intent intent;
                switch (id) {
                    case R.id.id_news_item_circle_iv_userhead:
                        intent = new Intent(MyApplication.getContext(), UserDataActivity.class);
                        intent.putExtra("friend_id", list_newsItem.get(position).getUsername());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getContext().startActivity(intent);
                        break;
                }
            }
        });

        viewHolder.tv_age.setText(news.getAge()+"");
        viewHolder.tv_sex.setText(news.getSex());
        if (news.getSex().equals("男")) viewHolder.ll_sex.setBackgroundColor(context.getResources().getColor(R.color.colorBlue_300));
        else viewHolder.ll_sex.setBackgroundColor(context.getResources().getColor(R.color.colorPink_300));




        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_LIKE:
                        //handleLike(msg,viewHolder);
                        break;
                }

            }
        };

        Glide.with(context).load(WebUtil.HTTP_ADDRESS + list_newsItem.get(position).getNewsimg_list().get(0)).into(viewHolder.iv_news_img_1);

        return view;

//        super.getView(position,convertView,parent);
//        View view;
//        final ViewHolder viewHolder;
//        if(convertView == null){
//            view = LayoutInflater.from(getContext()).inflate(news_item_res_id, parent,false);
//            viewHolder = new ViewHolder();
//            viewHolder.iv_news_img_1 = (ImageView)view.findViewById(R.id.id_news_item_iv_1);
//            view.setTag(viewHolder);
//        }else{
//            view = convertView;
//            viewHolder = (ViewHolder)view.getTag();
//        }
//
//        Glide.with(context).load(WebUtil.HTTP_ADDRESS + list_newsItem.get(position).getNewsimg_list().get(0)).into(viewHolder.iv_news_img_1);
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
        LinearLayout ll_sex;

        ImageView iv_news_img_1;
    }

    class Root{
        boolean success;
        String message;
        int likeCount;
    }
}
