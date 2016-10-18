//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.can.tvlib.ui.focus;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;


/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：view缩放Util
 * 修订历史：
 * ================================================
 */
/**
 * 用于view缩放<br/>
 * <p>
 * 1. 构造方法  初始化动画相关参数
 * 2. scaleToLarge() 放大指定view
 * 3. scaleToNormal() 恢复指定view到正常大小
 */
public class FocusScaleUtil {

    private View oldView;
    private int durationLarge;
    private int durationSmall;
    private float scale;
    private AnimatorSet animatorSet;
    private Interpolator interpolatorLarge;
    private Interpolator interpolatorSmall;

    public FocusScaleUtil() {
        this(300, 500, 1.1f, new AccelerateInterpolator(1.5f), new DecelerateInterpolator(1.5f));
    }

    public FocusScaleUtil(int duration, float scale, Interpolator interpolator) {
        this(duration, duration, scale, interpolator, interpolator);
    }

    public FocusScaleUtil(int durationLarge, int durationSmall, float scale, Interpolator interpolatorLarge,
                          Interpolator interpolatorSmall) {
        this.durationLarge = durationLarge;
        this.durationSmall = durationSmall;
        this.scale = scale;
        this.interpolatorLarge = interpolatorLarge;
        this.interpolatorSmall = interpolatorSmall;
    }

    public void scaleToLarge(View item) {
        scaleToLarge(item, this.scale, this.scale);
    }

    public void scaleToLarge(View item, float scaleW, float scaleH) {
        if (item.isFocused()) {
            this.animatorSet = new AnimatorSet();
            this.animatorSet.setDuration((long) this.durationLarge);
            this.animatorSet.setInterpolator(this.interpolatorLarge);
            this.animatorSet.play(ObjectAnimator.ofFloat(item, "ScaleX", 1.0F, scaleW)).with(ObjectAnimator.ofFloat(item, "ScaleY", 1.0F, scaleH));
            this.animatorSet.start();
            this.oldView = item;
        }
    }

    public void scaleToNormal() {
        this.scaleToNormal(this.oldView);
    }

    public void scaleToNormal(View item) {
        if (this.animatorSet != null && item != null) {
            if (this.animatorSet.isRunning()) {
                this.animatorSet.cancel();
            }
            item.animate().scaleX(1f).scaleY(1f).setDuration(durationSmall).setInterpolator(interpolatorSmall).start();
            this.oldView = null;
        }
    }

    public void setFocusScale(float scale){
        this.scale = scale;
    }

}
