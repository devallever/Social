package com.allever.social.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.allever.social.R;
import com.allever.social.pojo.FriendLocationItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Allever on 2016/11/5.
 */

public class FriendLocationItemBaseAdapter extends BaseAdapter {

    public FriendLocationItemBaseAdapter(Context context,List<FriendLocationItem> locationItems){
        this.context = context;
        this.locationItems = locationItems;
    }

    private List<FriendLocationItem> locationItems;
    private Context context;

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        FriendLocationItem friendLocationItem = locationItems.get(i);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.friend_location_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (ImageView)view.findViewById(R.id.id_friend_location_item_iv_head);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context).load(WebUtil.HTTP_ADDRESS + friendLocationItem.getUser_head_path()).into(viewHolder.iv_head);

        return view;
    }

    @Override
    public Object getItem(int i) {
        return locationItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return locationItems.size();
    }


    class ViewHolder{
        ImageView iv_head;
    }
}
