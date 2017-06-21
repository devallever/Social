package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.FriendItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.reflect.Array;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/21.
 * 好友列表项适配器
 */
public class FriendItemAdapter extends ArrayAdapter<FriendItem> {
    private Context context;
    private int friendItemResId;
    private List<FriendItem> list_friends;

    public FriendItemAdapter(Context context, int friendItemResId, List<FriendItem> listFriend){
        super(context,friendItemResId,listFriend);
        this.context = context;
        this.friendItemResId = friendItemResId;
        this.list_friends = listFriend;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        FriendItem friendItem = (FriendItem)getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(friendItemResId, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_friend_item_circle_iv_userhead);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_friend_item_tv_nickname);
            viewHolder.tv_signature = (TextView)view.findViewById(R.id.id_friend_item_tv_signature);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        //viewHolder.iv_head.setImageResource(R.mipmap.winchen);//静态

        viewHolder.tv_nickname.setText(friendItem.getNickname());
        viewHolder.tv_nickname.setText(friendItem.getNickname());
        viewHolder.tv_signature.setText(friendItem.getSignature());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + friendItem.getUser_head_path())
                .into(viewHolder.iv_head);
       //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + friendItem.getUser_head_path()).into(viewHolder.iv_head);
        return view;
    }

    private class ViewHolder{
        private CircleImageView iv_head;
        private TextView tv_nickname;
        private TextView tv_signature;
    }
}
