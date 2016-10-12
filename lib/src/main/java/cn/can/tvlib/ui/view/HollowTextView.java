package cn.can.tvlib.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * ================================================
 * 作    者：xzl
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：底层有加粗背景的TextView（非光晕）
 * 修订历史：
 *
 * ================================================
 */
public class HollowTextView extends TextView {


    private TextView mStrokeTextView = null;
    private TextPaint mStrokePaint;
    private int mStrokeColor;

    public HollowTextView(Context context) {
        this(context, null, 0);
    }

    public HollowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HollowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        setIncludeFontPadding(false);

        mStrokeTextView = new TextView(context, attrs, defStyleAttr);
        mStrokeTextView.setTextColor(mStrokeColor);
        mStrokeTextView.setIncludeFontPadding(false);
        mStrokeTextView.setGravity(getGravity());

        mStrokePaint = mStrokeTextView.getPaint();
        mStrokePaint.setFlags(mStrokePaint.getFlags() | Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mStrokePaint.setStrokeWidth(4);
    }

    public void setStrokeColor(int color){
        mStrokeColor = color;
        mStrokeTextView.setTextColor(color);
        invalidate();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mStrokeTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        CharSequence outlineText = mStrokeTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText()))
        {
            mStrokeTextView.setText(getText());
            postInvalidate();
        }
        mStrokeTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        mStrokeTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mStrokeTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
