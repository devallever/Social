package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.activity.AddPostActivity;
import com.allever.social.activity.PostDetailActivity;
import com.allever.social.pojo.NearByRecruitItem;
import com.allever.social.pojo.PostItem;
import com.allever.social.utils.ListViewUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/18.
 * 职位列表项适配器
 */
public class RecruitItemBaseAdapter extends BaseAdapter {

    private List<NearByRecruitItem> list_NearbyRecruitItems;
    private Context context;
    private LayoutInflater inflater;

    //private String[] groupStr = {"招聘职位"};

    private PostBaseAdapter postBaseAdapter;

    public RecruitItemBaseAdapter(Context context, List<NearByRecruitItem> list_NearbyRecruitItems){
        this.list_NearbyRecruitItems = list_NearbyRecruitItems;
        this.context  = context;
        inflater = LayoutInflater.from(context);
    }

    private Handler handler;

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_DELETE_RECRUIT:
                        handleDeleteRecruit(msg);
                        break;

                }
            }
        };

        final NearByRecruitItem nearByRecruitItem = list_NearbyRecruitItems.get(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            view = inflater.inflate(R.layout.nearby_recruit_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_near_by_recruit_item_iv_head);
            viewHolder.tv_commanyname = (TextView)view.findViewById(R.id.id_near_by_recruit_item_tv_commanyname);
            viewHolder.tv_distacne = (TextView)view.findViewById(R.id.id_near_by_recruit_item_tv_distance);
            viewHolder.lv_post = (ListView)view.findViewById(R.id.id_near_by_recruit_item_listview_post);
            viewHolder.tv_requirement = (TextView)view.findViewById(R.id.id_near_by_recruit_item_tv_requirement);
            viewHolder.btn_add_post = (ButtonRectangle)view.findViewById(R.id.id_near_by_recruit_item_btn_add_post);
            viewHolder.btn_dail = (ButtonFlat)view.findViewById(R.id.id_near_by_recruit_item_btn_dail);
            viewHolder.tv_link = (TextView)view.findViewById(R.id.id_near_by_recruit_item_tv_link);
            viewHolder.btn_delete_recruit = (ButtonRectangle)view.findViewById(R.id.id_near_by_recruit_item_btn_delete_recruit);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_distacne.setText(nearByRecruitItem.getDistance() + " km");
        viewHolder.tv_commanyname.setText(nearByRecruitItem.getCommanyname());
        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + nearByRecruitItem.getHead_img())
                .into(viewHolder.iv_head);
        viewHolder.tv_requirement.setText(nearByRecruitItem.getRequirement());
        viewHolder.btn_dail.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        viewHolder.tv_link.setText("联系人: " + nearByRecruitItem.getLink());
        if(nearByRecruitItem.getIs_owner()==1){
            viewHolder.btn_add_post.setVisibility(View.VISIBLE);
            viewHolder.btn_delete_recruit.setVisibility(View.VISIBLE);
        }else{
            viewHolder.btn_add_post.setVisibility(View.GONE);
            viewHolder.btn_delete_recruit.setVisibility(View.GONE);
        }
        viewHolder.btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecruitItemBase", "recruit_id = " + nearByRecruitItem.getId());
                Intent intent = new Intent(context, AddPostActivity.class);
                intent.putExtra("recruit_id",nearByRecruitItem.getId());
                context.startActivity(intent);
            }
        });

        viewHolder.btn_delete_recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkhttpUtil.deleteRecruit(handler,nearByRecruitItem.getId());
            }
        });

        viewHolder.btn_dail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecruitItemBase", "phone = " + nearByRecruitItem.getPhone());
                Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nearByRecruitItem.getPhone()));
                context.startActivity(intent);
            }
        });

        viewHolder.lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("RecruitItemBase", "post_id = " + nearByRecruitItem.getListPostItem().get(position).getId() + "\n postname = "  + nearByRecruitItem.getListPostItem().get(position).getPostname());
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post_id",nearByRecruitItem.getListPostItem().get(position).getId());
                context.startActivity(intent);
            }
        });

//        List<PostItem> list_post = new ArrayList<>();
//        PostItem postItem = new PostItem();
//        postItem.setId("1");
//        postItem.setPostname("Android 工程师");
//        postItem.setSalary("3k - 5k");
//        list_post.add(postItem);
//        list_post.add(postItem);
//        list_post.add(postItem);
//        list_post.add(postItem);
//        list_post.add(postItem);



        postBaseAdapter = new PostBaseAdapter(context,nearByRecruitItem.getListPostItem());
        viewHolder.lv_post.setAdapter(postBaseAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(viewHolder.lv_post);


        return view;
    }

    @Override
    public int getCount() {
        return list_NearbyRecruitItems.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_NearbyRecruitItems.get(i);
    }

    private void handleDeleteRecruit(Message msg){
        String result = msg.obj.toString();
        Log.d("RecruitItemBase", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(context,"错误","链接服务器失败").show();
            return ;
        }
        if (root.success == false){
            new Dialog(context,"错误",root.message).show();
        }

        final Dialog dialog = new Dialog(context,"Tips","删除成功");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.allever.social.updateNearbyRecruit");
                context.sendBroadcast(intent);
                dialog.cancel();
            }
        });
        dialog.show();
    }


    private class ViewHolder{
        CircleImageView iv_head;
        TextView tv_commanyname;
        TextView tv_distacne;
        ListView lv_post;
        TextView tv_requirement;
        ButtonRectangle btn_add_post;
        ButtonFlat btn_dail;
        TextView tv_link;
        ButtonRectangle btn_delete_recruit;
    }


    private class Root{
        boolean success;
        String message;
    }
}
