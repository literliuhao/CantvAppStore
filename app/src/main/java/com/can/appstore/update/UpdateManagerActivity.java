package com.can.appstore.update;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.view.LoadingDialog;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.PreferencesUtils;

/**
 * 更新管理
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerActivity extends Activity {

    private CanRecyclerView mRecyclerView;
    private List<AppInfoBean> mDatas;
    private UpdateManagerAdapter mRecyclerAdapter;
    private Button mDetectionButton;
    private Button mAutoButton;
    private TextView mReminder;
    private int mCurrentPositon;
    private TextView mRoomSize;
    private boolean mAutoUpdate;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private ProgressBar mSizeProgressBar;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private String mSdAvaliableSize;
    private int i;
    private ProgressBar mUpdateProgressBar;
    private Dialog mLoadingDialog;
    FocusMoveUtil mFocusMoveUtil;
    FocusScaleUtil mFocusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemanager);
        mDatas = new ArrayList<AppInfoBean>();
        mAutoUpdate = PreferencesUtils.getBoolean(MyApp.mContext, "AUTO_UPDATE", false);
        if (!mAutoUpdate) {
            showDialog();
        }
        initView();
        initData();
        initFocusChange();
        initClick();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    private void initFocusChange() {

        mDetectionButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mDetectionButton, 1.1f);
                    mFocusScaleUtil.scaleToLarge(mDetectionButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mDetectionButton);
                }
            }
        });

        mAutoButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mAutoButton, 1.1f);
                    mFocusScaleUtil.scaleToLarge(mAutoButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mAutoButton);
                }
            }
        });

        mRecyclerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mRecyclerView.postDelayed(myFocusRunnable, 50);
                    int total = mDatas.size() / 3;
                    if (mDatas.size() % 3 != 0) {
                        total += 1;
                    }
                    int cur = position / 3 + 1;
                    mCurrentnum.setText(cur + "/");
                    mTotalnum.setText(total + "行");

                } else {
                    mFocusScaleUtil.scaleToNormal();
                }
            }
        });
    }

    private void initClick() {

        mDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateManagerActivity.this, "点击检测更新,item总数：" + mDatas.size(), Toast.LENGTH_SHORT).show();
                mAutoUpdate = PreferencesUtils.getBoolean(MyApp.mContext, "AUTO_UPDATE", false);
                if (mAutoUpdate) {
                    mDatas.clear();
                    mRecyclerAdapter.notifyDataSetChanged();
                    mReminder.setVisibility(View.VISIBLE);
                    mReminder.setText(R.string.update_start_autoupdate);
                    Toast.makeText(MyApp.mContext, R.string.update_start_autoupdate, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mDatas.clear();
                    mRecyclerAdapter.notifyDataSetChanged();
                    showDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addData();
                        }
                    }, 2000);
                    //addData();
                }
            }
        });

        mAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAutoUpdate) {
                    PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", false);
                    mAutoUpdate = false;
                    mReminder.setVisibility(View.INVISIBLE);
                    Toast.makeText(UpdateManagerActivity.this, R.string.update_end_autoupdate, Toast.LENGTH_SHORT).show();
                } else {
                    PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", true);
                    mAutoUpdate = true;
                    if (mDatas.size() < 1) {
                        mReminder.setVisibility(View.VISIBLE);
                        mReminder.setText(R.string.update_start_autoupdate);
                    }
                    Toast.makeText(UpdateManagerActivity.this, R.string.update_start_autoupdate, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecyclerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                Toast.makeText(UpdateManagerActivity.this, position + 1 + "/" + mDatas.size(),
                        Toast.LENGTH_SHORT).show();
                mCurrentPositon = position;
                mUpdateProgressBar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
                initProgress();
            }
        });

    }

    protected void initData() {
        mSdTotalSize = UpdateUtils.getSDTotalSize();
        mSdSurplusSize = UpdateUtils.getSDSurplusSize();
        mSdAvaliableSize = UpdateUtils.getSDAvaliableSize();
        mSizeProgressBar.setMax(mSdTotalSize);
        mSizeProgressBar.setProgress(mSdSurplusSize);
        mRoomSize.setText(getString(R.string.update_sdavaliable_size) + mSdAvaliableSize);
        if (mAutoUpdate) {
            mReminder.setVisibility(View.VISIBLE);
            mReminder.setText(getString(R.string.update_start_autoupdate));
            return;
        }
        final List appList = UpdateUtils.getAppList();
        mDatas.clear();
        if (appList.size() < 1 || appList == null) {
            mReminder.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeDialog();
                    mDatas.addAll(appList);
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    }

    private void initView() {
        mTotalnum = (TextView) findViewById(R.id.tv_update_totalnum);
        mCurrentnum = (TextView) findViewById(R.id.tv_update_currentnum);
        mRoomSize = (TextView) findViewById(R.id.tv_update_roomsize);
        mDetectionButton = (Button) findViewById(R.id.bt_update_detection);
        mAutoButton = (Button) findViewById(R.id.bt_update_auto);
        mRecyclerView = (CanRecyclerView) findViewById(R.id.rv_update_recyclerview);
        mReminder = (TextView) findViewById(R.id.tv_update_reminder);
        mSizeProgressBar = (ProgressBar) findViewById(R.id.pb_update_progressbar);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mRecyclerAdapter = new UpdateManagerAdapter(mDatas);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setSelected(true);

    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);
                mFocusScaleUtil.scaleToLarge(mFocusedListChild);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UpdateManagerActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i == 100) {
                    Toast.makeText(UpdateManagerActivity.this, "进度更新完成", Toast.LENGTH_SHORT).show();
                    return;
                }
                ++i;
                mUpdateProgressBar.setProgress(i);
                initProgress();
            }
        }, 500);
    }

    /**
     * 显示Dialog
     */
    private void showDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.createLoadingDialog(this, getString((R.string.update_search_updateinfo)));
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
}
