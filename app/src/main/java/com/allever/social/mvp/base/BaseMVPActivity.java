package com.allever.social.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.ActivityCollector;

public abstract class BaseMVPActivity<V, P extends BasePresenter<V>> extends AppCompatActivity {
    protected P mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("BaseActivity", getClass().getSimpleName() + ": " + getClass().getSimpleName().hashCode());
        ActivityCollector.addActivity(this);

        //绑定Presenter
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BaseActivity", getClass().getSimpleName() + " Destroy");
        ActivityCollector.removeActivity(this);
        mPresenter.detchView();
    }

    public abstract P createPresenter();

    public abstract void initView();

    public abstract void initData();

    protected void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
