package com.can.appstore.installpkg;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.utils.InstallPkgUtils;
import com.can.appstore.installpkg.view.LoadingDialog;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.UpdateManagerActivity;
import com.can.appstore.update.utils.UpdateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;


/**
 * 安装包管理界面
 * Created by shenpx on 2016/10/12 0012.
 */

public class InstallManagerActivity extends Activity implements InstallContract.View {

    private CanRecyclerView mRecyclerView;
    private InstallManagerAdapter mRecyclerAdapter;
    private TextView mReminder;
    private Button mDeleteButton;
    private Button mDeleteAllButton;
    private Button mUpdateButton;
    private boolean isVisibility = false;
    private int mCurrentPositon;
    private RelativeLayout deleteLayout;
    private TextView mRoomSize;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private ProgressBar mProgressBar;
    private BroadcastReceiver mInstallApkReceiver;
    private IntentFilter intentFilter;
    private List<AppInfoBean> mInstallDatas;//安装中集合
    private List<AppInfoBean> mInstalledDatas;//安装完成集合
    private Dialog mLoadingDialog;
    FocusMoveUtil mFocusMoveUtil;
    FocusScaleUtil mFocusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private String mCurPackageName = "";//当前包名
    private String mCurVersionCode = "";//当前版本号
    private InstallPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installmanager);
        mInstallDatas = new ArrayList<AppInfoBean>();
        mPresenter = new InstallPresenter(this,InstallManagerActivity.this);
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
                        //刷新图标（可能多重版本）通过广播获取安装完成刷新ui  +&& bean.getVersionCode().equals(String.valueOf(versonCode))
                        int versonCode = UpdateUtils.getVersonCode(MyApp.mContext, packageName);
                        mPresenter.isInstalled(packageName,versonCode);
                        Toast.makeText(MyApp.mContext, packageName + "安装成功", Toast.LENGTH_LONG).show();
                    } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                        Toast.makeText(MyApp.mContext, "安装失败", Toast.LENGTH_LONG).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                    mFocusMoveUtil.startMoveFocus(mDeleteAllButton, 1.0f);
                    //mFocusScaleUtil.scaleToLarge(mDeleteAllButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mDeleteAllButton);
                }
            }
        });

        mDeleteButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mDeleteButton, 1.0f);
                    //mFocusScaleUtil.scaleToLarge(mDeleteButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mDeleteButton);
                }
            }
        });

        mUpdateButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mUpdateButton, 1.0f);
                    //mFocusScaleUtil.scaleToLarge(mUpdateButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mUpdateButton);
                }
            }
        });

        mRecyclerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mRecyclerView.postDelayed(myFocusRunnable, 50);
                    mPresenter.setNum(position);
                    mCurrentPositon = position;

                } else {
                    mFocusScaleUtil.scaleToNormal();
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });

    }

    private void initClick() {

        mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteAll();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteInstall();
            }
        });

        mRecyclerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                /*Toast.makeText(InstallManagerActivity.this, position + 1 + "/" + mDatas.size(),
                        Toast.LENGTH_SHORT).show();*/
                //mCurrentPositon = position;
                showMenu(view, position);
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
        mDeleteAllButton = (Button) findViewById(R.id.bt_install_deleteall);
        mDeleteButton = (Button) findViewById(R.id.bt_install_delete);
        mUpdateButton = (Button) findViewById(R.id.bt_install_update);
        mRoomSize = (TextView) findViewById(R.id.tv_install_roomsize);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_install_progressbar);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setSelected(true);

    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.createLoadingDialog(this, getString(R.string.install_search_updateinfo));
            mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void showNoData() {
        mReminder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoData() {
        mReminder.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSDProgressbar(int currentsize, int total, String sdinfo) {
        mProgressBar.setMax(total);
        mProgressBar.setProgress(currentsize);
        mRoomSize.setText(getString(R.string.install_sdavaliable_size) + sdinfo);
    }

    @Override
    public void refreshItem(int position) {
        mRecyclerAdapter.notifyItemRemoved(position);//自带动画
    }

    @Override
    public void refreshAll() {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCurrentNum(int current, int total) {
        mCurrentnum.setText(current+"");
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

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
                //mFocusScaleUtil.scaleToLarge(mFocusedListChild);
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
     * 显示菜单键布局
     *
     * @param view
     * @param position
     */
    private void showMenu(final View view, final int position) {
        deleteLayout = (RelativeLayout) view.findViewById(R.id.rl_installpkg_delete);
        final TextView mInstalling = (TextView) view.findViewById(R.id.tv_install_installing);
        if (deleteLayout != null) {
            if (deleteLayout.getVisibility() != View.VISIBLE) {
                isVisibility = true;
                deleteLayout.setVisibility(View.VISIBLE);
            } else if (deleteLayout.getVisibility() == View.VISIBLE) {
                isVisibility = false;
                deleteLayout.setVisibility(View.INVISIBLE);
                return;
            }

        }
        final Button start = (Button) view.findViewById(R.id.bt_installpkg_start);
        final Button delete = (Button) view.findViewById(R.id.bt_installpkg_delete);
        start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(start, 1.1f);
                    mFocusScaleUtil.scaleToLarge(start);
                } else {
                    mFocusScaleUtil.scaleToNormal(start);
                }
            }
        });
        delete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(delete, 1.1f);
                    mFocusScaleUtil.scaleToLarge(delete);
                } else {
                    mFocusScaleUtil.scaleToNormal(delete);
                }
            }
        });
        start.requestFocus();
        start.setNextFocusDownId(R.id.bt_installpkg_delete);
        start.setNextFocusUpId(R.id.bt_installpkg_start);
        start.setNextFocusLeftId(R.id.bt_installpkg_start);
        start.setNextFocusRightId(R.id.bt_installpkg_start);
        delete.setNextFocusUpId(R.id.bt_installpkg_start);
        delete.setNextFocusDownId(R.id.bt_installpkg_delete);
        delete.setNextFocusRightId(R.id.bt_installpkg_delete);
        delete.setNextFocusLeftId(R.id.bt_installpkg_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLayout.setVisibility(View.INVISIBLE);
                //删除键
                mPresenter.deleteOne(mCurrentPositon);
                isVisibility = false;
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLayout.setVisibility(View.INVISIBLE);
                //开始安装应用，安装键
                mInstalling.setVisibility(View.VISIBLE);
                isVisibility = false;
                mPresenter.installApk(position);
            }
        });

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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isVisibility) {
                deleteLayout.setVisibility(View.INVISIBLE);
                isVisibility = false;
                //mRecyclerView.requestFocus();
                return true;
            } else {
                InstallManagerActivity.this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 打开更新管理界面
     *
     * @param v
     */
    public void intoUpdate(View v) {
        startActivity(new Intent(this, UpdateManagerActivity.class));
    }

}
