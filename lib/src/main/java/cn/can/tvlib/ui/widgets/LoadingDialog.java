package cn.can.tvlib.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import cn.can.tvlib.R;
import cn.can.tvlib.ui.view.LoadingTipsView;
import cn.can.tvlib.ui.view.RotateView;

/**
 * Created by zhangbingyuan on 2016/8/26.
 */

public class LoadingDialog extends Dialog {

    private RotateView mLoadingView;
    private TextView mMsgView;

    public LoadingDialog(Context context) {
        this(context, -2);
    }

    public LoadingDialog(Context context, int loadingSize) {
        super(context, R.style.common_dialog_fade_in_out);

        LoadingTipsView loadingTipsView = new LoadingTipsView(context);
        loadingTipsView.addLoadingView(loadingSize, loadingSize);
        loadingTipsView.setMessage();
        mLoadingView = loadingTipsView.getLoadingView();
        mMsgView = loadingTipsView.getMessageView();

        setContentView(loadingTipsView);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
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
