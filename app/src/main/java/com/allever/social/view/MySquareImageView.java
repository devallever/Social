package com.allever.social.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by XM on 2016/7/13.
 */
public class MySquareImageView extends ImageView {
    public MySquareImageView(Context context){
        super(context);
    }

    public MySquareImageView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public MySquareImageView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
