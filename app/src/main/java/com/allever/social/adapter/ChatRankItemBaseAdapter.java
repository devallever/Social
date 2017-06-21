package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.ChatRankItem;
import com.allever.social.pojo.VisitedUserItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by XM on 2016/7/11.
 */
public class ChatRankItemBaseAdapter extends BaseAdapter {

    private List<ChatRankItem> list_chat_rank_item;
    private LayoutInflater inflater;
    private Context context;

    public ChatRankItemBaseAdapter(Context context,List<ChatRankItem> list_chat_rank_item){
        this.context = context;
        this.list_chat_rank_item = list_chat_rank_item;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        ChatRankItem chatRankItem = list_chat_rank_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.chat_rank_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_rank = (TextView)view.findViewById(R.id.id_chat_rank_item_tv_rank);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_chat_rank_item_tv_nickname);
            viewHolder.tv_chatcount= (TextView)view.findViewById(R.id.id_chat_rank_item_tv_chatcount);
            viewHolder.tv_chatcount_tv = (TextView)view.findViewById(R.id.id_chat_rank_item_tv_chat_count_tv);

            viewHolder.iv_cup = (ImageView)view.findViewById(R.id.id_chat_rank_item_iv_cup);
            viewHolder.iv_head = (ImageView)view.findViewById(R.id.id_chat_rank_item_iv_head);

            view.setTag(viewHolder);

        }else{
            view  = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        //设置资源
        viewHolder.tv_rank.setText(position+1+"");
        viewHolder.tv_nickname.setText(chatRankItem.getNickname());
        viewHolder.tv_chatcount.setText(chatRankItem.getChatcount()+"");
        Glide.with(context).load(WebUtil.HTTP_ADDRESS + chatRankItem.getUser_head_path()).into(viewHolder.iv_head);

        switch (position){
            case 0:
                viewHolder.iv_cup.setVisibility(View.VISIBLE);
                viewHolder.iv_cup.setImageDrawable(context.getResources().getDrawable(R.mipmap.medal_gold_3));
                break;
            case 1:
                viewHolder.iv_cup.setVisibility(View.VISIBLE);
                viewHolder.iv_cup.setImageDrawable(context.getResources().getDrawable(R.mipmap.medal_silver_3));
                break;
            case 2:
                viewHolder.iv_cup.setVisibility(View.VISIBLE);
                viewHolder.iv_cup.setImageDrawable(context.getResources().getDrawable(R.mipmap.medal_bronze_3));
                break;
            default:
                viewHolder.iv_cup.setVisibility(View.GONE);
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
        return list_chat_rank_item.get(i);
    }

    @Override
    public int getCount() {
        return list_chat_rank_item.size();
    }

    private class ViewHolder{
        TextView tv_rank;
        ImageView iv_cup;
        ImageView iv_head;
        TextView tv_nickname;
        TextView tv_chatcount;
        TextView tv_chatcount_tv;
    }
}
