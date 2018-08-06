package com.allever.social.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.SharedPreferenceUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/8/26.
 */
public class SelectedNearbyUserDialogActivity extends BaseActivity implements RippleView.OnRippleCompleteListener,View.OnClickListener{

    private TextView tv_sex_man;
    private TextView tv_sex_woman;
    private TextView tv_sex_all;

    private EditText et_min_age;
    private EditText et_max_age;
    private EditText et_distance;

    private RippleView rv_reset;
    private RippleView rv_commit;

    private String sex;
    private String min_age;
    private String max_age;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_nearby_user_dialog_activity_layout);

        initView();

    }

    private void initView(){
        tv_sex_man = (TextView)this.findViewById(R.id.id_selected_nearby_user_dialog_tv_sex_man);
        tv_sex_woman = (TextView)this.findViewById(R.id.id_selected_nearby_user_dialog_tv_sex_woman);
        tv_sex_all = (TextView)this.findViewById(R.id.id_selected_nearby_user_dialog_tv_sex_all);

        et_min_age = (EditText)this.findViewById(R.id.id_selected_nearby_user_dialog_et_min_age);
        et_max_age = (EditText)this.findViewById(R.id.id_selected_nearby_user_dialog_et_max_age);
        et_distance = (EditText)this.findViewById(R.id.id_selected_nearby_user_dialog_et_distance);

        rv_reset = (RippleView)this.findViewById(R.id.id_selected_nearby_user_dialog_rv_reset);
        rv_commit= (RippleView)this.findViewById(R.id.id_selected_nearby_user_dialog_rv_commit);

        sex = SharedPreferenceUtil.getSelectedNearbyUserSex();
        min_age = SharedPreferenceUtil.getSelectedNearbyUserMinage();
        max_age = SharedPreferenceUtil.getSelectedNearbyUserMaxage();
        distance = SharedPreferenceUtil.getSelectedNearbyUserDistance();

        switch (sex){
            case "男":
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.colorBlue_300));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "女":
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.colorPink_300));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "全部":
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.colorGray_300));
                break;
        }

        et_min_age.setText(SharedPreferenceUtil.getSelectedNearbyUserMinage());
        et_max_age.setText(SharedPreferenceUtil.getSelectedNearbyUserMaxage());
        et_distance.setText(SharedPreferenceUtil.getSelectedNearbyUserDistance());

        rv_commit.setOnRippleCompleteListener(this);
        rv_reset.setOnRippleCompleteListener(this);

        tv_sex_man.setOnClickListener(this);
        tv_sex_woman.setOnClickListener(this);
        tv_sex_all.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_selected_nearby_user_dialog_tv_sex_man:
                sex = "男";
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.colorBlue_300));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.id_selected_nearby_user_dialog_tv_sex_woman:
                sex = "女";
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.colorPink_300));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.id_selected_nearby_user_dialog_tv_sex_all:
                sex = "全部";
                tv_sex_man.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_woman.setBackgroundColor(getResources().getColor(R.color.white));
                tv_sex_all.setBackgroundColor(getResources().getColor(R.color.colorGray_300));
                break;

        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        switch (id){
            case R.id.id_selected_nearby_user_dialog_rv_commit:
                min_age = et_min_age.getText().toString();
                if (min_age.equals("")||min_age==null) {
                    Toast.makeText(this, "请输入年龄", Toast.LENGTH_LONG).show();
                    return;
                }
                max_age = et_max_age.getText().toString();
                if (max_age.equals("")||max_age==null) {
                    Toast.makeText(this, "请输入年龄", Toast.LENGTH_LONG).show();
                    return;
                }

                distance = et_distance.getText().toString();
                if (distance.equals("")||distance==null) {
                    Toast.makeText(this, "请输入距离", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferenceUtil.setSelectedNearbyUserSex(sex);
                SharedPreferenceUtil.setSelectedNearbyUserMinage(min_age);
                SharedPreferenceUtil.setSelectedNearbyUserMaxage(max_age);
                SharedPreferenceUtil.setSelectedNearbyUserDistance(distance);

                Log.d("SelectedActivity", SharedPreferenceUtil.getSelectedNearbyUserSex() + "\n" +
                        SharedPreferenceUtil.getSelectedNearbyUserMinage() + "\n" +
                        SharedPreferenceUtil.getSelectedNearbyUserMaxage() + "\n" +
                        SharedPreferenceUtil.getSelectedNearbyUserDistance());
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.id_selected_nearby_user_dialog_rv_reset:
                SharedPreferenceUtil.setSelectedNearbyUserSex("全部");
                SharedPreferenceUtil.setSelectedNearbyUserMinage("0");
                SharedPreferenceUtil.setSelectedNearbyUserMaxage("99");
                SharedPreferenceUtil.setSelectedNearbyUserDistance("1000");
                setResult(RESULT_OK);
                finish();
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
}
