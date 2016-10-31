package cn.can.tvlib.ui.view.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import static android.view.View.FOCUS_DOWN;
import static android.view.View.FOCUS_LEFT;
import static android.view.View.FOCUS_RIGHT;
import static android.view.View.FOCUS_UP;

/**
 * Created by zhangbingyuan on 2016/10/15.
 */

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：RecyclerView.Adapter封装，主要用于TV端ViewPager效果
 *          注1：配合RecyclerView使用时，必须配合LinearLayoutManager使用
 *          注2：如果ItemView布局为ViewGroup，则相关回调事件必须自己处理
 * 修订历史：
 * <p>
 * 1.0  zhangbingyuan
 * ================================================
 */
public abstract class CanViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CanViewPagerAdapter";

    public static final int VIEW_ID_OFFSET = 0x100;

    public interface OnPageChangeListener{
        public void onChanged(int oldPage, int newPage);
    }

    private RecyclerView mAttachedView;
    private ViewTreeObserver.OnGlobalFocusChangeListener mGlobalFocusChangeListener;
    private RecyclerView.OnScrollListener mScrollListener;
    private OnItemClickListener mItemClickListener;
    private OnFocusChangeListener mFocusChangeListener;
    private OnItemKeyEventListener mItemKeyEventListener;
    private OnPageChangeListener mPageChangeListener;
    private boolean hasFocusMoveOut;
    private View mOldFocus;
    private View mCurrFocus;
    private int mCurrPage;

    public CanViewPagerAdapter() {
        setHasStableIds(true);
    }

    @Override
    final public long getItemId(int position) {
        return position + VIEW_ID_OFFSET;
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindItemViewHolder(holder, position);
        initViewHolder(holder, position);
        registerListener(holder, position);
    }

    protected abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position);

    private void initViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        } else {
            if (mAttachedView.getLayoutManager().canScrollHorizontally()) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        holder.itemView.setLayoutParams(layoutParams);
    }

    private void registerListener(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(v, position);
                }
            }
        });
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mOldFocus != null) {
                        mOldFocus = mCurrFocus;
                    }
                    mCurrFocus = v;
                } else {
                    if (!hasFocusMoveOut) {
                        mOldFocus = mCurrFocus;
                    }
                }
                if (mFocusChangeListener != null) {
                    mFocusChangeListener.onItemFocusChanged(v, position, hasFocus);
                }
            }
        });
        holder.itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mItemKeyEventListener != null && handleFocusMoveOut(position, v, keyCode, event)) {
                    return true;
                }
                if (mItemKeyEventListener != null && mItemKeyEventListener.onItemKeyEvent(position, v, keyCode, event)) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new NullPointerException("CanGridLayoutManager of recyclerView must be initalized.");
        }
        if (layoutManager instanceof LinearLayoutManager == false) {
            throw new NullPointerException("The type of layoutManager must be LinearLayoutManager.");
        }
        mAttachedView = recyclerView;
        initGlobalFocusChangeListener();
        mAttachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(mGlobalFocusChangeListener);
        initScrollListener();
        mAttachedView.addOnScrollListener(mScrollListener);
    }

    private void initScrollListener() {
        if(mScrollListener != null){
            return;
        }
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int posi = ((LinearLayoutManager) mAttachedView.getLayoutManager()).findFirstVisibleItemPosition();
                    if(mCurrPage != posi){
                        int oldPosi = posi;
                        mCurrPage = posi;
                        if(mPageChangeListener != null){
                            mPageChangeListener.onChanged(oldPosi, mCurrPage);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    private void initGlobalFocusChangeListener() {
        if (mGlobalFocusChangeListener != null) {
            return;
        }
        mGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                View focusedChild = mAttachedView.getFocusedChild();
                if (focusedChild == null) {
                    hasFocusMoveOut = true;
                } else {
                    hasFocusMoveOut = false;
                }
                View containingItemView = mAttachedView.findContainingItemView(newFocus);
                if (containingItemView != null) {
                    mAttachedView.smoothScrollToPosition(mAttachedView.getChildAdapterPosition(containingItemView));
                }
            }
        };
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mAttachedView.getViewTreeObserver().removeOnGlobalFocusChangeListener(mGlobalFocusChangeListener);
        mAttachedView.removeOnScrollListener(mScrollListener);
        mAttachedView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private boolean hasAttachedToView() {
        return mAttachedView != null;
    }

    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    public void selectPage(int position) {
        selectPage(position, true);
    }

    public void selectPage(int position, boolean scrollTo) {
        if(!hasAttachedToView() || position < 0 || position >= getItemCount()){
            Log.w(TAG, "Failed to selectPage.[position is illegal]");
            return;
        }
        if(scrollTo){
            mAttachedView.smoothScrollToPosition(position);
        } else {
            mAttachedView.scrollToPosition(position);
        }
    }

    public int getCurrentPage() {
        return mCurrPage;
    }

    // ----------------------------   Adapter监听注册   ----------------------------
    public static abstract class OnItemClickListener {
        public abstract void onClick(View view, int position);
    }

    public static abstract class OnItemKeyEventListener {
        public abstract boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event);
    }

    public static abstract class OnFocusChangeListener {

        public abstract boolean onFocusMoveOutside(int currFocus, int direction);

        public abstract void onItemFocusChanged(View view, int position, boolean hasFocus);
    }

    final public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    final public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.mFocusChangeListener = listener;
    }

    final public void setItemKeyEventListener(OnItemKeyEventListener listener) {
        this.mItemKeyEventListener = listener;
    }

    private boolean handleFocusMoveOut(int position, View v, int keyCode, KeyEvent event) {
        if (!hasAttachedToView() || mFocusChangeListener == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }
        LinearLayoutManager lm = (LinearLayoutManager) mAttachedView.getLayoutManager();
        int layoutOrientation = lm.getLayoutDirection();
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (layoutOrientation == LinearLayoutManager.VERTICAL || layoutOrientation == LinearLayoutManager.HORIZONTAL && position == 0) {
                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_LEFT);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (layoutOrientation == LinearLayoutManager.VERTICAL && position == 0 || layoutOrientation == LinearLayoutManager.HORIZONTAL) {
                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_UP);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (layoutOrientation == LinearLayoutManager.VERTICAL || layoutOrientation == LinearLayoutManager.HORIZONTAL
                    && position == getItemCount() - 1) {
                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_RIGHT);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && position == getItemCount()) {
            if (layoutOrientation == LinearLayoutManager.HORIZONTAL || layoutOrientation == LinearLayoutManager.VERTICAL
                    && position == getItemCount() - 1) {
                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_DOWN);
            }
        }
        return false;
    }
}
