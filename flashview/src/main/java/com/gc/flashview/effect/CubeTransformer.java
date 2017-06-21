package com.gc.flashview.effect;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class CubeTransformer implements PageTransformer {
	

	@Override
	public void transformPage(View view, float position) {
		if (position <= 0) {

			ViewHelper.setPivotX(view, view.getMeasuredWidth());
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			

			ViewHelper.setRotationY(view, 90f * position);
		} else if (position <= 1) {

			ViewHelper.setPivotX(view, 0);
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			ViewHelper.setRotationY(view, 90f * position);
		}
	}
}
