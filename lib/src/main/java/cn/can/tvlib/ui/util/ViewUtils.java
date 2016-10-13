package cn.can.tvlib.ui.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by zhangbingyuan on 2016/9/12.
 */

public class ViewUtils {

    /**
     * 立即显示view
     * @param view
     */
    public static void show(View view){
        if(view.getVisibility() == View.VISIBLE){
            return;
        }
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 立即隐藏view
     * @param view
     */
    public static void hide(View view){
        if(view.getVisibility() != View.VISIBLE){
            return;
        }
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * 应用渐入动画到view
     * @param context
     * @param view
     */
    public static void fadeIn(Context context, final View view){
        if(view.getVisibility() == View.VISIBLE){
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    /**
     * 应用出动画到view
     * @param context
     * @param view
     */
    public static void fadeOut(Context context, final View view){
        if(view.getVisibility() != View.VISIBLE){
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }



}
