package com.can.appstore.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;

/**
 * Created by Atangs on 2016/10/30.
 * 暂时适配 安装框 / 卸载框 /更新设置框
 */

public class CanDialog extends Dialog implements View.OnFocusChangeListener {
    private TextView mDialogTitle;
    private TextView mDialogMsg;
    private TextView mDialogStateMsg;
    private TextView mDialogContentMsg;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private ImageView mDialogIcon;
    private RelativeLayout mMessageLayout;

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
        };
    }

    public CanDialog(Activity context) {
        this(context, R.style.CanDialog);
    }

    public CanDialog(Activity context, int themeResId) {
        super(context, themeResId);
        mFocusMoveUtil = new FocusMoveUtil(context, getWindow().getDecorView(), R.mipmap.btn_focus);
        mFocusMoveUtil.hideFocusForShowDelay(500);
        initUI();
    }

    private void initUI() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.can_dialog, null);
        setContentView(dialogView);

        mDialogTitle = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        mDialogIcon = (ImageView) dialogView.findViewById(R.id.iv_dialog_icon);
        mDialogMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_message);
        mDialogStateMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_state_message);
        mDialogContentMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_content_message);
        mPositiveBtn = (Button) dialogView.findViewById(R.id.btn_dialog_positive);
        mNegativeBtn = (Button) dialogView.findViewById(R.id.btn_dialog_negative);
        mMessageLayout = (RelativeLayout) dialogView.findViewById(R.id.rl_content);
//        Drawable drawable = BitmapUtils.blurBitmap(mContext);
//        dialogView.setBackground(drawable);
        dialogView.setBackgroundResource(R.color.black_opa90);
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
        ImageLoader.getInstance().load(getContext(), this.mDialogIcon, iconUrl);
        return this;
    }

    public CanDialog setIcon(Drawable icon) {
        this.mDialogIcon.setVisibility(View.VISIBLE);
        this.mDialogIcon.setImageDrawable(icon);
        return this;
    }

    /**
     * 修改内容区域背景颜色
     * （默认为黑色）
     */
    public CanDialog setMessageBackground(int resId) {
        this.mMessageLayout.setBackgroundResource(resId);
        return this;
    }

    public CanDialog setTitleToBottom(String contentMSg, int textSize) {
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        this.mDialogTitle.setTextSize(getContext().getResources().getDimensionPixelSize(textSize));
        this.mDialogTitle.setGravity(Gravity.BOTTOM);
        this.mDialogTitle.setVisibility(View.VISIBLE);
        this.mDialogTitle.setText(contentMSg);
        this.mDialogTitle.setLayoutParams(titleParams);
        return this;
    }

    public void setOnCanBtnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void release(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
            mFocusMoveUtil = null;
        }
    }
}
