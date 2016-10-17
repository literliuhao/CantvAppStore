package com.can.appstore.myapps;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by wei on 2016/10/13.
 */

public class MyApps extends Fragment {

    //本地全部的第三方应用
    List<AppInfo> mAppsList = new ArrayList<AppInfo>();
    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(15);

    //表格布局
    CanRecyclerView  mAppsRecyclerView = null;
    MyAppsRvAdapter mMyAppsRvAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        Log.i("MyShowingList------DATA","yShowingList------DATA "+ mShowList.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_myapps,container,false);
        mAppsRecyclerView = (CanRecyclerView) view .findViewById(R.id.cr_myapps);
        mAppsRecyclerView.setLayoutManager(new CanRecyclerView.LayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false));
        mMyAppsRvAdapter = new MyAppsRvAdapter(mShowList);
        mAppsRecyclerView.setAdapter(mMyAppsRvAdapter);
        mMyAppsRvAdapter.setItemKeyEventListener(new MyAppsItemKeyEventListener());
        mMyAppsRvAdapter.setOnItemClickListener(new MyAppsOnClickListenrer());
        return view;
    }
   private class MyAppsItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener{

       @Override
       public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
           if(keyCode == KeyEvent.KEYCODE_MENU){
               //菜单键
               ToastUtil.toastShort("菜单键---"+ mShowList.get(position).toString());
           }

           return false;
       }
   }

    private class MyAppsOnClickListenrer implements CanRecyclerViewAdapter.OnItemClickListener{

        @Override
        public void onClick(View view, int position, Object data) {
            ToastUtil.toastShort("打开---"+mShowList.get(position).appName);
        }
    }


}
