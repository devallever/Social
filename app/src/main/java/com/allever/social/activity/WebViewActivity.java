package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.modules.main.SocialMainActivity;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/6/1.
 * 浏览网页界面
 */
public class WebViewActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {
    private WebView webView;
    private String url;
    private String type;

    private RippleView rv_collect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity_layout);

        url = getIntent().getStringExtra("url");
        type= getIntent().getStringExtra("type");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("浏览网页");
        actionBar.hide();

        rv_collect = (RippleView)this.findViewById(R.id.id_web_view_activity_rv_collect);
        rv_collect.setOnRippleCompleteListener(this);

        webView = (WebView)this.findViewById(R.id.id_web_view_activity_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);//
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        switch (id){
            case R.id.id_web_view_activity_rv_collect:
                //弹窗 对话框 输入标题
                Intent intent = new Intent(this,SaveWebCollectionDialogActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_BACK) &&   webView.canGoBack()) {
            webView.goBack();
            return true;
        }else{
            if(type!=null){
                Intent intent = new Intent(this, SocialMainActivity.class);
                startActivity(intent);
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
