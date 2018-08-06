package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.UserDataActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.pojo.CommentItem;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/19.
 * 评论列表项适配器
 */
public class CommentItemAdapter extends ArrayAdapter<CommentItem> {
    private int comment_item_res_id;
    private List<CommentItem> list_commentItem;
    private Context context;
    private OkHttpClient okHttpClient;

    public CommentItemAdapter(Context context, int commentItemResId, List<CommentItem> commentItem_list){
        super(context,commentItemResId,commentItem_list);
        this.comment_item_res_id = commentItemResId;
        this.list_commentItem = commentItem_list;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CommentItem comment = (CommentItem)getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(comment_item_res_id, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_comment_item_circle_iv_userhead);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_comment_item_tv_nickname);
            viewHolder.tv_time = (TextView)view.findViewById(R.id.id_comment_item_tv_time);
            viewHolder.tv_content = (TextView)view.findViewById(R.id.id_comment_item_tv_content);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.iv_userhead.setImageResource(R.mipmap.winchen);//静态

        viewHolder.tv_nickname.setText(comment.getNickname());
        viewHolder.tv_time.setText(comment.getTime());
        viewHolder.tv_content.setText(comment.getContent());

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + comment.getUser_head_path())
                .into(viewHolder.iv_userhead);
        //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + comment.getUser_head_path()).resize(500, 500).into(viewHolder.iv_userhead);
        viewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                Intent intent;
                switch (id) {
                    case R.id.id_comment_item_circle_iv_userhead:
                        intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                        intent.putExtra("username", list_commentItem.get(position).getUsername());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getContext().startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }


    private static class ViewHolder{
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
    }
}
