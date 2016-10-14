package com.can.appstore.myapps;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;

/**
 * Created by wei on 2016/10/13.
 */

public class MyApps extends Fragment {

    CanRecyclerView mCanRecyclerView = null;
    //本地全部的第三方应用
    List<AppInfo> mAppsList = new ArrayList<AppInfo>();

    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(15);


    MyAppsRvAdapter mMyAppsRvAdapter = null;

    Button myappstop;
    Button myappsmoveout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppsList = AppUtils.findAllInstallApkInfo(getActivity());
        initData(mAppsList, mShowList);
    }

    private void initData(List<AppInfo> mmAppsList, List<AppInfo> mmShowList) {

        //列表前两个item是自动生成
        AppInfo allAppsItem = new AppInfo("全部应用", getActivity().getDrawable(R.drawable.ic_launcher));
        AppInfo sysAppsItem = new AppInfo("系统应用", getActivity().getDrawable(R.drawable.ic_launcher));
        mmShowList.add(allAppsItem);
        mmShowList.add(sysAppsItem);
        for (AppInfo app : mmAppsList) {
            if (!app.isSystemApp) {
                mmShowList.add(app);
            }
        }
        if (mmShowList.size() < 15) {
            int addItem = mmShowList.size() - 1;
            AppInfo addAppsItem = new AppInfo("添加应用", getActivity().getDrawable(R.drawable.ic_launcher));
            mmAppsList.add(addAppsItem);
        }
        mShowList = mmShowList;
        mMyAppsRvAdapter = new MyAppsRvAdapter(getActivity(), mShowList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myapps, container, false);
        mCanRecyclerView = (CanRecyclerView) view.findViewById(R.id.cr_myapps);
        mCanRecyclerView.setLayoutManager(new CanRecyclerView.LayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false));

        mCanRecyclerView.setAdapter(mMyAppsRvAdapter);

//        mMyAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
//            @Override
//            public boolean onItemKeyEvent(final int position, View itemview, int keyCode, KeyEvent event) {
//                if (position != 0 && position != 1 && keyCode == KeyEvent.KEYCODE_MENU) {
//                    final LinearLayout myappll = (LinearLayout) itemview.findViewById(R.id.myapps_ll);
//                    myappll.setVisibility(View.VISIBLE);
//                    myappstop = (Button) itemview.findViewById(R.id.myapps_top);
//                    myappstop.setOnClickListener(
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    topItem(mShowList, position);
//                                    myappll.setVisibility(View.GONE);
//                                }
//                            }
//                    );
//                    myappsmoveout = (Button) itemview.findViewById(R.id.myapps_moveout);
//                    myappsmoveout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            moveOutItem(mShowList, position);
//                            myappll.setVisibility(View.GONE);
//                        }
//                    });
//
//                }
//                return true;
//            }
//        });
        return view;
    }

    private void moveOutItem(List<AppInfo> showList, int position) {
        //移除条目
        ToastUtil.toastShort("该条目移除");
        showList.remove(position);
        mMyAppsRvAdapter.notifyDataSetChanged();

    }

    private void topItem(List<AppInfo> showList, int position) {
        //置顶条目
        ToastUtil.toastShort("该条目置顶");
        AppInfo appInfo = showList.get(position);
        showList.remove(position);
        showList.add(2, appInfo);
        mMyAppsRvAdapter.notifyDataSetChanged();
    }


}
