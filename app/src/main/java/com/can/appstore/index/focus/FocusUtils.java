//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.can.appstore.index.focus;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

public class FocusUtils {
    private FocusViewForBitmap focusView;
    private Context context;

    public FocusUtils(Context context, View actLayout, int bitmapRes) {
        this(context, actLayout, bitmapRes, bitmapRes);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, boolean isInitMoveHideFocus) {
        this(context, actLayout, bitmapRes, bitmapRes, isInitMoveHideFocus);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo) {
        this(context, actLayout, bitmapRes, bitmapResTwo, true);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo, boolean isInitMoveHideFocus) {
        this.context = context;
        this.initFocusView(actLayout, bitmapRes, bitmapResTwo, isInitMoveHideFocus);
    }

    private void initFocusView(View actLayout, int bitmapRes, int bitmapResTwo, boolean isInitMoveHideFocus) {
        this.focusView = new FocusViewForBitmap(this.context);
        this.focusView.initFocusBitmapRes(bitmapRes, bitmapResTwo, isInitMoveHideFocus);
        LayoutParams params = new LayoutParams(-1, -1);
        this.focusView.setFocusable(false);
        this.focusView.setClickable(false);
        if (actLayout instanceof ViewGroup) {
            ((ViewGroup) actLayout).addView(this.focusView, params);
        }

        this.focusView.setFocusLayout(-50.0F, -50.0F, -50.0F, -50.0F);
    }

    public void setFocusLayout(float l, float t, float r, float b) {
        this.focusView.setFocusLayout(l, t, r, b);
    }

