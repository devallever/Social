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
 * Created by XM on 2016/6/3.
 * 选择感情状态界面
 */
public class ChooseEmotionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayAdapter<String> ad;
    private String[] arr_emotion = {"单身","恋爱中","已婚","离婚"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_emotion_activity_layout);

        listView = (ListView)this.findViewById(R.id.id_choose_emotion_activity_listview_emotion);
        ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr_emotion);
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
        intent.putExtra("emotion",arr_emotion[i]);
        setResult(RESULT_OK,intent);
        finish();
    }
}
