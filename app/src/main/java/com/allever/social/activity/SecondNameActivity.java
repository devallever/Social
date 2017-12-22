package com.allever.social.activity;

import android.content.Intent;
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
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/15.
 * 备注界面
 */
public class SecondNameActivity extends BaseActivity {

    private String friend_id;
    private String old_second_name;

    private EditText et_second_name;
    private String second_name;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_name_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_SECOND_NAME:
                        handleModifySecondName(msg);
                        break;
                }
            }
        };

        friend_id = getIntent().getStringExtra("friend_id");
        old_second_name = getIntent().getStringExtra("old_second_name");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("备注");

        et_second_name = (EditText)this.findViewById(R.id.id_second_name_activity_et_second_name);
        et_second_name.setText(old_second_name);
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
                second_name = et_second_name.getText().toString();
                if (second_name.equals("")) {
                    Toast.makeText(this, "请输入备注", Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                modifySecondName();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 修改备注
     * **/
    private void modifySecondName(){
        OkhttpUtil.modifySecondName(handler, friend_id, second_name);
    }

    private void handleModifySecondName(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }


        Intent intent = new Intent();
        intent.putExtra("second_name", second_name);
        setResult(RESULT_OK, intent);
        Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();
        finish();


    }


    class Root{
        boolean success;
        String message;
    }
}
