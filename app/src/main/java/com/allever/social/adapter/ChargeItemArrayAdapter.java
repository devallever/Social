package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.ChargeItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/6/5.
 * 充值信用选择列表项适配器
 */
public class ChargeItemArrayAdapter extends ArrayAdapter<ChargeItem> {
    private Context context;
    private int chargeItemResid;
    private List<ChargeItem> list_chargeItem;

    public ChargeItemArrayAdapter(Context context,int resid,List<ChargeItem> list_chargeitem){
        super(context,resid,list_chargeitem);
        this.context = context;
        this.chargeItemResid = resid;
        this.list_chargeItem = list_chargeitem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChargeItem chargeItem = list_chargeItem.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(chargeItemResid, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_credit = (TextView)view.findViewById(R.id.id_charge_item_tv_credit);
            viewHolder.tv_money= (TextView)view.findViewById(R.id.id_charge_item_tv_money);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_credit.setText(chargeItem.getCredit()+" 信用");
        viewHolder.tv_money.setText("$ " + chargeItem.getMoney() + "元");


        return view;
    }

    class ViewHolder{
        ImageView iv_head;
        TextView tv_credit;
        TextView tv_money;
    }
}
