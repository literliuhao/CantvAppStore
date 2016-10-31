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


/**
 * 安装包管理界面
 * Created by shenpx on 2016/10/12 0012.
 */

public class InstallManagerActivity extends Activity {

    private RecyclerView mRecyclerView;
    private List<AppInfoBean> mDatas;//安装包集合
    private InstallManagerAdapter mRecyclerAdapter;
    private TextView mReminder;
    private Button mDeleteButton;
    private Button mDeleteAllButton;
    private boolean isVisibility = false;
    private int mCurrentPositon;
    private RelativeLayout deleteLayout;
    private TextView mRoomSize;
    private String path;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private ProgressBar mProgressBar;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private int mUsedSdSize;
    private String mSdAvaliableSize;
    private BroadcastReceiver mInstallApkReceiver;
    private IntentFilter intentFilter;
    private List<AppInfoBean> mInstallDatas;//安装中集合
    private List<AppInfoBean> mInstalledDatas;//安装完成集合
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installmanager);
        mDatas = new ArrayList<AppInfoBean>();
        mInstallDatas = new ArrayList<AppInfoBean>();

        initView();
        initData();
        initEvent();

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
                        /*for (int i = mInstallDatas.size() - 1; i >= 0; i--) {
                            AppInfoBean bean = mInstallDatas.get(i);
                            if (bean.getPackageName().equals(packageName)) {
                                bean.setInstall(true);
                            }
                        }*/
                        //刷新图标（可能多重版本）通过广播获取安装完成刷新ui  +&& bean.getVersionCode().equals(String.valueOf(versonCode))
                        int versonCode = UpdateUtils.getVersonCode(MyApp.mContext, packageName);
                        for (int i = mDatas.size() - 1; i >= 0; i--) {
                            AppInfoBean bean = mDatas.get(i);
                            if (bean.getPackageName().equals(packageName)) {
                                if (bean.getInstall()) {
                                    //bean.setInstall(true);
                                    mRecyclerAdapter.notifyItemChanged(i);
                                    Toast.makeText(MyApp.mContext, packageName + "111111", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
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
    }

    private void initEvent() {
        mRecyclerAdapter.setOnItemClickLitener(new InstallManagerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, final int position) {
                Toast.makeText(InstallManagerActivity.this, position + 1 + "/" + mDatas.size(),
                        Toast.LENGTH_SHORT).show();
                mCurrentPositon = position;
                showMenu(view, position);
//                mCurrentnum.setText(position+1);
//                mTotalnum.setText(mDatas.size());
            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

    }

    protected void initData() {
        mSdTotalSize = UpdateUtils.getSDTotalSize();
        mSdSurplusSize = UpdateUtils.getSDSurplusSize();
        mSdAvaliableSize = UpdateUtils.getSDAvaliableSize();
//        mUsedSdSize = mSdTotalSize - mSdSurplusSize;
        mProgressBar.setMax(mSdTotalSize);
        mProgressBar.setProgress(mSdSurplusSize);
        mRoomSize.setText(getString(R.string.install_sdavaliable_size) + mSdAvaliableSize);
        InstallPkgUtils.myFiles.clear();
        path = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "Movies";
        List appList = InstallPkgUtils.FindAllAPKFile(path);
        mDatas.clear();
        if (appList.size() < 1) {
            mReminder.setVisibility(View.INVISIBLE);
        } else {
            //closeDialog();
            mDatas.addAll(appList);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        mTotalnum = (TextView) findViewById(R.id.tv_install_totalnum);
        mCurrentnum = (TextView) findViewById(R.id.tv_install_currentnum);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_install_recyclerview);
        mReminder = (TextView) findViewById(R.id.tv_install_reminder);
        mDeleteAllButton = (Button) findViewById(R.id.bt_install_deleteall);
        mDeleteButton = (Button) findViewById(R.id.bt_install_delete);
        mRoomSize = (TextView) findViewById(R.id.tv_install_roomsize);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_install_progressbar);
        mRecyclerAdapter = new InstallManagerAdapter(this, mDatas);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setSelected(true);

        mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllData();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectData();
            }
        });

    }

    /**
     * 删除部分
     */
    private void removeSelectData() {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getInstall()) {
                mDatas.remove(i);
//                InstallPkgUtils.deleteApkPkg(mDatas.get(i).getFliePath());//可以删除安装包
                mRecyclerAdapter.notifyItemRemoved(i);//自带动画
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
     * 删除item
     *
     * @param position
     */
    public void removeData(int position) {
        mDatas.remove(position);
//        InstallPkgUtils.deleteApkPkg(mDatas.get(position).getFliePath());//可以删除安装包
        mRecyclerAdapter.notifyItemRemoved(position);//自带动画
        //mRecyclerAdapter.notifyDataSetChanged();无自带动画
    }

    /**
     * 删除全部
     *
     * @param
     */
    public void removeAllData() {
        mDatas.clear();
        mRecyclerAdapter.notifyDataSetChanged();
        mReminder.setVisibility(View.VISIBLE);
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
        Button start = (Button) view.findViewById(R.id.bt_installpkg_start);
        Button delete = (Button) view.findViewById(R.id.bt_installpkg_delete);
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
                removeData(position);
                isVisibility = false;
                view.requestFocus();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLayout.setVisibility(View.INVISIBLE);
                //开始安装应用，安装键
                mInstalling.setVisibility(View.VISIBLE);
                mDatas.get(position).setInstalling(true);//开始安装
                //mInstallDatas.add(mDatas.get(position));//加入安装中集合
                mDatas.get(position).setInstall(true);//positon传递
                isVisibility = false;
                InstallPkgUtils.installApkFromF(MyApp.mContext,
                        new File(mDatas.get(position).getFliePath()), true, mDatas.get(position).getPackageName());
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
     * 显示Dialog
     */
    private void showDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.createLoadingDialog(this, getString(R.string.install_search_updateinfo));
            mLoadingDialog.show();
        }
    }

    /**
     * 关闭Dialog
     */
    private void closeDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
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
