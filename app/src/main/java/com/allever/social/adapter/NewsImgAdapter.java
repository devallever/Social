package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/20.
 */
public class NewsImgAdapter extends ArrayAdapter<String> {
    private int image_layout_res_id;
    private List<String> list_path;
    private Context context;
    private OkHttpClient okHttpClient;


    public NewsImgAdapter(Context context, int img_res_id, List<String> listPath){
        super(context,img_res_id,listPath);
        this.image_layout_res_id = img_res_id;
        this.context = context;
        this.list_path = listPath;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(image_layout_res_id, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_news_img = (ImageView)view.findViewById(R.id.id_news_img_item_iv);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context).load(WebUtil.HTTP_ADDRESS + list_path.get(position)).into(viewHolder.iv_news_img);

        //Glide.with(context).load(WebUtil.HTTP_ADDRESS + list_path.get(position)).into(viewHolder.iv_news_img);
       // Picasso.with(context).load(WebUtil.HTTP_ADDRESS + list_path.get(position)).into(viewHolder.iv_news_img);
        return view;
    }

    private static class ViewHolder{
        ImageView iv_news_img;
    }

}
