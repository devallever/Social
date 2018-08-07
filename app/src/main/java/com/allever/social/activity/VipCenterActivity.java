package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.flashview.FlashView;
import com.gc.flashview.listener.FlashViewListener;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/3.
 * 会员中心界面
 */
public class VipCenterActivity extends BaseActivity {
    private static final int REQUEST_CODE_GET_VIT = 1000;

    private FlashView flashView_ad;
    private Handler handler;
    private List<AdDetail> list_addetail = new ArrayList<>();
    private List<String> imageUrls;

    private ImageView iv_head;
    private TextView tv_vip_logo;
    private RippleView rv_get;
    private TextView tv_vip_desc;
    private RippleView rv_get_vip;
    private RippleView rv_visited_user;
    private RippleView rv_add_recruit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vip_center_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AD_DETAIL:
                        handleADDetail(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("会员中心");

        initData();

        getADurl();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_GET_VIT:
                if (resultCode == RESULT_OK) this.finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void  getADurl(){
        OkhttpUtil.getAdDdtail(handler, "2");
    }

    private void handleADDetail(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADDetailRoot  root = gson.fromJson(result, ADDetailRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        list_addetail = root.addetail_list;
        for(AdDetail adDetail : list_addetail){
            imageUrls.add(WebUtil.HTTP_ADDRESS + adDetail.ad_path);
        }
        flashView_ad.setImageUris(imageUrls);
    }

    private void initData(){
        imageUrls = new ArrayList<>();
        flashView_ad = (FlashView)this.findViewById(R.id.id_vip_center_activity_flash_view_ad_bar);
        flashView_ad.setOnPageClickListener(new FlashViewListener() {
            @Override
            public void onClick(int position) {
                if(list_addetail.size()>0) {
                    Intent intent = new Intent(VipCenterActivity.this, WebViewActivity.class);
                    intent.putExtra("url",list_addetail.get(position).url);
                    if(list_addetail.size()>0) startActivity(intent);
                }
            }
        });

        iv_head = (ImageView)this.findViewById(R.id.id_vip_center_activity_iv_head);
        tv_vip_logo = (TextView)this.findViewById(R.id.id_vip_center_activity_tv_vip_logo);
        rv_get = (RippleView)this.findViewById(R.id.id_vip_center_activity_rv_get);
        tv_vip_desc = (TextView)this.findViewById(R.id.id_vip_center_activity_tv_vip_desc);
        rv_visited_user = (RippleView)this.findViewById(R.id.id_vip_center_activity_rv_visited_user);
        rv_add_recruit = (RippleView)this.findViewById(R.id.id_vip_center_activity_rv_add_recruit);
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .into(iv_head);

        if (SharedPreferenceUtil.getVip().equals("1")){
            tv_vip_logo.setBackgroundColor(getResources().getColor(R.color.colorRed_500));
            rv_get.setVisibility(View.INVISIBLE);
            tv_vip_desc.setText("vip会员尊享各种特权");
        }else{
            tv_vip_logo.setBackgroundColor(getResources().getColor(R.color.colorGray_300));
            rv_get.setVisibility(View.VISIBLE);
        }


        rv_get.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(VipCenterActivity.this,GetVipActivity.class);
                startActivityForResult(intent,REQUEST_CODE_GET_VIT);
                //startActivity(intent);
            }
        });

        rv_add_recruit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent;
                if (SharedPreferenceUtil.getVip().equals("1")){
                    intent = new Intent(VipCenterActivity.this, AddRecruitActivity.class);
                    startActivity(intent);
                }else{
                    Dialog dialog  = new Dialog(VipCenterActivity.this,"提示","您不是会员,无法发布招聘。\n是否开通会员?");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VipCenterActivity.this, GetVipActivity.class);
                            startActivity(intent);

                        }
                    });
                    dialog.show();
                }
            }
        });

        rv_visited_user.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(VipCenterActivity.this,VisitedUserActivity.class);
                startActivity(intent);
            }
        });



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


    class ADDetailRoot{
        boolean success;
        String message;
        List<AdDetail> addetail_list;
    }

    class AdDetail{
        String id;
        String ad_path;
        String url;
    }

}
