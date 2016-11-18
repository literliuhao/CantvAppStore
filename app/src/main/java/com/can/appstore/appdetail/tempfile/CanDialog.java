package com.can.appstore.appdetail.tempfile;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;

/**
 * Created by Atangs on 2016/10/30.
 * 暂时适配 安装框 / 卸载框 /更新设置框
 */

public class CanDialog extends Dialog {
    private TextView mTvDialogTitle;
    private TextView mTvDialogTopLeftContent;
    private TextView mTvDialogTopRightContent;
    private TextView mTvDialogBelowContent;
    private Button mBtnDialogPositive;
    private Button mBtnDialogNegative;
    private ImageView mIvDialogTitle;

    private RelativeLayout mContentLayout;

    private OnCanBtnClickListener mOnCanBtnClickListener;

    public interface OnCanBtnClickListener {
        public void onClickPositive();

        public void onClickNegative();
    }

    public CanDialog(Context context) {
        super(context);
        initUI(context);
    }

    private void initUI(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.can_dialog, null);
        setContentView(dialogView);

        mTvDialogTitle = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        mIvDialogTitle = (ImageView) dialogView.findViewById(R.id.iv_dialog_title);
        mTvDialogTopLeftContent = (TextView) dialogView.findViewById(R.id.tv_top_left_content);
        mTvDialogTopRightContent = (TextView) dialogView.findViewById(R.id.tv_top_right_content);
        mTvDialogBelowContent = (TextView) dialogView.findViewById(R.id.tv_bellow_content);
        mBtnDialogPositive = (Button) dialogView.findViewById(R.id.btn_dialog_positive);
        mBtnDialogNegative = (Button) dialogView.findViewById(R.id.btn_dialog_negative);

    }


    private void addListeneForBtn() {
        mBtnDialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnCanBtnClickListener.onClickPositive();
            }
        });
        mBtnDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnCanBtnClickListener.onClickNegative();
            }
        });
    }

    public CanDialog setmTvDialogTitle(String title) {
        this.mTvDialogTitle.setVisibility(View.VISIBLE);
        this.mTvDialogTitle.setText(title);
        return this;
    }

    public CanDialog setmTvDialogTopLeftContent(String topLeftContent) {
        this.mTvDialogTopLeftContent.setVisibility(View.VISIBLE);
        this.mTvDialogTopLeftContent.setText(topLeftContent);
        return this;
    }

    public CanDialog setmTvDialogTopRightContent(String topRightContent) {
        this.mTvDialogTopRightContent.setVisibility(View.VISIBLE);
        this.mTvDialogTopRightContent.setText(topRightContent);
        return this;
    }

    public CanDialog setmTvDialogBelowContent(String belowContent) {
        this.mTvDialogBelowContent.setVisibility(View.VISIBLE);
        this.mTvDialogBelowContent.setText(belowContent);
        return this;
    }

    public CanDialog setmBtnDialogPositive(String positiveStr) {
        this.mBtnDialogPositive.setVisibility(View.VISIBLE);
        this.mBtnDialogPositive.setText(positiveStr);
        this.mBtnDialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCanBtnClickListener.onClickPositive();
            }
        });
        return this;
    }

    public CanDialog setmBtnDialogNegative(String negativeStr) {
        this.mBtnDialogNegative.setVisibility(View.VISIBLE);
        this.mBtnDialogNegative.setText(negativeStr);
        this.mBtnDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCanBtnClickListener.onClickNegative();
            }
        });
        return this;
    }

    public CanDialog setmIvDialogTitle(Drawable drawable) {
        this.mIvDialogTitle.setVisibility(View.VISIBLE);
        this.mIvDialogTitle.setImageDrawable(drawable);
        return this;
    }

    public CanDialog setOnCanBtnClickListener(OnCanBtnClickListener listener) {
        this.mOnCanBtnClickListener = listener;
        return this;
    }

    public void setmContentLayout() {
        this.mContentLayout.setVisibility(View.VISIBLE);
    }
}
