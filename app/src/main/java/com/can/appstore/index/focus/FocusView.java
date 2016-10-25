//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class FocusView extends View {
	private static final String TAG = "FocusView";
	private int frameColor;
	private int lightColor;
	private int frameWidth;
	private int lightWidth;
	private int lightDrawFrequency;
	private int lightStrokeWidth;
	private float roundX;
	private float roundY;
	private Paint paintFrame;
	private Paint paintLight;
	private float lightAlpha;
	private float lightAlphaCopy;
	private float frameLeft;
	private float frameTop;
	private float frameRight;
	private float frameBottom;
	private float frameLeftMove;
	private float frameTopMove;
	private float frameRightMove;
	private float frameBottomMove;
	private float frameLeftMoveEnd;
	private float frameTopMoveEnd;
	private float frameRightMoveEnd;
	private float frameBottomMoveEnd;
	private int movingTime;
	private int movingNumber;
	private int movingVelocity;

	public FocusView(Context context) {
		this(context, (AttributeSet) null);
	}

	public FocusView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.frameColor = -256;
		this.lightColor = -16776961;
		this.frameWidth = 4;
		this.lightWidth = 30;
		this.lightDrawFrequency = 20;
		this.roundX = 10.0F;
		this.roundY = 10.0F;
		this.lightAlpha = 100.0F;
		this.movingTime = 1000;
		this.movingNumber = 10;
		this.movingVelocity = 1;
		this.init();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FocusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);
		this.frameColor = -256;
		this.lightColor = -16776961;
		this.frameWidth = 4;
		this.lightWidth = 30;
		this.lightDrawFrequency = 20;
		this.roundX = 10.0F;
		this.roundY = 10.0F;
		this.lightAlpha = 100.0F;
		this.movingTime = 1000;
		this.movingNumber = 10;
		this.movingVelocity = 1;
		this.init();
	}

	private void init() {
		this.lightAlphaCopy = this.lightAlpha;
		this.initFramePaint();
		this.initLightPaint();
	}

	private void initFramePaint() {
		this.paintFrame = new Paint();
		this.paintFrame.setAntiAlias(true);
		this.paintFrame.setColor(this.frameColor);
		this.paintFrame.setStrokeWidth((float) this.frameWidth);
		this.paintFrame.setStyle(Style.STROKE);
	}

	private void initLightPaint() {
		this.lightStrokeWidth = this.lightWidth / this.lightDrawFrequency;
		this.paintLight = new Paint();
		this.paintLight.setAntiAlias(true);
		this.paintLight.setColor(this.lightColor);
		this.paintLight.setStrokeWidth((float) this.lightStrokeWidth);
		this.paintLight.setStyle(Style.STROKE);
	}

	protected void onDraw(Canvas canvas) {
		this.drawFrame(canvas);
	}

	private void clearInit() {
		this.lightAlpha = this.lightAlphaCopy;
	}

	private void drawFrame(Canvas canvas) {
		RectF rectF = new RectF();
		rectF.left = this.frameLeft;
		rectF.top = this.frameTop;
		rectF.right = this.frameRight;
		rectF.bottom = this.frameBottom;
		canvas.drawRoundRect(rectF, this.roundX, this.roundY, this.paintFrame);
	}

	private void drawLight(Canvas canvas) {
		RectF rectF = new RectF();
		rectF.left = this.frameLeft;
		rectF.top = this.frameTop;
		rectF.right = this.frameRight;
		rectF.bottom = this.frameBottom;
		this.drawLightRoundRect(rectF, this.roundX, this.roundY, canvas);
	}

	private void drawLightRoundRect(RectF rectF, float roundX, float roundY, Canvas canvas) {
		++roundX;
		++roundY;
		rectF.left -= (float) this.lightStrokeWidth;
		rectF.top -= (float) this.lightStrokeWidth;
		rectF.right += (float) this.lightStrokeWidth;
		rectF.bottom += (float) this.lightStrokeWidth;
		this.lightAlpha -= this.lightAlpha
				/ (float) (this.lightDrawFrequency > 1 ? this.lightDrawFrequency-- : this.lightDrawFrequency);
		this.paintLight.setAlpha(this.lightAlpha > 0.0F ? (int) this.lightAlpha : 0);
		canvas.drawRoundRect(rectF, roundX, roundY, this.paintLight);
		if (rectF.left >= 0.0F) {
			this.drawLightRoundRect(rectF, roundX, roundY, canvas);
		}

	}

	public void setFocusLayout(float l, float t, float r, float b) {
		this.frameLeft = l;
		this.frameTop = t;
		this.frameRight = r;
		this.frameBottom = b;
		this.invalidate();
	}

	public void focusMove(float l, float t, float r, float b) {
		this.frameLeftMoveEnd = l;
		this.frameTopMoveEnd = t;
		this.frameRightMoveEnd = r;
		this.frameBottomMoveEnd = b;
		this.startMoveFocus();
	}

	private void startMoveFocus() {
		this.frameLeftMove = (this.frameLeftMoveEnd - this.frameLeft) / (float) this.movingNumber;
		this.frameTopMove = (this.frameTopMoveEnd - this.frameTop) / (float) this.movingNumber;
		this.frameRightMove = (this.frameRightMoveEnd - this.frameRight) / (float) this.movingNumber;
		this.frameBottomMove = (this.frameBottomMoveEnd - this.frameBottom) / (float) this.movingNumber;
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				if ((FocusView.this.frameLeftMove <= 0.0F
						|| FocusView.this.frameLeft + FocusView.this.frameLeftMove < FocusView.this.frameLeftMoveEnd)
						&& (FocusView.this.frameLeftMove >= 0.0F || FocusView.this.frameLeft
								+ FocusView.this.frameLeftMove > FocusView.this.frameLeftMoveEnd)
						&& (FocusView.this.frameTopMove <= 0.0F || FocusView.this.frameTop
								+ FocusView.this.frameTopMove < FocusView.this.frameTopMoveEnd)
						&& (FocusView.this.frameTopMove >= 0.0F || FocusView.this.frameTop
								+ FocusView.this.frameTopMove > FocusView.this.frameTopMoveEnd)
						&& (FocusView.this.frameRightMove <= 0.0F || FocusView.this.frameRight
								+ FocusView.this.frameRightMove < FocusView.this.frameRightMoveEnd)
						&& (FocusView.this.frameRightMove >= 0.0F || FocusView.this.frameRight
								+ FocusView.this.frameRightMove > FocusView.this.frameRightMoveEnd)
						&& (FocusView.this.frameBottomMove <= 0.0F || FocusView.this.frameBottom
								+ FocusView.this.frameBottomMove < FocusView.this.frameBottomMoveEnd)
						&& (FocusView.this.frameBottomMove >= 0.0F || FocusView.this.frameBottom
								+ FocusView.this.frameBottomMove > FocusView.this.frameBottomMoveEnd)) {
					FocusView.this.frameLeft = FocusView.this.frameLeft + FocusView.this.frameLeftMove;
					FocusView.this.frameTop = FocusView.this.frameTop + FocusView.this.frameTopMove;
					FocusView.this.frameRight = FocusView.this.frameRight + FocusView.this.frameRightMove;
					FocusView.this.frameBottom = FocusView.this.frameBottom + FocusView.this.frameBottomMove;
					FocusView.this.invalidate();
					(new Handler()).postDelayed(this, (long) FocusView.this.movingVelocity);
				} else {
					FocusView.this.frameLeft = FocusView.this.frameLeftMoveEnd;
					FocusView.this.frameTop = FocusView.this.frameTopMoveEnd;
					FocusView.this.frameRight = FocusView.this.frameRightMoveEnd;
					FocusView.this.frameBottom = FocusView.this.frameBottomMoveEnd;
					FocusView.this.invalidate();
					FocusView.this.setVisibility(0);
				}
			}
		}, (long) this.movingVelocity);
	}

	public void changeMoveEnd(float l, float t, float r, float b) {
		this.frameLeftMoveEnd += l;
		this.frameTopMoveEnd += t;
		this.frameRightMoveEnd += r;
		this.frameBottomMoveEnd += b;
		this.startMoveFocus();
	}

	public void scrollerFocusX(float scrollerX) {
		if (scrollerX != 0.0F) {
			this.changeMoveEnd(scrollerX, 0.0F, scrollerX, 0.0F);
			this.setVisibility(4);
		}
	}

	public void scrollerFocusY(float scrollerY) {
		if (scrollerY != 0.0F) {
			this.changeMoveEnd(0.0F, scrollerY, 0.0F, scrollerY);
			this.setVisibility(4);
		}
	}
}
