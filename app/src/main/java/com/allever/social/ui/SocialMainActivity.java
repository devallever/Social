package com.allever.social.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.fragment.RecommendFragment;
import com.allever.social.fragment.FriendFragment;
import com.allever.social.ui.fragment.MainFragment;
import com.allever.social.fragment.MineFragment;
import com.allever.social.mvp.base.BaseMVPActivity;
import com.allever.social.mvp.presenter.SocialMainPresenter;
import com.allever.social.mvp.view.ISocialMainView;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;


/**
 * Created by XM on 2016/5/7.
 */
public class SocialMainActivity extends BaseMVPActivity<ISocialMainView, SocialMainPresenter>
        implements View.OnClickListener, ISocialMainView{

    private static final String TAG = "SocialMainActivity";

    private TextView mTvNearby;
    private TextView mTvChat;
    private TextView tv_hot;
    private TextView tv_mine;
    private TextView mTvMsgCount;

    private RecommendFragment recommendFragment;//推荐
    private MineFragment mineFragment;//我的
    private MainFragment mainFragment;//附近
    private FriendFragment friendFragment;//聊天

    private FragmentManager fManager;

    private Bundle saveInstanceState;

    private int position = 0;
    private int mMsgCount;

    private RelativeLayout mRlMsgCount;

    private MyReceiver mMyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_main_activity_layout);

        initView();

        initData();

        //百度移动统计
        mPresenter.initMTJ();

        mPresenter.initXGPush();

        this.saveInstanceState = savedInstanceState;

        mPresenter.startLocationService();

        mPresenter.createSocialDir();

        registerBroadcast();

        setMenuItemSelected();

    }

    private void setMenuItemSelected() {
        if (saveInstanceState == null ) {//模拟一次点击，既进去后选择第一项
            //Toast.makeText(this,"saveInstanceState = null \nposition = " + position,Toast.LENGTH_LONG).show();
            mTvNearby.performClick();
        } else {
            //hideAllFragment(fManager.beginTransaction());
            position = saveInstanceState.getInt("position");
            //Toast.makeText(this,"saveInstanceState != null \nposition = " + position,Toast.LENGTH_LONG).show();
            switch (position){
                case 0:
                    mTvNearby.performClick();
                    break;
                case 1:
                    mTvChat.performClick();
                    break;
                case 2:
                    tv_hot.performClick();
                    break;
                case 3:
                    tv_mine.performClick();
                    break;
            }
            return;
        }
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.update_msg_count");
        intentFilter.addAction("com.allever.social.receiver_msg");
        mMyReceiver = new MyReceiver();
        registerReceiver(mMyReceiver, intentFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void initData() {

        mMsgCount = SharedPreferenceUtil.getMsgCount();

        if (mMsgCount !=0){
            mRlMsgCount.setVisibility(View.VISIBLE);
            mTvMsgCount.setText(mMsgCount +"");
        }

        mTvNearby.setOnClickListener(this);
        mTvChat.setOnClickListener(this);
        tv_hot.setOnClickListener(this);
        tv_mine.setOnClickListener(this);

        fManager = getSupportFragmentManager();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: ");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown: BACK");
            moveTaskToBack(false);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.d(TAG, "onKeyDown: HOME");
            moveTaskToBack(false);
            return true;
        }
        Log.d(TAG, "onKeyDown: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMyReceiver);
    }

    @Override
    public SocialMainPresenter createPresenter() {
        return new SocialMainPresenter();
    }

    @Override
    public void initView() {

        getSupportActionBar().hide();

        mTvNearby = (TextView) findViewById(R.id.id_social_main_tv_nearby);
        mTvChat = (TextView) findViewById(R.id.id_social_main_tv_chat);
        tv_hot = (TextView) findViewById(R.id.id_social_main_tv_hot);
        tv_mine = (TextView) findViewById(R.id.id_social_main_tv_mine);

        mRlMsgCount = (RelativeLayout)findViewById(R.id.id_social_main_rl_msg_count_container);
        mTvMsgCount = (TextView)findViewById(R.id.id_social_main_tv_msg_count);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTranslation = fManager.beginTransaction();
        hideAllFragment(fragmentTranslation);
        switch (view.getId()) {
            case R.id.id_social_main_tv_nearby:
                clickNearby(fragmentTranslation);
                break;
            case R.id.id_social_main_tv_chat:
                clickChat(fragmentTranslation);
                break;
            case R.id.id_social_main_tv_hot:
                clickRecommend(fragmentTranslation);
                break;
            case R.id.id_social_main_tv_mine:
                clickMine(fragmentTranslation);
                break;
            default:
                    break;
        }
    }

    private void clickMine(FragmentTransaction fragmentTransaction) {
        position = 3;
        resetBottomItemSelected();
        tv_mine.setSelected(true);
        if (mineFragment == null) {
            mineFragment = new MineFragment();
            if (saveInstanceState ==null) fragmentTransaction.add(R.id.ly_content, mineFragment,"mineFragment");
            else {
                mineFragment = (MineFragment)fManager.findFragmentByTag("mineFragment");
                if(mineFragment==null){
                    mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.ly_content, mineFragment,"mineFragment");
                }else{
                    fragmentTransaction.show(mineFragment);
                }
            }
        } else {
            fragmentTransaction.show(mineFragment);
        }
        fragmentTransaction.commit();
    }

    private void clickRecommend(FragmentTransaction fragmentTransaction) {
        position = 2;
        resetBottomItemSelected();
        tv_hot.setSelected(true);
        if (recommendFragment == null) {
            recommendFragment = new RecommendFragment();
            if (saveInstanceState ==null) fragmentTransaction.add(R.id.ly_content, recommendFragment,"recommendFragment");
            else {
                recommendFragment = (RecommendFragment)fManager.findFragmentByTag("recommendFragment");
                if(recommendFragment ==null){
                    recommendFragment = new RecommendFragment();
                    fragmentTransaction.add(R.id.ly_content, recommendFragment,"recommendFragment");
                }else{
                    fragmentTransaction.show(recommendFragment);
                }
            }
        } else {
            fragmentTransaction.show(recommendFragment);
        }
        fragmentTransaction.commit();
    }

    private void clickChat(FragmentTransaction fragmentTransaction) {
        position =1;
        resetBottomItemSelected();
        mTvChat.setSelected(true);
        if (friendFragment == null) {
            friendFragment = new FriendFragment();
            if (saveInstanceState ==null) fragmentTransaction.add(R.id.ly_content, friendFragment,"friendFragment");
            else {
                friendFragment = (FriendFragment)fManager.findFragmentByTag("friendFragment");
                if(friendFragment==null){
                    friendFragment = new FriendFragment();
                    fragmentTransaction.add(R.id.ly_content, friendFragment,"friendFragment");
                }else{
                    fragmentTransaction.show(friendFragment);
                }
            }
        } else {
            fragmentTransaction.show(friendFragment);
        }
        fragmentTransaction.commit();
    }

    private void clickNearby(FragmentTransaction fragmentTransaction) {
        position = 0;
        resetBottomItemSelected();
        mTvNearby.setSelected(true);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            if (saveInstanceState ==null) fragmentTransaction.add(R.id.ly_content, mainFragment,"mainFragment");
            else {
                mainFragment = (MainFragment)fManager.findFragmentByTag("mainFragment");
                if(mainFragment==null){
                    mainFragment = new MainFragment();
                    fragmentTransaction.add(R.id.ly_content, mainFragment,"mainFragment");
                }else{
                    fragmentTransaction.show(mainFragment);
                }
            }
        } else {
            fragmentTransaction.show(mainFragment);
        }
        fragmentTransaction.commit();
    }

    //重置所有文本的选中状态
    private void resetBottomItemSelected(){
        mTvNearby.setSelected(false);
        mTvChat.setSelected(false);
        tv_hot.setSelected(false);
        tv_mine.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(mainFragment != null)fragmentTransaction.hide(mainFragment);
        if(friendFragment != null)fragmentTransaction.hide(friendFragment);
        if(recommendFragment != null)fragmentTransaction.hide(recommendFragment);
        if(mineFragment != null)fragmentTransaction.hide(mineFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.update_msg_count":
                    mMsgCount = 0;
                    mRlMsgCount.setVisibility(View.GONE);
                    break;
                case "com.allever.social.receiver_msg":
                    String msg_type = intent.getStringExtra("msg_type");
                    if (msg_type==null) return;
                    if (msg_type.equals("add_news")){
                        mRlMsgCount.setVisibility(View.VISIBLE);
                        mMsgCount++;
                        mTvMsgCount.setText(mMsgCount + "");
                    }
                    break;
            }
        }
    }

    public static void startSelf(Context context){
        Intent intent = new Intent(context, SocialMainActivity.class);
        context.startActivity(intent);
    }
}
