package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/12.
 * 视频聊天设置
 */
public class VideoCallSettingActivity extends BaseActivity {

    private static final  int REQUESTCODE_CHOOSE_FEE = 1000;

    private Switch aSwitch_accept_video;
    private RippleView rv_video_call_fee;
    private TextView tv_video_call_fee;
    private Handler handler;
    private String accept_video;
    private String video_fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_call_setting_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_VIDEO_FEE_SETTING:
                        handleGetVideoFeeSetting(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SAVE_VIDEO_FEE_SETTING:
                        handleSaveVideoFeeSetting(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("视频聊天");

        initData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUESTCODE_CHOOSE_FEE:
                if (resultCode == RESULT_OK){
                    video_fee = data.getStringExtra("video_fee");
                    tv_video_call_fee.setText(video_fee + " 信用/分钟");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData(){
        aSwitch_accept_video = (Switch)this.findViewById(R.id.id_video_call_setting_switchView);
        rv_video_call_fee = (RippleView)this.findViewById(R.id.id_video_call_setting_rv_accept_video_call_fee);
        tv_video_call_fee = (TextView)this.findViewById(R.id.id_video_call_setting_tv_video_call_fee);

        rv_video_call_fee.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(VideoCallSettingActivity.this, ChooseFeeActivity.class);
                startActivityForResult(intent,REQUESTCODE_CHOOSE_FEE);
            }
        });

        getVideoFeeSetting();
    }

    private void getVideoFeeSetting(){
        OkhttpUtil.getVideoFeeSetting(handler);
    }

    private void handleGetVideoFeeSetting(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        if(root.fee.accept_video==1){
            aSwitch_accept_video.setChecked(true);
        }else{
            aSwitch_accept_video.setChecked(false);
        }

        if (aSwitch_accept_video.isCheck()) accept_video = "1";
        else accept_video = "0";
        video_fee = root.fee.video_fee+"";

        tv_video_call_fee.setText(root.fee.video_fee + " 信用/分钟");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_save:
                if (aSwitch_accept_video.isCheck()) accept_video = "1";
                else accept_video = "0";
                saveVideoFeeSetting();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存视频聊天设置
     * ***/
    private void saveVideoFeeSetting(){
        OkhttpUtil.saveVideoFeeSetting(handler,accept_video,video_fee);
    }

    private void handleSaveVideoFeeSetting(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show();
        this.finish();
    }


    class Root{
        boolean success;
        String message;
        Fee fee;
    }
    class Fee{
        int accept_video;
        int video_fee;
    }

}
