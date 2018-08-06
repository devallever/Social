package com.allever.social.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by XM on 2016/4/19.
 */
public class LinearLayoutForListView extends LinearLayout {
    private BaseAdapter adapter;
    private View.OnClickListener onClickListener = null;


    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);

            v.setOnClickListener(this.onClickListener);
            addView(v, i);
        }
        Log.v("countTAG", "" + count);
    }

    public LinearLayoutForListView(Context context) {
        super(context);
    }
}
