package com.can.appstore.myapps.ui;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.ICallBack;
import com.can.appstore.myapps.adapter.MyAppsRvAdapter;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.MyAppsListDataUtil;
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

public class MyAppsFragment extends Fragment {

    //本地全部的第三方应用
    List<AppInfo> mAppsList = new ArrayList<AppInfo>();
    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(18);

    //表格布局
    CanRecyclerView  mAppsRecyclerView ;
    MyAppsRvAdapter mMyAppsRvAdapter ;

    LinearLayout ll_edit;
    boolean needFocus;


//    FocusMoveUtil focusMoveUtil;
//    FocusScaleUtil focusScaleUtil;
//    private View mFocusedListChild;
//    MyFocusMoveRunnable mFocusMoveRunnable;
    private OnFocusChangeListener mFocusChangeListener;
    private ICallBack mICallBack;

    MyAppsListDataUtil mMyAppsListDataUtil;
    public MyAppsFragment(ICallBack iCallBack){
        this.mICallBack = iCallBack;

    }

    private void initData() {
        //初始化数据
        mMyAppsListDataUtil = new MyAppsListDataUtil(getActivity());
        mShowList = mMyAppsListDataUtil.getShowList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();
        View view  = inflater.inflate(R.layout.fragment_myapps,container,false);
        mAppsRecyclerView = (CanRecyclerView) view .findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(getActivity(), 6, GridLayoutManager.VERTICAL, false));
        mAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(Color.TRANSPARENT,10,0));

        mMyAppsRvAdapter = new MyAppsRvAdapter(mShowList);
        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);

//        focusMoveUtil = new FocusMoveUtil(getActivity(),getActivity().getWindow().getDecorView(),R.drawable.btn_focus);
//        focusScaleUtil = new FocusScaleUtil();
//        mFocusMoveRunnable = new MyFocusMoveRunnable();

        addListener();
        return view;
    }



    private void addListener() {

        /**
         * 焦点移动事件，处理焦点框
         */
        mFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if( !needFocus ){
                    mICallBack.onSuccess(view,hasFocus);
                }

//                if (hasFocus) {
//                    mFocusedListChild = view;
//                    mAppsRecyclerView.postDelayed(mFocusMoveRunnable, 50);
//                } else {
//                    focusScaleUtil.scaleToNormal();
//                }
            }

        };
        //添加焦点移动的监听,要在adapter里设置
       mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);

        //adapter的菜单按钮事件（全部应用，系统应用，添加应用不响应）
        mMyAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View item, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_MENU  && mShowList.get(position).packageName != ""){
                        needFocus = true;
                        ll_edit = (LinearLayout) item.findViewById(R.id.myapps_ll_edit);
                        ll_edit.setVisibility(View.VISIBLE);
//                        mMyAppsRvAdapter.setOnFocusChangeListener(null);
                        int width = ll_edit.getWidth();
                        int height = ll_edit.getHeight();
//                        ToastUtil.toastShort("ll_edit--宽"+width+"高"+height+"======item--"+item.getWidth()+"高"+item.getHeight());
                        editItem(position,item);
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
                if(position == 0){
                    Intent  intent = new Intent(getActivity(),AllAppsActivity.class);
                    startActivity(intent);
                }else if(position == 1){
                    ToastUtil.toastShort("显示popupwindow");
                }else{
                    if(mShowList.get(position).packageName == ""){
                        //添加更多
                        Intent i = new Intent(getActivity(),AddAppsActivity.class);
                        int add = 16 - mShowList.size();
                        i.putExtra("add",add);
                        startActivityForResult(i,0);
                    }else{
                        ToastUtil.toastShort("打开应用");
                        PackageManager pm =getActivity().getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(mShowList.get(position).packageName);//获取启动的包名
                        startActivity(intent);
                    }

                }
            }
        });

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        //TODO
//        super.onActivityResult(requestCode, resultCode, data);
//    }



//    public class MyFocusMoveRunnable  implements Runnable{
//
//        @Override
//        public void run() {
//            if (mFocusedListChild != null) {

//                mICallBack.onSuccess(mFocusedListChild,true);
//                focusScaleUtil.scaleToLarge(mFocusedListChild);
//                focusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);

//            }
//        }
//    }


    //显示浮层，有置顶或移除操作
    private void editItem(final int position, final View item) {

        //浮层按钮的点击事件
       Button mTopAppBtn = (Button)item.findViewById(R.id.myapps_but_top);
        mTopAppBtn.requestFocus();
        mTopAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInfo appInfo = mShowList.get(position);
                mShowList.remove(position);
                mShowList.add(2,appInfo);
                mMyAppsListDataUtil.saveShowList(mShowList);
                hideEditView(position,item);
                mMyAppsRvAdapter.notifyDataSetChanged();
            }
        });
        mTopAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    hideEditView(position,item);
                    return true;
                }
                return false;
            }
        });

       Button mRemoveAppBtn = (Button)item.findViewById(R.id.myapps_but_moveout) ;
        mRemoveAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowList.remove(position);
                mMyAppsListDataUtil.saveShowList(mShowList);
                hideEditView(position,item);
                mMyAppsRvAdapter.notifyDataSetChanged();
            }
        });
        mRemoveAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    hideEditView(position,item);
                    return true;
                }
                return false;
            }
        });




    }

    private void hideEditView(int position , View item) {
        needFocus = false;
        ll_edit.setVisibility(View.GONE);
        item.requestFocus();
        mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);
    }




}