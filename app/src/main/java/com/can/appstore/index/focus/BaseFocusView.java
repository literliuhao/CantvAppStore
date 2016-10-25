package com.can.appstore.index.focus;

import android.view.View;

public interface BaseFocusView {
	void initFocusBitmapRes(int var1);

	void initFocusBitmapRes(int var1, int var2);

	void setBitmapForTop();

	void clearBitmapForTop();

	void setFocusLayout(float var1, float var2, float var3, float var4);

	void focusMove(float var1, float var2, float var3, float var4);

	void scrollerFocusX(float var1);

	void scrollerFocusY(float var1);

	void hideFocus();

	void showFocus();

	void setFocusBitmap(int var1);

	void setMoveVelocity(int var1, int var2);

	void setMoveVelocityTemporary(int var1, int var2);

	void setOnFocusMoveEndListener(BaseFocusView.OnFocusMoveEndListener var1, View var2);

	public interface OnFocusMoveEndListener {
		void focusEnd(View var1);
	}
}
