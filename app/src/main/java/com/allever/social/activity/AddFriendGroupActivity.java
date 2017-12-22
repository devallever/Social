package com.allever.social.activity;

import android.media.ToneGenerator;
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
 * 新建分组界面
 */
public class AddFriendGroupActivity extends BaseActivity {
    private EditText et_friendgroup_name;
    private String friendgroup_name;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friendgroup_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_FRIEND_GROUP:
                        handleAddFriendGroup(msg);
                        break;
                }
            }
        };

        et_friendgroup_name = (EditText)this.findViewById(R.id.id_add_friendgroup_activity_et_friendgroup_name);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新建分组");
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
                friendgroup_name = et_friendgroup_name.getText().toString();
                if (friendgroup_name.equals("")) {
                    Toast.makeText(this,"请输入组名",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                addFriendGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 新建分组
     * */
    private void addFriendGroup(){
        OkhttpUtil.addFriendGroup(handler,friendgroup_name);
    }


    /**
     * 处理新建分组
     * */
    private void handleAddFriendGroup(Message msg){
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

        Toast.makeText(this,"已添加分组",Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        this.finish();

    }


    class Root{
        boolean success;
        String message;
    }
}
