package com.allever.social;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

/**
 * Created by Allever on 2016/12/3.
 */

public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;

    protected void showProgressDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    protected void dismissProgressDialog(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
