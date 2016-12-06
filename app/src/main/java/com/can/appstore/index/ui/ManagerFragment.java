package com.can.appstore.index.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.can.appstore.R;
import com.can.appstore.download.DownloadActivity;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.index.adapter.GridAdapter;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.installpkg.InstallManagerActivity;
import com.can.appstore.uninstallmanager.UninstallManagerActivity;
import com.can.appstore.update.UpdateManagerActivity;
import com.can.appstore.update.model.UpdateApkModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.can.downloadlib.DownloadTaskCountListener;
import cn.can.tvlib.utils.PromptUtils;

/**
 * Created by liuhao on 2016/10/21.
 */

public class ManagerFragment extends BaseFragment implements DownloadTaskCountListener {
    private final int[] NAMES = {R.string.index_manager_text1, R.string.index_manager_text2, R.string.index_manager_text3, R.string.index_manager_text4, R.string.index_manager_text5, R.string.index_manager_text6, R.string.index_manager_text7, R.string.index_manager_text8};
    private final int[] ICONS = {R.drawable.index_manager_icon1, R.drawable.index_manager_icon2, R.drawable.index_manager_icon3, R.drawable.index_manager_icon4, R.drawable.index_manager_icon5, R.drawable.index_manager_icon6, R.drawable.index_manager_icon7, R.drawable.index_manager_icon8};
    private final int[] COLORS = {R.drawable.index_item1_shape, R.drawable.index_item2_shape, R.drawable.index_item3_shape, R.drawable.index_item4_shape, R.drawable.index_item5_shape, R.drawable.index_item6_shape, R.drawable.index_item7_shape, R.drawable.index_item8_shape};
    private GridView gridView;
    private GridAdapter gridAdapter;
    private IAddFocusListener mFocusListener;
    private IOnPagerKeyListener mPagerKeyListener;
    private int UPDATE_INDEX = 1;
    private int updateNum;

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
//        gridView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//        gridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                Log.i("ManagerFragment", view.getId() + " " + b);
//                if (!b) {
//                    gridView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//                }
//            }
//        });
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
                        break;
                    //网络测速
                    case 4:
                        startAc("com.os.setting.NETWORK_SPEED_TEST");
                        break;
                    //卸载管理
                    case 5:
                        UninstallManagerActivity.actionStart(getActivity());
                        break;
                    //安装包管理
                    case 6:
                        InstallManagerActivity.actionStart(getActivity());
                        break;
                    //下载管理
                    case 7:
                        DownloadActivity.actionStart(getActivity());
                        break;
                    default:
                        break;
                }
            }
        });

        gridView.setAdapter(gridAdapter);
        return view;
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
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateApkModel model) {
        updateNum = model.getNumber();
        gridAdapter.refreshUI(UPDATE_INDEX, updateNum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ManagerFragment.this);
    }

    @Override
    public View getLastView() {
        return gridView.getChildAt(3);
    }

    @Override
    public void getTaskCount(int count) {
        Log.i("ManagerFragment", "count " + count);
    }
}
