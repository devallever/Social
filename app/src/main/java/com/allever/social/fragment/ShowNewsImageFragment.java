package com.allever.social.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;

/**
 * Created by XM on 2016/5/6.
 */
@SuppressLint("ValidFragment")
public class ShowNewsImageFragment extends Fragment implements View.OnLongClickListener{
    private String news_img_path;
    private Handler handler;
    private TextView tv_position;
    private int position;
    private int all;
    public ShowNewsImageFragment(){

    }

    public ShowNewsImageFragment(String path,int all, int position){
        this.news_img_path = path;
        this.position = position;
        this.all = all;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_news_image_fragment, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_DOWNLOAD_IMAGE:
                        Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        tv_position = (TextView)view.findViewById(R.id.id_show_news_image_fragment_tv_position);
        tv_position.setText((position) + "/" + all);

        EasePhotoView easePhotoView = (EasePhotoView)view.findViewById(R.id.id_show_news_image_fragment_imageview);

        easePhotoView.setOnLongClickListener(this);

        Glide.with(this)
                .load(news_img_path)
                .into(easePhotoView);

        //Picasso.with(getActivity()).load(news_img_path).into(easePhotoView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_show_news_image_fragment_imageview:
                Dialog dialog = new Dialog(getActivity(),"Tips", "保存图片？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(ShowBigImageActvity.this,"okkk",Toast.LENGTH_LONG).show();
                        OkhttpUtil.downloadNewsImage(handler,news_img_path);
                    }
                });
                dialog.show();

                break;
        }
        return false;
    }
}
