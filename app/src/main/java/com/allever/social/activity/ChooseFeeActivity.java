package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/6/12.
 * 选择视频聊天单价
 */
public class ChooseFeeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private String video_fee;
    private ListView listView;
    private ArrayAdapter<String> ad;
    private String[] arr_fee = {
            "0 信用/分钟",
            "1 信用/分钟",
            "2 信用/分钟",
            "3 信用/分钟",
            "4 信用/分钟",
            "5 信用/分钟",
            "6 信用/分钟",
            "7 信用/分钟",
            "8 信用/分钟",
            "9 信用/分钟",
            "10 信用/分钟",
            "15 信用/分钟",
            "20 信用/分钟",
            "30 信用/分钟",
            "50 信用/分钟",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_fee_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择费用");

        listView = (ListView)this.findViewById(R.id.id_choose_fee_activity_listview);
        ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr_fee);
        listView.setAdapter(ad);

        listView.setOnItemClickListener(this);

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                video_fee = "0";
                break;
            case 1:
                video_fee = "1";
                break;
            case 2:
                video_fee = "2";
                break;
            case 3:
                video_fee = "3";
                break;
            case 4:
                video_fee = "4";
                break;
            case 5:
                video_fee = "5";
                break;
            case 6:
                video_fee = "6";
                break;
            case 7:
                video_fee = "7";
                break;
            case 8:
                video_fee = "8";
                break;
            case 9:
                video_fee = "9";
                break;
            case 10:
                video_fee = "10";
                break;
            case 11:
                video_fee = "15";
                break;
            case 12:
                video_fee = "20";
                break;
            case 13:
                video_fee = "30";
                break;
            case 14:
                video_fee = "50";
                break;

        }

        Intent intent = new Intent();
        intent.putExtra("video_fee",video_fee);
        setResult(RESULT_OK, intent);
        finish();

    }

}
