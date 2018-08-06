package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.PhotoBaseAdapter;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/5/12.
 */
public class PhotoActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private GridView gridView;
    private PhotoBaseAdapter photoBaseAdapter;
    private String[] arr_news_img;
    //private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity_layout);

        arr_news_img = getIntent().getStringArrayExtra("arr_news_img");

        gridView = (GridView)this.findViewById(R.id.id_photo_activity_gridview);
        photoBaseAdapter = new PhotoBaseAdapter(this,arr_news_img);
        gridView.setAdapter(photoBaseAdapter);
        gridView.setOnItemClickListener(this);

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
        Intent intent = new Intent(this,ShowNewsImageActivity.class);
        intent.putExtra("position", i);
        intent.putExtra("listpath",arr_news_img);
        startActivity(intent);
    }
}
