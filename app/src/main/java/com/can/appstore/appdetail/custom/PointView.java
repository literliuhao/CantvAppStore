package com.can.appstore.appdetail.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.can.appstore.R;

/**
 * Created by JasonF on 2016/11/14.
 */

@SuppressWarnings("deprecation")
public class PointView extends View {
    private static final String TAG = "PointView";
    private Paint mPaintSelect;
    private Paint mPaintUnselect;
    private Context mContext;
    private int mUnselectColor;
    private int mSelectColor;
    private int mPointCount;
    private int mPonitRadius;
    private int mPonitSpace;
    private int mSelectPosition;

    public PointView(Context context) {
        this(context, null);
    }

    public PointView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.pointview);
        mPonitRadius = typedArray.getDimensionPixelSize(R.styleable.pointview_pointradius, 8);
        mPonitSpace = typedArray.getDimensionPixelSize(R.styleable.pointview_pointspace, 16);
        mSelectColor = typedArray.getColor(R.styleable.pointview_selectcolor, mContext.getResources().getColor(R.color.tv_cur_rows_color));
        mUnselectColor = typedArray.getColor(R.styleable.pointview_selectcolor, mContext.getResources().getColor(R.color.app_introduce_color));
        initPaint();
    }

    private void initPaint() {
        mPaintSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelect.setColor(mSelectColor);
        mPaintSelect.setStyle(Paint.Style.FILL);

        mPaintUnselect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintUnselect.setColor(mUnselectColor);
        mPaintUnselect.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");
        super.onDraw(canvas);
        for (int i = 0; i < mPointCount; i++) {
            if (mSelectPosition == i) {
                canvas.drawCircle(mPonitRadius + (i * mPonitSpace) + 2 * (i * mPonitRadius), mPonitRadius, mPonitRadius, mPaintSelect);
            } else {
                canvas.drawCircle(mPonitRadius + (i * mPonitSpace) + 2 * (i * mPonitRadius), mPonitRadius, mPonitRadius, mPaintUnselect);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: " + measureWidth(widthMeasureSpec) + "    " + measureHeight(heightMeasureSpec));
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = (int) getHeight();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = ((mPointCount * 2) - 1) * mPonitRadius * 2;//根据自己的需要更改
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    public int getUnselectColor() {
        return mUnselectColor;
    }

    public void setUnselectColor(int unselectColor) {
        mUnselectColor = unselectColor;
    }

    public int getSelectColor() {
        return mSelectColor;
    }

    public void setSelectColor(int selectColor) {
        mSelectColor = selectColor;
    }

    public int getPointCount() {
        return mPointCount;
    }

    public void setPointCount(int pointCount) {
        mPointCount = pointCount;
    }

    public int getPonitSpace() {
        return mPonitSpace;
    }

    public void setPonitSpace(int ponitSpace) {
        mPonitSpace = ponitSpace;
    }

    public int getPonitRadius() {
        return mPonitRadius;
    }

    public void setPonitRadius(int ponitRadius) {
        mPonitRadius = ponitRadius;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        mSelectPosition = selectPosition;
        postInvalidate();
    }
}
