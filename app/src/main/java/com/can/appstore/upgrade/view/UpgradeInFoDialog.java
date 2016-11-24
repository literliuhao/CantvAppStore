package com.can.appstore.upgrade.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.upgrade.widgets.UpgradeInfoNoticeCursor;
import com.can.appstore.upgrade.widgets.UpgradeInfoScrollView;

import cn.can.tvlib.utils.ToastUtils;


/**
 * Created by syl on 2016/10/31.
 */

public class UpgradeInFoDialog extends Dialog {
    private Context mContext;
    private TextView mTvUpgradeTitle;
    private TextView mTvUpgradeInfoVersion;
    private TextView mTvUpgradeInfoContent;
    private TextView tv_upgrade_install;
    private UpgradeInfoScrollView mSvUpgradeInfo;
    private UpgradeInfoNoticeCursor mUserNoticeCursor;
    private OnUpgradeClickListener mOnUpgradeClickListener;

    public interface OnUpgradeClickListener {
        void onClick();
    }

    public UpgradeInFoDialog(Context context, String title, String version, String content, String buttonText, OnUpgradeClickListener onUpgradeClickListener) {
        super(context, R.style.upgradeDialogStyle);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        mContext = context;
        mOnUpgradeClickListener = onUpgradeClickListener;
        initView();
        initData(title, version, content, buttonText);
    }

    private void initView() {
        setContentView(R.layout.layout_upgrade_info);

        mSvUpgradeInfo = (UpgradeInfoScrollView) findViewById(R.id.sv_upgrade_info);
        mUserNoticeCursor = (UpgradeInfoNoticeCursor) findViewById(R.id.user_notice_cursor);
        mSvUpgradeInfo.setCursor(mUserNoticeCursor);

        mTvUpgradeTitle = (TextView) findViewById(R.id.tv_upgrade_title);
        mTvUpgradeInfoVersion = (TextView) findViewById(R.id.tv_upgrade_info_version);
        mTvUpgradeInfoContent = (TextView) findViewById(R.id.tv_upgrade_info_content);
        tv_upgrade_install = (TextView) findViewById(R.id.tv_upgrade_install);
        tv_upgrade_install.setBackgroundResource(R.mipmap.btn_focus);

        mSvUpgradeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showMessage(mContext, "安装");
                mOnUpgradeClickListener.onClick();
                dismiss();
            }
        });
    }

    private void initData(String title, String version, String content, String buttonText) {
        mTvUpgradeTitle.setText(title);
        mTvUpgradeInfoVersion.setText("新版本：" + version);
        mTvUpgradeInfoContent.setText(content);
        tv_upgrade_install.setText(buttonText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
