package com.allever.social.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.allever.social.R;
import com.allever.social.activity.RegistActivity;
import com.allever.social.mvp.base.BaseMVPActivity;
import com.allever.social.mvp.presenter.FirstPresenter;
import com.allever.social.mvp.view.IFirstView;
import com.allever.social.utils.Constants;
import com.andexert.library.RippleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by XM on 2016/10/7.
 */
public class FirstActivity extends BaseMVPActivity<IFirstView, FirstPresenter> implements IFirstView {

    private static final String TAG = "FirstActivity";

    private static final int REQUEST_CODE_FIRST_ACTIVITY_REGISTER = 1001;

    private RippleView mRvLogin;
    private RippleView mRvRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity_layout);

        EventBus.getDefault().register(this);

        initView();
        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public FirstPresenter createPresenter() {
        return new FirstPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        finish();
    }

    @Override
    public void initView(){
        mRvLogin = (RippleView)this.findViewById(R.id.id_first_activity_rv_login);
        mRvLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mRvRegister = (RippleView)this.findViewById(R.id.id_first_activity_rv_regist);
        mRvRegister.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(FirstActivity.this,RegistActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FIRST_ACTIVITY_REGISTER);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceive(String event){
        switch (event){
            case Constants.EVENT_FINISH_ACTIVITY:
                finish();
                break;
            default:
                break;
        }
    }

    public static void startSelf(Context context){
        Intent intent = new Intent(context, FirstActivity.class);
        context.startActivity(intent);
    }
}
