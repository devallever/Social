package com.allever.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.FriendGroupItem;
import com.allever.social.pojo.FriendItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/6/14.
 * 好友分组项适配器
 */
public class FriendGroupItemExpandableBaseAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<FriendGroupItem> list_friendgroupItem;

    public FriendGroupItemExpandableBaseAdapter(Context context, List<FriendGroupItem> list_friendgroupItem){
        this.context = context;
        this.list_friendgroupItem = list_friendgroupItem;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getGroupView(int parentPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FriendGroupItem friendGroupItem = list_friendgroupItem.get(parentPosition);
        View view;
        ParentViewHolder parentViewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.friendgroup_item_parent,parent,false);
            parentViewHolder = new ParentViewHolder();
            parentViewHolder.tv_friendgroup_name = (TextView)view.findViewById(R.id.id_friendgroup_item_parent_tv_friendgroup_name);
            parentViewHolder.iv_expand = (ImageView)view.findViewById(R.id.id_friendgroup_item_parent_iv_expand);
            parentViewHolder.tv_friend_count = (TextView)view.findViewById(R.id.id_friendgroup_item_parent_tv_friend_count);
            view.setTag(parentViewHolder);
        }else{
            view  = convertView;
            parentViewHolder = (ParentViewHolder)view.getTag();
        }

        //判断isExpanded就可以控制是按下还是关闭，同时更换图片
        if (isExpanded){
            parentViewHolder.iv_expand.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_expanded));
        }else{
            parentViewHolder.iv_expand.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unexpanded));
        }

        parentViewHolder.tv_friend_count.setText(friendGroupItem.getList_friend().size()+"");
        parentViewHolder.tv_friendgroup_name.setText(friendGroupItem.getFriendgroup_name());
        return view;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition, boolean b, View convertView, ViewGroup parent) {
        FriendItem friendItem = list_friendgroupItem.get(parentPosition).getList_friend().get(childPosition);
        View view;
        ChildViewHolder childViewHolder;
        if(convertView == null){
            view = inflater.inflate(R.layout.friendgroup_item_child,parent,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_friendgroup_item_child_iv_head);
            childViewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_friendgroup_item_child_tv_nickname);
            childViewHolder.tv_signature = (TextView)view.findViewById(R.id.id_friendgroup_item_child_tv_signature);
            view.setTag(childViewHolder);
        }else{
            view = convertView;
            childViewHolder = (ChildViewHolder)view.getTag();
        }
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + friendItem.getUser_head_path())
                .into(childViewHolder.iv_head);
        childViewHolder.tv_nickname.setText(friendItem.getNickname());
        childViewHolder.tv_signature.setText(friendItem.getSignature());
        return view;
    }

    private class ParentViewHolder{
        ImageView iv_expand;
        TextView tv_friendgroup_name;
        TextView tv_friend_count;
    }

    private class ChildViewHolder{
        CircleImageView iv_head;
        TextView tv_nickname;
        TextView tv_signature;
    }

    @Override
    public int getGroupCount() {
        return list_friendgroupItem.size();
    }

    @Override
    public int getChildrenCount(int parentPosition) {
        return list_friendgroupItem.get(parentPosition).getList_friend().size();
    }

    @Override
    public Object getGroup(int parengPosition) {
        return list_friendgroupItem.get(parengPosition);
    }

    @Override
    public Object getChild(int parentPosition, int childPosition) {
        return list_friendgroupItem.get(parentPosition).getList_friend().get(childPosition);
    }

    @Override
    public long getGroupId(int parentPosition) {
        return parentPosition;
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    //
}
