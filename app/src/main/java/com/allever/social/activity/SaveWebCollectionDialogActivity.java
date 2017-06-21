package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/7/30.
 */
public class SaveWebCollectionDialogActivity extends BaseActivity implements View.OnClickListener{
    private String title;
    private String url;

    private EditText et_title;
    private TextView tv_url;

    private ButtonRectangle btn_save;
    private ButtonRectangle btn_cancle;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_webcollection_dialog_activity_layout);

        url = getIntent().getStringExtra("url");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_SAVE_WEB_COLLECTION:
                        handleSaveWebCollection(msg);
                        break;
                }
            }
        };

        initView();

    }

    private void initView(){
        btn_cancle = (ButtonRectangle)this.findViewById(R.id.id_save_webcollection_dialog_activity_btn_cancel);
        btn_save = (ButtonRectangle)this.findViewById(R.id.id_save_webcollection_dialog_activity_btn_save);

        et_title = (EditText)this.findViewById(R.id.id_save_webcollection_dialog_activity_et_title);

        tv_url = (TextView)this.findViewById(R.id.id_save_webcollection_dialog_activity_tv_url);
        tv_url.setText("网址：" + url);

        btn_save.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_save_webcollection_dialog_activity_btn_save:
                title = et_title.getText().toString();
                if (title.equals("")){
                    Toast.makeText(this,"请输入网址名称",Toast.LENGTH_LONG).show();
                    return;
                }
                saveWebCollection();

                break;
            case R.id.id_save_webcollection_dialog_activity_btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void saveWebCollection(){
        OkhttpUtil.saveWebCollection(handler, title, url);
    }

    private void handleSaveWebCollection(Message msg){
        String result = msg.obj.toString();
        Log.d("MyShareRankFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(this,"提示",root.message).show();
            return;
        }

        Toast.makeText(this,"添加成功",Toast.LENGTH_LONG).show();
        finish();
    }


    class Root{
        boolean success;
        String message;
    }
}
