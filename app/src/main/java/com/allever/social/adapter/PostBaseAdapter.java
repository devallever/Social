package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.PostItem;

import java.io.PipedOutputStream;
import java.util.List;

/**
 * Created by XM on 2016/5/18.
 */
public class PostBaseAdapter extends BaseAdapter {

    private List<PostItem> list_post;
    private Context context;
    private LayoutInflater inflater;

    public  PostBaseAdapter(Context context, List<PostItem> list_item){
        this.list_post = list_item;
        this.context  = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        PostItem postItem = list_post.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.post_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_postname = (TextView)view.findViewById(R.id.id_post_item_tv_postname);
            viewHolder.tv_salary = (TextView)view.findViewById(R.id.id_post_item_tv_salary);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_salary.setText(postItem.getSalary());
        viewHolder.tv_postname.setText(postItem.getPostname());

        return view;
    }

    @Override
    public int getCount() {
        return list_post.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_post.get(i);
    }

    private class ViewHolder{
        TextView tv_postname;
        TextView tv_salary;
    }
}
