package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/8/2.
 */
public class SetAutoReactionActivity extends BaseActivity {

    private EditText et_content;
    private String content;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_auto_reaction_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_AUTO_REACTION:
                        handleGetAutoReaction(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SAVE_AUTO_REACTION:
                        handleSaveAutoReaction(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置自动回复");

        initView();

        //getAutoReactionContent();

    }

    private void getAutoReactionContent(){
        OkhttpUtil.getAutoReaction(handler);
    }

    private void handleGetAutoReaction(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AutoReactionRoot root = gson.fromJson(result, AutoReactionRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        et_content.setText(root.content);

    }

    private void initView(){
        et_content = (EditText)this.findViewById(R.id.id_set_auto_reaction_activity_et_content);
        et_content.setText(SharedPreferenceUtil.getAutoReaction());
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
                content = et_content.getText().toString();
                if (OkhttpUtil.checkLogin()) saveAutoReaction();
                else Toast.makeText(this,"先登录",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAutoReaction(){
        OkhttpUtil.saveAutoReaction(handler,content);
    }

    private void handleSaveAutoReaction(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AutoReactionRoot root = gson.fromJson(result, AutoReactionRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        et_content.setText(root.content);
        SharedPreferenceUtil.setAutoReaction(root.content);
        Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show();
        this.finish();
    }


    class AutoReactionRoot{
        boolean success;
        String message;
        String content;
    }
}
