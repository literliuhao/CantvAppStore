package com.can.appstore.myapps.ui;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.myapps.adapter.MyAppsRvAdapter;
import com.can.appstore.myapps.addappsview.AddAppsActivity;
import com.can.appstore.myapps.allappsview.AllAppsActivity;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.myappsfragmview.MyAppsFragPresenter;
import com.can.appstore.myapps.myappsfragmview.MyAppsFramentContract;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter.OnFocusChangeListener;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by wei on 2016/10/13.
 */

public class MyAppsFragment extends BaseFragment implements MyAppsFramentContract.View {

    MyAppsFragPresenter mMyAppsFramPresenter;

    //表格布局
    CanRecyclerView mAppsRecyclerView;
    MyAppsRvAdapter mMyAppsRvAdapter;

    //浮层对话框
    Dialog dialog;

    //焦点的监听和主Activity处理焦点框的回调
    private OnFocusChangeListener mFocusChangeListener;
    private IAddFocusListener mFocusListener;


    //浮层对话框的按钮
    private Button mTopAppBtn;
    private Button mRemoveAppBtn;

    //显示的list数据
    public List<AppInfo> mShowList;

    public MyAppsFragment(IAddFocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("myappfragment","----onCreatView()");
        View view = inflater.inflate(R.layout.fragment_myapps, container, false);
        mAppsRecyclerView = (CanRecyclerView) view.findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(getActivity(), 6, GridLayoutManager.VERTICAL, false));
        mAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(Color.TRANSPARENT, 10, 0));

        mMyAppsFramPresenter = new MyAppsFragPresenter(this, getContext());

        return view;
    }

    @Override
    public void onResume() {
        Log.i("myappfragment","----onResume()");
        if (mMyAppsFramPresenter != null) {
            mMyAppsFramPresenter.addListener();
            mMyAppsFramPresenter.startLoad();
        }
        super.onResume();
    }


    @Override
    public void loadAddAppInfoSuccess(List<AppInfo> infoList) {
        mShowList = infoList;
        if(infoList.size()-2 < MyApp.myAppList.size() && infoList.size() < 18 && !infoList.get(infoList.size()-1).packageName .isEmpty()){
            infoList.add(new AppInfo("添加应用", getActivity().getResources().getDrawable(R.drawable.addapp_icon)));
        }
        if (mMyAppsRvAdapter == null) {
            mMyAppsRvAdapter = new MyAppsRvAdapter(infoList);
            baseSetting();
        } else {
          mMyAppsRvAdapter.notifyDataSetChanged();
        }
    }

    private void baseSetting() {
        Drawable d1 = getResources().getDrawable(R.drawable.bj_02);
        Drawable d2 = getResources().getDrawable(R.drawable.bj_04);
        Drawable d3 = getResources().getDrawable(R.drawable.bj_03);
        Drawable d4 = getResources().getDrawable(R.drawable.bj_04);
        Drawable d5 = getResources().getDrawable(R.drawable.bj_05);

        ArrayList<Drawable> list = new ArrayList<Drawable>();
        list.add(d1);
        list.add(d2);
                list.add(d3);
                list.add(d4);
                list.add(d5);

        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);
        mMyAppsRvAdapter.setCustomData(list);
        addItemListener();
    }


    private void addItemListener() {
        /**
         * 焦点移动事件，处理焦点框
         */
        mFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                mFocusListener.addFocusListener(view, hasFocus);
            }
        };
        //添加焦点移动的监听,要在adapter里设置
        mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);

        //adapter的菜单按钮事件（全部应用，系统应用，添加应用不响应）
        mMyAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View item, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_MENU && mShowList.get(position).packageName != "" && event.getAction() == KeyEvent.ACTION_DOWN) {
                    showEditView(position, item);
                    return true;
                }
                return false;
            }
        });

        /** adapter的普通点击事件
         *  需要注意的是：
         *          全部应用：打开全部应用的activity
         *          系统应用：弹出对话框，显示系统应用
         *          添加应用：弹出对话框，显示待选择的应用
         */
        mMyAppsRvAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View item, int position, Object data) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), AllAppsActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    ToastUtil.toastShort("显示popupwindow");
                    Intent intent = new Intent(getActivity(), SystemAppsActivity.class);
                    startActivity(intent);
                } else {
                    if (mShowList.get(position).packageName == "") {
                        //添加更多
                        Intent i = new Intent(getActivity(), AddAppsActivity.class);
                        int add = 16 - mShowList.size();
                        i.putExtra("add", add);
                        startActivityForResult(i, 0);
                    } else {
                        ToastUtil.toastShort("打开应用");
                        PackageManager pm = getActivity().getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(mShowList.get(position).packageName);//获取启动的包名
                        startActivity(intent);
                    }
                }
            }
        });
    }


    public void showEditView(int position, View item) {
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.MyEditDialogStyle);
            dialog.setContentView(R.layout.myapps_edit_linearlayout);
        }
        int[] location = new int[2];
        item.getLocationOnScreen(location);
        mTopAppBtn = (Button) dialog.findViewById(R.id.myapps_but_top);
        mRemoveAppBtn = (Button) dialog.findViewById(R.id.myapps_but_remove);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = location[0] + 2; // 新位置X坐标
        lp.y = location[1] + 2; // 新位置Y坐标
        lp.width = (int) (270 * 1.04f); // 宽度
        lp.height = (int) (180 * 1.04f); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();
        editItem(position, item);
    }

    //显示浮层，有置顶或移除操作
    private void editItem(final int position, final View item) {

        //浮层按钮的点击事件
        mTopAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInfo appInfo = mShowList.get(position);
                mMyAppsFramPresenter.topApp(position);
                hideEditView(position, item);
            }
        });
        mTopAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(position, item);
                    return true;
                }
                return false;
            }
        });

        mRemoveAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyAppsFramPresenter.removeApp(position);
                hideEditView(position, item);
            }
        });
        mRemoveAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(position, item);
                    return true;
                }
                return false;
            }
        });

    }

    private void hideEditView(int position, View item) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        Log.i("myappfragment","----onStop()");
        if (mMyAppsFramPresenter != null) {
            mMyAppsFramPresenter.unRegiestr();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("myappfragment","----onDestory()");
        mMyAppsFramPresenter.release();
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        Log.i("myappfragment","----onStart()");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.i("myappfragment","----onPause()");
        super.onPause();
    }


    public View getLastView() {
        return null;
    }
}