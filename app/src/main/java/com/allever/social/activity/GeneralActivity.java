package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;

/**
 * Created by XM on 2016/6/12.
 * 设置界面/通用
 */
public class GeneralActivity extends BaseActivity implements RippleView.OnRippleCompleteListener{

    private RippleView rv_ad;
    private RippleView rv_video;
    private RippleView rv_clean_cache;
    private RippleView rv_auto_recation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");

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


    private void initData(){
        rv_ad = (RippleView)this.findViewById(R.id.id_general_activity_rv_ad);
        rv_ad.setOnRippleCompleteListener(this);

        rv_video = (RippleView)this.findViewById(R.id.id_general_activity_rv_video_setting);
        rv_video.setOnRippleCompleteListener(this);

        rv_clean_cache = (RippleView)this.findViewById(R.id.id_general_activity_rv_clean_cache);
        rv_clean_cache.setOnRippleCompleteListener(this);

        rv_auto_recation = (RippleView)this.findViewById(R.id.id_general_activity_rv_autoreaction);
        rv_auto_recation.setOnRippleCompleteListener(this);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        Intent intent;
        int id = rippleView.getId();
        switch (id){
            case R.id.id_general_activity_rv_ad:
                intent = new Intent(GeneralActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.id_general_activity_rv_video_setting:
                intent = new Intent(GeneralActivity.this, VideoCallSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.id_general_activity_rv_clean_cache:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(MyApplication.mContext).clearDiskCache();
                    }
                }).start();
                Toast.makeText(this,"清理完成",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_general_activity_rv_autoreaction:
                intent = new Intent(this,SetAutoReactionActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
