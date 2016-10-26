package com.can.appstore.special_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.can.appstore.R;
import com.can.appstore.special_detail.adapter.SpecialDetailAdapter;
import com.can.appstore.special_detail.bean.AppDetail;
import com.can.appstore.special_detail.bean.AppDetailUtils;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;

public class SpecialDetailActivity extends AppCompatActivity {

    public static void startAc(Context context) {
        Intent intent = new Intent(context, SpecialDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        CanRecyclerView canRecyclerView = (CanRecyclerView) findViewById(R.id.special_detail_crview);
        setData(canRecyclerView);
    }

    private void setData(CanRecyclerView canRecyclerView) {
        List<AppDetail> appDetails = AppDetailUtils.getAppData();
        if (appDetails == null || appDetails.size() == 0) {
            return;
        }

        SpecialDetailAdapter adapter = new SpecialDetailAdapter(appDetails, this);
        canRecyclerView.setAdapter(adapter);

        CanRecyclerView.LayoutManager layoutManager = new CanRecyclerView.LayoutManager(this, 1, CanRecyclerView.LayoutManager.HORIZONTAL, false);
        canRecyclerView.setLayoutManager(layoutManager);
    }
}
