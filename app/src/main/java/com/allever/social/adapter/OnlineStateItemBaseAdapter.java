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
import com.allever.social.pojo.OnlineStateItem;
import com.allever.social.pojo.ShareRankItem;

import java.util.List;

/**
 * Created by XM on 2016/8/2.
 */
public class OnlineStateItemBaseAdapter extends BaseAdapter {
    private List<OnlineStateItem> list_online_state_item;
    private LayoutInflater inflater;
    private Context context;

    public OnlineStateItemBaseAdapter(Context context,List<OnlineStateItem> list_online_state_item){
        this.context = context;
        this.list_online_state_item = list_online_state_item;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        OnlineStateItem onlineStateItem = list_online_state_item.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.online_state_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_state = (TextView)view.findViewById(R.id.id_online_state_item_tv_state);
            viewHolder.iv_state = (ImageView)view.findViewById(R.id.id_online_state_item_iv_state);

            view.setTag(viewHolder);

        }else{
            view  = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.tv_state.setText(onlineStateItem.getState());

        switch (position){
            case 0:
                viewHolder.iv_state.setImageResource(R.mipmap.online_36);
                break;
            case 1:
                viewHolder.iv_state.setImageResource(R.mipmap.offline_36);
                break;
            case 2:
                viewHolder.iv_state.setImageResource(R.mipmap.busy_36);
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
        return list_online_state_item.get(i);
    }

    @Override
    public int getCount() {
        return list_online_state_item.size();
    }

    class ViewHolder{
        private TextView tv_state;
        private ImageView iv_state;
    }
}
