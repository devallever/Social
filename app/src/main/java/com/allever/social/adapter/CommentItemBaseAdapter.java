package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.pojo.CommentItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/6/23.
 * 评论列表项适配器
 */
public class CommentItemBaseAdapter extends BaseAdapter {
    private List<CommentItem> list_commentItem;
    private Context context;
    private LayoutInflater inflater;

    //private String comment_voice_url;
    //private String comment_voice_local_path;

    //语音操作对象
   // private MediaPlayer mPlayer = null;
    private Handler handler_two;

    public CommentItemBaseAdapter(Context context, List<CommentItem> list_commentItem){
        this.context = context;
        this.list_commentItem = list_commentItem;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        handler_two = new Handler();
        CommentItem commentItem = list_commentItem.get(position);
        ViewHolder viewHolder = null;
        VoiceViewHolder voiceViewHolder = null;
        View view;
        if (convertView == null){
            if (list_commentItem.get(position).getComment_voice()==null){
                view = inflater.inflate(R.layout.comment_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_comment_item_circle_iv_userhead);
                viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_comment_item_tv_nickname);
                viewHolder.tv_time = (TextView)view.findViewById(R.id.id_comment_item_tv_time);
                viewHolder.tv_content = (TextView)view.findViewById(R.id.id_comment_item_tv_content);
                view.setTag(viewHolder);
            }else{
                view = inflater.inflate(R.layout.comment_item_voice,parent,false);
                voiceViewHolder = new VoiceViewHolder();
                voiceViewHolder.iv_userhead = (CircleImageView)view.findViewById(R.id.id_comment_item_circle_iv_userhead);
                voiceViewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_comment_item_tv_nickname);
                voiceViewHolder.tv_time = (TextView)view.findViewById(R.id.id_comment_item_tv_time);
                voiceViewHolder.tv_audio = (TextView)view.findViewById(R.id.id_comment_item_tv_audio);
                voiceViewHolder.rv_play_audio = (RippleView)view.findViewById(R.id.id_comment_item_rv_audio);
                voiceViewHolder.tv_react_comment = (TextView)view.findViewById(R.id.id_comment_item_tv_react_comment);
                voiceViewHolder.mPlayer = new MediaPlayer();
                view.setTag(voiceViewHolder);
            }
        }else{
            view = convertView;
            if (list_commentItem.get(position).getComment_voice()==null){
                viewHolder = (ViewHolder)view.getTag();
            }else{
                voiceViewHolder = (VoiceViewHolder)view.getTag();
            }
        }


        //设置资源，适配数据
        if (list_commentItem.get(position).getComment_voice()==null){
            viewHolder.iv_userhead.setImageResource(R.mipmap.winchen);//静态
            viewHolder.tv_nickname.setText(commentItem.getNickname());
            viewHolder.tv_time.setText(commentItem.getTime());
            viewHolder.tv_content.setText(commentItem.getContent());
            Glide.with(context)
                    .load(WebUtil.HTTP_ADDRESS + commentItem.getUser_head_path())
                    .into(viewHolder.iv_userhead);
            viewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    Intent intent;
                    switch (id) {
                        case R.id.id_comment_item_circle_iv_userhead:
                            intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                            intent.putExtra("username", list_commentItem.get(position).getUsername());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getContext().startActivity(intent);
                            break;
                    }
                }
            });
        }else{
            final VoiceViewHolder finalVoiceViewHolder = voiceViewHolder;
            voiceViewHolder.iv_userhead.setImageResource(R.mipmap.winchen);//静态
            voiceViewHolder.tv_nickname.setText(commentItem.getNickname());
            voiceViewHolder.tv_time.setText(commentItem.getTime());

            if (commentItem.getContent().split(":").length>0) {
                voiceViewHolder.tv_react_comment.setVisibility(View.VISIBLE);
                if (!commentItem.getContent().split(":")[0].equals("")) voiceViewHolder.tv_react_comment.setText("#" + commentItem.getContent().split(":")[0]);
                else voiceViewHolder.tv_react_comment.setVisibility(View.GONE);
            }else{
                voiceViewHolder.tv_react_comment.setVisibility(View.GONE);
            }

            final String comment_voice_url = commentItem.getComment_voice();
            final String comment_voice_local_path = Environment.getExternalStorageDirectory().getPath() + "/social/voice/comment_voice/"+ commentItem.getId() +".arm";
            FileInputStream fin = null;
            String filename = comment_voice_local_path;
            Log.d("CommentItemBase", filename);
            File file = new File(filename);
            //判断文件是否存在，不存在就去下载
            if (!file.exists()) {
                //下载语音
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("user_id", SharedPreferenceUtil.getUserId())
                        .build();
                Request request = new Request.Builder()
                        .url(WebUtil.HTTP_ADDRESS + comment_voice_url)
                        .post(formBody)
                        .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                        .build();
                Log.d("CommentBaseAdapter", WebUtil.HTTP_ADDRESS + comment_voice_url);
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        //NOT UI Thread
                        if (response.isSuccessful()) {
                            System.out.println(response.code());
                            byte[] result = response.body().bytes();

                            FileOutputStream fos;
                            String filePath= "";
                            File file = null;
                            try{
                                filePath = Environment.getExternalStorageDirectory().getPath() + "/social/";
                                file = new File(filePath);
                                if  (!file .exists()  && !file .isDirectory()) {
                                    System.out.println("//不存在");
                                    file .mkdir();
                                } else{
                                    System.out.println("//目录存在");
                                }

                                filePath = filePath + "/voice/";
                                file = new File(filePath);
                                if  (!file .exists()  && !file .isDirectory()) {
                                    System.out.println("//不存在");
                                    file .mkdir();
                                } else{
                                    System.out.println("//目录存在");
                                }

                                filePath = filePath + "/comment_voice/";
                                file = new File(filePath);
                                if  (!file .exists()  && !file .isDirectory()) {
                                    System.out.println("//不存在");
                                    file .mkdir();
                                } else{
                                    System.out.println("//目录存在");
                                }
                                filePath = comment_voice_local_path;
                                System.out.println("path = " + filePath);
                                fos = new FileOutputStream(filePath);
                                fos.write(result);
                                fos.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }else{
                //不干嘛
            }

            finalVoiceViewHolder.rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (finalVoiceViewHolder.tv_audio.getText().toString().equals("播放")){
                        //播放录音
                        finalVoiceViewHolder.tv_audio.setText("停止");
                        finalVoiceViewHolder.mPlayer = new MediaPlayer();
                        try{
                            //Toast.makeText(context,comment_voice_local_path,Toast.LENGTH_LONG).show();
                            finalVoiceViewHolder.mPlayer.setDataSource(comment_voice_local_path);
                            finalVoiceViewHolder.mPlayer.prepare();
                            finalVoiceViewHolder.mPlayer.start();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean flag = true;
                                    try {
                                        while (flag){
                                            Thread.sleep(1000);
                                            if (finalVoiceViewHolder.mPlayer !=null){
                                                if (!finalVoiceViewHolder.mPlayer.isPlaying()) flag = false;
                                            }
                                        }
                                        handler_two.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                finalVoiceViewHolder.tv_audio.setText("播放");
                                            }
                                        });
                                    }catch (InterruptedException e){
                                    }
                                }
                            }).start();


                        }catch(IOException e){
                            Log.e("CommentItemBase",comment_voice_local_path);
                            Log.e("CommentItemBase","播放失败");
                        }
                    }else if (finalVoiceViewHolder.tv_audio.getText().toString().equals("停止")){
                        //停止播放
                        finalVoiceViewHolder.tv_audio.setText("播放");
                        finalVoiceViewHolder.mPlayer.stop();
                        //mPlayer = null;
                    }
                }
            });


            Glide.with(context)
                    .load(WebUtil.HTTP_ADDRESS + commentItem.getUser_head_path())
                    .into(voiceViewHolder.iv_userhead);
            voiceViewHolder.iv_userhead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    Intent intent;
                    switch (id) {
                        case R.id.id_comment_item_circle_iv_userhead:
                            intent = new Intent(MyApplication.getContext(), UserDataDetailActivity.class);
                            intent.putExtra("username", list_commentItem.get(position).getUsername());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getContext().startActivity(intent);
                            break;
                    }
                }
            });
        }

        return view;
    }

    private  class ViewHolder{
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
    }
    private class VoiceViewHolder{
        ImageView iv_userhead;
        TextView tv_nickname;
        TextView tv_time;
        RippleView rv_play_audio;
        TextView tv_audio;
        TextView tv_react_comment;
        MediaPlayer mPlayer;
    }



    @Override
    public int getCount() {
        return list_commentItem.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_commentItem.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        if (list_commentItem.get(position).getComment_voice()==null){
            return 0;//文本评论
        }else{
            return 1;//语音评论
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
