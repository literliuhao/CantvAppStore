package com.can.appstore.myapps;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.CanRecyclerViewFocusHelper;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by wei on 2016/10/13.
 */

public class MyApps extends Fragment implements View.OnClickListener {

    //本地全部的第三方应用
    List<AppInfo> mAppsList = new ArrayList<AppInfo>();
    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(15);

    //表格布局
    CanRecyclerView  mAppsRecyclerView = null;
    MyAppsRvAdapter mMyAppsRvAdapter = null;

     public  boolean isFocusRv = true;
    private LinearLayout mEditView;
    private Button mOpenAppBtn;
    private Button mUninstallAppBtn;
    private int mCurrentPos;

    CanRecyclerViewFocusHelper mFocusHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity() .requestWindowFeature(Window.FEATURE_NO_TITLE);
        initData();
    }


    private void initData() {
        mAppsList = AppUtils.findAllInstallApkInfo(getActivity());
        mShowList.add(new AppInfo("全部应用", getResources().getDrawable(R.drawable.ic_launcher,null)));
        mShowList.add(new AppInfo("系统应用", getResources().getDrawable(R.drawable.ic_launcher,null)));
        for (AppInfo  app : mAppsList){
            if(!app.isSystemApp){
                mShowList.add(app);
            }
        }
        if(mShowList.size()<15){
            mShowList.add(new AppInfo("添加应用", getResources().getDrawable(R.drawable.ic_launcher,null)));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_myapps,container,false);
        mAppsRecyclerView = (CanRecyclerView) view .findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.LayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false));

        mEditView = (LinearLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.appmanager_gridview_item_edit_layout, null)
                .findViewById(R.id.app_list_page_cell_edit_layout);
        mEditView.setVisibility(View.GONE);
        RelativeLayout mContent = (RelativeLayout)view.findViewById(R.id.myapps_main_page);
        FrameLayout decorView1 = (FrameLayout) getActivity().getWindow().getDecorView();
        decorView1.addView(mEditView, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mOpenAppBtn = (Button)mEditView. findViewById(R.id.app_list_page_cell_open);
        mOpenAppBtn.setText("置顶");
        mUninstallAppBtn = (Button)mEditView. findViewById(R.id.app_list_page_cell_remove);
        mUninstallAppBtn.setText("移除");


        mMyAppsRvAdapter = new MyAppsRvAdapter(mShowList);
        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);

        View decorView = getActivity().getWindow().getDecorView();
        FocusMoveUtil focusMoveUtil = new FocusMoveUtil(getActivity(),decorView,R.drawable.focus);

        FocusScaleUtil focusScaleUtil = new FocusScaleUtil(500, 1.1f, new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 1.1f;
            }
        });
        mFocusHelper = new CanRecyclerViewFocusHelper(focusMoveUtil,focusScaleUtil);
        mFocusHelper.attachToRecyclerView(mAppsRecyclerView,R.drawable.focus,1.1f);


        mMyAppsRvAdapter.setItemKeyEventListener(new MyAppsItemKeyEventListener());
        mMyAppsRvAdapter.setOnItemClickListener(new MyAppsOnClickListenrer());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_list_page_cell_open:
                AppInfo appInfo = mShowList.get(mCurrentPos);
                String className = appInfo.packageName;
                mShowList.remove(mCurrentPos);
                mShowList.add(2,appInfo);
                hideEditViewIfNecessary();
                mMyAppsRvAdapter.notifyDataSetChanged();
                break;
            case R.id.app_list_page_cell_remove:
                mShowList.remove(mCurrentPos);
                hideEditViewIfNecessary();
                mMyAppsRvAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }


    private void hideEditViewIfNecessary() {
        if (mEditView != null) {
            mEditView.setVisibility(View.GONE);
        }
    }


    private class MyAppsItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener{
       @Override
       public boolean onItemKeyEvent(final int position, View view, int keyCode, KeyEvent event) {
           mCurrentPos = position;
           if(keyCode == KeyEvent.KEYCODE_MENU){
               //菜单键
               if(mShowList.get(position).packageName == ""){
                   ToastUtil.toastShort("菜单键无法响应");
               }else{
                   ToastUtil.toastShort("菜单键操作-------------");
                   isFocusRv = false;
                   mFocusHelper = null;
                   showMenuAction(position,view);
               }
           }
           return false;
       }
   }


    private void showMenuAction(final int position, View view) {
        mEditView.setBackgroundColor(Color.BLUE);

        if (mShowList.get(mCurrentPos).isSystemApp) {
            mUninstallAppBtn.setVisibility(View.GONE);
        } else {
            mUninstallAppBtn.setVisibility(View.VISIBLE);
            mUninstallAppBtn.setOnClickListener(this);
        }
        mOpenAppBtn.setOnClickListener(this);
        mEditView.setVisibility(View.VISIBLE);
        mEditView.requestFocus();

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        Log.e("zby", "location " + location[0] + ", location[]" + location[1]);
        float w = view.getWidth() * view.getScaleX();
        float h = view.getHeight() * view.getScaleY();
        float wOffset = (w - view.getWidth()) / 2;
        float hOffset = (h - view.getHeight()) / 2;


//        mEditView.layout(, location[1], ((int)()), ((int)()));
        FrameLayout.LayoutParams edit_button_lp = (FrameLayout.LayoutParams) mEditView.getLayoutParams();
        edit_button_lp.leftMargin = (int)(location[0] - wOffset);
        edit_button_lp.topMargin = (int)(location[1] - hOffset) ;
        edit_button_lp.width = (int)(w);
        edit_button_lp.height = (int)(h);
        mEditView.requestLayout();
    }



    private class MyAppsOnClickListenrer implements CanRecyclerViewAdapter.OnItemClickListener{

        @Override
        public void onClick(View view, int position, Object data) {
            if(position == 0){
                Intent  intent = new Intent(getActivity(),AllAppsActivity.class);
                startActivity(intent);
            }else if(position == 1){
                ToastUtil.toastShort("显示popupwindow");
            }else{
                if(mShowList.get(position).packageName == ""){
                    //添加更多
                    ToastUtil.toastShort("打开添加应用");
                }else{
                    ToastUtil.toastShort("打开应用");
                    PackageManager pm =getActivity().getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(mShowList.get(position).packageName);//获取启动的包名
                    startActivity(intent);
                }

            }

        }
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if(mEditView.getVisibility() == View.VISIBLE){
                mEditView.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }



}