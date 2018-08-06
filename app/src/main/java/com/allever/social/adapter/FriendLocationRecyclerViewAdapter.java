package com.allever.social.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.allever.social.R;
import com.allever.social.pojo.FriendLocationItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Allever on 2016/11/5.
 */

public class FriendLocationRecyclerViewAdapter extends RecyclerView.Adapter<FriendLocationRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private List<FriendLocationItem> list_friend_location_items;

    public FriendLocationRecyclerViewAdapter(Context context, List<FriendLocationItem> list_friend_location_items){
        this.context = context;
        this.list_friend_location_items = list_friend_location_items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_location_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FriendLocationItem friendLocationItem = list_friend_location_items.get(position);
        Glide.with(context).load(WebUtil.HTTP_ADDRESS + friendLocationItem.getUser_head_path()).into(holder.iv_head);
    }

    @Override
    public int getItemCount() {
        return list_friend_location_items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_head;
        public MyViewHolder(View itemView){
            super(itemView);
            iv_head = (ImageView)itemView.findViewById(R.id.id_friend_location_item_iv_head);
        }
    }
}
