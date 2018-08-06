package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
 * Created by XM on 2016/8/3.
 */
public class FeedbackActivity extends BaseActivity {

    private EditText et_content;
    private String content;
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_FEEDBACK:
                        handleAddFeedback(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("反馈");

        initView();

    }

    private void initView(){
        et_content = (EditText)this.findViewById(R.id.id_feedback_activity_et_content);

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
                if (content.equals("")){
                    Toast.makeText(this,"请填写反馈信息",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }

                addFeedback();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFeedback(){
        OkhttpUtil.addFeedback(handler,content);
    }

    private void handleAddFeedback(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        Toast.makeText(this,"提交成功",Toast.LENGTH_LONG).show();
        finish();

    }

    class Root{
        boolean success;
        String message;
    }


}
