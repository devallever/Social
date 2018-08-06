package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.ShowBigImageActvity;
import com.allever.social.activity.ShowNewsImageActivity;
import com.allever.social.pojo.SwipeCardItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by XM on 2016/10/14.
 */
public class SwipeCardItemBaseAdapter extends BaseAdapter {

    private Context context;
    private List<SwipeCardItem> list_swipe_card_items;

    public SwipeCardItemBaseAdapter(Context context,List<SwipeCardItem> list_swipe_card_items){
        this.context = context;
        this.list_swipe_card_items = list_swipe_card_items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SwipeCardItem swipeCardItem = list_swipe_card_items.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.swipe_card_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.iv_img_1 = (ImageView)view.findViewById(R.id.id_swipe_card_item_iv_img_1);
            viewHolder.iv_img_2 = (ImageView)view.findViewById(R.id.id_swipe_card_item_iv_img_2);
            viewHolder.iv_img_3 = (ImageView)view.findViewById(R.id.id_swipe_card_item_iv_img_3);
            viewHolder.iv_img_4 = (ImageView)view.findViewById(R.id.id_swipe_card_item_iv_img_4);

            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_swipe_card_item_tv_nickname);
            viewHolder.tv_sex = (TextView)view.findViewById(R.id.id_swipe_card_item_tv_sex);
            viewHolder.tv_age= (TextView)view.findViewById(R.id.id_swipe_card_item_tv_age);
            viewHolder.tv_occupation = (TextView)view.findViewById(R.id.id_swipe_card_item_tv_occupation);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_swipe_card_item_tv_distance);
            viewHolder.tv_signature = (TextView)view.findViewById(R.id.id_swipe_card_item_tv_signature);

            viewHolder.ll_sex = (LinearLayout)view.findViewById(R.id.id_swipe_card_item_ll_sex);
            viewHolder.ll_occupation = (LinearLayout)view.findViewById(R.id.id_swipe_card_item_ll_occupation);

            view.setTag(viewHolder);

        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_nickname.setText(swipeCardItem.getNickname());
        viewHolder.tv_sex.setText(swipeCardItem.getSex());
        viewHolder.tv_age.setText(swipeCardItem.getAge()+"");
        viewHolder.tv_distance.setText(swipeCardItem.getDistance()+" km");
        viewHolder.tv_signature.setText(swipeCardItem.getSignature());

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + swipeCardItem.getList_imgs().get(0))
                .into(viewHolder.iv_img_1);
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + swipeCardItem.getList_imgs().get(1))
                .into(viewHolder.iv_img_2);
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + swipeCardItem.getList_imgs().get(2))
                .into(viewHolder.iv_img_3);
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + swipeCardItem.getList_imgs().get(3))
                .into(viewHolder.iv_img_4);

        viewHolder.tv_occupation.setText(swipeCardItem.getOccupation());
        switch (swipeCardItem.getOccupation()){
            case "学生":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("学生");
                break;
            case "IT":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("IT");
                break;
            case "农业":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("保险");
                break;
            case "制造":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("制造");
                break;
            case "商业":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_blue_bg_round);
                //viewHolder.tv_occupation.setText("商务");
                break;
            case "模特":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("交通");
                break;
            case "文化":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_purple_bg_round);
                //viewHolder.tv_occupation.setText("传媒");
                break;
            case "教育":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("教育");
                break;
            case "医疗":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_pink_bg_round);
                //viewHolder.tv_occupation.setText("娱乐");
                break;
            case "艺术":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("公共");
                break;
            case "金融":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "行政":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_brown_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "空姐":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "法律":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
        }

        if (swipeCardItem.getSex().equals("男")){
            viewHolder.ll_sex.setBackgroundResource(R.drawable.color_indigo_bg_round);
        }else{
            viewHolder.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);
        }

        return view;
    }

    @Override
    public int getCount() {
        return list_swipe_card_items.size();
    }

    @Override
    public Object getItem(int i) {
        return list_swipe_card_items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    class ViewHolder{
        ImageView iv_img_1;
        ImageView iv_img_2;
        ImageView iv_img_3;
        ImageView iv_img_4;

        TextView tv_nickname;
        TextView tv_sex;
        TextView tv_age;
        TextView tv_occupation;
        TextView tv_distance;
        TextView tv_signature;

        LinearLayout ll_occupation;
        LinearLayout ll_sex;

    }
}
