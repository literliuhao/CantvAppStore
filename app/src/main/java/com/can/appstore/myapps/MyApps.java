package com.can.appstore.myapps;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter.OnFocusChangeListener;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wei on 2016/10/13.
 */

public class MyApps extends Fragment implements View.OnClickListener{

    //本地全部的第三方应用
    List<AppInfo> mAppsList = new ArrayList<AppInfo>();
    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(18);

    //表格布局
    CanRecyclerView  mAppsRecyclerView = null;
    MyAppsRvAdapter mMyAppsRvAdapter = null;

     public  boolean isFocusRv = true;
    private LinearLayout mEditView;
    private Button mTopAppBtn;
    private Button mRemoveAppBtn;

    private int mCurrentPos;

    FocusMoveUtil focusMoveUtil;
    FocusScaleUtil focusScaleUtil;
    private View mFocusedListChild;
    MyFocusMoveRunnable mFocusMoveRunnable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    MyAppsShowListHelper  myAppsShowListHelper;
    private void initData() {
        //初始化数据
        myAppsShowListHelper = new MyAppsShowListHelper(getActivity());
        mShowList = myAppsShowListHelper.getShowList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_myapps,container,false);
        mAppsRecyclerView = (CanRecyclerView) view .findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.LayoutManager(getActivity(), 6, GridLayoutManager.VERTICAL, false));
        mAppsRecyclerView.addItemDecoration(new  SpacesItemDecoration(8));

        mEditView = (LinearLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.myapps_item_edit_layout, null)
                .findViewById(R.id.myapps_edit_layout);
        mEditView.setVisibility(View.GONE);
        FrameLayout decorView1 = (FrameLayout) getActivity().getWindow().getDecorView();
        decorView1.addView(mEditView, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mTopAppBtn = (Button)mEditView.findViewById(R.id.myapps_but_edit_top);
        mRemoveAppBtn = (Button)mEditView.findViewById(R.id.myapps_but_edit_remove);

        mMyAppsRvAdapter = new MyAppsRvAdapter(mShowList);
        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);

        focusMoveUtil = new FocusMoveUtil(getActivity(),getActivity().getWindow().getDecorView(),R.drawable.btn_focus);
        focusScaleUtil = new FocusScaleUtil();
        mFocusMoveRunnable= new MyFocusMoveRunnable();
        addListener();
        return view;
    }

    private OnFocusChangeListener mFocusChangeListener;

    private void addListener() {
        mFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public boolean onFocusMoveOutside(int currFocus, int direction) {
                return false;
            }

            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus, Object dataType) {
                if (hasFocus || mEditView.getVisibility() == View.VISIBLE) {
                    mFocusedListChild = view;
                    Log.i(TAG, "onItemFocusChanged " + position);
                    mAppsRecyclerView.postDelayed(mFocusMoveRunnable, 50);
                } else {
                    focusScaleUtil.scaleToNormal();
                }
            }
        };
        //添加焦点移动的监听,要在adapter里设置
       mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);

        //adapter的菜单按钮事件/全部应用，系统应用，添加应用不响应
        mMyAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View view, int keyCode, KeyEvent event) {
                mCurrentPos = position;

                if(keyCode == KeyEvent.KEYCODE_MENU){
                    //菜单键
                    int height = view.getHeight();
                    int width = view.getWidth();
                    ToastUtil.toastShort("---"+ height);
                    if(mShowList.get(position).packageName == ""){
//                        ToastUtil.toastShort("菜单键无法响应");
                    }else{
//                        ToastUtil.toastShort("菜单键操作-------------");
                        isFocusRv = false;
                        mMyAppsRvAdapter.setOnFocusChangeListener(null);
                        showEditViewIfNecessary(position,view);
                    }
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
        });

    }

//    浮层按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myapps_but_edit_top:
                AppInfo appInfo = mShowList.get(mCurrentPos);
                String className = appInfo.packageName;
                mShowList.remove(mCurrentPos);
                mShowList.add(2,appInfo);
                myAppsShowListHelper.saveShowList(mShowList);
                hideEditView();
                mMyAppsRvAdapter.notifyDataSetChanged();

                break;
            case R.id.myapps_but_edit_remove:
                mShowList.remove(mCurrentPos);
                myAppsShowListHelper.saveShowList(mShowList);
                hideEditView();
                mMyAppsRvAdapter.notifyDataSetChanged();

                break;
            default:
                break;
        }

    }

    public class MyFocusMoveRunnable  implements Runnable{

        @Override
        public void run() {
            if (mFocusedListChild != null) {
                focusScaleUtil.scaleToLarge(mFocusedListChild);
                    focusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);

            }
        }
    }


    //显示浮层，有置顶或移除操作
    private void showEditViewIfNecessary(int position, View view) {

        //浮层按钮的BACK事件
        mTopAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( mEditView.getVisibility()== View.VISIBLE  &&  keyCode == KeyEvent.KEYCODE_BACK) {
                     hideEditView();
                     return true;
                }
                if(mEditView.getVisibility()== View.VISIBLE  &&  keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                     return true;
                }
                if(mEditView.getVisibility()== View.VISIBLE  &&  keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    return  true;
                }
                if(mEditView.getVisibility()== View.VISIBLE  &&  keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    return true;
                }
                return false;
            }
        });
        mRemoveAppBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( mEditView.getVisibility()== View.VISIBLE &&  keyCode == KeyEvent.KEYCODE_BACK ){
                    hideEditView();
                    return true;
                }
                if( mEditView.getVisibility()== View.VISIBLE &&  keyCode == KeyEvent.KEYCODE_DPAD_LEFT ){
                    return true;
                }
                if( mEditView.getVisibility()== View.VISIBLE &&  keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    return true;
                }
                if( mEditView.getVisibility()== View.VISIBLE &&  keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    return true;
                }
                return false;
            }
        });



        mTopAppBtn.setOnClickListener(this);
        mEditView.setVisibility(View.VISIBLE);
        mEditView.requestFocus();

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float w = view.getWidth() * view.getScaleX();
        float h = view.getHeight() * view.getScaleY();
        float wOffset = (w - view.getWidth()) / 2 ;
        float hOffset = (h - view.getHeight()) / 2 ;

        FrameLayout.LayoutParams edit_button_lp = (FrameLayout.LayoutParams) mEditView.getLayoutParams();
        edit_button_lp.leftMargin = (int)(location[0] - wOffset);
        edit_button_lp.topMargin = (int)(location[1] - hOffset) ;
        edit_button_lp.width = (int)(w*1.02);
        edit_button_lp.height = (int)(h*1.05);
        mEditView.requestLayout();
    }

    private void hideEditView() {
        mEditView.setVisibility(View.GONE);
        mAppsRecyclerView.getChildAt(mCurrentPos).requestFocus();
        mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);
    }




}