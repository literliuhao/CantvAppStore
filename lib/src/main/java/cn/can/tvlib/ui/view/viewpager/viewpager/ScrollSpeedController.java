package cn.can.tvlib.ui.view.viewpager.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.DecelerateInterpolator;

import java.lang.reflect.Field;

import cn.can.tvlib.ui.view.LazyViewPager;

/**
 * ================================================
 * 作    者：zby
 * 版    本：1.0
 * 创建日期：2016.11.28
 * 描    述：用于控制viewpager滑动速度
 * 修订历史：
 *
 * ================================================
 */
public class ScrollSpeedController {

    private static FixedSpeedScroller mScroller = null;
    /**
     * 设置ViewPager的滑动时间
     * @param context 
     * @param viewpager ViewPager控件
     * @param DurationSwitch 滑动延时
     */  
    public static void controlViewPagerSpeed(Context context, ViewPager viewpager, int DurationSwitch) {
        try {  
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);  

            mScroller = new FixedSpeedScroller(context, new DecelerateInterpolator());
            mScroller.setmDuration(DurationSwitch);
            mField.set(viewpager, mScroller);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }

    /**
     * 设置ViewPager的滑动时间
     * @param context
     * @param viewpager ViewPager控件
     * @param DurationSwitch 滑动延时
     */
    public static void controlViewPagerSpeed(Context context, LazyViewPager viewpager, int DurationSwitch) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);

            mScroller = new FixedSpeedScroller(context, new DecelerateInterpolator());
            mScroller.setmDuration(DurationSwitch);
            mField.set(viewpager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}