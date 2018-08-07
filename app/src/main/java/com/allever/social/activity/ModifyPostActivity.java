package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.network.util.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by XM on 2016/5/22.
 * 修改职业信息界面
 */
public class ModifyPostActivity extends BaseActivity implements View.OnClickListener {

    private String postname;
    private String salary;
    private String requirement;
    private String description;
    private String post_id;

    private MaterialEditText et_postname;
    private MaterialEditText et_salary;
    private MaterialEditText et_requirement;
    private MaterialEditText et_description;
    private ButtonFlat btn_submit;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_post_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_POST:
                        handleModify(msg);
                        break;

                }
            }
        };

        post_id = getIntent().getStringExtra("post_id");
        postname= getIntent().getStringExtra("postname");
        salary = getIntent().getStringExtra("salary");
        description = getIntent().getStringExtra("description");
        requirement = getIntent().getStringExtra("requirement");


        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("修改职位信息");


        btn_submit = (ButtonFlat)this.findViewById(R.id.id_modify_post_activity_btn_submit);
        btn_submit.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));

        et_postname = (MaterialEditText)this.findViewById(R.id.id_modify_post_activity_et_postname);
        et_salary = (MaterialEditText)this.findViewById(R.id.id_modify_post_activity_et_salary);
        et_requirement = (MaterialEditText)this.findViewById(R.id.id_modify_post_activity_et_requirement);
        et_description = (MaterialEditText)this.findViewById(R.id.id_modify_post_activity_et_description);

        et_postname.setText(postname);
        et_salary.setText(salary);
        et_description.setText(description);
        et_requirement.setText(requirement);

        btn_submit.setOnClickListener(this);
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_modify_post_activity_btn_submit:
                postname = et_postname.getText().toString();
                if(postname.equals("")){
                    new Dialog(this,"Tips", "请输入职位名称.").show();
                    return;
                }

                salary = et_salary.getText().toString();
                if(salary.equals("")){
                    new Dialog(this,"Tips", "请输入薪资.").show();
                    return;
                }

                description = et_description.getText().toString();
                if(description.equals("")){
                    new Dialog(this,"Tips", "请输入职位描述.").show();
                    return;
                }

                requirement = et_requirement.getText().toString();
                if(requirement.equals("")){
                    new Dialog(this,"Tips", "请输入任职要求.").show();
                    return;
                }

                modifyPost();

                break;
        }
    }


    /**
     * 修改职位信息
     * **/
    private void modifyPost(){
        OkhttpUtil.modifyPost(handler, post_id, postname, salary, requirement, description);
    }
    /**
     * 处理修改职位信息
     * **/
    private void handleModify(Message msg){
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            if (root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }

        final Dialog dialog = new Dialog(this,"Tips", "修改成功.");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent("com.allever.social.updateNearbyPost");
                sendBroadcast(intent);
                setResult(RESULT_OK);
                ModifyPostActivity.this.finish();
            }
        });
        dialog.show();



    }

    class Root{
        boolean success;
        String message;
    }
}
