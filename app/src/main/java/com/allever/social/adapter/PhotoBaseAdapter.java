package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.allever.social.R;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

/**
 * Created by XM on 2016/5/12.
 */
public class PhotoBaseAdapter extends BaseAdapter {
    private Context context;
    private String[] arr_news_Img;
    private LayoutInflater inflater;;

    public PhotoBaseAdapter(Context context,String[] arr_news_img){
        this.context = context;
        this.arr_news_Img = arr_news_img;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = inflater.inflate(R.layout.photo_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_news_img = (ImageView)view.findViewById(R.id.id_photo_item_iv_news_img);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context).load(arr_news_Img[position]).into(viewHolder.iv_news_img);

        return view;
    }

    @Override
    public int getCount() {
        return arr_news_Img.length;
    }

    @Override
    public Object getItem(int i) {
        return arr_news_Img[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView iv_news_img;
    }
}
