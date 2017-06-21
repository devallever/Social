package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.activity.ShowBigImageActvity;
import com.allever.social.pojo.FollowUserItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by XM on 2016/7/27.
 */
public class FollowItemBaseAdapter extends BaseAdapter {
    private List<FollowUserItem> list_follwo_user_item;
    private LayoutInflater inflater;
    private Context context;

    public FollowItemBaseAdapter(Context context,List<FollowUserItem> list_follwo_user_item){
        this.context = context;
        this.list_follwo_user_item = list_follwo_user_item;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final  FollowUserItem followUserItem = list_follwo_user_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = inflater.inflate(R.layout.follow_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (ImageView)view.findViewById(R.id.id_follow_user_item_iv_head);
            viewHolder.iv_vip_logo = (ImageView)view.findViewById(R.id.id_follow_user_item_iv_vip_logo);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_follow_user_item_tv_nickname);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_follow_user_item_tv_distance);
            viewHolder.tv_signature= (TextView)view.findViewById(R.id.id_follow_user_item_tv_signature);
            viewHolder.tv_sex_age = (TextView)view.findViewById(R.id.id_follow_user_item_tv_sex_age);
            viewHolder.tv_occupation = (TextView)view.findViewById(R.id.id_follow_user_item_tv_occupation);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }


        if(followUserItem.getIs_vip()==1){
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.colorRed_500));
            viewHolder.iv_vip_logo.setVisibility(View.VISIBLE);
        } else{
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.black_deep));
            viewHolder.iv_vip_logo.setVisibility(View.GONE);
        }
        viewHolder.tv_nickname.setText(followUserItem.getNickname());
        viewHolder.tv_distance.setText(followUserItem.getDistance() + " km");
        viewHolder.tv_sex_age.setText(followUserItem.getSex() + " " + followUserItem.getAge());
        viewHolder.tv_occupation.setText(followUserItem.getOccupation());
        viewHolder.tv_signature.setText(followUserItem.getSignature());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + followUserItem.getUser_head_path())
                .into(viewHolder.iv_head);


        if (followUserItem.getSex().equals("男")){
            viewHolder.tv_sex_age.setBackgroundResource(R.drawable.color_indigo_bg_round);
        }else{
            viewHolder.tv_sex_age.setBackgroundResource(R.drawable.color_pink_bg_round);
        }

        viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, ShowBigImageActvity.class);
                intent.putExtra("image_path", WebUtil.HTTP_ADDRESS + followUserItem.getUser_head_path());
                context.startActivity(intent);
            }
        });

        switch (followUserItem.getOccupation()){
            case "学生":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("学生");
                break;
            case "IT":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("IT");
                break;
            case "农业":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("保险");
                break;
            case "制造":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("制造");
                break;
            case "商业":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_blue_bg_round);
                //viewHolder.tv_occupation.setText("商务");
                break;
            case "模特":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("交通");
                break;
            case "文化":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_purple_bg_round);
                //viewHolder.tv_occupation.setText("传媒");
                break;
            case "教育":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("教育");
                break;
            case "医疗":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_pink_bg_round);
                //viewHolder.tv_occupation.setText("娱乐");
                break;
            case "艺术":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("公共");
                break;
            case "金融":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "行政":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_brown_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "空姐":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "法律":
                viewHolder.tv_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
        }
        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_follwo_user_item.get(i);
    }

    @Override
    public int getCount() {
        return list_follwo_user_item.size();
    }

    private class ViewHolder{
        ImageView iv_head;
        ImageView iv_vip_logo;
        TextView tv_nickname;
        TextView tv_distance;
        TextView tv_signature;
        TextView tv_sex_age;
        TextView tv_occupation;
    }
}
