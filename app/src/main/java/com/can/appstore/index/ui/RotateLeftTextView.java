package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
public class RotateLeftTextView extends TextView {

	public RotateLeftTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.rotate(-90, getMeasuredWidth() / 2, getMeasuredHeight() /2);
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(60, 150); 
	}

}
