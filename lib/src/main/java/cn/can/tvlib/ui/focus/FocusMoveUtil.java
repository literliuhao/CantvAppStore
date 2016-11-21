package cn.can.tvlib.ui.focus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.HashMap;
import java.util.Iterator;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：焦点移动Util
 * 修订历史：
 * ================================================
 */

/**
 * Created by zhangbingyuan on 2016/9/21.<p/>
 * <p>
 * 焦点框移动工具类<br/>
 * <br/>
 * 1. 构造方法  初始化动画相关参数<br/>
 * 2. setFocusRes() 更改焦点框资源文件<br/>
 * 3. setFocusView() 根据提供的view的位置直接设置焦点框显示位置(无移动动画)<br/>
 * 4. setFocusLayoutParams() 设置焦点框显示位置(无移动动画)<br/>
 * 5. startMoveFocus() 移动焦点框到指定view的对应位置<br/>
 * 6. showFocus() 显示焦点框<br/>
 * 7. hideFocus() 隐藏焦点框<br/>
 * 8. release() 资源释放操作<br/>  *******  使用完后务必调用此方法
 */
public class FocusMoveUtil {

    public static final int CANCEL_FACTOR_UNLIMIT = -1;

    private boolean mCacheEnable;
    private SparseArray<Drawable> mNinePathcs;
    private SparseArray<Rect> mFocusPaddingRects;

    private FocusMoveView mFocusMoveView;
    private boolean isFocusVisible;
    private int mFocusResId;
    private Rect mFocusPaddingRect;

    private ValueAnimator mAnimator;
    private int mAnimDuration;
    private Interpolator mAnimInterpolator;
    private RectF mStartRect;
    private RectF mDestRect;
    private RectF mCurRect;
    private Rect mActiveRegion;//focus焦点框可以移动的范围

    private HashMap<Animator, RectFEvaluator> mEvaluators;
    private Handler mHandler;

    /**
     * 初始化该Util
     *
     * @param context
     * @param focusResId 焦点框资源ID
     * @param mFocusRoot 焦点框父布局（建议为DecorView）
     */
    public FocusMoveUtil(Context context, View mFocusRoot, int focusResId) {
        this(context, focusResId, mFocusRoot, false, false, 0);
    }

    /**
     * 初始化该Util
     *
     * @param context
     * @param focusResId   焦点框资源ID
     * @param mFocusRoot   焦点框父布局（建议为DecorView）
     * @param initWithHide 初始化时焦点框是否隐藏
     */
    public FocusMoveUtil(Context context, int focusResId, View mFocusRoot, boolean initWithHide) {
        this(context, focusResId, mFocusRoot, initWithHide, false, 0);
    }

    /**
     * 初始化该Util
     *
     * @param context
     * @param focusResId   焦点框资源ID
     * @param mFocusRoot   焦点框父布局（建议为DecorView）
     * @param initWithHide 初始化时焦点框是否隐藏
     * @param cacheEnable  是否使用焦点框资源文件缓存
     * @param cacheSize    缓存大小，即缓存的资源文件信息个数
     */
    public FocusMoveUtil(Context context, int focusResId, View mFocusRoot, boolean initWithHide, boolean cacheEnable, int cacheSize) {
        initFocusView(context, mFocusRoot, initWithHide);
        if (cacheEnable) {
            initCache(cacheSize);
        }
        setFocusRes(context, focusResId);
        mStartRect = new RectF();
        mCurRect = new RectF();
        mDestRect = new RectF();
        mAnimDuration = 300;
        mAnimInterpolator = new LinearInterpolator();
        mEvaluators = new HashMap<>();
    }

    private void initCache(int cacheSize) {
        if (cacheSize <= 0) {
            return;
        }
        this.mCacheEnable = true;
        mNinePathcs = new SparseArray<>(cacheSize);
        mFocusPaddingRects = new SparseArray<>(cacheSize);
    }

    private void initFocusView(Context context, View focusParent, boolean initWithHide) {
        mFocusMoveView = new FocusMoveView(context);
        mFocusMoveView.setFocusable(false);
        mFocusMoveView.setClickable(false);
        isFocusVisible = !initWithHide;
        if (initWithHide) {
            mFocusMoveView.setVisibility(View.INVISIBLE);
        }
        if (focusParent instanceof ViewGroup) {
            ((ViewGroup) focusParent).addView(mFocusMoveView, new ViewGroup.LayoutParams(-1, -1));
        }
    }

