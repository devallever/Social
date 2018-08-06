package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.MyRecruitItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/21.
 */
public class MyRecruitItemBaseAdapter extends BaseAdapter {

    private List<MyRecruitItem> list_myrecruitItem;
    private Context context;
    private LayoutInflater inflater;

    public MyRecruitItemBaseAdapter(Context context,List<MyRecruitItem> list_myrecruitItem){
        this.context = context;
        this.list_myrecruitItem = list_myrecruitItem;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        MyRecruitItem myRecruitItem = list_myrecruitItem.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.my_recruit_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_my_recruit_item_iv_head);
            viewHolder.tv_companyname = (TextView)view.findViewById(R.id.id_my_recruit_item_tv_companyname);
            viewHolder.tv_requirement = (TextView)view.findViewById(R.id.id_my_recruit_item_tv_requirement);
            viewHolder.tv_date = (TextView)view.findViewById(R.id.id_my_recruit_item_tv_date);
            viewHolder.iv_delete = (ImageView)view.findViewById(R.id.id_my_recruit_item_iv_delete);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + myRecruitItem.getUser_head_path())
                .into(viewHolder.iv_head);
        viewHolder.tv_companyname.setText(myRecruitItem.getCompanyname());
        viewHolder.tv_requirement.setText(myRecruitItem.getRequirement());
        viewHolder.tv_date.setText(myRecruitItem.getDate());


        return view;
    }

    @Override
    public int getCount() {
        return list_myrecruitItem.size();
    }

    @Override
    public Object getItem(int i) {
        return list_myrecruitItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        CircleImageView iv_head;
        TextView tv_companyname;
        TextView tv_requirement;
        TextView tv_date;
        ImageView iv_delete;
    }

}
