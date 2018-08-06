package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/5/10.
 * 选择星座界面
 */
public class ChooseConstellationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayAdapter<String> ad;
    private String[] arr_constellation = {"白羊座","金牛座","双子座","巨蟹座","狮子座","处女座","天秤座","天蝎座","射手座","魔蝎座","水瓶座","双鱼座",};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_constellation_activity_layout);

        listView = (ListView)this.findViewById(R.id.id_choose_constellation_activity_listview_constellation);
        ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr_constellation);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.putExtra("constellation",arr_constellation[i]);
        setResult(RESULT_OK, intent);
        finish();
    }
}
