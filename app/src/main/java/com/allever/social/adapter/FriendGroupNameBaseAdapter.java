package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.FriendGroupNameItem;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by XM on 2016/6/15.
 */
public class FriendGroupNameBaseAdapter extends BaseAdapter {
    private List<FriendGroupNameItem> list_friendgroupnameItem;
    private Context context;
    private LayoutInflater inflater;

    public FriendGroupNameBaseAdapter(Context context, List<FriendGroupNameItem> list_friendgroupnameItem){
        this.context = context;
        this.list_friendgroupnameItem = list_friendgroupnameItem;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        FriendGroupNameItem friendGroupNameItem = list_friendgroupnameItem.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.friendgroup_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_show_menu = (ImageView)view.findViewById(R.id.id_friendgroup_list_item_iv_show_menu);
            viewHolder.tv_friendgroup_name = (TextView)view.findViewById(R.id.id_friendgroup_list_item_tv_friendgroup_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.tv_friendgroup_name.setText(friendGroupNameItem.getFriendgroup_name());
        return view;
    }

    class ViewHolder{
        TextView tv_friendgroup_name;
        ImageView iv_show_menu;
    }

    @Override
    public Object getItem(int i) {
        return list_friendgroupnameItem.get(i);
    }

    @Override
    public int getCount() {
        return list_friendgroupnameItem.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}

