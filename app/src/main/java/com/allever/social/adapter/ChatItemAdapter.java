package com.allever.social.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.Msg;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/3.
 */
public class ChatItemAdapter extends ArrayAdapter<Msg> {
    private int resid;
    private List<Msg> list_msg;
    private Context context;

    public ChatItemAdapter(Context context, int resId, List<Msg> msgs){
        super(context,resId,msgs);
        this.context = context;
        this.resid = resId;
        this.list_msg = msgs;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resid, parent,false);
            viewHolder = new ViewHolder();
            if (resid == R.layout.chat_item_receive) {
                viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_chat_item_receive_head);
                viewHolder.tv_text = (TextView)view.findViewById(R.id.id_chat_item_receive_text);
            }else {
                viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_chat_item_send_head);
                viewHolder.tv_text = (TextView)view.findViewById(R.id.id_chat_item_send_text);
            }
            view.setTag(viewHolder);

        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load((WebUtil.HTTP_ADDRESS + msg.getHead_path()))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(viewHolder.iv_head);
        //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + msg.getHead_path()).into(viewHolder.iv_head);
        viewHolder.tv_text.setText(msg.getContent());

        return view;
    }

    class ViewHolder{
        ImageView iv_head;
        TextView tv_text;
    }

}
