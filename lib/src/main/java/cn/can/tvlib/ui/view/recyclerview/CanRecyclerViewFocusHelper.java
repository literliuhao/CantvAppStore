package cn.can.tvlib.ui.view.recyclerview;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

/**
 * Created by zhangbingyuan on 2016/10/16.
 */

public class CanRecyclerViewFocusHelper {

    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private RecyclerView mAttachedView;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private ViewTreeObserver.OnGlobalFocusChangeListener mFocusChangeListener;
    private Handler mHandler;
    private View mCurrFocusView;
    private Runnable mFocusMoveRunanble;

    public CanRecyclerViewFocusHelper(FocusMoveUtil focusMoveUtil, FocusScaleUtil focusScaleUtil) {
        this.mFocusMoveUtil = focusMoveUtil;
        this.mFocusScaleUtil = focusScaleUtil;

        mHandler = new Handler();
        mFocusMoveRunanble = new Runnable() {
            @Override
            public void run() {
                if(mCurrFocusView != null && mCurrFocusView.isFocused()){
                    mFocusMoveUtil.startMoveFocus(mCurrFocusView);
                    mFocusScaleUtil.scaleToLarge(mCurrFocusView);
                }
            }
        };

        mFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                mFocusScaleUtil.scaleToNormal(oldFocus);
                mCurrFocusView = newFocus;
                mHandler.removeCallbacks(mFocusMoveRunanble);
                mHandler.postDelayed(mFocusMoveRunanble, 30);
            }
        };

        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mHandler.removeCallbacks(mFocusMoveRunanble);
                mHandler.postDelayed(mFocusMoveRunanble, 30);
            }
        };
    }

    public void attachToRecyclerView(RecyclerView attachedView){
        if(mAttachedView != null){
            mAttachedView.getViewTreeObserver().removeOnGlobalFocusChangeListener(mFocusChangeListener);
            mAttachedView.removeOnScrollListener(mOnScrollListener);
        }
        this.mAttachedView = attachedView;
        mAttachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(mFocusChangeListener);
        mAttachedView.addOnScrollListener(mOnScrollListener);
    }

    public void release(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if(mAttachedView != null){
            mAttachedView.getViewTreeObserver().removeOnGlobalFocusChangeListener(mFocusChangeListener);
            mFocusChangeListener = null;
            mAttachedView.removeOnScrollListener(mOnScrollListener);
            mOnScrollListener = null;
            mAttachedView = null;
        }
        mCurrFocusView = null;
        mFocusMoveRunanble = null;
        mFocusMoveUtil.release();
        mFocusMoveUtil = null;
        mFocusScaleUtil = null;
    }

    public FocusMoveUtil getFocusUtil() {
        return mFocusMoveUtil;
    }

    public void setFocusUtil(FocusMoveUtil focusMoveUtil) {
        this.mFocusMoveUtil = focusMoveUtil;
    }

    public FocusScaleUtil getFocusScaleUtil() {
        return mFocusScaleUtil;
    }

    public void setFocusScaleUtil(FocusScaleUtil fousScaleUtil) {
        this.mFocusScaleUtil = mFocusScaleUtil;
    }

    public RecyclerView getAttachedView() {
        return mAttachedView;
    }

    public void setAttachedView(RecyclerView atachedView) {
        this.mAttachedView = mAttachedView;
    }
}
