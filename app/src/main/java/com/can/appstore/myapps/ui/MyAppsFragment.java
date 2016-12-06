package com.can.appstore.myapps.ui;

import android.app.Dialog;
import android.content.Intent;
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

import com.can.appstore.R;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FragmentEnum;
import com.can.appstore.myapps.adapter.MyAppsRvAdapter;
import com.can.appstore.myapps.addappsview.AddAppsActivity;
import com.can.appstore.myapps.allappsview.AllAppsActivity;
import com.can.appstore.myapps.myappsfragmview.MyAppsFragPresenter;
import com.can.appstore.myapps.myappsfragmview.MyAppsFramentContract;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter.OnFocusChangeListener;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wei on 2016/10/13.
 */

public class MyAppsFragment extends BaseFragment implements MyAppsFramentContract.View {
    public static String TAG = "MyAppsFragment";
    private MyAppsFragPresenter mMyAppsFramPresenter;
    //表格布局
    private CanRecyclerView mAppsRecyclerView;
    private MyAppsRvAdapter mMyAppsRvAdapter;

    //焦点的监听和主Activity处理焦点框的回调
    private OnFocusChangeListener mFocusChangeListener;
    private IOnPagerKeyListener mOnPagerKeyListener;
    private IAddFocusListener mFocusListener;
    //浮层对话框/浮层对话框的按钮
    private Dialog dialog;
    private Button mTopAppBtn;
    private Button mRemoveAppBtn;
    //显示的list数据
    private List<PackageUtil.AppInfo> mShowList;

    public MyAppsFragment(IndexActivity indexActivity) {
        this.mFocusListener = indexActivity;
        this.mOnPagerKeyListener = indexActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "----onCreatView()");
        View view = inflater.inflate(R.layout.fragment_myapps, container, false);
        mAppsRecyclerView = (CanRecyclerView) view.findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(getActivity(), 6, GridLayoutManager.VERTICAL, false));
        mAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(Color.TRANSPARENT, 8, 8));

        mMyAppsFramPresenter = new MyAppsFragPresenter(this, getContext());
        mMyAppsFramPresenter.startLoad();
        mMyAppsFramPresenter.addListener();

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "----onResume()");
        super.onResume();
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> infoList, int myapplistsize) {
        mShowList = infoList;
        if (infoList.size() - 2 < myapplistsize && infoList.size() < 18 && !infoList.get(infoList.size() - 1).packageName.isEmpty()) {
            infoList.add(new AppInfo(getResources().getString(R.string.add_app), getActivity().getResources().getDrawable(R.drawable.addapp_icon)));
        }
        if (mMyAppsRvAdapter == null) {
            mMyAppsRvAdapter = new MyAppsRvAdapter(infoList);
            baseSetting();
        } else {
            mMyAppsRvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadCustomDataSuccess(List<Drawable> mDrawbleList) {
        //系统应用的图标集合
        mMyAppsRvAdapter.setCustomData(mDrawbleList);
    }

    private void baseSetting() {
        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);
        addItemListener();
    }

    private void addItemListener() {
        /**
         * 焦点移动事件，处理焦点框
         */
        mFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                mFocusListener.addFocusListener(view, hasFocus, FragmentEnum.MYAPP);
            }
        };
        //添加焦点移动的监听,要在adapter里设置
        mMyAppsRvAdapter.setOnFocusChangeListener(mFocusChangeListener);

        //adapter的菜单按钮事件（全部应用，系统应用，添加应用不响应）
        mMyAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View item, int keyCode, KeyEvent event) {
                if (position % 6 == 0 && event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mOnPagerKeyListener.onKeyEvent(item, keyCode, event);
                }
                if (keyCode == KeyEvent.KEYCODE_MENU && !"".equals(mShowList.get(position).packageName) && event.getAction() == KeyEvent.ACTION_DOWN) {
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
                    Intent intent = new Intent(getActivity(), SystemAppsActivity.class);
                    startActivity(intent);
                } else {
                    if ("".equals(mShowList.get(position).packageName)) {
                        //添加更多
                        Intent i = new Intent(getActivity(), AddAppsActivity.class);
                        int add = 16 - mShowList.size();
                        i.putExtra("add", add);
                        startActivityForResult(i, 2);
                    } else {
                        Log.d(TAG, "OPENAPP__PACKAGENAME:" + mShowList.get(position).packageName);
                        try {
                            PackageUtil.openApp(getActivity(), mShowList.get(position).packageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            boolean isAdd = bundle.getBoolean("isAdd");
            if (isAdd) {
                mMyAppsFramPresenter.startLoad();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        lp.x = location[0] + 1; // 新位置X坐标
        lp.y = location[1] + 1; // 新位置Y坐标
        lp.width = (int) (getResources().getDimensionPixelSize(R.dimen.dimen_270px) * 1.095f); // 宽度
        lp.height = (int) (getResources().getDimensionPixelSize(R.dimen.dimen_180px) * 1.093f); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();
        editItem(position, item);
    }

    //显示浮层，有置顶或移除操作
    private void editItem(final int position, final View item) {
        mTopAppBtn.requestFocus();
        //浮层按钮的点击事件
        mTopAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void onDestroyView() {
        Log.d(TAG, "----onDestory()");
        if (mMyAppsFramPresenter != null) {
            mMyAppsFramPresenter.unRegiestr();
            mMyAppsFramPresenter.release();
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "----onPause()");
        super.onPause();
    }

    @Override
    public View getLastView() {
        return null;
    }
}