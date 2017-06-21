package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.activity.ShowBigImageActvity;
import com.allever.social.pojo.GroupItem;
import com.allever.social.pojo.VisitedUserItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by XM on 2016/7/10.
 */
public class VisitedUserItemBaseAdapter extends BaseAdapter {
    private List<VisitedUserItem> list_visited_user_item;
    private LayoutInflater inflater;
    private Context context;

    public VisitedUserItemBaseAdapter(Context context, List<VisitedUserItem> list_visited_user_item){
        this.context = context;
        this.list_visited_user_item = list_visited_user_item;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final VisitedUserItem visitedUserItem = list_visited_user_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = inflater.inflate(R.layout.visited_user_item_layout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (ImageView)view.findViewById(R.id.id_visited_user_item_iv_head);
            viewHolder.iv_vip_logo = (ImageView)view.findViewById(R.id.id_visited_user_item_iv_vip_logo);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_visited_user_item_tv_nickname);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_visited_user_item_tv_distance);
            viewHolder.tv_visited_date = (TextView)view.findViewById(R.id.id_visited_user_item_tv_visited_date);
            viewHolder.tv_sex_age = (TextView)view.findViewById(R.id.id_visited_user_item_tv_sex_age);
            viewHolder.tv_constellation = (TextView)view.findViewById(R.id.id_visited_user_item_tv_constellation);
            viewHolder.tv_occupation = (TextView)view.findViewById(R.id.id_visited_user_item_tv_occupation);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }


        if(visitedUserItem.getIs_vip()==1){
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.colorRed_500));
            viewHolder.iv_vip_logo.setVisibility(View.VISIBLE);
        } else{
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.black_deep));
            viewHolder.iv_vip_logo.setVisibility(View.GONE);
        }
        viewHolder.tv_nickname.setText(visitedUserItem.getNickname());
        viewHolder.tv_distance.setText(visitedUserItem.getDistance() + " km");
        viewHolder.tv_visited_date.setText(visitedUserItem.getDate());
        viewHolder.tv_sex_age.setText(visitedUserItem.getSex() + " " + visitedUserItem.getAge());
        viewHolder.tv_constellation.setText(visitedUserItem.getConstellation());
        viewHolder.tv_occupation.setText(visitedUserItem.getOccupation());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + visitedUserItem.getUser_head_path())
                .into(viewHolder.iv_head);


        if (visitedUserItem.getSex().equals("男")){
            viewHolder.tv_sex_age.setBackgroundResource(R.drawable.color_indigo_bg_round);
        }else{
            viewHolder.tv_sex_age.setBackgroundResource(R.drawable.color_pink_bg_round);
        }

        viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, ShowBigImageActvity.class);
                intent.putExtra("image_path", WebUtil.HTTP_ADDRESS + visitedUserItem.getUser_head_path());
                context.startActivity(intent);
            }
        });

        switch (visitedUserItem.getOccupation()){
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

//        switch (visitedUserItem.getConstellation()){
//            case "白羊座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorGray_300));
//                viewHolder.tv_constellation.setText("白羊");
//                break;
//            case "金牛座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("金牛");
//                break;
//            case "双子座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorRed_300));
//                viewHolder.tv_constellation.setText("双子");
//                break;
//            case "巨蟹座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("巨蟹");
//                break;
//            case "狮子座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("狮子");
//                break;
//            case "处女座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorPink_300));
//                viewHolder.tv_constellation.setText("处女");
//                break;
//            case "天秤座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorGreen_300));
//                viewHolder.tv_constellation.setText("天秤");
//                break;
//            case "天蝎座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorPurple_300));
//                viewHolder.tv_constellation.setText("天蝎");
//                break;
//            case "射手座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorBlue_300));
//                viewHolder.tv_constellation.setText("射手");
//                break;
//            case "魔蝎座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorIndigo_300));
//                viewHolder.tv_constellation.setText("魔蝎");
//                break;
//            case "水瓶座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorBlue_300));
//                viewHolder.tv_constellation.setText("水瓶");
//                break;
//            case "双鱼座":
//                viewHolder.tv_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("双鱼");
//                break;
//        }



        return view;
    }

    @Override
    public int getCount() {
        return list_visited_user_item.size();
    }

    @Override
    public Object getItem(int i) {
        return list_visited_user_item.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView iv_head;
        ImageView iv_vip_logo;
        TextView tv_nickname;
        TextView tv_distance;
        TextView tv_visited_date;
        TextView tv_sex_age;
        TextView tv_constellation;
        TextView tv_occupation;
    }


}
