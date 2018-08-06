package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.ShareRankItemBaseAdapter;
import com.allever.social.adapter.WebCollectionItemBaseAdapter;
import com.allever.social.pojo.ShareRankItem;
import com.allever.social.pojo.WebCollectionItem;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/7/30.
 */
public class WebCollectionActivity extends BaseActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private ListView listView;

    private List<WebCollectionItem> list_webcollection_item;

    private WebCollectionItemBaseAdapter webCollectionItemBaseAdapter;

    private int page = 1;
    private int seletced_long_position;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webcollection_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_WEB_COLLECTION_LIST:
                        handleWebcollectionList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_WEB_COLLECTION:
                        handleDeleteWebCollection(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("收藏");

        initView();

        getWebcollectionList();
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

    private void initView(){
        listView = (ListView)this.findViewById(R.id.id_webcollection_activity_listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        list_webcollection_item = new ArrayList<>();
    }

    private void getWebcollectionList(){
        OkhttpUtil.getWebcollectionList(handler, page + "");
    }

    private void handleWebcollectionList(Message msg){
        String result = msg.obj.toString();
        Log.d("ShareRankFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
            return;
        }

        if (page == 1) list_webcollection_item.clear();
        WebCollectionItem webCollectionItem;
        for (WebCollection webCollection: root.list_webcollection){
            webCollectionItem = new WebCollectionItem();
            webCollectionItem.setId(webCollection.id);
            webCollectionItem.setTitle(webCollection.title);
            webCollectionItem.setUrl(webCollection.url);
            list_webcollection_item.add(webCollectionItem);
        }

        webCollectionItemBaseAdapter = new WebCollectionItemBaseAdapter(this,list_webcollection_item);
        listView.setAdapter(webCollectionItemBaseAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //刷新
        Intent intent = new Intent(this,WebViewActivity.class);
        intent.putExtra("url",list_webcollection_item.get(i).getUrl());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //删除操作
        seletced_long_position  = i;
        Dialog dialog = new Dialog(this,"提示","确定删除该收藏吗?");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWebcollection();
            }
        });
        dialog.show();
        return false;
    }

    private void deleteWebcollection(){
        OkhttpUtil.deleteWebCollection(handler, list_webcollection_item.get(seletced_long_position).getId());
    }

    private void handleDeleteWebCollection(Message msg){
        String result = msg.obj.toString();
        Log.d("ShareRankFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        DeleteWebCollectionRoot root = gson.fromJson(result, DeleteWebCollectionRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
            return;
        }

        getWebcollectionList();

    }

    class Root{
        boolean success;
        String message;
        List<WebCollection> list_webcollection;
    }

    class WebCollection{
        String id;
        String title;
        String url;
    }

    class DeleteWebCollectionRoot{
        boolean success;
        String message;
    }
}
