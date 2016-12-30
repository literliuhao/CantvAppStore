package com.can.appstore.index.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.about.AboutUsActivity;
import com.can.appstore.download.DownloadActivity;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.index.adapter.GridAdapter;
import com.can.appstore.index.entity.FragmentEnum;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.model.DataUtils;
import com.can.appstore.installpkg.InstallManagerActivity;
import com.can.appstore.message.manager.MessageDBManager;
import com.can.appstore.uninstallmanager.UninstallManagerActivity;
import com.can.appstore.update.UpdateManagerActivity;
import com.can.appstore.update.model.UpdateApkModel;
import com.can.appstore.widgets.CanDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTaskCountListener;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.utils.PromptUtils;

/**
 * Created by liuhao on 2016/10/21.
 */

public class ManagerFragment extends BaseFragment implements DownloadTaskCountListener {

    private final int[] NAMES = {R.string.index_manager_text1, R.string.index_manager_text2, R.string.index_manager_text3,
            R.string.index_manager_text4, R.string.index_manager_text5, R.string.index_manager_text6,
            R.string.index_manager_text7, R.string.index_manager_text8, R.string.index_manager_text9,
            R.string.index_manager_text10};
    private final int[] ICONS = {R.drawable.index_manager_icon1, R.drawable.index_manager_icon2, R.drawable.index_manager_icon3,
            R.drawable.index_manager_icon4, R.drawable.index_manager_icon5, R.drawable.index_manager_icon6,
            R.drawable.index_manager_icon7, R.drawable.index_manager_icon8, R.drawable.index_manager_icon9,
            R.drawable.index_manager_icon10};
    private final int[] COLORS = {R.drawable.index_item1_shape, R.drawable.index_item2_shape, R.drawable.index_item3_shape,
            R.drawable.index_item4_shape, R.drawable.index_item5_shape, R.drawable.index_item6_shape,
            R.drawable.index_item7_shape, R.drawable.index_item8_shape, R.drawable.index_item9_shape,
            R.drawable.index_item10_shape};
    private GridView gridView;
    private GridAdapter gridAdapter;
    private IAddFocusListener mFocusListener;
    private IOnPagerKeyListener mPagerKeyListener;
    private int UPDATE_INDEX = 1;
    private int DOWNLOAD_INDEX = 8;
    private CanDialog mClearCacheConfirmDialog;

    public ManagerFragment() {
    }

