package com.allever.social.foundModule.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.foundModule.bean.NewsBeen;
import com.allever.social.utils.WebUtil;
import com.baidu.mapapi.map.Text;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Allever on 2016/12/3.
 */

public class NewsListBaseAdapter  extends RecyclerView.Adapter<NewsListBaseAdapter.NewsListViewHolder>{

    private List<NewsBeen> list_news;
    private Context context;

    public NewsListBaseAdapter(Context context, List<NewsBeen> list_news){
        this.context = context;
        this.list_news = list_news;
    }

    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.news_item_layout,parent,false);
        NewsListViewHolder viewHolder = new NewsListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsListViewHolder holder, final int position) {
        final NewsBeen news = list_news.get(position);

        holder.tv_content.setText(news.getContent());

        if (news.getNewsimg_list().size()==0 ){
            Glide.with(context).load(news.getUser_head_path()).into(holder.iv_news_img);
        }else{
            Glide.with(context).load(WebUtil.HTTP_ADDRESS + news.getNewsimg_list().get(0)).into(holder.iv_news_img);
        }

        if (news.getIsLiked() == 1){
            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.liked_red_40));
        }else{
            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.like_white_40));
        }
        holder.tv_like_count.setText(news.getLickCount() + "");

        holder.tv_img_count.setText(news.getNewsimg_list().size() + "");

        Glide.with(context).load(news.getUser_head_path()).into(holder.iv_head);

        holder.tv_nickname.setText(news.getNickname());

        holder.tv_img_count.setText(news.getNewsimg_list().size()+"");

        holder.tv_time.setText(news.getTime());

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Click", Toast.LENGTH_LONG).show();
                Intent broadintent = new Intent("com.allever.action_update_like_news");
                broadintent.putExtra("position",position);
                if (news.getIsLiked() == 0){
                    //holder1.iv_like.setImageResource(R.mipmap.like_48);
                    broadintent.putExtra("islike",1);
                }else{
                    //holder1.iv_like.setImageResource(R.mipmap.liked_48);
                    broadintent.putExtra("islike",0);
                }
                broadintent.putExtra("news_from", list_news.get(position).getNews_from());
                context.sendBroadcast(broadintent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list_news.size();
    }

    class NewsListViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_news_img;
        CircleImageView iv_head;
        ImageView iv_like;
        ImageView iv_img_count;

        TextView tv_content;
        TextView tv_like_count;
        TextView tv_img_count;
        TextView tv_nickname;
        TextView tv_time;

        public NewsListViewHolder(View itemView){
            super(itemView);
            iv_news_img = (ImageView) itemView.findViewById(R.id.id_news_item_iv_img);
            iv_head = (CircleImageView) itemView.findViewById(R.id.id_news_item_iv_head);
            iv_like = (ImageView) itemView.findViewById(R.id.id_news_item_iv_like);
            iv_img_count = (ImageView)itemView.findViewById(R.id.id_news_item_iv_img_count);
            tv_content = (TextView) itemView.findViewById(R.id.id_news_item_tv_content);
            tv_like_count = (TextView)itemView.findViewById(R.id.id_news_item_tv_like_count);
            tv_img_count = (TextView)itemView.findViewById(R.id.id_news_item_tv_img_count);
            tv_nickname = (TextView)itemView.findViewById(R.id.id_news_item_tv_nickname);
            tv_time = (TextView)itemView.findViewById(R.id.id_news_item_tv_time);
        }
    }
}
