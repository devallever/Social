package com.gc.flashview.effect;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class RotateTransformer implements PageTransformer {


	@Override
	public void transformPage(View view, float position) {
		if (position < -1) {
		} else if (position <= 0) {
			ViewHelper.setScaleX(view, 1 + position);
			ViewHelper.setScaleY(view, 1 + position);
			ViewHelper.setRotation(view, 360 * position);
		} else if (position <= 1) {
			ViewHelper.setScaleX(view, 1 - position);
			ViewHelper.setScaleY(view, 1 - position);
			ViewHelper.setRotation(view, 360 * position);
		} else {
		}
	}

}
