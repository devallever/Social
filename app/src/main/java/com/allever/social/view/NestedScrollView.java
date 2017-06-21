package com.allever.social.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by XM on 2016/4/22.
 */
public class NestedScrollView extends ListView {

   public NestedScrollView (Context context){
       super(context);
   }

    public NestedScrollView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public NestedScrollView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
