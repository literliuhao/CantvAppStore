package cn.can.tvlib.ui.widgets;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by Atangs on 2016/10/28.
 */

public class CanBaseDialog extends Dialog {

    public interface OnBtnClickListener {
        public void onClickPositive();

        public void onClickNegative();
    }

    public CanBaseDialog(Context context) {
        super(context);
    }

    protected CanBaseDialog(Context context, String title, String positiveBtnStr, String nagetiveBtnStr,
                            OnBtnClickListener onBtnClickListener) {
        super(context);
    }
}
