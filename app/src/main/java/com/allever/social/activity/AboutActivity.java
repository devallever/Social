package com.allever.social.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/5/27.
 * 关于界面
 */
public class AboutActivity extends BaseActivity {
    private TextView tv_app_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("关于互信");

        tv_app_version = (TextView)this.findViewById(R.id.id_about_activity_tv_appversion);
        try {
            PackageInfo packageInfoo = getPackageManager().getPackageInfo(this.getPackageName(),0);
            tv_app_version.setText("版本号: "  + packageInfoo.versionName);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
