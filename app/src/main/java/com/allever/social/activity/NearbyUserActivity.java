package com.allever.social.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.fragment.NearbyUserFragment;

/**
 * Created by allever on 17-7-3.
 */

public class NearbyUserActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_user_activity_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.id_near_by_user_activity_fg_container,new NearbyUserFragment());
        fragmentTransaction.commit();


    }
}
