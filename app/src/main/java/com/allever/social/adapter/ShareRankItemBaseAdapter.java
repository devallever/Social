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
import com.allever.social.pojo.ShareRankItem;
import com.allever.social.pojo.VisitedUserItem;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by XM on 2016/7/11.
 */
public class ShareRankItemBaseAdapter extends BaseAdapter {

    private List<ShareRankItem> list_share_rank_item;
    private LayoutInflater inflater;
    private Context context;

    public ShareRankItemBaseAdapter(Context context,List<ShareRankItem> list_share_rank_item){
        this.context = context;
        this.list_share_rank_item = list_share_rank_item;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        ShareRankItem shareRankItem = list_share_rank_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.share_rank_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_rank = (TextView)view.findViewById(R.id.id_share_rank_item_tv_rank);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_share_rank_item_tv_nickname);
            viewHolder.tv_sharecount= (TextView)view.findViewById(R.id.id_share_rank_item_tv_sharecount);
            viewHolder.tv_sharecount_tv = (TextView)view.findViewById(R.id.id_share_rank_item_tv_share_count_tv);

            viewHolder.iv_cup = (ImageView)view.findViewById(R.id.id_share_rank_item_iv_cup);
            viewHolder.iv_head = (ImageView)view.findViewById(R.id.id_share_rank_item_iv_head);

            view.setTag(viewHolder);

        }else{
            view  = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        //设置资源
        viewHolder.tv_rank.setText(position+1+"");
        viewHolder.tv_nickname.setText(shareRankItem.getNickname());
        viewHolder.tv_sharecount.setText(shareRankItem.getShare_count()+"");
        Glide.with(context).load(WebUtil.HTTP_ADDRESS + shareRankItem.getUser_head_path()).into(viewHolder.iv_head);

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
        return list_share_rank_item.get(i);
    }

    @Override
    public int getCount() {
        return list_share_rank_item.size();
    }

    private class ViewHolder{
        TextView tv_rank;
        ImageView iv_cup;
        ImageView iv_head;
        TextView tv_nickname;
        TextView tv_sharecount;
        TextView tv_sharecount_tv;
    }
}
