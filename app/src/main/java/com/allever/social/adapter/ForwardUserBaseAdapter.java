package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.FollowUserItem;
import com.allever.social.pojo.ForwardUserItem;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MySquareImageView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by XM on 2016/8/10.
 */
public class ForwardUserBaseAdapter extends BaseAdapter {
    private List<ForwardUserItem> list_forward_user;
    private Context context;
    private LayoutInflater inflater;


    public ForwardUserBaseAdapter(Context context,List<ForwardUserItem> list_forward_user){
        this.context = context;
        this.list_forward_user = list_forward_user;
        inflater = (LayoutInflater.from(context));
    }



    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        final ForwardUserItem forwardUserItem = list_forward_user.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = inflater.inflate(R.layout.forward_user_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (MySquareImageView)view.findViewById(R.id.id_forward_user_item_iv_head);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_forward_user_item_tv_nickname);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_nickname.setText(forwardUserItem.getNickname());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + forwardUserItem.getUser_head_path())
                .into(viewHolder.iv_head);

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_forward_user.get(i);
    }

    @Override
    public int getCount() {
        return list_forward_user.size();
    }

    class ViewHolder{
        MySquareImageView iv_head;
        TextView tv_nickname;
    }
}
