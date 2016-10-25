//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class FocusViewForAnimation extends View implements BaseFocusView {
	private static final String TAG = "FocusView";
	private float frameLeft;
	private float frameTop;
	private float frameRight;
	private float frameBottom;
	private float frameLeftMoveEnd;
	private float frameTopMoveEnd;
	private float frameRightMoveEnd;
	private float frameBottomMoveEnd;
	private float frameWidth;
	private float frameHeight;
	private float newFrameWidth;
	private float newFrameHeight;
	private int movingNumber;
	private int movingVelocity;
	private int movingNumberDefault;
	private int movingVelocityDefault;
	private int movingNumberTemporary;
	private int movingVelocityTemporary;
	private int imageMainRes;
	private Rect rectBitmapMain;
	private int imageTopRes;
	private Rect rectBitmapTopView;
	private int imageRes;
	private Rect rectBitmap;
	private Handler handler;
	private AnimatorSet mCurrentAnimatorSet;
	private OnFocusMoveEndListener onFocusMoveEndListener;
	private View changeFocusView;

	public FocusViewForAnimation(Context context) {
		this(context, (AttributeSet) null);
	}

	public FocusViewForAnimation(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FocusViewForAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.movingNumber = 80;
		this.movingVelocity = 3;
		this.movingNumberDefault = 80;
		this.movingVelocityDefault = 3;
		this.movingNumberTemporary = -1;
		this.movingVelocityTemporary = -1;
		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				FocusViewForAnimation.this.setVisibility(0);
			}
		};
		this.init(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FocusViewForAnimation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);
		this.movingNumber = 80;
		this.movingVelocity = 3;
		this.movingNumberDefault = 80;
		this.movingVelocityDefault = 3;
		this.movingNumberTemporary = -1;
		this.movingVelocityTemporary = -1;
		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				FocusViewForAnimation.this.setVisibility(0);
			}
		};
		this.init(context);
	}

	protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
	}

	private void setFocusLayout() {
		Rect rect = new Rect();
		rect.left = (int) (this.frameLeft - (float) this.rectBitmap.left);
		rect.top = (int) (this.frameTop - (float) this.rectBitmap.top);
		rect.right = (int) (this.frameRight + (float) this.rectBitmap.right);
		rect.bottom = (int) (this.frameBottom + (float) this.rectBitmap.bottom);
		this.layout(rect.left, rect.top, rect.right, rect.bottom);
	}

	private void init(Context context) {
	}

	public void initFocusBitmapRes(int imageRes) {
		this.initFocusBitmapRes(imageRes, imageRes);
	}

	public void initFocusBitmapRes(int imageRes, int imageResTwo) {
		this.imageMainRes = imageRes;
		this.imageTopRes = imageResTwo;
		this.imageRes = imageRes;
		this.setBackgroundResource(imageRes);
		this.rectBitmapMain = new Rect();
		Drawable drawableMain = this.getResources().getDrawable(imageRes);
		if (drawableMain != null) {
			drawableMain.getPadding(this.rectBitmapMain);
		}

		this.rectBitmapTopView = new Rect();
		Drawable drawableTopView = this.getResources().getDrawable(imageResTwo);
		if (drawableTopView != null) {
			drawableTopView.getPadding(this.rectBitmapTopView);
		}

		this.rectBitmap = this.rectBitmapMain;
		Log.e("zxcFocus", "rectBitmap==" + this.rectBitmap.left + "==" + this.rectBitmap.top + "=="
				+ this.rectBitmap.right + "==" + this.rectBitmap.bottom);
	}

	public void setBitmapForTop() {
		this.imageRes = this.imageTopRes;
		this.rectBitmap = this.rectBitmapTopView;
		this.changeFocusBg();
	}

	public void clearBitmapForTop() {
		this.imageRes = this.imageMainRes;
		this.rectBitmap = this.rectBitmapMain;
		this.changeFocusBg();
	}

	private void changeFocusBg() {
		this.setBackgroundResource(this.imageRes);
	}

	private void clearInit() {
	}

	public void setFocusLayout(float l, float t, float r, float b) {
		this.setVisibility(0);
	}

	public void focusMove(float l, float t, float r, float b) {
		this.frameLeftMoveEnd = l;
		this.frameTopMoveEnd = t;
		this.frameRightMoveEnd = r;
		this.frameBottomMoveEnd = b;
		this.newFrameWidth = r - l;
		this.newFrameHeight = b - t;
		this.startMoveFocus();
	}

	private void startMoveFocus() {
		if (this.mCurrentAnimatorSet != null) {
			this.mCurrentAnimatorSet.cancel();
		}

		ObjectAnimator transAnimatorX = ObjectAnimator.ofFloat(this, "translationX",
				new float[] { this.frameLeftMoveEnd - (float) this.rectBitmap.left });
		ObjectAnimator transAnimatorY = ObjectAnimator.ofFloat(this, "translationY",
				new float[] { this.frameTopMoveEnd - (float) this.rectBitmap.top });
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofInt(new FocusViewForAnimation.ScaleView(this), "width",
				new int[] { (int) this.frameWidth + this.rectBitmap.left + this.rectBitmap.right,
						(int) this.newFrameWidth + this.rectBitmap.left + this.rectBitmap.right });
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofInt(new FocusViewForAnimation.ScaleView(this), "height",
				new int[] { (int) this.frameHeight + this.rectBitmap.top + this.rectBitmap.bottom,
						(int) this.newFrameHeight + this.rectBitmap.top + this.rectBitmap.bottom });
		AnimatorSet mAnimatorSet = new AnimatorSet();
		mAnimatorSet.playTogether(new Animator[] { transAnimatorX, transAnimatorY, scaleXAnimator, scaleYAnimator });
		mAnimatorSet.setInterpolator(new DecelerateInterpolator(1.0F));
		mAnimatorSet.setDuration(200L);
		mAnimatorSet.addListener(new AnimatorListener() {
			public void onAnimationStart(Animator animator) {
				Log.i("FocusAnimation", "Start");
			}

			public void onAnimationEnd(Animator animator) {
				Log.i("FocusAnimation", "End");
				if (FocusViewForAnimation.this.onFocusMoveEndListener != null) {
					FocusViewForAnimation.this.onFocusMoveEndListener
							.focusEnd(FocusViewForAnimation.this.changeFocusView);
				}

			}

			public void onAnimationCancel(Animator animator) {
				Log.i("FocusAnimation", "Cancel");
			}

			public void onAnimationRepeat(Animator animator) {
				Log.i("FocusAnimation", "Repeat");
			}
		});
		mAnimatorSet.start();
		this.mCurrentAnimatorSet = mAnimatorSet;
		this.frameWidth = this.newFrameWidth;
		this.frameHeight = this.newFrameHeight;
		this.frameLeft = this.frameLeftMoveEnd;
		this.frameTop = this.frameTopMoveEnd;
		this.frameRight = this.frameRightMoveEnd;
		this.frameBottom = this.frameBottomMoveEnd;
	}

	private void changeMoveEnd(float l, float t, float r, float b) {
		this.frameLeftMoveEnd += l;
		this.frameTopMoveEnd += t;
		this.frameRightMoveEnd += r;
		this.frameBottomMoveEnd += b;
		this.startMoveFocus();
	}

	public void scrollerFocusX(float scrollerX) {
		if (scrollerX != 0.0F) {
			this.changeMoveEnd(scrollerX, 0.0F, scrollerX, 0.0F);
		}
	}

	public void scrollerFocusY(float scrollerY) {
		if (scrollerY != 0.0F) {
			this.changeMoveEnd(0.0F, scrollerY, 0.0F, scrollerY);
		}
	}

	public void hideFocus() {
		this.setVisibility(4);
	}

	public void showFocus() {
		this.setVisibility(0);
	}

	public void setFocusBitmap(int resId) {
		try {
			Rect rectBitmapNew = new Rect();
			Drawable drawableNew = this.getResources().getDrawable(resId);
			drawableNew.getPadding(rectBitmapNew);
			this.imageRes = resId;
			this.rectBitmap = rectBitmapNew;
			this.changeFocusBg();
		} catch (Exception var4) {
			;
		}

	}

	public void setMoveVelocity(int movingNumberDefault, int movingVelocityDefault) {
		this.movingNumberDefault = movingNumberDefault;
		this.movingVelocityDefault = movingVelocityDefault;
	}

	public void setMoveVelocityTemporary(int movingNumberTemporary, int movingVelocityTemporary) {
		this.movingNumberTemporary = movingNumberTemporary;
		this.movingVelocityTemporary = movingVelocityTemporary;
	}

	public void setOnFocusMoveEndListener(OnFocusMoveEndListener onFocusMoveEndListener, View changeFocusView) {
		this.onFocusMoveEndListener = onFocusMoveEndListener;
		this.changeFocusView = changeFocusView;
	}

	public class ScaleView {
		private View view;
		private int width;
		private int height;

		public ScaleView(View view) {
			this.view = view;
		}

		public int getWidth() {
			return this.view.getLayoutParams().width;
		}

		public void setWidth(int width) {
			this.width = width;
			this.view.getLayoutParams().width = width;
			this.view.requestLayout();
		}

		public int getHeight() {
			return this.view.getLayoutParams().height;
		}

		public void setHeight(int height) {
			this.height = height;
			this.view.getLayoutParams().height = height;
			this.view.requestLayout();
		}
	}
}
