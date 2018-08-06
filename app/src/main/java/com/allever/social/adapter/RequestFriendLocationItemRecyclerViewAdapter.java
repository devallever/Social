package com.allever.social.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.RequestFriendLocationItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Allever on 2016/11/5.
 */

public class RequestFriendLocationItemRecyclerViewAdapter extends RecyclerView.Adapter<RequestFriendLocationItemRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private List<RequestFriendLocationItem> requestFriendLocationItemList;
    public RequestFriendLocationItemRecyclerViewAdapter(Context context, List<RequestFriendLocationItem> requestFriendLocationItemList){
        this.context = context;
        this.requestFriendLocationItemList = requestFriendLocationItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_friend_location_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RequestFriendLocationItem requestFriendLocationItem = requestFriendLocationItemList.get(position);
        holder.tv_nickname.setText(requestFriendLocationItem.getNickname());
        Glide.with(context).load(WebUtil.HTTP_ADDRESS + requestFriendLocationItem.getHead_path()).into(holder.iv_head);
    }

    @Override
    public int getItemCount() {
        return requestFriendLocationItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_nickname;
        ImageView iv_head;
        public MyViewHolder(View itemView){
            super(itemView);
            tv_nickname = (TextView)itemView.findViewById(R.id.id_request_friend_location_item_tv_nickname);
            iv_head = (ImageView)itemView.findViewById(R.id.id_request_friend_location_item_iv_head);
        }
    }
}