    public ManagerFragment(IndexActivity indexActivity) {
        mFocusListener = indexActivity;
        mPagerKeyListener = indexActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(inflater.getContext()).inflate(R.layout.index_manage_grid, container, false);
        EventBus.getDefault().register(ManagerFragment.this);
        gridView = (GridView) view.findViewById(R.id.manage_grid);
        gridView.setFocusable(false);
        gridView.findViewById(R.id.iv_manage_size);
        gridAdapter = new GridAdapter(inflater.getContext());
        gridAdapter.setNames(NAMES);
        gridAdapter.setIcons(ICONS);
        gridAdapter.setColors(COLORS);
        gridAdapter.setFocusListener(new IAddFocusListener() {
            @Override
            public void addFocusListener(View v, boolean hasFocus, FragmentEnum sourceEnum) {
                mFocusListener.addFocusListener(v, hasFocus, sourceEnum);
            }
        });
        gridAdapter.setKeyListener(new IOnPagerKeyListener() {
            @Override
            public void onKeyEvent(View view, int i, KeyEvent keyEvent) {
                mPagerKeyListener.onKeyEvent(view, i, keyEvent);
            }
        });

        gridAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //一键加速
                    case 0:
                        startAc("com.cantv.action.CLEAN_MASTER");
                        break;
                    //更新管理
                    case 1:
                        UpdateManagerActivity.actionStart(getActivity());
                        break;
                    //文件管理
                    case 2:
                        startAc("com.cantv.media", "com.cantv.media.center.activity.HomeActivity");
                        break;
                    //电视助手
                    case 3:
                        PromptUtils.toast(ManagerFragment.this.getContext(), getResources().getString(R.string.index_nofind));
                        break;
                    //关于
                    case 4:
                        AboutUsActivity.actionStart(getActivity());
                        break;
                    //网络测速
                    case 5:
                        startAc("com.os.setting.NETWORK_SPEED_TEST");
                        break;
                    //卸载管理
                    case 6:
                        UninstallManagerActivity.actionStart(getActivity());
                        break;
                    //安装包管理
                    case 7:
                        InstallManagerActivity.actionStart(getActivity());
                        break;
                    //下载管理
                    case 8:
                        DownloadActivity.actionStart(getActivity());
                        break;
                    //缓存清理
                    case 9:
                        showClearCacheConfirmDialog();
                        break;
                    default:
                        break;
                }
            }
        });

        gridView.setAdapter(gridAdapter);
        return view;
    }

    public void setAdapterFocus() {
        if (null != gridAdapter) {
            gridAdapter.setFocusAll();
        }
    }

    public void startAc(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        try {
            startActivity(intent);
        } catch (Exception e) {
            PromptUtils.toast(ManagerFragment.this.getContext(), getResources().getString(R.string.index_nofind));
            e.printStackTrace();
        }
    }

    public void startAc(String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.setAction(Intent.ACTION_VIEW);
        try {
            startActivity(intent);
        } catch (Exception e) {
            PromptUtils.toast(ManagerFragment.this.getContext(), getResources().getString(R.string.index_nofind));
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateApkModel model) {
        gridAdapter.refreshUI(UPDATE_INDEX, model.getNumber());
    }

    @Override
    public void onResume() {
        super.onResume();
        DownloadManager downloadManager=DownloadManager.getInstance(getActivity());
        downloadManager.setTaskCntListener(this);
        gridAdapter.refreshUI(DOWNLOAD_INDEX, downloadManager.getCurrentTaskList().size());
    }

    @Override
    public void onPause() {
        super.onPause();
        DownloadManager.getInstance(getActivity()).removeTaskCntListener();
    }

    @Override
    public void onDestroy() {
        if (mClearCacheConfirmDialog != null) {
            mClearCacheConfirmDialog.dismiss();
            mClearCacheConfirmDialog.release();
            mClearCacheConfirmDialog = null;
        }
        super.onDestroy();
        EventBus.getDefault().unregister(ManagerFragment.this);
    }

    @Override
    public View getLastView() {
        return gridView.getChildAt(4);
    }

    @Override
    public void getTaskCount(final int count) {
        Log.i("ManagerFragment", "count " + count);
        if(gridView!=null){
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridAdapter.refreshUI(DOWNLOAD_INDEX, count);
                }
            });
        }

    }

    private void showClearCacheConfirmDialog() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (mClearCacheConfirmDialog == null) {
            Resources resources = getActivity().getResources();
            mClearCacheConfirmDialog = new CanDialog(getActivity())
                    .setTitleToBottom(resources.getString(R.string.if_clear_cache), R.dimen.dimen_32px)
                    .setMessageBackground(Color.TRANSPARENT)
                    .setPositiveButton(resources.getString(R.string.confirm))
                    .setNegativeButton(resources.getString(R.string.back))
                    .setOnCanBtnClickListener(new CanDialog.OnClickListener() {
                        @Override
                        public void onClickPositive() {
                            mClearCacheConfirmDialog.dismiss();
                            ManagerFragment.this.clearCache();
                        }

                        @Override
                        public void onClickNegative() {
                            mClearCacheConfirmDialog.dismiss();
                        }
                    });
        }
        mClearCacheConfirmDialog.show();
    }

    private void clearCache() {
        final FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        // 清理图片内存缓存
        ImageLoader.getInstance().clearMemoryCache(activity);
        new Thread() {
            @Override
            public void run() {
                // 清理图片磁盘缓存
                ImageLoader.getInstance().clearDiskCache(activity);
                // 清理首页布局缓存
                DataUtils.getInstance(MyApp.getContext()).clearData();
                // 清理数据库
                new MessageDBManager(activity).clearAllMsg();

                DownloadManager.getInstance(activity).cancelAll();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        PromptUtils.toast(activity, getResources().getString(R.string.clear_cache_success));
                    }
                });
            }
        }.start();
    }

}
