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

import java.util.List;

/**
 * Created by XM on 2016/5/10.
 * 选择职业界面
 */
public class ChooseOccupationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayAdapter<String> ad;
    private String[] arr_occupation = {
            "IT",
            "制造",
            "医疗",
            "金融",
            "商业",
            "农业",
            "文化",
            "艺术",
            "法律",
            "教育",
            "行政",
            "模特",
            "空姐",
            "学生"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_occupation_activity_layout);

        listView = (ListView)this.findViewById(R.id.id_choose_occupation_listview_occupation);
        ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr_occupation);
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
        intent.putExtra("occupation",arr_occupation[i]);
        setResult(RESULT_OK,intent);
        finish();
    }
}
