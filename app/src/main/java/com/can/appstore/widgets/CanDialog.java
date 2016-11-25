package com.can.appstore.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.utils.BitmapUtils;
import cn.can.tvlib.utils.FastBlurUtil;

/**
 * Created by Atangs on 2016/10/30.
 * 暂时适配 安装框 / 卸载框 /更新设置框
 */

public class CanDialog extends Dialog implements View.OnFocusChangeListener {
    private Activity mContext;
    private TextView mDialogTitle;
    private TextView mDialogMsg;
    private TextView mDialogStateMsg;
    private TextView mDialogContentMsg;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private ImageView mDialogIcon;
    private RelativeLayout mRlContent;

    private OnClickListener mOnClickListener;
    private FocusMoveUtil mFocusMoveUtil;
    private View mCurrentView;
    private Handler mHandler = new Handler();

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mCurrentView = v;
            mHandler.removeCallbacks(mfocusMoveRunnable);
            mHandler.postDelayed(mfocusMoveRunnable, 50);
        }
    }

    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentView != null && mCurrentView.isFocused()) {
                mFocusMoveUtil.startMoveFocus(mCurrentView, 1.0f);
            }
        }
    };

    public static abstract class OnClickListener {
        public abstract void onClickPositive();

        public void onClickNegative() {
        }

        ;
    }

    public CanDialog(Activity context) {
        this(context, R.style.CanDialog);
    }

    public CanDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        mFocusMoveUtil = new FocusMoveUtil(mContext, getWindow().getDecorView(), R.mipmap.btn_focus);
        mFocusMoveUtil.hideFocusForShowDelay(500);
        initUI();
    }

    private void initUI() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.can_dialog, null);
        setContentView(dialogView);

        mDialogTitle = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        mDialogIcon = (ImageView) dialogView.findViewById(R.id.iv_dialog_icon);
        mDialogMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_message);
        mDialogStateMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_state_message);
        mDialogContentMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_content_message);
        mPositiveBtn = (Button) dialogView.findViewById(R.id.btn_dialog_positive);
        mNegativeBtn = (Button) dialogView.findViewById(R.id.btn_dialog_negative);
        mRlContent = (RelativeLayout) dialogView.findViewById(R.id.rl_content);
        Drawable drawable = BitmapUtils.blurBitmap(mContext);
        dialogView.setBackground(drawable);
    }

    public CanDialog setTitle(String title) {
        this.mDialogTitle.setVisibility(View.VISIBLE);
        this.mDialogTitle.setText(title);
        return this;
    }

    public CanDialog setTitleMessage(String titleMessage) {
        this.mDialogMsg.setVisibility(View.VISIBLE);
        this.mDialogMsg.setText(titleMessage);
        return this;
    }

    public CanDialog setStateMessage(String stateMessage) {
        this.mDialogStateMsg.setVisibility(View.VISIBLE);
        this.mDialogStateMsg.setText(stateMessage);
        return this;
    }

    public CanDialog setContentMessage(String contentMessage) {
        this.mDialogContentMsg.setVisibility(View.VISIBLE);
        this.mDialogContentMsg.setText(contentMessage);
        return this;
    }

    /**
     * 只有一个按钮时，请使用PositiveButton
     *
     * @param positiveStr
     * @return
     */
    public CanDialog setPositiveButton(String positiveStr) {
        this.mPositiveBtn.setVisibility(View.VISIBLE);
        this.mPositiveBtn.setText(positiveStr);
        this.mPositiveBtn.setFocusable(true);
        this.mPositiveBtn.setOnFocusChangeListener(this);
        this.mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClickPositive();
            }
        });
        return this;
    }

    public CanDialog setNegativeButton(String negativeStr) {
        this.mNegativeBtn.setVisibility(View.VISIBLE);
        this.mNegativeBtn.setText(negativeStr);
        this.mNegativeBtn.setOnFocusChangeListener(this);
        this.mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClickNegative();
            }
        });
        return this;
    }

    public CanDialog setIcon(int iconResId) {
        this.mDialogIcon.setVisibility(View.VISIBLE);
        this.mDialogIcon.setImageResource(iconResId);
        return this;
    }

    public CanDialog setIcon(String iconUrl) {
        this.mDialogIcon.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().load(mContext, this.mDialogIcon, iconUrl);
        return this;
    }

    public CanDialog setIcon(Drawable icon) {
        this.mDialogIcon.setVisibility(View.VISIBLE);
        this.mDialogIcon.setImageDrawable(icon);
        return this;
    }

    /**
     * 是否显示内容区背景图（默认显示）
     *
     * @param visible
     * @return
     */
    public CanDialog setRlCOntent(boolean visible) {
        this.mRlContent.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public CanDialog setOnCanBtnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
        }
    }
}
