package cn.can.tvlib.ui.view.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.view.View.FOCUS_DOWN;
import static android.view.View.FOCUS_LEFT;
import static android.view.View.FOCUS_RIGHT;
import static android.view.View.FOCUS_UP;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：RecyclerView.Adapter封装
 * 修订历史：
 * <p>
 * 1.0  zhangbingyuan
 * 1. 添加header、footer支持
 * 2. 封装选择模式
 * 3. 提供分页加载回调
 * 4. 封装item点击、焦点变化、KeyEvent事件回调
 * 5. 增加焦点移出RecyclerView边界回调（header和footer如果为viewGroup，则其焦点移出边界情况需自己处理）
 * <p>
 * ps：如果有新的用功能可以封装 或者 出现任何问题，请及时沟通
 * ================================================
 */
public abstract class CanRecyclerViewAdapter<DataType> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CanRecyclerViewAdapter";

    public static final int VIEW_TYPE_HEADER = 0x101;
    public static final int VIEW_TYPE_CONTENT = 0x102;
    public static final int VIEW_TYPE_FOOTER = 0x103;

    public static final String TAG_VIEW_FLAG = "tagView%d";
    public static final int VIEW_ID_OFFSET = 0x100;

    protected List<DataType> mDatas;
    private RecyclerView mAttachedView;
    private ViewTreeObserver.OnGlobalFocusChangeListener mGlobalFocusChangeListener;

    public CanRecyclerViewAdapter(List<DataType> datas) {
        mDatas = datas;
        setHasStableIds(true);
    }

    public void setDatas(List<DataType> datas) {
        mDatas = datas;
        mTotalDataCount = datas.size();
    }

    @Override
    final public int getItemCount() {
        int count = 0;
        if (hasHeader()) {
            count++;
        }
        if (hasFooter()) {
            count++;
        }
        if (hasData()) {
            count += mDatas.size();
        }
        return count;
    }

    @Override
    final public long getItemId(int position) {
        return position + VIEW_ID_OFFSET;
    }

    @Override
    final public int getItemViewType(int position) {
        if (hasHeader() && position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (hasFooter() && position == getHeaderAndContentSize()) {
            return VIEW_TYPE_FOOTER;
        }
        return getViewType(getActualItemPosition(position));
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                holder = new DefaultViewHolder(mHeaderView);
                break;

            case VIEW_TYPE_FOOTER:
                holder = new DefaultViewHolder(mFooterView);
                break;

            default:
                holder = generateViewHolder(parent, viewType);
                break;
        }
        return holder;
    }

    @Override
    final public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == VIEW_TYPE_HEADER || itemViewType == VIEW_TYPE_FOOTER) {
            resolveStaggeredLayoutItemView(holder, position);
            return;
        }
        int actualPosi = getActualItemPosition(position);
        initViewHolder(holder, actualPosi);
        setupItemClickListener(holder, actualPosi);
        setupItemFocusChangeListener(holder, actualPosi);
        setupItemKeyEventListener(holder, actualPosi);
    }

    private void initViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindContentData(mDatas.get(position), holder, position);
        initTagViewIfNeed(holder, position);
    }

    private void initTagViewIfNeed(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TagViewHolder) {
            TagViewHolder mHolder = (TagViewHolder) holder;
            mHolder.tagView.setTag(String.format(TAG_VIEW_FLAG, position));
            if (mSelectMode == MODE_NORMAL) {
                mHolder.hideTagView(false);
            } else if (mSelectMode == MODE_SELECT) {
                mHolder.showTagView(false);
                mHolder.refreshTagViewOnSelectChanged(isItemSelected(position));
            }
        }
    }

    // 瀑布流中item如果要根据layoutOrientation填充，需特殊处理
    private void resolveStaggeredLayoutItemView(RecyclerView.ViewHolder holder, int position) {
        if (mAttachedView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(position) || isFooter(position))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAttachedView = recyclerView;
        if (mGlobalFocusChangeListener == null) {
            initGlobalFocusChangeListener();
        }
        mAttachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(mGlobalFocusChangeListener);
        changeAttachViewConfigForHeaderFooter();
    }

    private void initGlobalFocusChangeListener() {
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
        mAttachedView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    protected boolean hasAttachedToView() {
        return mAttachedView != null;
    }

    protected RecyclerView getAttachedView() {
        return mAttachedView;
    }

    //----------------------------   子类需要复写的方法   ----------------------------
    protected abstract RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType);

    protected abstract void bindContentData(DataType mDatas, RecyclerView.ViewHolder holder, int position);

    // 如果需要支持多种item类型，复写此方法
    public int getViewType(int position) {
        return VIEW_TYPE_CONTENT;
    }

    //----------------------------   支持添加header、footer   ----------------------------
    private View mHeaderView;
    private View mFooterView;

    final private int getActualItemPosition(int position) {
        return hasHeader() ? position - 1 : position;
    }

    private int getDataCount() {
        return hasData() ? mDatas.size() : 0;
    }

    final private boolean isHeader(int position) {
        return hasHeader() && position == 0;
    }

    final private boolean hasHeader() {
        return mHeaderView != null;
    }

    final private boolean hasData() {
        return mDatas != null;
    }

    final private boolean isFooter(int position) {
        return hasFooter() && position == getHeaderAndContentSize();
    }

    final private boolean hasFooter() {
        return mFooterView != null;
    }

    public void addHeader(View headerView) throws IllegalStateException {
        if (hasHeader()) {
            throw new IllegalStateException("An header has been added to RecyclerView.");
        }
        mHeaderView = headerView;
        if (hasAttachedToView()) {
            changeAttachViewConfigForHeaderFooter();
            notifyItemInserted(0);
        }
    }

    private void changeAttachViewConfigForHeaderFooter() {
        if (!hasHeader() && !hasFooter()) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mAttachedView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager glm = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = glm.getSpanSizeLookup();
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isHeader(position) || isFooter(position) ? glm.getSpanCount() : spanSizeLookup.getSpanSize(position);
                }
            });
        }
    }

    public void removeHeader() {
        if (!hasHeader()) {
            return;
        }
        mHeaderView = null;
        if (hasAttachedToView()) {
            notifyItemRemoved(0);
        }
    }

    public void addFooter(View footerView) throws IllegalStateException {
        if (hasFooter()) {
            throw new IllegalStateException("An header has been added to RecyclerView.");
        }
        mFooterView = footerView;
        if (hasAttachedToView()) {
            changeAttachViewConfigForHeaderFooter();
            notifyItemInserted(getHeaderAndContentSize());
        }
    }

    public void removeFooter() {
        if (!hasFooter()) {
            return;
        }
        mFooterView = null;
        if (hasAttachedToView()) {
            notifyItemRemoved(getHeaderAndContentSize());
        }
    }

    private int getHeaderAndContentSize() {
        int dataCount = hasData() ? mDatas.size() : 0;
        return hasHeader() ? dataCount + 1 : dataCount;
    }


    //----------------------------   支持选择模式   ----------------------------
    public static final int MODE_NORMAL = 0x001;//正常模式
    public static final int MODE_SELECT = 0x002;//选择模式

    private int mSelectMode = MODE_NORMAL;
    private boolean alwaysKeepSelectStatus;//选择模式退出后，再次进入是否记录原先的选择位置

    /**
     * 如果要支持选择模式，Adapter中传入的列表数据必须实现Selectable接口
     */
    public interface Selectable {

        boolean isSelected();

        void setSelected(boolean selected);
    }

    /**
     * 如果需要Adapter控制tagView显示隐藏，需要使用此TagViewHolder
     */
    public static abstract class TagViewHolder extends RecyclerView.ViewHolder {

        private View tagView;

        public TagViewHolder(View itemView) {
            super(itemView);
            tagView = itemView.findViewById(specifyTagViewId());
        }

        protected abstract int specifyTagViewId();

        public View getTagView() {
            return tagView;
        }

        /**
         * 当item选中状态改变时回调
         *
         * @param selected
         * @throws IllegalStateException
         */
        public void refreshTagViewOnSelectChanged(boolean selected) throws IllegalStateException {
            if (getTagView() == null) {
                throw new IllegalStateException("The tagView in TagViewHolder didn't found.");
            }
            if (selected) {
                showTagView(true);
            } else {
                hideTagView(true);
            }
        }

        /**
         * 显示tagView
         *
         * @param anim
         */
        public void showTagView(boolean anim) {
            if (getTagView() == null) {
                throw new IllegalStateException("The tagView in TagViewHolder didn't found.");
            }
            tagView.setVisibility(View.VISIBLE);
        }

        /**
         * 隐藏tagView
         *
         * @param anim
         */
        public void hideTagView(boolean anim) {
            if (getTagView() == null) {
                throw new IllegalStateException("The tagView in TagViewHolder didn't found.");
            }
            tagView.setVisibility(View.INVISIBLE);
        }
    }

    public static abstract class OnItemSelectChangeListener {
        /**
         * @param position content 列表中的位置（不包括header、footer）
         */
        public abstract void onSelectChanged(int position, boolean selected, Object data);
    }

    final public void setOnItemSelectListener(OnItemSelectChangeListener listener) {
        this.mItemSelectListener = listener;
    }

    final public void switchSelectMode(int selectMode) {
        if (mSelectMode == selectMode) {
            return;
        }
        mSelectMode = selectMode;
        switch (selectMode) {
            case MODE_NORMAL:
                hideAllTagViews();
                if (!alwaysKeepSelectStatus && hasData()) {
                    //退出选择模式时不保留选择状态
                    for (DataType data : mDatas) {
                        if (data instanceof Selectable) {
                            ((Selectable) data).setSelected(false);
                        }
                    }
                }
                break;

            case MODE_SELECT:
                showAllTagViewsWithSelectStatus();
                break;
        }
    }

    /**
     * 切换为选择模式是否记住上次的选项位置
     *
     * @param keepSelectStatus
     */
    final public void setAlwaysKeepSelectStatus(boolean keepSelectStatus) {
        this.alwaysKeepSelectStatus = keepSelectStatus;
    }

    public boolean isItemSelected(int position) {
        if (mSelectMode == MODE_NORMAL && !alwaysKeepSelectStatus) {
            return false;
        }
        DataType data = mDatas.get(position);
        return data instanceof Selectable && ((Selectable) data).isSelected();
    }

    public void setItemSelected(int position) {
        if (mSelectMode == MODE_NORMAL || !hasData() || position < 0 || position >= getDataCount()) {
            return;
        }
        DataType data = mDatas.get(position);
        if (data != null && data instanceof Selectable) {
            ((Selectable) data).setSelected(true);
        }
        refreshTagViewSelectStatus(position, true);
        if (mItemSelectListener != null) {
            mItemSelectListener.onSelectChanged(position, true, mDatas.get(position));
        }
    }

    public void setItemUnselected(int position) {
        if (mSelectMode == MODE_NORMAL || !hasData() || position < 0 || position >= getDataCount()) {
            return;
        }
        DataType data = mDatas.get(position);
        if (data != null && data instanceof Selectable) {
            ((Selectable) data).setSelected(false);
        }
        refreshTagViewSelectStatus(position, false);
        if (mItemSelectListener != null) {
            mItemSelectListener.onSelectChanged(position, false, mDatas.get(position));
        }
    }

    private void refreshTagViewSelectStatus(int position, boolean selected) {
        if (!hasAttachedToView()) {
            return;
        }
        View tagView = findTagView(position);
        if (tagView == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = mAttachedView.findContainingViewHolder(tagView);
        if (viewHolder != null && viewHolder instanceof TagViewHolder) {
            TagViewHolder holder = (TagViewHolder) viewHolder;
            if (tagView.getVisibility() != View.VISIBLE) {
                tagView.setVisibility(View.VISIBLE);
            }
            holder.refreshTagViewOnSelectChanged(selected);
        }
    }

    private View findTagView(int position) {
        return mAttachedView.findViewWithTag(String.format(TAG_VIEW_FLAG, position));
    }

    private void showAllTagViewsWithSelectStatus() {
        changeTagViewsVisible(true);
    }

    private void hideAllTagViews() {
        changeTagViewsVisible(false);
    }

    private void changeTagViewsVisible(boolean visible) {
        if (hasAttachedToView()) {
            RecyclerView.LayoutManager layoutManager = mAttachedView.getLayoutManager();
            if (layoutManager == null) {
                return;
            }
            if (layoutManager instanceof GridLayoutManager || layoutManager instanceof LinearLayoutManager) {
                int firstVisiblePosi = -1;
                int lastVisiblePosi = -1;
                try {
                    Class<? extends RecyclerView.LayoutManager> clazz = layoutManager.getClass();
                    Method method1 = clazz.getMethod("findFirstVisibleItemPosition");
                    method1.setAccessible(true);
                    firstVisiblePosi = (int) method1.invoke(layoutManager);

                    Method method2 = clazz.getMethod("findLastVisibleItemPosition");
                    method2.setAccessible(true);
                    lastVisiblePosi = (int) method2.invoke(layoutManager);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (firstVisiblePosi >= 0 && lastVisiblePosi >= 0) {
                    for (int i = firstVisiblePosi; i <= lastVisiblePosi; i++) {
                        if (isHeader(i) || isFooter(i)) {
                            continue;
                        }
                        int posi = getActualItemPosition(i);
                        DataType data = mDatas.get(posi);
                        boolean selected = (data instanceof Selectable) && ((Selectable) data).isSelected();
                        changeTagViewVisible(posi, visible, selected);
                    }
                }

            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) layoutManager;
                int[] firstVisiblePosis = lm.findFirstVisibleItemPositions(null);
                int[] lastVisiblePosis = lm.findLastVisibleItemPositions(null);
                int firstVisiblePosi = -1;
                for (int i = 0; i < firstVisiblePosis.length; i++) {
                    firstVisiblePosi = Math.min(firstVisiblePosi, firstVisiblePosis[i]);
                }
                int lastVisiblePosi = -1;
                for (int i = 0; i < lastVisiblePosis.length; i++) {
                    lastVisiblePosi = Math.max(lastVisiblePosi, lastVisiblePosis[i]);
                }
                if (firstVisiblePosi >= 0 && lastVisiblePosi >= 0) {
                    for (int i = firstVisiblePosi; i <= lastVisiblePosi; i++) {
                        if (isHeader(i) || isFooter(i)) {
                            continue;
                        }
                        int posi = getActualItemPosition(i);
                        DataType data = mDatas.get(posi);
                        boolean selected = (data instanceof Selectable) && ((Selectable) data).isSelected();
                        changeTagViewVisible(posi, visible, selected);
                    }
                }
            }
        }
    }

    private void changeTagViewVisible(int position, boolean visbile, boolean selected) {
        if (!hasAttachedToView()) {
            return;
        }
        View tagView = findTagView(position);
        if (tagView == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = mAttachedView.findContainingViewHolder(tagView);
        if (viewHolder != null && viewHolder instanceof TagViewHolder) {
            TagViewHolder holder = (TagViewHolder) viewHolder;
            if (visbile) {
                holder.refreshTagViewOnSelectChanged(selected);
                holder.showTagView(true);
            } else {
                holder.hideTagView(true);
            }
        } else {
            tagView.setVisibility(visbile ? View.VISIBLE : View.INVISIBLE);
        }
    }

    //----------------------------   支持分页加载（只是触发，加载逻辑需要使用者自己控制）   ----------------------------

    public interface OnPageLoadCallback {
        public void onLoadMore();
    }

    // 分页下载
    private boolean mPagingEnable;
    private int mTotalDataCount; //总数据条数，用于分页加载，注：并非一直等于mDatas的大小
    private OnPageLoadCallback mPageLoadCallback;
    private boolean isLoading;
    private RecyclerView.OnScrollListener mOnAttachViewScrollListener;

    public void setPagingEnable(OnPageLoadCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("The OnPageLoadCallback shouldn't be NULL.");
        }
        mPagingEnable = true;
        this.setPageLoadCallback(callback);
        registerPagingCallback();
    }

    public void setPagingDisable() {
        mPagingEnable = false;
        mPageLoadCallback = null;
        unRegisterPagingCallback();
    }

    public void setPageLoadCallback(final OnPageLoadCallback callback) {
        initAttachedViewPagingCallback();
        mPageLoadCallback = callback;
    }

    public void setTotalDataCount(int totalCount) {
        mTotalDataCount = totalCount;
    }

    /**
     * 分页加载完成后必须调用此方法
     */
    public void setLoadSuccess() {
        isLoading = false;
    }

    private void registerPagingCallback() {
        if (hasAttachedToView() && mPagingEnable == true) {
            mAttachedView.addOnScrollListener(mOnAttachViewScrollListener);
        }
    }

    private void unRegisterPagingCallback() {
        if (hasAttachedToView()) {
            mAttachedView.removeOnScrollListener(mOnAttachViewScrollListener);
        }
    }

    private void initAttachedViewPagingCallback() {
        if (mOnAttachViewScrollListener == null) {
            mOnAttachViewScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (mPagingEnable == false || mTotalDataCount == mDatas.size() || isLoading) {
                        super.onScrolled(recyclerView, dx, dy);
                        return;
                    }
                    RecyclerView.LayoutManager layoutManager = mAttachedView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int posi = getActualItemPosition(((LinearLayoutManager) layoutManager).findLastVisibleItemPosition());
                        if (posi == mDatas.size()) {
                            isLoading = true;
                            mPageLoadCallback.onLoadMore();
                        }

                    } else if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager glm = (GridLayoutManager) layoutManager;
                        int spanCount = glm.getSpanCount();
                        int currItemCount = getDataCount();

                        int range = currItemCount % spanCount;
                        range = currItemCount - (range == 0 ? spanCount : range);
                        int posi = getActualItemPosition(glm.findLastVisibleItemPosition());
                        if (posi >= range) {
                            isLoading = true;
                            mPageLoadCallback.onLoadMore();
                        }

                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) layoutManager;
                        int spanCount = sglm.getSpanCount();
                        int[] lastVisiblePosis = sglm.findLastVisibleItemPositions(null);
                        int lastVisiblePosi = -1;
                        for (int i = 0; i < lastVisiblePosis.length; i++) {
                            lastVisiblePosi = Math.max(lastVisiblePosi, lastVisiblePosis[i]);
                        }
                        if (getActualItemPosition(lastVisiblePosi) == mDatas.size()) {
                            isLoading = true;
                            mPageLoadCallback.onLoadMore();
                        }
                    }
                }
            };
        }
    }


    // ----------------------------   Adapter监听注册   ----------------------------
    public interface OnItemClickListener {
        void onClick(View view, int position, Object data);
    }

    public interface OnItemKeyEventListener {
        boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event);
    }

    public abstract static class OnFocusChangeListener {
        public boolean onFocusMoveOutside(int currFocus, int direction) {
            return false;
        }

        public abstract void onItemFocusChanged(View view, int position, boolean hasFocus);
    }

    private OnItemClickListener mItemClickListener;
    private OnItemSelectChangeListener mItemSelectListener;
    private OnFocusChangeListener mFocusChangeListener;
    private OnItemKeyEventListener mItemKeyEventListener;
    private boolean hasFocusMoveOut;
    private View mOldFocus;
    private View mCurrFocus;

    final public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    final public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.mFocusChangeListener = listener;
    }

    final public void setItemKeyEventListener(OnItemKeyEventListener listener) {
        this.mItemKeyEventListener = listener;
    }

    private void setupItemClickListener(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectMode == MODE_SELECT) {
                    if (isItemSelected(position)) {
                        setItemUnselected(position);
                    } else {
                        setItemSelected(position);
                    }
                } else if (mSelectMode == MODE_NORMAL && mItemClickListener != null) {
                    mItemClickListener.onClick(v, position, mDatas.get(position));
                }
            }
        });
    }

    private void setupItemFocusChangeListener(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.itemView.bringToFront();
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
    }

    private void setupItemKeyEventListener(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mFocusChangeListener != null && handleFocusMoveOut(position, v, keyCode, event)) {
                    return true;
                }
                if (mItemKeyEventListener != null && mItemKeyEventListener.onItemKeyEvent(getActualItemPosition(position), v, keyCode, event)) {
                    return true;
                }
                return false;
            }
        });
    }

    private boolean handleFocusMoveOut(int position, View v, int keyCode, KeyEvent event) {
        if (mAttachedView == null || mAttachedView.getLayoutManager() == null || mFocusChangeListener == null
                || event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }

        RecyclerView.LayoutManager layoutManager = mAttachedView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            GridLayoutManager gm = (GridLayoutManager) layoutManager;
            int layoutDirection = gm.getOrientation();
            int spanCount = gm.getSpanCount();
            int posi = getActualItemPosition(position);

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (layoutDirection == GridLayoutManager.VERTICAL && posi % spanCount == 0) {
                    return callbackFocusMoveOutSide(position, FOCUS_LEFT);
                } else if (layoutDirection == GridLayoutManager.HORIZONTAL && posi < spanCount) {
                    return callbackFocusMoveOutSide(position, FOCUS_LEFT);
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (layoutDirection == GridLayoutManager.VERTICAL && posi < spanCount) {
                    return callbackFocusMoveOutSide(position, FOCUS_UP);
                } else if (layoutDirection == GridLayoutManager.HORIZONTAL && posi % spanCount == 0) {
                    return callbackFocusMoveOutSide(position, FOCUS_UP);
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (layoutDirection == GridLayoutManager.VERTICAL && (posi + 1) % spanCount == 0) {
                    return callbackFocusMoveOutSide(position, FOCUS_RIGHT);
                } else if (layoutDirection == GridLayoutManager.HORIZONTAL) {
                    int itemCount = getItemCount();
                    int range = itemCount % spanCount;
                    if (range == 0) {
                        range = itemCount - spanCount;
                    } else {
                        range = itemCount - range;
                    }
                    if (posi >= range) {
                        if (isLoading) {
                            return true;
                        }
                        return callbackFocusMoveOutSide(position, FOCUS_RIGHT);
                    }
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (layoutDirection == GridLayoutManager.VERTICAL) {
                    int itemCount = getItemCount();
                    int range = itemCount % spanCount;
                    if (range == 0) {
                        range = itemCount - spanCount;
                    } else {
                        range = itemCount - range;
                    }
                    if (posi >= range) {
                        if (isLoading) {
                            return true;
                        }
                        return callbackFocusMoveOutSide(position, FOCUS_DOWN);
                    }
                } else if (layoutDirection == GridLayoutManager.HORIZONTAL && (posi + 1) % spanCount == 0) {
                    return callbackFocusMoveOutSide(position, FOCUS_DOWN);
                }
            }

        } else if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
            int layoutOrientation = lm.getOrientation();

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (layoutOrientation == LinearLayoutManager.VERTICAL || layoutOrientation == LinearLayoutManager.HORIZONTAL && position == 0) {
                    return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_LEFT);
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (layoutOrientation == LinearLayoutManager.VERTICAL && position == 0 || layoutOrientation == LinearLayoutManager.HORIZONTAL) {
                    return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_UP);
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (layoutOrientation == LinearLayoutManager.VERTICAL) {
                    return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_RIGHT);
                } else if (layoutOrientation == LinearLayoutManager.HORIZONTAL) {
                    if (isLoading) {
                        return true;
                    }
                    if (position == getItemCount() - 1) {
                        return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_RIGHT);
                    }
                }

            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && position == getItemCount()) {
                if (layoutOrientation == LinearLayoutManager.HORIZONTAL) {
                    return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_DOWN);
                } else if (layoutOrientation == LinearLayoutManager.VERTICAL) {
                    if (isLoading) {
                        return true;
                    }
                    if (position == getItemCount() - 1) {
                        return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_DOWN);
                    }
                }
            }

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //TODO
//                StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) layoutManager;
//                int[] firstVisiblePosis = sglm.findFirstVisibleItemPositions(null);
//                int[] lastVisiblePosis = sglm.findLastVisibleItemPositions(null);
//                int spanCount = sglm.getSpanCount();
//                if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
//                    if(sglm.getLayoutDirection() == LinearLayoutManager.VERTICAL){
//                        for(int posi = firstVisiblePosis[0]; posi < lastVisiblePosis[0]; posi++){
//                            if(position == posi){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_LEFT);
//                            }
//                        }
//                    } else if(sglm.getLayoutDirection() == LinearLayoutManager.HORIZONTAL){
//                        for(int i = 0; i < firstVisiblePosis.length; i++){
//                            if(position == firstVisiblePosis[i]){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_LEFT);
//                            }
//                        }
//                    }
//                } else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
//                    if(sglm.getLayoutDirection() == LinearLayoutManager.VERTICAL){
//                        for(int i = 0; i < firstVisiblePosis.length; i++){
//                            if(position == firstVisiblePosis[i]){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_UP);
//                            }
//                        }
//                    } else if(sglm.getLayoutDirection() == LinearLayoutManager.HORIZONTAL){
//                        for(int posi = firstVisiblePosis[spanCount]; posi < lastVisiblePosis[spanCount]; posi++){
//                            if(position == posi){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_UP);
//                            }
//                        }
//                    }
//                }  else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
//                    if(sglm.getLayoutDirection() == LinearLayoutManager.VERTICAL){
//                        for(int i = 0; i < lastVisiblePosis.length; i++){
//                            if(position == lastVisiblePosis[i]){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_RIGHT);
//                            }
//                        }
//                    } else if(sglm.getLayoutDirection() == LinearLayoutManager.HORIZONTAL){
//                        for(int posi = firstVisiblePosis[spanCount]; posi < lastVisiblePosis[spanCount]; posi++){
//                            if(position == posi){
//                                return mFocusChangeListener.onFocusMoveOutside(position, FOCUS_RIGHT);
//                            }
//                        }
//                    }
//                }
        }
        return false;
    }

    private boolean callbackFocusMoveOutSide(int position, int direction) {
        hasFocusMoveOut = true;
        mOldFocus = null;
        return mFocusChangeListener.onFocusMoveOutside(position, direction);
    }

    public static class DefaultViewHolder extends RecyclerView.ViewHolder {

        public DefaultViewHolder(View itemView) {
            super(itemView);
        }
    }

}