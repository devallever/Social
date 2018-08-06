package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.MyGroupItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/12.
 * 我的群组列表项适配器
 */
public class MyGroupItemArrayAdapter extends ArrayAdapter<MyGroupItem> {
    private Context context;
    private List<MyGroupItem> list_MygroupItem;
    private int resid;
    public MyGroupItemArrayAdapter(Context context,int resid,List<MyGroupItem> list_groupItem){
        super(context,resid,list_groupItem);
        this.context = context;
        this.list_MygroupItem = list_groupItem;
        this.resid = resid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyGroupItem myGroupItem = list_MygroupItem.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resid, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_group_img = (CircleImageView)view.findViewById(R.id.id_my_group_item_iv_group_img);
            viewHolder.tv_groupname = (TextView)view.findViewById(R.id.id_my_group_item_tv_groupname);
            viewHolder.tv_description = (TextView)view.findViewById(R.id.id_my_group_item_tv_description);
            viewHolder.tv_checked = (TextView)view.findViewById(R.id.id_my_group_item_tv_checked);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + myGroupItem.getGroup_img())
                .into(viewHolder.iv_group_img);
        viewHolder.tv_groupname.setText(myGroupItem.getGroupname());
        viewHolder.tv_description.setText(myGroupItem.getDescription());
        if ((myGroupItem.getIs_my_group()==1)&& (myGroupItem.getState()==0)) viewHolder.tv_checked.setVisibility(View.VISIBLE);
        return view;
    }

    private class ViewHolder{
        CircleImageView iv_group_img;
        TextView tv_groupname;
        TextView tv_description;
        TextView tv_checked;
    }
}
