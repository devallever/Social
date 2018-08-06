package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.andexert.library.RippleView;

/**
 * Created by XM on 2016/10/7.
 */
public class FirstActivity extends BaseActivity {

    private static final int REQUEST_CODE_FIRST_ACTIVITY_LOGIN = 1000;
    private static final int REQUEST_CODE_FIRST_ACTIVITY_REGIST = 1001;

    private RippleView rv_login;
    private RippleView rv_regist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity_layout);

        initView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_FIRST_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK){
                    //Intent intent = new Intent(this, SocialMainActivity.class);
                    //startActivity(intent);
                    Intent intent = new Intent(this,ShuaShuaActivity.class);
                    intent.putExtra("is_first",true);
                    startActivity(intent);
                    finish();
                }
                break;
            case REQUEST_CODE_FIRST_ACTIVITY_REGIST:
                if (resultCode == RESULT_OK){
//                    Intent intent = new Intent(this,SocialMainActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(this,ShuaShuaActivity.class);
                    intent.putExtra("is_first",true);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void initView(){
        rv_login = (RippleView)this.findViewById(R.id.id_first_activity_rv_login);
        rv_login.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                intent.putExtra("isFromFirstActivity",true);
                startActivityForResult(intent, REQUEST_CODE_FIRST_ACTIVITY_LOGIN);
                //FirstActivity.this.finish();
            }
        });
        rv_regist = (RippleView)this.findViewById(R.id.id_first_activity_rv_regist);
        rv_regist.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //Intent intent = new Intent(FirstActivity.this, RegistFirstAcrivity.class);
                Intent intent = new Intent(FirstActivity.this,RegistActivity.class);
                startActivityForResult(intent,REQUEST_CODE_FIRST_ACTIVITY_REGIST);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
