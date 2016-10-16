package cn.can.tvlib.ui.view.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

/**
 * Created by zhangbingyuan on 2016/10/16.
 */

public class CanRecyclerViewFocusHelper {

    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private HashMap<RecyclerView, ViewTreeObserver.OnGlobalFocusChangeListener> mFocusChangeListeners;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private Handler mHandler;
    private View mCurrFocusView;
    private Runnable mFocusMoveRunanble;
    private int cacheSize = 4;

    public CanRecyclerViewFocusHelper(final Context context, FocusMoveUtil focusMoveUtil, FocusScaleUtil focusScaleUtil) {
        this.mFocusMoveUtil = focusMoveUtil;
        this.mFocusScaleUtil = focusScaleUtil;
        mFocusChangeListeners = new HashMap<RecyclerView, ViewTreeObserver.OnGlobalFocusChangeListener>(cacheSize);
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

    public void attachToRecyclerView(final RecyclerView attachedView, final int focusResId, final float focusScale){
        ViewTreeObserver.OnGlobalFocusChangeListener listener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if(oldFocus != null){
                    mFocusScaleUtil.scaleToNormal(oldFocus);
                }
                if(newFocus != null){
                    mCurrFocusView = newFocus;
                    mFocusMoveUtil.setFocusRes(attachedView.getContext(), focusResId);
                    mFocusScaleUtil.setFocusScale(focusScale);
                    mHandler.removeCallbacks(mFocusMoveRunanble);
                    mHandler.postDelayed(mFocusMoveRunanble, 30);
                }
            }
        };
        attachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(listener);
        attachedView.addOnScrollListener(mOnScrollListener);
        mFocusChangeListeners.put(attachedView, listener);
    }

    public void release(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if(mFocusChangeListeners != null){
            Iterator<Map.Entry<RecyclerView, ViewTreeObserver.OnGlobalFocusChangeListener>> iterator = mFocusChangeListeners.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<RecyclerView, ViewTreeObserver.OnGlobalFocusChangeListener> entry = iterator.next();
                RecyclerView attachedView = entry.getKey();
                ViewTreeObserver.OnGlobalFocusChangeListener listener = entry.getValue();
                attachedView.getViewTreeObserver().removeOnGlobalFocusChangeListener(listener);
                attachedView.removeOnScrollListener(mOnScrollListener);
            }
            mFocusChangeListeners.clear();
            mFocusChangeListeners = null;
        }
        mOnScrollListener = null;
        mCurrFocusView = null;
        mFocusMoveRunanble = null;
        mFocusMoveUtil.release();
        mFocusMoveUtil = null;
        mFocusScaleUtil = null;
    }

    public FocusMoveUtil getFocusUtil() {
        return mFocusMoveUtil;
    }

    public FocusScaleUtil getFocusScaleUtil() {
        return mFocusScaleUtil;
    }

}
