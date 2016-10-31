package com.can.appstore.wights;

import android.app.Dialog;
import android.content.Context;
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

        addListeneForBtn();
    }

    /**
     * 安装框
     * @param titleImgId  应用图标
     * @param titleStr    标题（应用名称）
     * @param positiveStr 按钮一
     * @param negativeStr 按钮二
     * @param onCanBtnClickListener  按钮点击事件
     */
    public void showDialogForInstallAPP(int titleImgId, String titleStr, String positiveStr, String negativeStr,
                                        OnCanBtnClickListener onCanBtnClickListener) {
        mIvDialogTitle.setImageResource(titleImgId);
        setViewConent(titleImgId, titleStr, "", "", "", positiveStr, negativeStr, onCanBtnClickListener);
    }

    /**
     *应用卸载框
     * @param titleImgId  应用图标
     * @param titleStr  标题（应用名称）
     * @param contentStr  提示内容
     * @param positiveStr 按钮一
     * @param negativeStr 按钮二
     * @param onCanBtnClickListener  按钮点击事件
     */
    public void showDialogForUninstallAPP(int titleImgId, String titleStr, String contentStr, String positiveStr, String
            negativeStr, OnCanBtnClickListener onCanBtnClickListener) {
        setViewConent(titleImgId, titleStr, contentStr, "", "", positiveStr, negativeStr, onCanBtnClickListener);
    }

    /**
     * 针对更新设置特定的对话框
     * @param titleStr   标题
     * @param leftContentStr  左上内容
     * @param rightContentStr 右上内容
     * @param belowContentStr 左下内容
     * @param positiveStr 按钮一
     * @param negativeStr 按钮二
     * @param onCanBtnClickListener 按钮点击事件
     */
    public void showDialogForUpdateSetting(String titleStr, String leftContentStr, String rightContentStr, String
            belowContentStr, String positiveStr, String negativeStr, OnCanBtnClickListener onCanBtnClickListener) {
        setViewConent(-1, titleStr, leftContentStr, rightContentStr, belowContentStr, positiveStr, negativeStr, onCanBtnClickListener);
    }

    private void setViewConent(int titleImgId, String titleStr, String leftContentStr, String rightContentStr,
                              String belowContentStr, String positiveStr, String negativeStr,
                              OnCanBtnClickListener onCanBtnClickListener) {
        if (titleImgId == -1) {
            mIvDialogTitle.setVisibility(View.GONE);
        } else {
            mIvDialogTitle.setImageResource(titleImgId);
        }
        if (TextUtils.isEmpty(leftContentStr)) {
            mTvDialogTopLeftContent.setVisibility(View.GONE);
        } else {
            mTvDialogTopLeftContent.setText(leftContentStr);
        }
        if (TextUtils.isEmpty(rightContentStr)) {
            mTvDialogTopRightContent.setVisibility(View.GONE);
        } else {
            mTvDialogTopRightContent.setText(rightContentStr);
        }
        if (TextUtils.isEmpty(belowContentStr)) {
            mTvDialogBelowContent.setVisibility(View.GONE);
        } else {
            mTvDialogBelowContent.setText(belowContentStr);
        }
        mTvDialogTitle.setText(titleStr);
        mBtnDialogPositive.setText(positiveStr);
        mBtnDialogNegative.setText(negativeStr);
        this.mOnCanBtnClickListener = onCanBtnClickListener;
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

}
