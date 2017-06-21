package com.allever.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.social.R;
import com.allever.social.activity.GetVipActivity;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/6/4.
 */
public class GetVipFragment extends Fragment {
    private static final int REQUEST_CODE_GET_VIT = 1000;
    private RippleView rv_get_vip;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.get_vip_fragment_layout,container,false);
        rv_get_vip = (RippleView)view.findViewById(R.id.id_get_vip_fg_get_vip);
        rv_get_vip.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(getActivity(),GetVipActivity.class);
                startActivityForResult(intent,REQUEST_CODE_GET_VIT);
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_GET_VIT:
                if (resultCode == getActivity().RESULT_OK) getActivity().finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