    /**
     * 设置焦点框资源文件
     *
     * @param context
     * @param focusResId
     * @throws Resources.NotFoundException
     */
    public void setFocusRes(Context context, int focusResId) throws Resources.NotFoundException {
        if (mFocusResId == focusResId) {
            return;
        }
        mFocusResId = focusResId;

        Drawable focusDrawable = null;
        Rect paddingRect = null;
        if (mCacheEnable && mNinePathcs.indexOfKey(focusResId) >= 0) {
            focusDrawable = mNinePathcs.get(mFocusResId);
            paddingRect = mFocusPaddingRects.get(mFocusResId);
        } else {
            try {
                Bitmap resBmp = BitmapFactory.decodeResource(context.getResources(), focusResId);
                byte[] ninePatchChunk = resBmp.getNinePatchChunk();

                Drawable drawable;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(focusResId, null);
                } else {
                    drawable = context.getResources().getDrawable(focusResId);
                }

                if (NinePatch.isNinePatchChunk(ninePatchChunk)) {
                    focusDrawable = new NinePatchDrawable(context.getResources(), new NinePatch(resBmp, ninePatchChunk));
                    paddingRect = new Rect();
                    drawable.getPadding(paddingRect);
                } else {
                    focusDrawable = drawable;
                }

                if (mCacheEnable) {
                    mNinePathcs.put(focusResId, focusDrawable);
                    mFocusPaddingRects.put(focusResId, paddingRect);
                }
            } catch (Exception e) {
            }
        }
        mFocusPaddingRect = paddingRect;
        mFocusMoveView.setFocusResource(focusDrawable);
    }

    /**
     * 限制焦点框可以移动的区域
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setFocusActiveRegion(int left, int top, int right, int bottom) {
        Log.d("AppListActivity", "限制焦点框可以移动的区域: "+left+"---"+top+"---"+right+"--"+bottom);
        if (mActiveRegion == null) {
            mActiveRegion = new Rect(left, top, right, bottom);
        } else {
            mActiveRegion.set(left, top, right, bottom);
        }
    }

    /**
     * 根据指定view设置焦点框位置
     *
     * @param view
     */
    public void setFocusView(View view) {
        this.setFocusLayoutParams(calculateViewRegion(view, 1, 1, 0, 0));
    }

    /**
     * 根据指定view设置焦点框位置
     *
     * @param view
     */
    public void setFocusView(View view, float scale) {
        this.setFocusLayoutParams(calculateViewRegion(view, scale, scale, 0, 0));
    }

    /**
     * 根据指定view设置焦点框位置
     *
     * @param view
     */
    public void setFocusView(View view, float scaleX, float scaleY) {
        this.setFocusLayoutParams(calculateViewRegion(view, scaleX, scaleY, 0, 0));
    }

    /**
     * 指定焦点框位置
     *
     * @param rect
     */
    public void setFocusLayoutParams(RectF rect) {
        this.setFocusLayoutParams(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * 指定焦点框位置
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setFocusLayoutParams(float left, float top, float right, float bottom) {
        left -= mFocusPaddingRect.left;
        top -= mFocusPaddingRect.top;
        right += mFocusPaddingRect.right;
        bottom += mFocusPaddingRect.bottom;

        mStartRect.set(left, top, right, bottom);
        mCurRect.set(left, top, right, bottom);
        mFocusMoveView.setFocusLayout(left, top, right, bottom);
    }

    // 根据获取焦点的view计算焦点框大小
    private RectF calculateViewRegion(View newView, float scaleX, float scaleY, int offsetX, int offsetY) {
        RectF rectF = new RectF();

        int[] location = new int[2];
        newView.getLocationInWindow(location);

        int viewW = newView.getWidth();
        int viewH = newView.getHeight();

        float currScaleX = newView.getScaleX();
        float currScaleY = newView.getScaleY();

        if (scaleX == 1 && scaleY == 1) {
            int left = (int) (location[0] + viewW * (currScaleX - 1) / 2 + 0.5f);
            int top = (int) (location[1] + viewH * (currScaleY - 1) / 2 + 0.5f);
            int right = left + viewW;
            int bottom = top + viewH;
            rectF.set(left, top, right, bottom);

        } else {
            int left = (int) (location[0] + viewW * (currScaleX - scaleX) / 2 + 0.5f);
            int top = (int) (location[1] + viewH * (currScaleY - scaleY) / 2 + 0.5f);
            int right = (int) (left + viewW * scaleX + 0.5f);
            int bottom = (int) (top + viewH * scaleY + 0.5f);
            rectF.set(left, top, right, bottom);
        }
        if (offsetX != 0) {
            rectF.left += offsetX;
            rectF.right += offsetX;
        }
        if (offsetY != 0) {
            rectF.top += offsetY;
            rectF.bottom += offsetY;
        }
        return rectF;
    }

    /**
     * 移动焦点框
     *
     * @param view
     */
    public void startMoveFocus(View view) {
        this.startMoveFocus(view, 1, 1, 0, CANCEL_FACTOR_UNLIMIT);
    }

    /**
     * 移动焦点框
     *
     * @param view
     * @param cancelFactor 当前移动动画延迟多少帧后取消
     */
    public void startMoveFocus(View view, int cancelFactor) {
        this.startMoveFocus(view, 1, 1, 0, 0, cancelFactor);
    }

    /**
     * 移动焦点框
     *
     * @param view
     * @param scale 缩放比例
     */
    public void startMoveFocus(View view, float scale) {
        this.startMoveFocus(view, scale, scale, 0, 0, CANCEL_FACTOR_UNLIMIT);
    }

    /**
     * 移动焦点框
     *
     * @param view
     * @param scale        缩放比例
     * @param cancelFactor 当前移动动画延迟多少帧后取消
     */
    public void startMoveFocus(View view, float scale, int cancelFactor) {
        this.startMoveFocus(view, scale, scale, 0, 0, cancelFactor);
    }

    /**
     * 移动焦点框
     *
     * @param view
     * @param scale        缩放比例
     * @param offsetX      x轴偏移量
     * @param offsetY      y轴偏移量
     * @param cancelFactor 当前移动动画延迟多少帧后取消
     */
    public void startMoveFocus(View view, float scale, int offsetX, int offsetY, int cancelFactor) {
        this.startMoveFocus(view, scale, scale, offsetX, offsetY, cancelFactor);
    }

    /**
     * 移动焦点框
     *
     * @param view
     * @param scaleX       x轴缩放比例
     * @param scaleY       y轴缩放比例
     * @param offsetX      x轴偏移量
     * @param offsetY      y轴偏移量
     * @param cancelFactor 当前移动动画延迟多少帧后取消
     */
    public void startMoveFocus(View view, float scaleX, float scaleY, int offsetX, int offsetY, int cancelFactor) {
        RectF destRect = calculateViewRegion(view, scaleX, scaleY, offsetX, offsetY);
        destRect = validateActiveRegion(destRect);
        if (destRect == null) {
            return;
        }

        if (mAnimator != null && mAnimator.isRunning()) {
            if ((mDestRect != null && mDestRect.equals(destRect))) {
                return;
            }
            if (cancelFactor == 0) {
                mEvaluators.remove(mAnimator);
                mAnimator.cancel();
            } else if (cancelFactor != CANCEL_FACTOR_UNLIMIT && cancelFactor > 0) {
                RectFEvaluator evaluator = mEvaluators.get(mAnimator);
                evaluator.set(cancelFactor);
                mEvaluators.remove(mAnimator);
            }
        }


        mStartRect = mCurRect;
        mDestRect = destRect;
        resolveRectPadding(mDestRect, mFocusPaddingRect);
        RectFEvaluator rectFEvaluator = new RectFEvaluator();
        ValueAnimator am = ValueAnimator.ofObject(rectFEvaluator, mStartRect, mDestRect);
        mEvaluators.put(am, rectFEvaluator);
        am.setTarget(view);
        am.setDuration(mAnimDuration);
        am.setInterpolator(mAnimInterpolator);
        am.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEvaluators.remove(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mEvaluators.remove(mAnimator);
            }
        });
        mAnimator = am;
        am.start();
    }

    private RectF validateActiveRegion(RectF region) {
        RectF currRect = mCurRect;
        if (currRect != null && currRect.equals(region)) {
            return null;
        }

        Rect activeRegion = this.mActiveRegion;
        if (activeRegion == null) {
            return region;
        }
        if (region.left < activeRegion.left) {
            float width = region.width();
            region.left = activeRegion.left;
            region.right = region.left + width;

        } else if (region.top < activeRegion.top) {
            float height = region.height();
            region.top = activeRegion.top;
            region.bottom = region.top + height;

        } else if (region.right > activeRegion.right) {
            float width = region.width();
            region.right = activeRegion.right;
            region.left = region.right - width;

        } else if (region.bottom > activeRegion.bottom) {
            float height = region.height();
            region.bottom = activeRegion.bottom;
            region.top = region.bottom - height;
        }
        return region;
    }

    // 根据焦点框资源文件的padding（.9图内容区之外的padding）resize焦点框
    private void resolveRectPadding(RectF mDestRect, Rect mFocusPaddingRect) {
        if (mFocusPaddingRect != null) {
            mDestRect.left -= mFocusPaddingRect.left;
            mDestRect.top -= mFocusPaddingRect.top;
            mDestRect.right += mFocusPaddingRect.right;
            mDestRect.bottom += mFocusPaddingRect.bottom;
        }
    }

    // 焦点框在移动过程中每帧的位置计算和移动
    private class RectFEvaluator implements TypeEvaluator<RectF> {

        public static final int MOVE_DIRECTION_LEFT = 1;
        public static final int MOVE_DIRECTION_TOP = 2;
        public static final int MOVE_DIRECTION_RIGHT = 3;
        public static final int MOVE_DIRECTION_BOTTOM = 4;

        private boolean cancelFlag;
        private int mCancelFrame;
        private int mMaxCancelFrame;
        private int mMoveDirectionX;
        private int mMoveDirectionY;

        @Override
        public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
            if (cancelFlag) {
                if (mCancelFrame > mMaxCancelFrame) {
                    return null;
                }
                mCancelFrame++;
            }

            if (mMoveDirectionX == 0) {
                if (endValue.right > startValue.right) {
                    mMoveDirectionX = MOVE_DIRECTION_RIGHT;
                } else if (endValue.left < startValue.left) {
                    mMoveDirectionX = MOVE_DIRECTION_LEFT;
                }

                if (endValue.top < startValue.top) {
                    mMoveDirectionY = MOVE_DIRECTION_TOP;
                } else if (endValue.bottom > startValue.bottom) {
                    mMoveDirectionY = MOVE_DIRECTION_BOTTOM;
                }
            }

            float left = startValue.left + (endValue.left - startValue.left) * fraction;
            float top = startValue.top + (endValue.top - startValue.top) * fraction;
            float right = startValue.right + (endValue.right - startValue.right) * fraction;
            float bottom = startValue.bottom + (endValue.bottom - startValue.bottom) * fraction;

            RectF destRect = FocusMoveUtil.this.mDestRect;
            if (mMoveDirectionX == MOVE_DIRECTION_LEFT && left < destRect.left
                    || mMoveDirectionY == MOVE_DIRECTION_TOP && top < destRect.top
                    || mMoveDirectionX == MOVE_DIRECTION_RIGHT && right > destRect.right
                    || mMoveDirectionY == MOVE_DIRECTION_BOTTOM && bottom > destRect.bottom) {
                return null;
            }

            mCurRect.set(left, top, right, bottom);
            mFocusMoveView.setFocusLayout(left, top, right, bottom);
            return null;
        }

        public void set(int maxCancelFrame) {
            cancelFlag = true;
            mMaxCancelFrame = maxCancelFrame;
        }
    }

    /**
     * 焦点框消失一段时间后显示
     *
     * @param delayInMillis
     */
    public void hideFocusForShowDelay(int delayInMillis) {
        hideFocus();
        showFocus(delayInMillis);
    }

    /**
     * 显示焦点框
     */
    public void showFocus() {
        if (mFocusMoveView != null && !isFocusVisible) {
            isFocusVisible = true;
            mFocusMoveView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 延迟一段时间后显示焦点框
     *
     * @param delayInMillis
     */
    public void showFocus(int delayInMillis) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFocus();
            }
        }, delayInMillis);
    }

    /**
     * 隐藏焦点框
     */
    public void hideFocus() {
        if (mFocusMoveView != null && isFocusVisible) {
            isFocusVisible = false;
            mFocusMoveView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 延迟一段时间后隐藏焦点框
     *
     * @param delayInMillis
     */
    public void hideFocus(int delayInMillis) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFocus();
            }
        }, delayInMillis);
    }

    public boolean isFocusShowing() {
        return isFocusVisible;
    }

    /**
     * 释放资源 ** 务必在使用完毕后调用此方法 **
     */
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mNinePathcs != null) {
            mNinePathcs.clear();
            mNinePathcs = null;
        }
        if (mFocusPaddingRects != null) {
            mFocusPaddingRects.clear();
            mFocusPaddingRects = null;
        }
        if (mEvaluators != null) {
            Iterator<Animator> iterator = mEvaluators.keySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().cancel();
            }
            mEvaluators.clear();
            mEvaluators = null;
        }
    }

    public int getFocusResId() {
        return mFocusResId;
    }

    public int getAnimDuration() {
        return mAnimDuration;
    }

    public static class FocusMoveView extends View {

        private Drawable mFocusDrawable;
        private Rect mFocusRegion;

        public FocusMoveView(Context context) {
            super(context);
            mFocusRegion = new Rect();
        }

        private boolean isInitlized() {
            return mFocusDrawable != null;
        }

        public void setFocusResource(Drawable drawable) {
            mFocusDrawable = drawable;
            if (isInitlized()) {
                mFocusDrawable.setBounds(mFocusRegion);
                postInvalidate();
            }
        }

        public void setFocusLayout(float left, float top, float right, float bottom) {
            mFocusRegion.set((int) left, (int) top, (int) right, (int) bottom);
            if (mFocusDrawable != null) {
                mFocusDrawable.setBounds(mFocusRegion);
            }
            if (isInitlized()) {
                postInvalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (isInitlized()) {
                mFocusDrawable.draw(canvas);
            }
        }
    }
}
