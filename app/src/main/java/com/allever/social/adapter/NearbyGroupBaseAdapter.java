package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.fragment.ContactFragment;
import com.allever.social.pojo.GroupItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/12.
 */
public class NearbyGroupBaseAdapter extends BaseAdapter {
    private List<GroupItem> list_group;
    private LayoutInflater inflater;
    private Context context;

    public NearbyGroupBaseAdapter(Context context, List<GroupItem> list_group){
        this.list_group = list_group;
        this.context = context;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return list_group.size();
    }

    @Override
    public Object getItem(int i) {
        return list_group.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        GroupItem groupItem = list_group.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.nearby_group_item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.iv_group_img = (CircleImageView)view.findViewById(R.id.id_near_by_group_iv_group);
            viewHolder.iv_member_1 = (CircleImageView)view.findViewById(R.id.id_near_by_group_iv_member_1);
            viewHolder.iv_member_2 = (CircleImageView)view.findViewById(R.id.id_near_by_group_iv_member_2);
            viewHolder.iv_member_3 = (CircleImageView)view.findViewById(R.id.id_near_by_group_iv_member_3);

            viewHolder.tv_groupname = (TextView)view.findViewById(R.id.id_near_by_group_tv_groupname);
            viewHolder.tv_point = (TextView)view.findViewById(R.id.id_near_by_group_tv_point);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_near_by_group_tv_distance);
            viewHolder.tv_groupmember_count = (TextView)view.findViewById(R.id.id_near_by_group_tv_member_count);
            viewHolder.tv_attention_desc = (TextView)view.findViewById(R.id.id_near_by_group_tv_attention_desc);

            viewHolder.btn_join = (ButtonRectangle)view.findViewById(R.id.id_near_by_group_btn_join);
            view.setTag(viewHolder);

        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + groupItem.getGroup_img())
                .into(viewHolder.iv_group_img);
        viewHolder.tv_groupname.setText(groupItem.getGroupname());
        viewHolder.tv_point.setText(groupItem.getPoint());
        viewHolder.tv_distance.setText(groupItem.getDistance() + "");
        viewHolder.tv_attention_desc.setText(groupItem.getAttention());
        viewHolder.tv_groupmember_count.setText("本群共" + groupItem.getMember_count() + "(女生" + groupItem.getWomen_count() + "人)");

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + groupItem.getGroup_bulider_path())
                .into(viewHolder.iv_member_1);
        return view;
    }

    private class ViewHolder{
        CircleImageView iv_group_img;
        TextView tv_groupname;
        TextView tv_point;
        TextView tv_distance;
        TextView tv_groupmember_count;
        ButtonRectangle btn_join;
        TextView tv_attention_desc;
        CircleImageView iv_member_1;
        CircleImageView iv_member_2;
        CircleImageView iv_member_3;

    }

}
