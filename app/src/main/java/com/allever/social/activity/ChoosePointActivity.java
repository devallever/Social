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
import com.allever.social.utils.SerializableHttpCookie;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;

import java.util.List;

/**
 * Created by XM on 2016/5/13.
 * 选择建群地点
 */
public class ChoosePointActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private ArrayAdapter<String> ad;
    private String[] arr_point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_point_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择地点");

        arr_point = SharedPreferenceUtil.getPoints().split("_");//保存在shareperference  数据格式：xx_x_xxx_
        listView = (ListView)this.findViewById(R.id.id_choose_point_listview);
        ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr_point);
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
        Intent intent = new Intent();
        intent.putExtra("point",arr_point[i]);
        setResult(RESULT_OK,intent);
        finish();
    }
}