    public void setFocusLayout(View view, boolean isScalable, float scale) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (isScalable) {
            float pL = ((float) view.getWidth() * scale - (float) view.getWidth()) / 2.0F;
            float pT = ((float) view.getHeight() * scale - (float) view.getHeight()) / 2.0F;
            this.focusView.setFocusLayout((float) ((int) ((float) location[0] - pL)),
                    (float) ((int) ((float) location[1] - pT)),
                    (float) ((int) ((float) (location[0] + view.getWidth()) + pL)),
                    (float) ((int) ((float) (location[1] + view.getHeight()) + pT)));
        } else {
            this.focusView.setFocusLayout((float) location[0], (float) location[1],
                    (float) (location[0] + view.getWidth()), (float) (location[1] + view.getHeight()));
        }

    }

    public void setFocusLayout(View view, Rect clipRect, boolean isScalable, float scale) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (isScalable) {
            float pL = ((float) view.getWidth() * scale - (float) view.getWidth()) / 2.0F;
            float pT = ((float) view.getHeight() * scale - (float) view.getHeight()) / 2.0F;
            this.focusView.setFocusLayout((float) ((int) ((float) location[0] - pL + (float) clipRect.left)),
                    (float) ((int) ((float) location[1] - pT + (float) clipRect.top)),
                    (float) ((int) ((float) (location[0] + view.getWidth()) + pL - (float) clipRect.right)),
                    (float) ((int) ((float) (location[1] + view.getHeight()) + pT - (float) clipRect.bottom)));
        } else {
            this.focusView.setFocusLayout((float) (location[0] + clipRect.left), (float) (location[1] + clipRect.top),
                    (float) (location[0] + view.getWidth() - clipRect.right),
                    (float) (location[1] + view.getHeight() - clipRect.bottom));
        }

    }

    public void startMoveFocus(float l, float t, float r, float b) {
        this.focusView.focusMove(l, t, r, b);
    }

    public void startMoveFocus(View view, boolean isScalable, float scale) {
        this.startMoveFocus(view, (Rect) null, isScalable, scale, 0.0F, 0.0F);
    }

    public void startMoveFocus(View view, boolean isScalable, float scaleX, float scaleY) {
        this.startMoveFocus(view, (Rect) null, isScalable, scaleX, scaleY, 0.0F, 0.0F);
    }

    public void startMoveFocus(View view, Rect clipRect, boolean isScalable, float scale) {
        this.startMoveFocus(view, clipRect, isScalable, scale, 0.0F, 0.0F);
    }

    public void startMoveFocus(View view, boolean isScalable, float scale, float scrollerX, float scrollerY) {
        this.startMoveFocus(view, (Rect) null, isScalable, scale, scrollerX, scrollerY);
    }

    public void startMoveFocus(View view, Rect clipRect, boolean isScalable, float scale, float scrollerX, float scrollerY) {
        if (view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            float pL;
            float pT;
            if (clipRect == null) {
                if (isScalable) {
                    pL = ((float) view.getWidth() * (scale * 1.0f) - (float) view.getWidth()) / 2.0F;
                    pT = ((float) view.getHeight() * (scale * 1.0f) - (float) view.getHeight()) / 2.0F;
                    this.focusView.focusMove((float) ((int) ((float) location[0] - pL + scrollerX)),
                            (float) ((int) ((float) location[1] - pT + scrollerY)),
                            (float) ((int) ((float) (location[0] + view.getWidth()) + pL + scrollerX)),
                            (float) ((int) ((float) (location[1] + view.getHeight()) + pT + scrollerY)));
                } else {
                    this.focusView.focusMove((float) location[0] + scrollerX, (float) location[1] + scrollerY,
                            (float) (location[0] + view.getWidth()) + scrollerX,
                            (float) (location[1] + view.getHeight()) + scrollerY);
                }
            } else if (isScalable) {
                pL = ((float) view.getWidth() * scale - (float) view.getWidth()) / 2.0F;
                pT = ((float) view.getHeight() * scale - (float) view.getHeight()) / 2.0F;
                this.focusView.focusMove((float) ((int) ((float) location[0] - pL + (float) clipRect.left + scrollerX)),
                        (float) ((int) ((float) location[1] - pT + (float) clipRect.top + scrollerY)),
                        (float) ((int) ((float) (location[0] + view.getWidth()) + pL - (float) clipRect.right
                                + scrollerX)),
                        (float) ((int) ((float) (location[1] + view.getHeight()) + pT - (float) clipRect.bottom
                                + scrollerY)));
            } else {
                this.focusView.focusMove((float) (location[0] + clipRect.left) + scrollerX,
                        (float) (location[1] + clipRect.top) + scrollerY,
                        (float) (location[0] + view.getWidth() - clipRect.right) + scrollerX,
                        (float) (location[1] + view.getHeight() - clipRect.bottom) + scrollerY);
            }

        }
    }

    public void startMoveFocus(View view, Rect clipRect, boolean isScalable, float scaleX, float scaleY, float scrollerX, float scrollerY) {
        if (view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            float pL;
            float pT;
            if (clipRect == null) {
                if (isScalable) {
                    pL = ((float) view.getWidth() * (scaleX * 1.0f) - (float) view.getWidth()) / 2.0F;
                    pT = ((float) view.getHeight() * (scaleY * 1.0f) - (float) view.getHeight()) / 2.0F;
                    this.focusView.focusMove((float) ((int) ((float) location[0] - pL + scrollerX)),
                            (float) ((int) ((float) location[1] - pT + scrollerY)),
                            (float) ((int) ((float) (location[0] + view.getWidth()) + pL + scrollerX)),
                            (float) ((int) ((float) (location[1] + view.getHeight()) + pT + scrollerY)));
                } else {
                    this.focusView.focusMove((float) location[0] + scrollerX, (float) location[1] + scrollerY,
                            (float) (location[0] + view.getWidth()) + scrollerX,
                            (float) (location[1] + view.getHeight()) + scrollerY);
                }
            } else if (isScalable) {
                pL = ((float) view.getWidth() * scaleX - (float) view.getWidth()) / 2.0F;
                pT = ((float) view.getHeight() * scaleY - (float) view.getHeight()) / 2.0F;
                this.focusView.focusMove((float) ((int) ((float) location[0] - pL + (float) clipRect.left + scrollerX)),
                        (float) ((int) ((float) location[1] - pT + (float) clipRect.top + scrollerY)),
                        (float) ((int) ((float) (location[0] + view.getWidth()) + pL - (float) clipRect.right
                                + scrollerX)),
                        (float) ((int) ((float) (location[1] + view.getHeight()) + pT - (float) clipRect.bottom
                                + scrollerY)));
            } else {
                this.focusView.focusMove((float) (location[0] + clipRect.left) + scrollerX,
                        (float) (location[1] + clipRect.top) + scrollerY,
                        (float) (location[0] + view.getWidth() - clipRect.right) + scrollerX,
                        (float) (location[1] + view.getHeight() - clipRect.bottom) + scrollerY);
            }

        }
    }

    public void scrollerFocusX(float scrollerX) {
        this.focusView.scrollerFocusX(scrollerX);
    }

    public void scrollerFocusY(float scrollerY) {
        this.focusView.scrollerFocusY(scrollerY);
    }

    public void hideFocusForStartMove(long delayMillis) {
        this.focusView.hideFocus();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                FocusUtils.this.focusView.showFocus();
            }
        }, delayMillis);
    }

    public void hideFocus() {
        this.focusView.hideFocus();
    }

    public void showFocus() {
        this.focusView.showFocus();
    }

    public void changeBitmapForTopView() {
        this.focusView.setBitmapForTop();
    }

    public void clearBitMapForTopView() {
        this.focusView.clearBitmapForTop();
    }

    public void setFocusBitmap(int resId) {
        this.focusView.setFocusBitmap(resId);
    }

    public void setMoveVelocity(int movingNumberDefault, int movingVelocityDefault) {
        this.focusView.setMoveVelocity(movingNumberDefault, movingVelocityDefault);
    }

    public void setMoveVelocityTemporary(int movingNumberTemporary, int movingVelocityTemporary) {
        this.focusView.setMoveVelocityTemporary(movingNumberTemporary, movingVelocityTemporary);
    }

    public void setOnFocusMoveEndListener(BaseFocusView.OnFocusMoveEndListener onFocusMoveEndListener,
                                          View changeFocusView) {
        this.focusView.setOnFocusMoveEndListener(onFocusMoveEndListener, changeFocusView);
    }

    public View getFocusView() {
        return this.focusView;
    }

}
