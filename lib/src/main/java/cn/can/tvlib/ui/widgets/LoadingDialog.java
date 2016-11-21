package cn.can.tvlib.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import cn.can.tvlib.R;
import cn.can.tvlib.ui.view.LoadingTipsView;
import cn.can.tvlib.ui.view.RotateView;

/**
 * Created by zhangbingyuan on 2016/8/26.
 */

public class LoadingDialog extends Dialog {
    //常量
    public static final int NORMAL_DIALOG = 0x100;
    public static final int OFFSET_X_DIALOG = 0x101;
    //控件
    private RotateView mLoadingView;
    private TextView mMsgView;

    public LoadingDialog(Context context) {
        this(context, -2, NORMAL_DIALOG, -1);
    }

    public LoadingDialog(Context context, int loadingSize) {
        this(context, loadingSize, NORMAL_DIALOG, -1);
    }

    public LoadingDialog(Context context, int loadingSize, int offsetX) {
        this(context, loadingSize, OFFSET_X_DIALOG, offsetX);
    }

    public LoadingDialog(Context context, int loadingSize, int type, int offsetX) {
        super(context, R.style.common_dialog_fade_in_out);

        LoadingTipsView loadingTipsView = new LoadingTipsView(context);
        loadingTipsView.addLoadingView(loadingSize, loadingSize);
        loadingTipsView.setMessage();
        mLoadingView = loadingTipsView.getLoadingView();
        mMsgView = loadingTipsView.getMessageView();

        setContentView(loadingTipsView);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        if (type == NORMAL_DIALOG) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else if (type == OFFSET_X_DIALOG) {
            Log.d("", "LoadingDialog: "+offsetX);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.x = offsetX;
        }
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void show() {
        mLoadingView.startRotate();
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mLoadingView.stopRotate();
    }

    @Override
    public void hide() {
        super.hide();
        mLoadingView.stopRotate();
    }

    public LoadingDialog setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            mMsgView.setText(message);
        }
        return this;
    }
}
