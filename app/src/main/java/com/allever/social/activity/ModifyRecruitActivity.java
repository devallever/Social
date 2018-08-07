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
 */
public class ModifyRecruitActivity extends BaseActivity implements View.OnClickListener {

    private String recruit_id;
    private String companyname;
    private String link;
    private String phone;
    private String requirement;
    private String address;

    private MaterialEditText et_companyname;
    private MaterialEditText et_link;
    private MaterialEditText et_phone;
    private MaterialEditText et_requitement;
    private MaterialEditText et_address;
    private ButtonFlat btn_submit;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_recruit_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_RECRUIT:
                        handleModifyRecruit(msg);
                        break;
                }
            }
        };

        recruit_id = getIntent().getStringExtra("recruit_id");
        companyname = getIntent().getStringExtra("companyname");
        link = getIntent().getStringExtra("link");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        requirement = getIntent().getStringExtra("requirement");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("修改招聘信息");

        btn_submit = (ButtonFlat)this.findViewById(R.id.id_modify_recruit_activity_btn_submit);
        btn_submit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_submit.setOnClickListener(this);

        et_phone = (MaterialEditText)this.findViewById(R.id.id_modify_recruit_activity_et_phone);
        et_companyname = (MaterialEditText)this.findViewById(R.id.id_modify_recruit_activity_et_companyname);
        et_link = (MaterialEditText)this.findViewById(R.id.id_modify_recruit_activity_et_link);
        et_requitement = (MaterialEditText)this.findViewById(R.id.id_modify_recruit_activity_et_requirement);
        et_address = (MaterialEditText)this.findViewById(R.id.id_modify_recruit_activity_et_address);

        et_companyname.setText(companyname);
        et_link.setText(link);
        et_phone.setText(phone);
        et_address.setText(address);
        et_requitement.setText(requirement);


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


    private void  modifyRecruit(){
        OkhttpUtil.modifyRecruit(handler, recruit_id, companyname, link, phone, address, requirement);
    }

    private void handleModifyRecruit(Message msg){
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
                ModifyRecruitActivity.this.finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_modify_recruit_activity_btn_submit:
                companyname = et_companyname.getText().toString();
                if (companyname.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入公司名称.");
                    dialog.show();
                    return;
                }
                link = et_link.getText().toString();
                if (link.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入联系人.");
                    dialog.show();
                    return;
                }

                requirement = et_requitement.getText().toString();
                if (requirement.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入您的要求.");
                    dialog.show();
                    return;
                }

                address = et_address.getText().toString();
                if (address.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入您公司地址.");
                    dialog.show();
                    return;
                }

                phone = et_phone.getText().toString();
                if (phone.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入联系人.");
                    dialog.show();
                    return;
                }

                if(!phone.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
                    new Dialog(this,"Tips","请输入正确的手机号..").show();
                    return;
                }

                modifyRecruit();

                break;
        }
    }


    class Root{
        boolean success;
        String message;
    }
}
