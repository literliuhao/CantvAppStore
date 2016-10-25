//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class FocusScaleUtils {
    private View oldView;
    private int durationLarge;
    private int durationSmall;
    private float scale;
    private AnimatorSet animatorSet;
    private ObjectAnimator largeX;
    private Interpolator interpolatorLarge;
    private Interpolator interpolatorSmall;

    public FocusScaleUtils() {
        this.durationLarge = 300;
        this.durationSmall = 500;
        this.scale = 1.1F;
        this.interpolatorLarge = new AccelerateInterpolator(1.5F);
        this.interpolatorSmall = new DecelerateInterpolator(1.5F);
    }

    public FocusScaleUtils(int duration, float scale, Interpolator interpolator) {
        this(duration, duration, scale, interpolator, interpolator);
    }

    public FocusScaleUtils(int durationLarge, int durationSmall, float scale, Interpolator interpolatorLarge,
                           Interpolator interpolatorSmall) {
        this.durationLarge = 500;
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
            this.animatorSet = new AnimatorSet();
            this.largeX = ObjectAnimator.ofFloat(item, "ScaleX", new float[]{1.0F, this.scale});
            ObjectAnimator largeY = ObjectAnimator.ofFloat(item, "ScaleY", new float[]{1.0F, this.scale});
            this.animatorSet.setDuration((long) this.durationLarge);
            this.animatorSet.setInterpolator(this.interpolatorLarge);
            this.animatorSet.play(this.largeX).with(largeY);
            this.animatorSet.start();
            this.oldView = item;
        }
    }

    public void scaleToLargeWH(View item, float scaleW, float scaleH) {
        if (item.isFocused()) {
            this.animatorSet = new AnimatorSet();
            this.largeX = ObjectAnimator.ofFloat(item, "ScaleX", new float[]{1.0F, scaleW});
            ObjectAnimator largeY = ObjectAnimator.ofFloat(item, "ScaleY", new float[]{1.0F, scaleH});
            this.animatorSet.setDuration((long) this.durationLarge);
            this.animatorSet.setInterpolator(this.interpolatorLarge);
            this.animatorSet.play(this.largeX).with(largeY);
            this.animatorSet.start();
            this.oldView = item;
        }
    }

    public void scaleToNormal(View item) {
        if (this.animatorSet != null && item != null) {
            if (this.animatorSet.isRunning()) {
                this.animatorSet.cancel();
            }

            ObjectAnimator oa = ObjectAnimator.ofFloat(item, "ScaleX", new float[]{1.0F});
            oa.setDuration((long) this.durationSmall);
            oa.setInterpolator(this.interpolatorSmall);
            oa.start();
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(item, "ScaleY", new float[]{1.0F});
            oa.setInterpolator(this.interpolatorSmall);
            oa2.setDuration((long) this.durationSmall);
            oa2.start();
            this.oldView = null;
        }
    }

    public void scaleToNormal() {
        this.scaleToNormal(this.oldView);
    }

}
