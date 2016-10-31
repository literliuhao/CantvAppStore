package cn.can.tvlib.ui.focus;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhangbingyuan on 2016/10/16.
 */

public class CanRecyclerViewFocusHelper {

    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private HashMap<RecyclerView, AttachInfo> mAttachedViews;
    private Handler mHandler;
    private View mCurrFocusView;
    private int cacheSize = 4;



    public CanRecyclerViewFocusHelper(FocusMoveUtil focusMoveUtil, FocusScaleUtil focusScaleUtil) {
        this.mFocusMoveUtil = focusMoveUtil;
        this.mFocusScaleUtil = focusScaleUtil;
        mAttachedViews = new HashMap<>(cacheSize);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    AttachInfo attachInfo = (AttachInfo) msg.obj;
                    if(mCurrFocusView != null && mCurrFocusView.isFocused()){
                        int focusResId = mFocusMoveUtil.getFocusResId();
                        if(focusResId == 0 || focusResId != attachInfo.getFocusResId()){
                            mFocusMoveUtil.setFocusRes(mCurrFocusView.getContext(), focusResId);
                        }
                        mFocusScaleUtil.setFocusScale(attachInfo.getFocusScale());
                        mFocusMoveUtil.startMoveFocus(mCurrFocusView, attachInfo.getFocusScale());
                        mFocusScaleUtil.scaleToLarge(mCurrFocusView);
                    }
                }
            }
        };
    }

    public void attachToRecyclerView(RecyclerView attachedView, int focusResId, float focusScale){
        attachToRecyclerView(attachedView, new AttachInfo().setFocusResId(focusResId).setFocusScale(focusScale));
    }

    public void attachToRecyclerView(final RecyclerView attachedView, final AttachInfo attachInfo){
        attachInfo.setAttachedView(attachedView);
        mAttachedViews.put(attachedView, attachInfo);
        ViewTreeObserver.OnGlobalFocusChangeListener listener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Log.i("", "onGlobalFocusChanged oldFocus = " + oldFocus + ", newFocus = " + newFocus);
                if(oldFocus != null){
                    mFocusScaleUtil.scaleToNormal(oldFocus);
                }
                if(newFocus != null){
                    mCurrFocusView = newFocus;

                    mHandler.removeMessages(0);
                    Message message = mHandler.obtainMessage(0);
                    message.obj = attachInfo;
                    mHandler.sendMessageDelayed(message, 30);
                }
            }
        };
        attachInfo.setFocusListener(listener);
        attachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(listener);
        if(attachInfo.getScrollListener() == null && attachInfo.getPreScroll() != null && attachInfo.getPostScroll() != null){
            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                        attachInfo.getPreScroll().run();
                    } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        attachInfo.getPostScroll().run();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if(mHandler.hasMessages(0)){
                        mHandler.removeMessages(0);
                        Message message = mHandler.obtainMessage(0);
                        message.obj = attachInfo;
                        mHandler.sendMessageDelayed(message, 30);
                    }
                }
            };
            attachInfo.setScrollListener(scrollListener);
        }
        if(attachInfo.getScrollListener() != null){
            attachedView.addOnScrollListener(attachInfo.getScrollListener());
        }
    }

    public void detachFromRecyclerView(RecyclerView attachedView){
        AttachInfo attachInfo = mAttachedViews.get(attachedView);
        if(attachInfo == null || attachInfo.getAttachedView() == null){
            return;
        }
        mAttachedViews.remove(attachedView);
        RecyclerView view = attachInfo.getAttachedView();
        if(attachInfo.getScrollListener() != null){
            view.removeOnScrollListener(attachInfo.getScrollListener());
        }
        if(attachInfo.getFocusListener() != null){
            view.getViewTreeObserver().removeOnGlobalFocusChangeListener(attachInfo.getFocusListener());
        }
        attachInfo.release();
    }

    public void release(){
        if(mAttachedViews != null){
            Iterator<RecyclerView> iterator = mAttachedViews.keySet().iterator();
            while(iterator.hasNext()){
                detachFromRecyclerView(iterator.next());
            }
            mAttachedViews = null;
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mCurrFocusView = null;
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

    public static class AttachInfo{

        int mFocusResId;
        float mFocusScale;
        Runnable mPreScroll;
        Runnable mPostScroll;
        RecyclerView.OnScrollListener mScrollListener;
        ViewTreeObserver.OnGlobalFocusChangeListener mFocusListener;
        SoftReference<RecyclerView> mAttachedView;

        public ViewTreeObserver.OnGlobalFocusChangeListener getFocusListener() {
            return mFocusListener;
        }

        public AttachInfo setFocusListener(ViewTreeObserver.OnGlobalFocusChangeListener focusListener) {
            this.mFocusListener = focusListener;
            return this;
        }

        public int getFocusResId() {
            return mFocusResId;
        }

        public AttachInfo setFocusResId(int focusResId) {
            this.mFocusResId = focusResId;
            return this;
        }

        public float getFocusScale() {
            return mFocusScale;
        }

        public AttachInfo setFocusScale(float focusResId) {
            this.mFocusScale = focusResId;
            return this;
        }

        public Runnable getPreScroll() {
            return mPreScroll;
        }

        public AttachInfo setPreScroll(Runnable preScroll) {
            this.mPreScroll = preScroll;
            return this;
        }

        public Runnable getPostScroll() {
            return mPostScroll;
        }

        public void setPostScroll(Runnable postScroll) {
            this.mPostScroll = postScroll;
        }

        public RecyclerView.OnScrollListener getScrollListener() {
            return mScrollListener;
        }

        public AttachInfo setScrollListener(RecyclerView.OnScrollListener scrollListener) {
            this.mScrollListener = scrollListener;
            return this;
        }

        public RecyclerView getAttachedView() {
            if(mAttachedView != null){
                return mAttachedView.get();
            }
            return null;
        }

        public void setAttachedView(RecyclerView attachedView) {
            if(mAttachedView != null){
                mAttachedView.clear();
                mAttachedView = null;
            }
            this.mAttachedView = new SoftReference<RecyclerView>(attachedView);
        }

        public void release() {
            mPreScroll = null;
            mPostScroll = null;
            mScrollListener = null;
            mFocusListener = null;
            if(mAttachedView != null){
                mAttachedView.clear();
                mAttachedView = null;
            }
        }
    }
}
