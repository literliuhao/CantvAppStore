package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：组合封装上边loadingView和下边描述信息的控件
 * 修订历史：
 * <p>
 * ================================================
 */
public class LoadingTipsView extends RelativeLayout {

    private final String TAG = "LoadingTipsView";

    private LinearLayout mContentView;
    private RotateView mLoadingView;
    private TextView mMsgView;

    private Animation mShowLoadingAnim;
    private Animation mHideLoadingAnim;

    private String msg = "加载中，请稍后";
    private Rect mContentPadding;
    private int mContentSpace;
    private int mMsgTextSize;
    private int mMsgTextColor;

    public LoadingTipsView(Context context) {
        this(context, null, 0);
    }

    public LoadingTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContentPadding = new Rect(50, 50, 50, 50);
        mContentSpace = 50;
        mMsgTextSize = 25;
        mMsgTextColor = Color.parseColor("#80FFFFFF");

        mContentView = new LinearLayout(context);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mContentView.setPadding(mContentPadding.left, mContentPadding.top, mContentPadding.right, mContentPadding.bottom);
        LayoutParams contentLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        contentLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mContentView, contentLayoutParams);

        mShowLoadingAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        mShowLoadingAnim.setFillAfter(true);
        mShowLoadingAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LoadingTipsView.this.setVisibility(View.VISIBLE);
                if (mLoadingView != null) {
                    mLoadingView.startRotate();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHideLoadingAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        mHideLoadingAnim.setFillAfter(true);
        mHideLoadingAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LoadingTipsView.this.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mLoadingView != null) {
                    mLoadingView.stopRotate();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show() {
        if (getVisibility() == View.VISIBLE) {
            return;
        }
        clearAnimation();
        startAnimation(mShowLoadingAnim);
    }

    public void hide() {
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        clearAnimation();
        startAnimation(mHideLoadingAnim);
    }

    public void setContentBackground(int resId) {
        mContentView.setBackgroundResource(resId);
    }

    public void addLoadingView(int width, int height) {
        if (mLoadingView != null) {
            Log.w(TAG, "Filed to addLoadingView.[loading view has added]");
            return;
        }
        mLoadingView = new RotateView(getContext());
        LinearLayout.LayoutParams loadingLayoutParams = new LinearLayout.LayoutParams(width, height);
        loadingLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mContentView.addView(mLoadingView, 0, loadingLayoutParams);
    }

    public void addMessageView() {
        addMessageView(null);
    }

    public void addMessageView(String msg) {
        mMsgView = new TextView(getContext());
        mMsgView.setSingleLine();
        Resources res = getResources();
        mMsgView.setText(!TextUtils.isEmpty(msg) ? msg : this.msg);
        mMsgView.setTextColor(mMsgTextColor);
        mMsgView.setTextSize(mMsgTextSize);
        LinearLayout.LayoutParams msgLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        msgLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        msgLayoutParams.topMargin = mContentSpace;
        mContentView.addView(mMsgView, mContentView.getChildCount(), msgLayoutParams);
    }

    public void setContentPadding(int left, int top, int right, int bottom) {
        mContentPadding.set(left, top, right, bottom);
        mContentView.setPadding(left, left, right, bottom);
        mContentView.requestLayout();
    }

    public void setContentSpace(int spaceInPixels){
        mContentSpace = spaceInPixels;
        if(mMsgView != null){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMsgView.getLayoutParams();
            layoutParams.topMargin = spaceInPixels;
            mMsgView.requestLayout();
        }
    }

    public void setMessageTextColor(int color){
        mMsgTextColor = color;
        if(mMsgView != null){
            mMsgView.setTextColor(color);
        }
    }

    public void setMessageTextSize(int textSize){
        mMsgTextSize = textSize;
        if(mMsgView != null){
            mMsgView.setTextSize(textSize);
        }
    }

    public RotateView getLoadingView() {
        return mLoadingView;
    }

    public TextView getMessageView() {
        return mMsgView;
    }
}
