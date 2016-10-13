package cn.can.tvlib.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import cn.can.tvlib.R;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：使用RotateDrawable实现旋转效果（一般用于显示loading）的控件
 * 修订历史：
 * ================================================
 */
public class RotateView extends ImageView {

    private ValueAnimator mRotateAnim;
    private RotateDrawable mDrawable;

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public RotateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateView(Context context) {
        this(context, null, 0);
    }

    private void init(Context context) {
        setScaleType(ScaleType.CENTER_INSIDE);
        mDrawable = (RotateDrawable) context.getResources().getDrawable(R.drawable.rotate_drawable);
        setBackground(mDrawable);

        ValueAnimator loadingRotateAnim = new ValueAnimator().ofFloat(0, 1);
        loadingRotateAnim.setDuration(1000);
        loadingRotateAnim.setInterpolator(new LinearInterpolator());
        loadingRotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        loadingRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDrawable.setLevel((int) (10000 * (float)valueAnimator.getAnimatedValue()));
            }
        });
        mRotateAnim = loadingRotateAnim;
    }

    public void startRotate() {
        mRotateAnim.start();
    }

    public void stopRotate() {
        if (mRotateAnim.isStarted()) {
            mRotateAnim.pause();
        }
    }

}
