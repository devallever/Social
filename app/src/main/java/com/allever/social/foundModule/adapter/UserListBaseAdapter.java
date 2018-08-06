package com.allever.social.foundModule.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.foundModule.bean.UserBeen;
import com.allever.social.modules.main.nearByUser.event.DownloadHeadFinishEvent;
import com.allever.social.utils.FileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Allever on 2016/12/2.
 */

public class UserListBaseAdapter extends RecyclerView.Adapter<UserListBaseAdapter.UserListViewHolder> {

    private static final String TAG = "UserListBaseAdapter";

    private List<UserBeen> list_users;
    private Context context;

    public UserListBaseAdapter(Context context, List<UserBeen> list_users){
        this.context = context;
        this.list_users = list_users;

    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.user_item_layout,parent,false);
        UserListViewHolder viewHolder = new UserListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserListViewHolder holder, int position) {
        final UserBeen userBeen = list_users.get(position);

        holder.tv_nickname.setText(userBeen.getNickname());
        holder.tv_sex.setText(userBeen.getSex());
        holder.tv_age.setText(userBeen.getAge()+"");
        holder.tv_occupation.setText(userBeen.getOccupation());
        holder.tv_login_time.setText(userBeen.getLogin_time());

        Glide.with(context).load(userBeen.getHead_path()).into(holder.iv_head);

        //final File headFile = new File(FileUtil.USER_HEAD_DIR,FileUtil.getFileNameFromUrl(userBeen.getHead_path()));
        //Glide.with(context).load(headFile).into(holder.iv_head);
/*        final File headFile = new File(FileUtil.USER_HEAD_DIR,FileUtil.getFileNameFromUrl(userBeen.getHead_path()));
        if (headFile.exists()){
            Glide.with(context).load(headFile).into(holder.iv_head);
        }else {
            //Glide.with(context).load(userBeen.getHead_path()).into(holder.iv_head);
            FileUtil.downloadImage(userBeen.getHead_path(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = bufferedInputStream.read(b)) > 0 ){
                        baos.write(b,0,len);
                    }
                    FileOutputStream fos = new FileOutputStream(headFile);
                    fos.write(baos.toByteArray());
                    fos.close();
                    bufferedInputStream.close();
                    inputStream.close();
                    response.body().close();
                    //通知更新界面
                    //DownloadHeadFinishEvent downloadHeadFinishEvent = new DownloadHeadFinishEvent();
                    //EventBus.getDefault().post(downloadHeadFinishEvent);
                }
            });

        }*/


        if (userBeen.getIs_accept_video()==1){
            holder.iv_video.setVisibility(View.VISIBLE);
        }else{
            holder.iv_video.setVisibility(View.GONE);
        }

        if (userBeen.getSex().endsWith("女")){
            holder.ll_sex.setBackground(context.getResources().getDrawable(R.drawable.color_pink_bg_round));
        }else{
            holder.ll_sex.setBackground(context.getResources().getDrawable(R.drawable.color_blue_bg_round));
        }

        switch (userBeen.getOccupation()){
            case "学生":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("学生");
                break;
            case "IT":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("IT");
                break;
            case "农业":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("保险");
                break;
            case "制造":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("制造");
                break;
            case "商业":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_blue_bg_round);
                //viewHolder.tv_occupation.setText("商务");
                break;
            case "模特":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("交通");
                break;
            case "文化":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_purple_bg_round);
                //viewHolder.tv_occupation.setText("传媒");
                break;
            case "教育":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("教育");
                break;
            case "医疗":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_pink_bg_round);
                //viewHolder.tv_occupation.setText("娱乐");
                break;
            case "艺术":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("公共");
                break;
            case "金融":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "行政":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_brown_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "空姐":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "法律":
                holder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list_users.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_head;
        ImageView iv_video;
        TextView tv_nickname;
        TextView tv_sex;
        TextView tv_age;
        TextView tv_login_time;
        TextView tv_occupation;
        LinearLayout ll_sex;
        LinearLayout ll_occupation;
        public UserListViewHolder(View itemView){
            super(itemView);
            iv_head = (ImageView)itemView.findViewById(R.id.id_user_item_iv_head);
            iv_video = (ImageView)itemView.findViewById(R.id.id_user_item_iv_video);
            tv_nickname = (TextView)itemView.findViewById(R.id.id_user_item_tv_nickname);
            tv_sex = (TextView)itemView.findViewById(R.id.id_user_item_tv_sex);
            tv_age = (TextView)itemView.findViewById(R.id.id_user_item_tv_age);
            tv_occupation = (TextView)itemView.findViewById(R.id.id_user_item_tv_occupation);
            tv_login_time = (TextView)itemView.findViewById(R.id.id_user_item_tv_login_time);
            ll_occupation =  (LinearLayout)itemView.findViewById(R.id.id_user_item_ll_occupation);
            ll_sex =  (LinearLayout)itemView.findViewById(R.id.id_user_item_ll_sex);
        }
    }
}
