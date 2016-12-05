package com.can.appstore.installpkg;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.model.UpdateApkModel;
import com.can.appstore.update.utils.UpdateUtils;
import com.can.appstore.widgets.CanDialog;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.ToastUtils;


/**
 * 安装包管理界面
 * Created by shenpx on 2016/10/12 0012.
 */

public class InstallManagerActivity extends BaseActivity implements InstallContract.View {

    private CanRecyclerView mRecyclerView;
    private InstallManagerAdapter mRecyclerAdapter;
    private TextView mReminder;
    private TextView mDeleteButton;
    private TextView mDeleteAllButton;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private TextProgressBar mProgressBar;
    private BroadcastReceiver mInstallApkReceiver;
    private IntentFilter intentFilter;
    FocusMoveUtil mFocusMoveUtil;
    FocusScaleUtil mFocusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private InstallPresenter mPresenter;
    private CanDialog canDialog;
    private GridLayoutManager mGridLayoutManager;
    private static final String TAG = "installManagerActivity";
    private int mWinH;
    private int mWinW;
    private long mLastClickTime;
    private long moveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installmanager);
        mPresenter = new InstallPresenter(this, InstallManagerActivity.this);
        //获取到屏幕的宽高
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWinH = outMetrics.heightPixels;
        mWinW = outMetrics.widthPixels;
        initView();
        initData();
        initFocusChange();
        initClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerInstalledReceiver();
        registerDownloadedReceiver();

    }

    /**
     * 下载完成广播，刷新数据
     */
    private void registerDownloadedReceiver() {

    }

    /**
     * 安装完成广播，刷新数据,失败广播获取包名，install为true再刷新 再加集合存储安装成功
     */
    private void registerInstalledReceiver() {
        if (mInstallApkReceiver == null) {
            mInstallApkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED") || intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
                        String packageName = intent.getDataString().substring(8);
                        int versonCode = UpdateUtils.getVersonCode(InstallManagerActivity.this, packageName);
                        mPresenter.isInstalled(packageName, versonCode);
                    } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                    }
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_VIEW);
            intentFilter.addDataScheme("package");
        }
        registerReceiver(mInstallApkReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver();
//        initData();
        DCPage.onEntry(AppConstants.PACKAGE_MANAGE);
        DCEvent.onEvent(AppConstants.PACKAGE_MANAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.PACKAGE_MANAGE);
        DCEvent.onEventDuration(AppConstants.PACKAGE_MANAGE, mDuration);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mInstallApkReceiver);
        mPresenter.release();
    }

    private void initFocusChange() {

        mDeleteAllButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mFocusMoveUtil.startMoveFocus(mDeleteAllButton, 1.0f);
                    mPresenter.setNum(0);
                    if (mPresenter.isNull()) {
                        mDeleteAllButton.setNextFocusRightId(R.id.bt_install_deleteall);
                    }
                } else {
                    mFocusScaleUtil.scaleToNormal(mDeleteAllButton);
                }
            }
        });

        mDeleteButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mFocusMoveUtil.startMoveFocus(mDeleteButton, 1.0f);
                    mPresenter.setNum(0);
                    if (mPresenter.isNull()) {
                        mDeleteButton.setNextFocusRightId(R.id.bt_install_delete);
                    }
                } else {
                    mFocusScaleUtil.scaleToNormal(mDeleteButton);
                }
            }
        });

       /* mUpdateButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mFocusMoveUtil.startMoveFocus(mUpdateButton, 1.0f);
                } else {
                    mFocusScaleUtil.scaleToNormal(mUpdateButton);
                }
            }
        });*/

        mRecyclerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mRecyclerView.postDelayed(myFocusRunnable, 80);
                    mPresenter.setNum(position);

                } else {
                    mFocusScaleUtil.scaleToNormal();
                }
                view.setSelected(hasFocus);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == CanRecyclerView.SCROLL_STATE_SETTLING) {
                    //限制移动区域
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int[] posi = new int[2];
                            mRecyclerView.getLocationInWindow(posi);
                            mFocusMoveUtil.setFocusActiveRegion(posi[0], posi[1] + mRecyclerView.getPaddingTop(),
                                    posi[0] + mRecyclerView.getWidth(),
                                    posi[1] + mRecyclerView.getHeight() - mRecyclerView.getPaddingBottom() - getResources().getDimensionPixelSize(R.dimen.px40));
                        }
                    });
                    setLeftLayoutFocus(false);
                } else if (newState == CanRecyclerView.SCROLL_STATE_IDLE) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mFocusMoveUtil.setFocusActiveRegion(0, 0, mWinW, mWinH);
                        }
                    });
                    setLeftLayoutFocus(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });

    }

    /**
     * 设置左侧布局焦点
     *
     * @param focusable
     */
    private void setLeftLayoutFocus(boolean focusable) {
        mDeleteButton.setFocusable(focusable);
        mDeleteAllButton.setFocusable(focusable);
        //mUpdateButton.setFocusable(focusable);
    }

    private void initClick() {

        mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteAll();
                if (mPresenter.isNull()) {
                    mDeleteAllButton.setFocusable(true);
                    mDeleteAllButton.requestFocus();
                }
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteInstall();
                if (mPresenter.isNull()) {
                    mDeleteAllButton.setFocusable(true);
                    mDeleteAllButton.requestFocus();
                }
            }
        });

        mRecyclerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                initDialog(view, position);
            }
        });
    }

    protected void initData() {
        mPresenter.getSDInfo();
        mPresenter.getInstallPkgList();
        mPresenter.setNum(0);
    }

    private void initView() {
        mTotalnum = (TextView) findViewById(R.id.tv_install_totalnum);
        mCurrentnum = (TextView) findViewById(R.id.tv_install_currentnum);
        mRecyclerView = (CanRecyclerView) findViewById(R.id.rv_install_recyclerview);
        mReminder = (TextView) findViewById(R.id.tv_install_reminder);
        mDeleteAllButton = (TextView) findViewById(R.id.bt_install_deleteall);
        mDeleteButton = (TextView) findViewById(R.id.bt_install_delete);
        //mUpdateButton = (Button) findViewById(R.id.bt_install_update);
        mProgressBar = (TextProgressBar) findViewById(R.id.pb_install_progressbar);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        CanRecyclerViewDivider canRecyclerViewDivider = new CanRecyclerViewDivider(0, getResources().getDimensionPixelSize(R.dimen.px38), 0);
        mRecyclerView.addItemDecoration(canRecyclerViewDivider);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDeleteAllButton.post(new Runnable() {
            @Override
            public void run() {
                mDeleteAllButton.setFocusable(true);
                mDeleteAllButton.requestFocus();
            }
        });

    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showNoData() {
        mReminder.setVisibility(View.VISIBLE);
        mCurrentnum.setVisibility(View.INVISIBLE);
        mTotalnum.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideNoData() {
        mReminder.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSDProgressbar(int currentsize, String sdinfo) {
        mProgressBar.setProgress(currentsize);
        mProgressBar.setTextSize(getResources().getDimensionPixelOffset(R.dimen.px18));
        mProgressBar.setText(sdinfo);
    }

    @Override
    public void refreshItem(int position) {
        mRecyclerAdapter.notifyItemChanged(position);
    }

    @Override
    public void removeItem(int position) {
        mRecyclerAdapter.notifyItemRemoved(position);//自带动画
    }

    @Override
    public void refreshAll() {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCurrentNum(int current, int total) {
        mCurrentnum.setText(current + "");
        mTotalnum.setText("/" + total + "行");
    }

    @Override
    public void showInstallPkgList(List<AppInfoBean> mDatas) {
        if (mRecyclerAdapter == null) {
            mRecyclerAdapter = new InstallManagerAdapter(mDatas);
        }
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteLastItem(final int position) {
        if (position <= 0) {
            return;
        }
        mFocusMoveUtil.hideFocusForShowDelay(500);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mRecyclerView.getChildAt(position - 1);
                if (childAt != null) {
                    mDeleteAllButton.setFocusable(false);
                    childAt.setFocusable(true);
                    childAt.requestFocus();
                } else {
                    mDeleteAllButton.setFocusable(true);
                    mDeleteAllButton.requestFocus();
                }
            }
        }, 500);
    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
            }
        }
    }

    /**
     * 添加item
     *
     * @param
     */
    public void addData() {
        initData();
        mRecyclerAdapter.notifyDataSetChanged();
        mReminder.setVisibility(View.INVISIBLE);
    }

    /**
     * 按键事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 打开更新管理界面
     *
     * @param
     */
    /*public void intoUpdate(View v) {
        startActivity(new Intent(this, UpdateManagerActivity.class));
    }*/
    private void initDialog(final View view, final int position) {
        canDialog = new CanDialog(InstallManagerActivity.this);
        AppInfoBean bean = mPresenter.getItem(position);
        if (bean != null && bean.getIsInstalling() && !bean.getInstalledFalse()) {
            showToast(getResources().getString(R.string.install_dialog_installing));
            return;
        }
        if (bean != null) {
            canDialog.setTitle(bean.getAppName()).setIcon(bean.getIcon()).setRlCOntent(false).setNegativeButton(getResources().getString(R.string.install_dialog_delete)).setPositiveButton(getResources().getString(R.string.install_dialog_install));
            canDialog.setOnCanBtnClickListener(new CanDialog.OnClickListener() {
                @Override
                public void onClickPositive() {
                    final TextView mInstalling = (TextView) view.findViewById(R.id.tv_install_installing);
                    mInstalling.setText(getResources().getString(R.string.install_installing));
                    mInstalling.setVisibility(View.VISIBLE);
                    //mPresenter.installApk(position);
                    canDialog.dismiss();
                    int versonCode = mPresenter.getVersonCode(InstallManagerActivity.this, position);
                    if (versonCode == 0) {
                        showToast(getResources().getString(R.string.install_dialog_error));
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.installApp(position);
                            }
                        }, 1000);
                    }
                }

                @Override
                public void onClickNegative() {
                    //删除键
                    if (mPresenter.isLastItem(position)) {
                        mFocusMoveUtil.hideFocus();
                        setLeftLayoutFocus(false);
                        View childAt = mRecyclerView.getChildAt(position - 1);
                        childAt.requestFocus();
                        childAt.setFocusable(true);
                        mFocusMoveUtil.showFocus(100);
                        setLeftLayoutFocus(true);
                    }
                    mPresenter.deleteOne(position);
                    canDialog.dismiss();
                }
            });
            canDialog.show();
        }
    }

    /**
     * 启动安装包管理
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, InstallManagerActivity.class);
        context.startActivity(intent);
    }

    /**
     * 限制点击频率
     * @return
     */
    private boolean isFastContinueClickView() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime < 1500) {
            return true;
        }
        mLastClickTime = curClickTime;
        return false;
    }

    /**
     * 限制移动速度
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            //控制按键响应的速度
            case KeyEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - moveTime > 200) {
                    moveTime = System.currentTimeMillis();
                } else {
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);

    }

    @Override
    protected void onHomeKeyDown() {
        mPresenter.release();
        finish();
    }
}
