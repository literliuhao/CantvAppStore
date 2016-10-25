//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

public class FocusScaleAnimationUtils {
	private View oldView;
	private int durationLarge;
	private int durationSmall;
	private float scale;
	private ScaleAnimation scaleAnimation;
	private Interpolator interpolatorLarge;
	private Interpolator interpolatorSmall;

	public FocusScaleAnimationUtils() {
		this.durationLarge = 300;
		this.durationSmall = 500;
		this.scale = 1.1F;
		this.interpolatorLarge = new AccelerateInterpolator(1.5F);
		this.interpolatorSmall = new DecelerateInterpolator(1.5F);
	}

	public FocusScaleAnimationUtils(int duration, float scale, Interpolator interpolator) {
		this(duration, duration, scale, interpolator, interpolator);
	}

	public FocusScaleAnimationUtils(int durationLarge, int durationSmall, float scale, Interpolator interpolatorLarge,
			Interpolator interpolatorSmall) {
		this.durationLarge = 300;
		this.durationSmall = 500;
		this.scale = 1.1F;
		this.durationLarge = durationLarge;
		this.durationSmall = durationSmall;
		this.scale = scale;
		this.interpolatorLarge = interpolatorLarge;
		this.interpolatorSmall = interpolatorSmall;
	}

	public void scaleToLarge(View item) {
		if (item.isFocused()) {
			this.scaleAnimation = new ScaleAnimation(1.0F, this.scale, 1.0F, this.scale, 1, 0.5F, 1, 0.5F);
			this.scaleAnimation.setFillAfter(true);
			this.scaleAnimation.setInterpolator(this.interpolatorLarge);
			this.scaleAnimation.setDuration((long) this.durationLarge);
			this.scaleAnimation.setStartOffset(10L);
			item.startAnimation(this.scaleAnimation);
			this.oldView = item;
		}
	}

	public void scaleToNormal(View item) {
		if (this.scaleAnimation != null && item != null) {
			this.scaleAnimation.cancel();
			this.oldView = null;
		}
	}

	public void scaleToNormal() {
		this.scaleToNormal(this.oldView);
	}
}
