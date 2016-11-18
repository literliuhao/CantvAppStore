package com.can.appstore.download.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadStatus;

/**
 * Created by laiforg on 2016/11/14.
 */

public class RotateView extends ImageView {

    private ValueAnimator mRotateAnim;
    private int status= DownloadStatus.DOWNLOAD_STATUS_INIT;
    public RotateView(Context context) {
        this(context,null);
    }

    public RotateView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_INSIDE);
        mRotateAnim = new ValueAnimator().ofFloat(0, 1);
        mRotateAnim.setDuration(1000);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setRepeatCount(ValueAnimator.INFINITE);
    }

    public void updateStatus(int status){
        this.status=status;
        startRotate();
    }

    public void startRotate() {
        final  Drawable drawable=getDrawable();
        if(AppInstallListener.APP_INSTALLING==status&&drawable instanceof RotateDrawable){
            mRotateAnim.removeAllUpdateListeners();
            mRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    drawable.setLevel((int) (10000 * (float)valueAnimator.getAnimatedValue()));
                }
            });
            mRotateAnim.start();
        }
    }

    public void stopRotate() {
        if (mRotateAnim.isStarted()) {
            mRotateAnim.pause();
        }
    }




}
