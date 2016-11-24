package com.can.appstore.index.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.IAddFocusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhao on 2016/10/17.
 */
public class TitleBar extends LinearLayout implements View.OnFocusChangeListener {
    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;
    private int mTriangleHeight;

    private static final float RADIO_TRIANGEL = 1.0f / 6;
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);

    private int offsetX = (int) getResources().getDimension(R.dimen.px30);
    private int mInitTranslationX;
    private float mTranslationX;

    private static final int COUNT_DEFAULT_TAB = 7;
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    private List<String> mTabTitles;
    public ViewPager mViewPager;

    private static final int COLOR_TEXT_NORMAL = 0xCCFFFFFF;
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;

    private int temp = 0;

    private List<LiteText> textViewList;
    private int mCurrentIndex = 0;
    private IAddFocusListener mFocusListener;
    private View mCurrentView;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

//        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        this.setLayoutParams(mainParams);

        // 获得自定义属性，tab的数量
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mTabVisibleCount = a.getInt(R.styleable.TitleBar_item_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) mTabVisibleCount = COUNT_DEFAULT_TAB;
        a.recycle();

        // 初始化画笔
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.parseColor("#ffffffff"));
//        mPaint.setStyle(Style.FILL);
//        mPaint.setPathEffect(new CornerPathEffect(3));
        textViewList = new ArrayList<>();
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        // 画笔平移到正确的位置
        canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 1);
//        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 初始化三角形的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);// 1/6 of
        // width
        mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);

        // 初始化三角形
        initTriangle();

        // 初始时的偏移量
        mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth / 2;
    }

    public void initTitle(IAddFocusListener onFocusChange) {
        this.mFocusListener = onFocusChange;

    }

    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (int i = 0; i < mTabTitles.size(); i++) {
                LiteText liteText = generateTextView(mTabTitles.get(i));
                addView(liteText, i);
            }
            //得到最后titleBar进行相关设置
            LiteText lastBar = (LiteText) this.getChildAt(mTabTitles.size() - 1);
            lastBar.setId(R.id.tv_index_title_last);
            lastBar.setNextFocusRightId(R.id.rl_search);
            // 设置item的click事件
            setItemClickEvent();

        }
    }

//    public View getFirstView(){
//        return mFirstView = this.getChildAt(2);
//    }

    @Override
    public void onFocusChange(View view, boolean b) {
//        Log.i("TitleBar", "addFocusListener " + String.valueOf(view));
    }

    /**
     * 对外的ViewPager的回调接口
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    // 对外的ViewPager的回调接口的设置
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    private boolean isScrolling = false;

    // 设置关联的ViewPager
    public void setViewPager(final ViewPager mViewPager, final int pos) {
        this.mViewPager = mViewPager;

        this.mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i("onPageSelected", position + "");
                mCurrentIndex = position;
                resetTextViewColor();
                if(!(mCurrentView instanceof LiteText)){
                    highLightTextView(position);
                }
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i("onPagescrolled", "position " + position);
//                Log.i("onPagescrolled", "positionOffset " + positionOffset);
//                Log.i("onPagescrolled", "positionOffsetPixels " + positionOffsetPixels);
                // 滚动
                scroll(position, positionOffset);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1) {
                    isScrolling = true;
                } else {
                    isScrolling = false;
                }
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });

        TitleBar.this.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                Log.i("TitleBar", "onGlobalFocusChanged old " + String.valueOf(oldFocus));
//                Log.i("TitleBar", "onGlobalFocusChanged new " + String.valueOf(newFocus));
                mCurrentView = newFocus;
                if (!(oldFocus instanceof LiteText) && newFocus instanceof LiteText) {
                    newFocus = textViewList.get(mViewPager.getCurrentItem());
                    newFocus.requestFocus();
                    mFocusListener.addFocusListener(newFocus, true);
                    resetTextViewColor();
                } else if (oldFocus instanceof LiteText && newFocus instanceof LiteText) {
                    if (null == oldFocus || null == newFocus) return;
                    mFocusListener.addFocusListener(newFocus, true);
                    resetTextViewColor();
                } else if (oldFocus instanceof LiteText && !(newFocus instanceof LiteText)) {
                    if (null == oldFocus || null == newFocus) return;
                    highLightTextView(mCurrentIndex);
                }
            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
//        highLightTextView(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof LiteText) {
            view.setBackground(getResources().getDrawable(R.drawable.index_title_normal));
        }
    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof LiteText) {
                view.setBackgroundResource(0);
            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成TextView
     *
     * @param text
     * @return LiteText
     */
    private LiteText generateTextView(String text) {
        LiteText textLayout = new LiteText(getContext());
        textLayout.setPadding((int) getResources().getDimension(R.dimen.px20), (int) getResources().getDimension(R.dimen.px0), (int) getResources().getDimension(R.dimen.px20), (int) getResources().getDimension(R.dimen.px0));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.setMargins((int) getResources().getDimension(R.dimen.px30), (int) getResources().getDimension(R.dimen.px10), (int) getResources().getDimension(R.dimen.px30), (int) getResources().getDimension(R.dimen.px10));
        textLayout.setClipToPadding(false);
        textLayout.setClipChildren(false);
        textLayout.setLayoutParams(textParams);
        textLayout.setFocusable(true);
        textLayout.setOnFocusChangeListener(this);

        TextView textView = new TextView(getContext());
        textView.setPadding((int) getResources().getDimension(R.dimen.px0), (int) getResources().getDimension(R.dimen.px5), (int) getResources().getDimension(R.dimen.px0), (int) getResources().getDimension(R.dimen.px5));

        RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        txtParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView.setLayoutParams(txtParams);

        int textSize = (int) getResources().getDimension(R.dimen.px38);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textLayout.addView(textView);
        textViewList.add(textLayout);
        return textLayout;
    }

    /**
     * 初始化三角
     */
    private void initTriangle() {
        mPath = new Path();
        mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        // 不断改变偏移量，invalidate
        mTranslationX = getWidth() / mTabVisibleCount * (position + offset);

        int tabWidth = getScreenWidth() / mTabVisibleCount;

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (offset > 0 && position >= (mTabVisibleCount - 2) && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * offset), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }

        invalidate();
    }

    /**
     * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate() {
        Log.e("TAG", "onFinishInflate");
        super.onFinishInflate();

        int cCount = getChildCount();

        if (cCount == 0) return;

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();

    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
