package com.can.appstore.myapps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

/**
 * Created by wei on 2016/10/17.
 */

public class AllAppsActivity extends Activity implements View.OnClickListener{
    private List<AppInfo> mAppsList;
    private ArrayList<AppInfo> mShowList = new ArrayList<AppInfo>();
    public  TextView mNum;
    public GridView  mGridView;
    public  GridViewAdapter  mGridViewAdapter;
    private LinearLayout mEditView;
    private Button mOpenAppBtn;
    private Button mUninstallAppBtn;
    private View mSelectView;
    private int mCurrentPos;

    FocusMoveUtil  mFocusMoveUtil;
    FocusScaleUtil  mFocusScaleUtil;
//    private View mFocusedListChild;
    MyFocusMoveRunnable mFocusMoveRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_allapps);

        initData();
        initView();
    }


    private void initData() {
        mAppsList = AppUtils.findAllInstallApkInfo(this);
        for (AppInfo  app : mAppsList){
            if(!app.isSystemApp){
                mShowList.add(app);
            }
        }
        mGridViewAdapter = new GridViewAdapter(this,mShowList);
    }


    private void initView() {
        mFocusMoveUtil = new FocusMoveUtil(this,getWindow().getDecorView(),R.drawable.focus);
        mFocusScaleUtil = new FocusScaleUtil();
        mFocusMoveRunnable = new MyFocusMoveRunnable();

        mNum = (TextView) findViewById(R.id.allapps_Num);
        mGridView = (GridView) findViewById(R.id.id_recyclerview);
        mEditView = (LinearLayout) LayoutInflater.from(getBaseContext())
                .inflate(R.layout.allapps_item_edit_layout, null)
                .findViewById(R.id.allapps_edit_layout);
        mEditView.setVisibility(View.GONE);
        RelativeLayout mContent = (RelativeLayout)findViewById(R.id.allapps_main_page);
        mContent.addView(mEditView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mOpenAppBtn = (Button) findViewById(R.id.allapps_but_edit_open);
        mUninstallAppBtn = (Button) findViewById(R.id.allapps_but_edit_uninstall);

        mGridView.setAdapter(mGridViewAdapter);
//        mFocusMoveUtil.setFocusView(mGridViewAdapter.getView(0,null,mGridView));
        mGridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mGridView.getChildAt(0);
                mFocusMoveUtil.setFocusView(childAt);
                childAt.requestFocus();
            }
        },50);

        addListener();

    }

    private void addListener() {

//       mGridViewAdapter.setOnFocusChangeListener(new GridViewAdapter.OnFocusChangeListener() {
//           @Override
//           public void onItemFocusChanged(View view, int position, boolean hasFocus, Object dataType) {
//               if (hasFocus) {
//                   mSelectView = view;
//                   mGridView.postDelayed(mFocusMoveRunnable, 50);
//               } else {
//                   mFocusScaleUtil.scaleToNormal();
//               }
//           }
//
//           @Override
//           public boolean onFocusMoveOutside(int currFocus, int direction) {
//               return false;
//           }
//       });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageManager pm =getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(mShowList.get(position).packageName);//获取启动的包名
                startActivity(intent);
            }
        });

        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
//                mGridView.postDelayed(mFocusMoveRunnable, 50);
                mFocusMoveUtil.setFocusView(view,1);
                mFocusScaleUtil.scaleToLarge(view);
                // 第一行最右边item 或 最后一个item的next焦点设置为其自身
                if (position == mGridViewAdapter.getCount() - 1 || position == mGridView.getColumnWidth() - 1) {
                    if (view != null) {
                        view.setNextFocusRightId(view.getId());
                        view.setNextFocusForwardId(view.getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mGridView.setNextFocusRightId(mGridView.getId());
        mGridView.setNextFocusForwardId(mGridView.getId());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MENU:
                mSelectView = mGridView.getSelectedView();
                if (mSelectView != null) {
                    int[] location = new int[2];
                    mGridView.getSelectedView().getLocationInWindow(location);
                    RelativeLayout.LayoutParams edit_button_lp = (RelativeLayout.LayoutParams) mEditView.getLayoutParams();
                    int scalew = (int) (mSelectView.getWidth() * 1.1);
                    int scaleh = (int) (mSelectView.getHeight() * 1.1);
                    edit_button_lp.leftMargin = location[0] - (scalew - mSelectView.getWidth()) / 2;
                    edit_button_lp.topMargin = location[1] - (scaleh - mSelectView.getHeight()) / 2;
                    edit_button_lp.width = scalew;
                    edit_button_lp.height = scaleh;
                    mEditView.setLayoutParams(edit_button_lp);
                    if (mShowList.get(mCurrentPos).isSystemApp) {
                        mUninstallAppBtn.setVisibility(View.GONE);
                    } else {
                        mUninstallAppBtn.setVisibility(View.VISIBLE);
                        mUninstallAppBtn.setOnClickListener(this);
                    }
                    mOpenAppBtn.setOnClickListener(this);
                    mEditView.setVisibility(View.VISIBLE);
                    mEditView.requestFocus();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mEditView.isShown()) {
                        mGridView.requestFocus();
                        mEditView.setVisibility(View.GONE);
                        return true;
                    } else {
                        finish();
                    }
                } else {
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mEditView.isShown()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mEditView.isShown()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mEditView.isShown() && mOpenAppBtn.isFocused()) {
                    return true;
                }
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    private void hideEditViewIfNecessary() {
        if (mEditView != null) {
            mEditView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allapps_but_edit_open:
                AppInfo appInfo = mShowList.get(mCurrentPos);
                String className = appInfo.packageName;
                PackageManager pm =getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(mShowList.get(mCurrentPos).packageName);//获取启动的包名
                startActivity(intent);
                hideEditViewIfNecessary();
                break;
            case R.id.allapps_but_edit_uninstall:
                ToastUtil.toastShort("卸载"+mShowList.get(mCurrentPos).appName);
                hideEditViewIfNecessary();
                break;
            default:
                break;
        }
    }

    public class MyFocusMoveRunnable  implements Runnable{

        @Override
        public void run() {
            if (mSelectView != null) {
                mFocusScaleUtil.scaleToLarge(mSelectView);
                mFocusMoveUtil.startMoveFocus(mSelectView, 1.1f);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
