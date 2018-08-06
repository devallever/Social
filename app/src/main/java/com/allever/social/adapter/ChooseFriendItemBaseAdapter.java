package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.activity.ChooseConstellationActivity;
import com.allever.social.pojo.ChooseFriendItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/7/18.
 */
public class ChooseFriendItemBaseAdapter extends BaseAdapter {
    private List<ChooseFriendItem> list_chooseFriendItem;
    private Context context;
    private LayoutInflater inflater;
    public ChooseFriendItemBaseAdapter(Context context ,List<ChooseFriendItem> list_chooseFriendItem){
        this.context = context;
        this.list_chooseFriendItem = list_chooseFriendItem;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ChooseFriendItem chooseFriendItem = list_chooseFriendItem.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = inflater.inflate(R.layout.choose_friend_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_choose_friend_item_iv_head);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_choose_friend_item_tv_nickname);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        //设置资源
        Glide.with(context).load(WebUtil.HTTP_ADDRESS + chooseFriendItem.getUser_head_path()).into(viewHolder.iv_head);
        viewHolder.tv_nickname.setText(chooseFriendItem.getNickname());


        return view;
    }

    @Override
    public int getCount() {
        return list_chooseFriendItem.size();
    }

    @Override
    public Object getItem(int i) {
        return list_chooseFriendItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    class ViewHolder{
        CircleImageView iv_head;
        TextView tv_nickname;
    }
}
