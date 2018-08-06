package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.widgets.Dialog;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;

/**
 * Created by XM on 2016/5/6.
 * 查看动态大图
 */
public class ShowBigImageActvity extends BaseActivity implements View.OnLongClickListener{
    private EasePhotoView epv_image;
    private ProgressBar progressBar;
    private String image_path;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_big_image_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_DOWNLOAD_IMAGE:
                        Toast.makeText(ShowBigImageActvity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };

        image_path = getIntent().getStringExtra("image_path");

        epv_image = (EasePhotoView)this.findViewById(R.id.id_show_big_iamge_iv_image);
        progressBar = (ProgressBar)this.findViewById(R.id.id_show_big_iamge_progress_bar);
        Glide.with(this)
                .load(image_path)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(epv_image);

        epv_image.setOnLongClickListener(this);

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
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_show_big_iamge_iv_image:
                Dialog  dialog = new Dialog(this,"Tips", "保存图片？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(ShowBigImageActvity.this,"okkk",Toast.LENGTH_LONG).show();
                        OkhttpUtil.downloadHeadImage(handler,image_path);
                    }
                });
                dialog.show();

                break;
        }
        return false;
    }

}
