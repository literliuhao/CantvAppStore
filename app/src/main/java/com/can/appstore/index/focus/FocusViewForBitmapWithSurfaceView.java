//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class FocusViewForBitmapWithSurfaceView extends SurfaceView implements BaseFocusView, Runnable, Callback {
	private static final String TAG = "FocusView";
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
	private int movingNumberDefault;
	private int movingVelocityDefault;
	private int movingNumberTemporary;
	private int movingVelocityTemporary;
	private Bitmap bitmapMain;
	private Rect rectBitmapMain;
	private NinePatch ninePatchMain;
	private Bitmap bitmapTopView;
	private Rect rectBitmapTopView;
	private NinePatch ninePatchTopView;
	private Bitmap bitmap;
	private Rect rectBitmap;
	private NinePatch ninePatch;
	private Thread thread;
	private boolean isThreadRun;
	private SurfaceHolder surfaceHolder;
	private Canvas mCanvas;
	private boolean isCanRun;
	private Thread threadDraw;
	private boolean isDraw;
	private OnFocusMoveEndListener onFocusMoveEndListener;
	private View changeFocusView;
	boolean isHide;

	public FocusViewForBitmapWithSurfaceView(Context context) {
		this(context, (AttributeSet) null);
	}

	public FocusViewForBitmapWithSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FocusViewForBitmapWithSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.frameLeft = 100.0F;
		this.frameTop = 100.0F;
		this.frameRight = 1000.0F;
		this.frameBottom = 1000.0F;
		this.movingTime = 1000;
		this.movingNumber = 40;
		this.movingVelocity = 3;
		this.movingNumberDefault = 40;
		this.movingVelocityDefault = 3;
		this.movingNumberTemporary = -1;
		this.movingVelocityTemporary = -1;
		this.thread = new Thread();
		this.init();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public FocusViewForBitmapWithSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);
		this.frameLeft = 100.0F;
		this.frameTop = 100.0F;
		this.frameRight = 1000.0F;
		this.frameBottom = 1000.0F;
		this.movingTime = 1000;
		this.movingNumber = 40;
		this.movingVelocity = 3;
		this.movingNumberDefault = 40;
		this.movingVelocityDefault = 3;
		this.movingNumberTemporary = -1;
		this.movingVelocityTemporary = -1;
		this.thread = new Thread();
		this.init();
	}

	private void init() {
		this.surfaceHolder = this.getHolder();
		this.surfaceHolder.addCallback(this);
		this.setZOrderOnTop(true);
		this.surfaceHolder.setFormat(-3);
	}

	public void initFocusBitmapRes(int imageRes) {
		this.initFocusBitmapRes(imageRes, imageRes);
	}

	public void initFocusBitmapRes(int imageRes, int imageResTwo) {
		this.bitmapMain = BitmapFactory.decodeResource(this.getResources(), imageRes);
		this.rectBitmapMain = new Rect();
		Drawable drawableMain = this.getResources().getDrawable(imageRes);
		if (drawableMain != null) {
			drawableMain.getPadding(this.rectBitmapMain);
		}

		this.ninePatchMain = new NinePatch(this.bitmapMain, this.bitmapMain.getNinePatchChunk());
		if (imageResTwo == 0) {
			imageResTwo = imageRes;
		}

		this.bitmapTopView = BitmapFactory.decodeResource(this.getResources(), imageResTwo);
		this.rectBitmapTopView = new Rect();
		Drawable drawableTopView = this.getResources().getDrawable(imageResTwo);
		if (drawableTopView != null) {
			drawableTopView.getPadding(this.rectBitmapTopView);
		}

		this.ninePatchTopView = new NinePatch(this.bitmapTopView, this.bitmapTopView.getNinePatchChunk());
		this.bitmap = this.bitmapMain;
		this.rectBitmap = this.rectBitmapMain;
		this.ninePatch = this.ninePatchMain;
	}

	public void setBitmapForTop() {
		this.bitmap = this.bitmapTopView;
		this.rectBitmap = this.rectBitmapTopView;
		this.ninePatch = this.ninePatchTopView;
	}

	public void clearBitmapForTop() {
		this.bitmap = this.bitmapMain;
		this.rectBitmap = this.rectBitmapMain;
		this.ninePatch = this.ninePatchMain;
	}

	private void drawBitmap(Canvas canvas) {
		Rect rect = new Rect();
		rect.left = (int) (this.frameLeft - (float) this.rectBitmap.left);
		rect.top = (int) (this.frameTop - (float) this.rectBitmap.top);
		rect.right = (int) (this.frameRight + (float) this.rectBitmap.right);
		rect.bottom = (int) (this.frameBottom + (float) this.rectBitmap.bottom);
		Log.i("SurfaceView", "drawBitmap+left==" + rect.left + "==right==" + rect.right + "==top==" + rect.top
				+ "==bottom==" + rect.bottom);
		this.ninePatch.draw(canvas, rect);
	}

	public void setFocusLayout(float l, float t, float r, float b) {
		this.frameLeftMoveEnd = l;
		this.frameTopMoveEnd = t;
		this.frameRightMoveEnd = r;
		this.frameBottomMoveEnd = b;
		this.frameLeft = l;
		this.frameTop = t;
		this.frameRight = r;
		this.frameBottom = b;
		this.setVisibility(0);
	}

	public void focusMove(float l, float t, float r, float b) {
		this.frameLeftMoveEnd = l;
		this.frameTopMoveEnd = t;
		this.frameRightMoveEnd = r;
		this.frameBottomMoveEnd = b;
		this.startMoveFocus();
	}

	private void startMoveFocus() {
		if (this.movingNumberTemporary != -1 && this.movingVelocityTemporary != -1) {
			this.movingNumber = this.movingNumberTemporary;
			this.movingVelocity = this.movingVelocityTemporary;
			this.movingNumberTemporary = -1;
			this.movingVelocityTemporary = -1;
		} else {
			this.movingNumber = this.movingNumberDefault;
			this.movingVelocity = this.movingVelocityDefault;
		}

		this.frameLeftMove = (this.frameLeftMoveEnd - this.frameLeft) / (float) this.movingNumber;
		this.frameTopMove = (this.frameTopMoveEnd - this.frameTop) / (float) this.movingNumber;
		this.frameRightMove = (this.frameRightMoveEnd - this.frameRight) / (float) this.movingNumber;
		this.frameBottomMove = (this.frameBottomMoveEnd - this.frameBottom) / (float) this.movingNumber;
		if (this.isThreadRun) {
			Class var1 = FocusViewForBitmapWithSurfaceView.class;
			synchronized (FocusViewForBitmapWithSurfaceView.class) {
				if (this.isThreadRun) {
					return;
				}
			}
		}

		this.isThreadRun = true;
		this.thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Log.i("SurfaceView", "ThreadStart");
					Log.i("SurfaceView", "frameLeftMove==" + FocusViewForBitmapWithSurfaceView.this.frameLeftMove
							+ "==frameTopMove==" + FocusViewForBitmapWithSurfaceView.this.frameTopMove
							+ "==frameRightMove==" + FocusViewForBitmapWithSurfaceView.this.frameRightMove
							+ "==frameBottomMove==" + FocusViewForBitmapWithSurfaceView.this.frameBottomMove + "/n"
							+ "==frameLeft==" + FocusViewForBitmapWithSurfaceView.this.frameLeft + "==frameTop=="
							+ FocusViewForBitmapWithSurfaceView.this.frameTop + "==frameRight=="
							+ FocusViewForBitmapWithSurfaceView.this.frameRight + "==frameBottom=="
							+ FocusViewForBitmapWithSurfaceView.this.frameBottom + "/n" + "==frameLeftMoveEnd=="
							+ FocusViewForBitmapWithSurfaceView.this.frameLeftMoveEnd + "==frameTopMoveEnd=="
							+ FocusViewForBitmapWithSurfaceView.this.frameTopMoveEnd + "==frameRightMoveEnd=="
							+ FocusViewForBitmapWithSurfaceView.this.frameRightMoveEnd + "==frameBottomMoveEnd=="
							+ FocusViewForBitmapWithSurfaceView.this.frameBottomMoveEnd);
					if (FocusViewForBitmapWithSurfaceView.this.checkMoveOk()) {
						Class e = FocusViewForBitmapWithSurfaceView.class;
						synchronized (FocusViewForBitmapWithSurfaceView.class) {
							if (FocusViewForBitmapWithSurfaceView.this.checkMoveOk()) {
								FocusViewForBitmapWithSurfaceView.this.frameLeft = FocusViewForBitmapWithSurfaceView.this.frameLeftMoveEnd;
								FocusViewForBitmapWithSurfaceView.this.frameTop = FocusViewForBitmapWithSurfaceView.this.frameTopMoveEnd;
								FocusViewForBitmapWithSurfaceView.this.frameRight = FocusViewForBitmapWithSurfaceView.this.frameRightMoveEnd;
								FocusViewForBitmapWithSurfaceView.this.frameBottom = FocusViewForBitmapWithSurfaceView.this.frameBottomMoveEnd;
								FocusViewForBitmapWithSurfaceView.this.threadDraw = new Thread(
										FocusViewForBitmapWithSurfaceView.this);
								FocusViewForBitmapWithSurfaceView.this.threadDraw.start();
								FocusViewForBitmapWithSurfaceView.this.isDraw = true;
								Log.i("SurfaceView", "ThreadEnd");
								if (FocusViewForBitmapWithSurfaceView.this.onFocusMoveEndListener != null) {
									FocusViewForBitmapWithSurfaceView.this.onFocusMoveEndListener
											.focusEnd(FocusViewForBitmapWithSurfaceView.this.changeFocusView);
								}

								FocusViewForBitmapWithSurfaceView.this.isThreadRun = false;
								return;
							}
						}
					}

					FocusViewForBitmapWithSurfaceView.this.frameLeft = FocusViewForBitmapWithSurfaceView.this.frameLeft
							+ FocusViewForBitmapWithSurfaceView.this.frameLeftMove;
					FocusViewForBitmapWithSurfaceView.this.frameTop = FocusViewForBitmapWithSurfaceView.this.frameTop
							+ FocusViewForBitmapWithSurfaceView.this.frameTopMove;
					FocusViewForBitmapWithSurfaceView.this.frameRight = FocusViewForBitmapWithSurfaceView.this.frameRight
							+ FocusViewForBitmapWithSurfaceView.this.frameRightMove;
					FocusViewForBitmapWithSurfaceView.this.frameBottom = FocusViewForBitmapWithSurfaceView.this.frameBottom
							+ FocusViewForBitmapWithSurfaceView.this.frameBottomMove;
					Log.i("SurfaceView",
							"Thread===frameLeft==" + FocusViewForBitmapWithSurfaceView.this.frameLeft + "==frameTop="
									+ FocusViewForBitmapWithSurfaceView.this.frameTop + "==frameRight=="
									+ FocusViewForBitmapWithSurfaceView.this.frameRight + "==frameBottom=="
									+ FocusViewForBitmapWithSurfaceView.this.frameBottom);
					if (!FocusViewForBitmapWithSurfaceView.this.isDraw) {
						FocusViewForBitmapWithSurfaceView.this.threadDraw = new Thread(
								FocusViewForBitmapWithSurfaceView.this);
						FocusViewForBitmapWithSurfaceView.this.threadDraw.start();
						FocusViewForBitmapWithSurfaceView.this.isDraw = true;
					}

					try {
						Thread.sleep((long) FocusViewForBitmapWithSurfaceView.this.movingVelocity);
						Log.i("SurfaceView", "ThreadSleep");
					} catch (InterruptedException var3) {
						var3.printStackTrace();
					}
				}
			}
		});
		this.thread.start();
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

	public void setFocusBitmap(int resId) {
		try {
			Bitmap e = BitmapFactory.decodeResource(this.getResources(), resId);
			Rect rectBitmapNew = new Rect();
			Drawable drawableNew = this.getResources().getDrawable(resId);
			drawableNew.getPadding(rectBitmapNew);
			NinePatch ninePatchNew = new NinePatch(e, e.getNinePatchChunk());
			this.bitmap = e;
			this.rectBitmap = rectBitmapNew;
			this.ninePatch = ninePatchNew;
		} catch (Exception var6) {
			throw new RuntimeException("resId == null");
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

	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		Log.i("SurfaceView", "surfaceCreated");
		this.isCanRun = true;
	}

	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
	}

	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		Log.i("SurfaceView", "surfaceDestroyed");
		this.isCanRun = false;
	}

	public void run() {
		Log.i("SurfaceView", "run");
		synchronized (this) {
			if (!this.isHide) {
				if (this.isCanRun) {
					Log.i("SurfaceView", "run==ok");
					this.mCanvas = null;

					try {
						this.mCanvas = this.surfaceHolder.lockCanvas();
						this.mCanvas.drawColor(0, Mode.CLEAR);
						this.drawBitmap(this.mCanvas);
					} catch (Exception var47) {
						var47.printStackTrace();
					} finally {
						try {
							if (this.mCanvas != null) {
								this.surfaceHolder.unlockCanvasAndPost(this.mCanvas);
							}
						} catch (Exception var45) {
							;
						} finally {
							this.isDraw = false;
						}

					}
				}

			}
		}
	}

	private boolean checkMoveOk() {
		return this.frameLeftMove > 0.0F && this.frameLeft + this.frameLeftMove >= this.frameLeftMoveEnd
				|| this.frameLeftMove < 0.0F && this.frameLeft + this.frameLeftMove <= this.frameLeftMoveEnd
				|| this.frameTopMove > 0.0F && this.frameTop + this.frameTopMove >= this.frameTopMoveEnd
				|| this.frameTopMove < 0.0F && this.frameTop + this.frameTopMove <= this.frameTopMoveEnd
				|| this.frameRightMove > 0.0F && this.frameRight + this.frameRightMove >= this.frameRightMoveEnd
				|| this.frameRightMove < 0.0F && this.frameRight + this.frameRightMove <= this.frameRightMoveEnd
				|| this.frameBottomMove > 0.0F && this.frameBottom + this.frameBottomMove >= this.frameBottomMoveEnd
				|| this.frameBottomMove < 0.0F && this.frameBottom + this.frameBottomMove <= this.frameBottomMoveEnd;
	}

	public void hideFocus() {
	}

	public void showFocus() {
	}
}
