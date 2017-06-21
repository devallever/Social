package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.allever.social.R;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MySquareImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by XM on 2016/5/27.
 * 照片墙适配器
 */
public class PhotoWallImgBaseAdapter extends BaseAdapter {
    private List<String> list_photo_wall;
    private Context context;
    private LayoutInflater inflater;

    public PhotoWallImgBaseAdapter(Context context,List<String> list_photo_wall){
        this.context = context;
        this.list_photo_wall = list_photo_wall;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = inflater.inflate(R.layout.photo_wall_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (MySquareImageView)view.findViewById(R.id.id_photo_wall_item_iv);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load(list_photo_wall.get(position))
                .into(viewHolder.imageView);


        return view;
    }

    @Override
    public int getCount() {
        return list_photo_wall.size();
    }

    @Override
    public Object getItem(int i) {
        return list_photo_wall.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        MySquareImageView imageView;
    }

}
