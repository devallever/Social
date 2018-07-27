package com.allever.social.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

/**
 * Created by XM on 2016/6/17.
 * 动态列表弹出评论对话框
 */
public class AddCommentDialogActivity extends BaseActivity{
    private String news_id;
    private EditText et_content;
    private String content;
    private RippleView rv_calcle;
    private RippleView rv_comment;

    private Handler handler;
    private int position;



    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private String audio_path = "";//语音文件保存路径
    private Handler handler_two;

    private RippleView rv_choose_audio_comment;
    private RippleView rv_choose_text_comment;
    private RelativeLayout rl_comment_audio_container;

    private RippleView rv_record;
    private RippleView rv_play_audio;
    private TextView tv_play_audio_comment;
    private ImageView iv_delete_record;
    private TextView tv_audio_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment_dialog_activity_layout);

        handler_two = new Handler();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_COMMENT:
                        handleAddComment(msg);
                        break;
                }
            }
        };

        news_id = getIntent().getStringExtra("news_id");
        position = getIntent().getIntExtra("position", 0);

        et_content = (EditText)this.findViewById(R.id.id_add_comment_dialog_activity_et_content);
        rv_calcle = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_cancle);
        rv_comment = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_comment);



        rv_choose_audio_comment = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_choose_audio_comment);
        rv_choose_text_comment = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_choose_text_comment);
        rl_comment_audio_container = (RelativeLayout)this.findViewById(R.id.id_add_comment_dialog_activity_rl_comment_audio_container);
        rv_record = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_audio_record);
        rv_play_audio = (RippleView)this.findViewById(R.id.id_add_comment_dialog_activity_rv_play_audio);
        tv_play_audio_comment = (TextView)this.findViewById(R.id.id_add_comment_dialog_activity_tv_play_audio_comment);
        iv_delete_record = (ImageView)this.findViewById(R.id.id_add_comment_dialog_activity_iv_delete_record);
        tv_audio_record = (TextView)this.findViewById(R.id.id_add_comment_dialog_activity_tv_audio_record);

        rv_choose_audio_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                rv_choose_audio_comment.setVisibility(View.GONE);
                rv_choose_text_comment.setVisibility(View.VISIBLE);
                et_content.setVisibility(View.GONE);
                rl_comment_audio_container.setVisibility(View.VISIBLE);
            }
        });
        rv_choose_text_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                rv_choose_text_comment.setVisibility(View.GONE);
                rv_choose_audio_comment.setVisibility(View.VISIBLE);
                et_content.setVisibility(View.VISIBLE);
                rl_comment_audio_container.setVisibility(View.GONE);
            }
        });


        rv_record.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(tv_audio_record.getText().toString().equals("点我开始")){
                    tv_audio_record.setText("停止");
                    rv_record.setBackgroundColor(AddCommentDialogActivity.this.getResources().getColor(R.color.colorAccent));
                    //录音
                    audio_path = Environment.getExternalStorageDirectory().getPath();
                    audio_path += "/audio_temp.arm";
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mRecorder.setOutputFile(audio_path);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("NewsDetailActivity", "prepare() failed");
                    }
                    mRecorder.start();


                }else if (tv_audio_record.getText().toString().equals("停止")){
                    tv_audio_record.setText("点我开始");
                    rv_record.setBackgroundColor(AddCommentDialogActivity.this.getResources().getColor(R.color.colorPrimary));
                    rv_record.setVisibility(View.GONE);
                    rv_play_audio.setVisibility(View.VISIBLE);
                    //停止录音
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                }
            }
        });

        rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (tv_play_audio_comment.getText().toString().equals("播放")) {
                    //播放录音
                    tv_play_audio_comment.setText("停止");
                    mPlayer = new MediaPlayer();
                    try {
                        mPlayer.setDataSource(audio_path);
                        mPlayer.prepare();
                        mPlayer.start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean flag = true;
                                try {
                                    while (flag) {
                                        Thread.sleep(1000);
                                        if (mPlayer != null) {
                                            if (!mPlayer.isPlaying()) flag = false;
                                        }
                                    }

                                    handler_two.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_play_audio_comment.setText("播放");
                                        }
                                    });
                                } catch (InterruptedException e) {
                                }
                            }
                        }).start();


                    } catch (IOException e) {
                        Log.e("AddNewsActivity", audio_path);
                        Log.e("AddNewsActivity", "播放失败");
                    }


                } else if (tv_play_audio_comment.getText().toString().equals("停止")) {
                    //停止播放录音
                    tv_play_audio_comment.setText("播放");
                    mPlayer.stop();
                }
            }
        });

        iv_delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_play_audio.setVisibility(View.GONE);
                rv_record.setVisibility(View.VISIBLE);
                audio_path = "";
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        tv_play_audio_comment.setText("播放");
                        mPlayer.release();
                        mPlayer = null;
                    }
                }
            }
        });




        rv_comment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                content = et_content.getText().toString();
                if (audio_path.equals("") && content.equals("")){
                    if(content.equals("") || content.equals("评论")){
                        new Dialog(AddCommentDialogActivity.this,"Tips","请输入评论内容").show();
                        return;
                    }
                }
                addComment();
            }
        });
        rv_calcle.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    /**
     * 发布评论
     * */
    private void addComment(){
        OkhttpUtil.addComment(handler, content, news_id, "",audio_path);
    }

    /**
     * 处理发布评论
     * */
    private void handleAddComment(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddCommentRoot root = gson.fromJson(result, AddCommentRoot.class);

        if(root==null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (root.success == true){
            Intent intent = new Intent();
            intent.putExtra("comment_count",root.comment_count);
            intent.putExtra("position",position);
            intent.putExtra("result_type","comment");
            setResult(RESULT_OK, intent);
            finish();
        }else if(root.success == false){
            if(root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }


    }

    class AddCommentRoot{
        boolean success;
        String message;
        int comment_count;
        Comment comment;
    }
    class Comment{
        String id;
        String content;
        String user_id;
        String nickname;
        String username;
        String user_head_path;
        String date;
        String comment_id;
    }
}
