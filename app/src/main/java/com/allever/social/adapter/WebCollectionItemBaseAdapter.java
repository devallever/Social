package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.pojo.ShareRankItem;
import com.allever.social.pojo.WebCollectionItem;

import java.util.List;

/**
 * Created by XM on 2016/7/30.
 */
public class WebCollectionItemBaseAdapter extends BaseAdapter {

    private List<WebCollectionItem> list_webcollection_item;
    private LayoutInflater inflater;
    private Context context;

    public WebCollectionItemBaseAdapter(Context context,List<WebCollectionItem> list_webcollection_item){
        this.context = context;
        this.list_webcollection_item = list_webcollection_item;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        WebCollectionItem webCollectionItem = list_webcollection_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.webcollection_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView)view.findViewById(R.id.id_webcollection_item_tv_title);
            viewHolder.tv_url = (TextView)view.findViewById(R.id.id_webcollection_item_tv_url);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_url.setText(webCollectionItem.getUrl());
        viewHolder.tv_title.setText(webCollectionItem.getTitle());

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_webcollection_item.get(i);
    }

    @Override
    public int getCount() {
        return list_webcollection_item.size();
    }


    class ViewHolder{
        TextView tv_title;
        TextView tv_url;
    }
}
