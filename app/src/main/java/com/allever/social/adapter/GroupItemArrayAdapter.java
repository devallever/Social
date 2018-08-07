package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.GroupChatActivity;
import com.allever.social.activity.GroupMemberActivity;
import com.allever.social.pojo.GroupItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/12.
 * 附近群组项适配器
 */
public class GroupItemArrayAdapter extends ArrayAdapter<GroupItem> {
    private Context context;
    private List<GroupItem> list_group;
    private int resId;


    private Handler handler;
    public GroupItemArrayAdapter(Context context,int resid,List<GroupItem> list_group){
        super(context,resid,list_group);
        this.context = context;
        this.resId = resid;
        this.list_group = list_group;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_JOIN_GROUP:
                        //handleJoinGroup(msg,viewHolder);
                        //发广播刷新附近群组
                        Intent braodIntent = new Intent("com.allever.social.REFRESH_NEARBY_GROUP");
                        context.sendBroadcast(braodIntent);
                        break;
                }
            }
        };

        final GroupItem groupItem = list_group.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resId, parent, false);
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
            viewHolder.btn_chat = (ButtonRectangle)view.findViewById(R.id.id_near_by_group_btn_chat);

            viewHolder.rv_group_member_container = (RippleView)view.findViewById(R.id.id_near_by_group_rv_group_member_container);
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
        viewHolder.tv_distance.setText(groupItem.getDistance() + " km");
        viewHolder.tv_attention_desc.setText(groupItem.getAttention());
        viewHolder.tv_groupmember_count.setText("本群共" + groupItem.getMember_count() + "人 (女生" + groupItem.getWomen_count() + "人)");

        if (groupItem.getIs_member()==1){
            viewHolder.btn_chat.setVisibility(View.VISIBLE);
            viewHolder.btn_join.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.btn_chat.setVisibility(View.INVISIBLE);
            viewHolder.btn_join.setVisibility(View.VISIBLE);
        }

        viewHolder.btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("roupItemArrayAdapter", "position = " + position);
                //发请求
                //joinGroup(list_group.get(position).getId() + "");
                switch (groupItem.getGroup_type()){
                    case 1:
                    case 2:
                        Toast.makeText(context, "私有群只有群主可以邀请人", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(context, "已申请", Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
                                    EMClient.getInstance().groupManager().applyJoinToGroup(groupItem.getHx_group_id(), "请求加入群组");//需异步处理
                                }catch (HyphenateException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                    case 4:
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
//                                    EMClient.getInstance().groupManager().joinGroup(groupItem.getHx_group_id());//需异步处理
//                                }catch (HyphenateException e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//                        OkhttpUtil.joinGroup(handler,groupItem.getId(), SharedPreferenceUtil.getUserName());
                        break;
                }

            }
        });

        viewHolder.btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("roupItemArrayAdapter", "position = " + position);
                //进入聊天界面
                //joinGroup(list_group.get(position).getId() + "");
                Intent intent;
                intent = new Intent(context,GroupChatActivity.class);
                intent.putExtra("hx_group_id", groupItem.getHx_group_id());
                context.startActivity(intent);
            }
        });

        viewHolder.rv_group_member_container.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(context, GroupMemberActivity.class);
                intent.putExtra("group_id", groupItem.getId());
                intent.putExtra("hx_group_id",groupItem.getHx_group_id());
                context.startActivity(intent);
            }
        });


        switch (groupItem.getList_members_path().length){
            case 0:
                break;
            case 1:
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[0])
                        .into(viewHolder.iv_member_1);
                break;
            case 2:
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[0])
                        .into(viewHolder.iv_member_1);
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[1])
                        .into(viewHolder.iv_member_2);
                break;
            case 3:
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[0])
                        .into(viewHolder.iv_member_1);
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[1])
                        .into(viewHolder.iv_member_2);
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[2])
                        .into(viewHolder.iv_member_3);
                break;
            default:
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[0])
                        .into(viewHolder.iv_member_1);
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[1])
                        .into(viewHolder.iv_member_2);
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + groupItem.getList_members_path()[2])
                        .into(viewHolder.iv_member_3);
                break;
        }
        return view;
    }

    private void joinGroup(String group_id){
        if (OkhttpUtil.checkLogin()){
            //OkhttpUtil.joinGroup(handler,group_id);
        }
    }

    private void handleJoinGroup(Message msg, final ViewHolder viewHolder){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(context,"错误","链接服务器失败").show();
            return ;
        }
        if (root.success == false){
            new Dialog(context,"错误",root.message).show();
        }

        Dialog dialog = new Dialog(context,"Tips","加群成功");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // viewHolder.btn_join.setVisibility(View.INVISIBLE);
                //viewHolder.btn_chat.setVisibility(View.VISIBLE);\
                Intent intent = new Intent("com.allever.social.refresh_group_list");
                context.sendBroadcast(intent);

            }
        });
        dialog.show();

    }

    private class ViewHolder{
        CircleImageView iv_group_img;
        TextView tv_groupname;
        TextView tv_point;
        TextView tv_distance;
        TextView tv_groupmember_count;
        ButtonRectangle btn_join;
        ButtonRectangle btn_chat;
        TextView tv_attention_desc;
        CircleImageView iv_member_1;
        CircleImageView iv_member_2;
        CircleImageView iv_member_3;
        RippleView rv_group_member_container;

    }

    private class Root{
        boolean success;
        String message;
    }
}
