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
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/15.
 * 修改分组界面
 */
public class ModifyFriendGroupActivity extends BaseActivity {

    private String friendgroup_id;
    private String old_friendgroup_name;
    private EditText et_friendgroup_name;
    private String friendgroup_name;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_friendgroup_activity_layout);

        old_friendgroup_name = getIntent().getStringExtra("old_friendgroup_name");
        friendgroup_id = getIntent().getStringExtra("friendgroup_id");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_FRIEND_GROUP:
                        handleModifyFriendGroup(msg);
                        break;
                }
            }
        };

        et_friendgroup_name = (EditText)this.findViewById(R.id.id_modify_friendgroup_activity_et_friendgroup_name);
        et_friendgroup_name.setText(old_friendgroup_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("修改分组");

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
                    Toast.makeText(this, "请输入组名", Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                modifyFriendGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 修改组名
     * **/
    private void modifyFriendGroup(){
        OkhttpUtil.modifyFriendGroup(handler,friendgroup_id,friendgroup_name);
    }

    private void handleModifyFriendGroup(Message msg){
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

        Toast.makeText(this,"已修改分组",Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        this.finish();

    }

    class Root{
        boolean success;
        String message;
    }

}
