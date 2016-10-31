package com.can.appstore.myapps.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;

/**
 * Created by wei on 2016/10/26.
 */

public class AddAppsActivity extends Activity {
    MyAppsListDataUtil mMyAppListData;
    List<AppInfo> isShown;
    List<AppInfo> addShowList;
    private List<AppInfo> mAllAppList;

    Button addBut;
    TextView  tv_select;
    TextView  tv_canSelect;
    TextView  tv_curRows;
    TextView  tv_totalRows;
    CanRecyclerView mAddRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_addapps);

        initData();
    }

    private void initData() {
        mMyAppListData = new MyAppsListDataUtil(this);
        mAllAppList = mMyAppListData.getAllAppList();
        isShown = mMyAppListData.getShowList();
        for (AppInfo  app:mAllAppList) {
            if(! isShown.contains(app)){
                addShowList.add(app);
            }
        }
    }


}