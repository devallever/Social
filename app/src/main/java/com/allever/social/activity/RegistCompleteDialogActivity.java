package com.allever.social.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.andexert.library.RippleView;

/**
 * Created by XM on 2016/10/7.
 */
public class RegistCompleteDialogActivity extends BaseActivity {

    private static final int REQUEST_CODE_AFTER_REGIST_COMPLETE = 1000;

    private TextView tv_username;
    private String username = "";

    private RippleView rv_share;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_complete_dialog_activity_layout);

        username = getIntent().getStringExtra("username");

        initView();
    }

    private void initView(){
        tv_username = (TextView)this.findViewById(R.id.id_regist_complete_dialog_activity_tv_account);
        tv_username.setText("您的互信号为：" +username);
        rv_share = (RippleView)this.findViewById(R.id.id_regist_complete_dialog_activity_rv_share);
        rv_share.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //share Dialog
                Intent intent = new Intent(RegistCompleteDialogActivity.this,AfterRegistShareDialogActivity.class);
                intent.putExtra("username",username);
                startActivityForResult(intent,REQUEST_CODE_AFTER_REGIST_COMPLETE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_AFTER_REGIST_COMPLETE:
                if (resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"邀请好友玩互信.",Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }
}
