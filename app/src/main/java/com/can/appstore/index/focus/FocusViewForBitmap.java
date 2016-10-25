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
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class FocusViewForBitmap extends View implements BaseFocusView {
    private static final String TAG = "FocusView";
    private int frameColor;
    private int lightColor;
    private int frameWidth;
    private int lightWidth;
    private int lightDrawFrequency;
    private int lightStrokeWidth;
    private float roundX;
    private float roundY;
    private Paint paintDrawBitmap;
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
    private Handler handler;
    private Thread thread;
    private boolean isThreadRun;
    private boolean isMoveHideFocus;
    private OnFocusMoveEndListener onFocusMoveEndListener;
    private View changeFocusView;

    public FocusViewForBitmap(Context context) {
        this(context, (AttributeSet) null);
    }

    public FocusViewForBitmap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusViewForBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
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
        this.movingNumber = 15;
        this.movingVelocity = 0;
        this.movingNumberDefault = 15;
        this.movingVelocityDefault = 4;
        this.movingNumberTemporary = -1;
        this.movingVelocityTemporary = -1;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                FocusViewForBitmap.this.setVisibility(0);
            }
        };
        this.thread = new Thread();
        this.isMoveHideFocus = false;
        this.init();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public FocusViewForBitmap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        this.movingNumber = 60;
        this.movingVelocity = 0;
        this.movingNumberDefault = 60;
        this.movingVelocityDefault = 4;
        this.movingNumberTemporary = -1;
        this.movingVelocityTemporary = -1;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                FocusViewForBitmap.this.setVisibility(0);
            }
        };
        this.thread = new Thread();
        this.isMoveHideFocus = false;
        this.init();
    }

    private void init() {
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

    public void initFocusBitmapRes(int imageRes) {
        this.initFocusBitmapRes(imageRes, imageRes);
    }

    public void initFocusBitmapRes(int imageRes, boolean isMoveHideFocus) {
        this.isMoveHideFocus = isMoveHideFocus;
        this.initFocusBitmapRes(imageRes, imageRes);
    }

    public void initFocusBitmapRes(int imageRes, int imageResTwo, boolean isMoveHideFocus) {
        this.isMoveHideFocus = isMoveHideFocus;
        this.initFocusBitmapRes(imageRes, imageResTwo);
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
        if (this.isMoveHideFocus) {
            this.hideFocus();
        }
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

    protected void onDraw(Canvas canvas) {
        this.drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        Rect rect = new Rect();
        rect.left = (int) (this.frameLeft - (float) this.rectBitmap.left);
        rect.top = (int) (this.frameTop - (float) this.rectBitmap.top);
        rect.right = (int) (this.frameRight + (float) this.rectBitmap.right);
        rect.bottom = (int) (this.frameBottom + (float) this.rectBitmap.bottom);
        this.ninePatch.draw(canvas, rect);
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
        this.lightAlpha -= this.lightAlpha / (float) (this.lightDrawFrequency > 1 ? this.lightDrawFrequency-- : this.lightDrawFrequency);
        this.paintLight.setAlpha(this.lightAlpha > 0.0F ? (int) this.lightAlpha : 0);
        canvas.drawRoundRect(rectF, roundX, roundY, this.paintLight);
        if (rectF.left >= 0.0F) {
            this.drawLightRoundRect(rectF, roundX, roundY, canvas);
        }

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
//        Log.i("Focus", "frameLeftMove " + frameLeftMove);
//        Log.i("Focus", "frameTopMove " + frameTopMove);
//        Log.i("Focus", "frameRightMove " + frameRightMove);
//        Log.i("Focus", "frameBottomMove " + frameBottomMove);
//        Log.i("Focus", "frameLeft " + frameLeft);
//        Log.i("Focus", "frameTop " + frameTop);
//        Log.i("Focus", "frameRight " + frameRight);
//        Log.i("Focus", "frameBottom " + frameBottom);
        if (!this.isThreadRun) {
            this.isThreadRun = true;
            FocusViewForBitmap.this.postDelayed(new AutoMoveRunnable(), 16);
            FocusViewForBitmap.this.isThreadRun = false;
        }
    }

    private class AutoMoveRunnable implements Runnable {
        public AutoMoveRunnable() {
        }

        @Override
        public void run() {
            if ((FocusViewForBitmap.this.frameLeftMove <= 0.0F || FocusViewForBitmap.this.frameLeft + FocusViewForBitmap.this.frameLeftMove < FocusViewForBitmap.this.frameLeftMoveEnd) && (FocusViewForBitmap.this.frameLeftMove >= 0.0F || FocusViewForBitmap.this.frameLeft + FocusViewForBitmap.this.frameLeftMove > FocusViewForBitmap.this.frameLeftMoveEnd) && (FocusViewForBitmap.this.frameTopMove <= 0.0F || FocusViewForBitmap.this.frameTop + FocusViewForBitmap.this.frameTopMove < FocusViewForBitmap.this.frameTopMoveEnd) && (FocusViewForBitmap.this.frameTopMove >= 0.0F || FocusViewForBitmap.this.frameTop + FocusViewForBitmap.this.frameTopMove > FocusViewForBitmap.this.frameTopMoveEnd) && (FocusViewForBitmap.this.frameRightMove <= 0.0F || FocusViewForBitmap.this.frameRight + FocusViewForBitmap.this.frameRightMove < FocusViewForBitmap.this.frameRightMoveEnd) && (FocusViewForBitmap.this.frameRightMove >= 0.0F || FocusViewForBitmap.this.frameRight + FocusViewForBitmap.this.frameRightMove > FocusViewForBitmap.this.frameRightMoveEnd) && (FocusViewForBitmap.this.frameBottomMove <= 0.0F || FocusViewForBitmap.this.frameBottom + FocusViewForBitmap.this.frameBottomMove < FocusViewForBitmap.this.frameBottomMoveEnd) && (FocusViewForBitmap.this.frameBottomMove >= 0.0F || FocusViewForBitmap.this.frameBottom + FocusViewForBitmap.this.frameBottomMove > FocusViewForBitmap.this.frameBottomMoveEnd)) {
                FocusViewForBitmap.this.frameLeft = FocusViewForBitmap.this.frameLeft + FocusViewForBitmap.this.frameLeftMove;
                FocusViewForBitmap.this.frameTop = FocusViewForBitmap.this.frameTop + FocusViewForBitmap.this.frameTopMove;
                FocusViewForBitmap.this.frameRight = FocusViewForBitmap.this.frameRight + FocusViewForBitmap.this.frameRightMove;
                FocusViewForBitmap.this.frameBottom = FocusViewForBitmap.this.frameBottom + FocusViewForBitmap.this.frameBottomMove;
                FocusViewForBitmap.this.postInvalidateDelayed(0);
                FocusViewForBitmap.this.postDelayed(new AutoMoveRunnable(), 16);
            } else {
                FocusViewForBitmap.this.frameLeft = FocusViewForBitmap.this.frameLeftMoveEnd;
                FocusViewForBitmap.this.frameTop = FocusViewForBitmap.this.frameTopMoveEnd;
                FocusViewForBitmap.this.frameRight = FocusViewForBitmap.this.frameRightMoveEnd;
                FocusViewForBitmap.this.frameBottom = FocusViewForBitmap.this.frameBottomMoveEnd;
                FocusViewForBitmap.this.postInvalidateDelayed((long) FocusViewForBitmap.this.movingVelocity);
//                Log.i("Focus", "FocusViewForBitmap.this.frameLeft " + FocusViewForBitmap.this.frameLeft);
//                Log.i("Focus", "FocusViewForBitmap.this.frameRight " + FocusViewForBitmap.this.frameRight);
                if (FocusViewForBitmap.this.isMoveHideFocus) {
                    FocusViewForBitmap.this.handler.sendMessage(FocusViewForBitmap.this.handler.obtainMessage());
                    FocusViewForBitmap.this.isMoveHideFocus = false;
                }

                if (FocusViewForBitmap.this.onFocusMoveEndListener != null) {
                    FocusViewForBitmap.this.onFocusMoveEndListener.focusEnd(FocusViewForBitmap.this.changeFocusView);
                }
            }
        }
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
}
