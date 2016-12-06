package com.can.appstore.index.model;

import android.content.Context;
import android.util.Log;

import com.can.appstore.entity.Layout;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;
import com.dataeye.sdk.api.app.channel.DCResourceLocation;
import com.dataeye.sdk.api.app.channel.DCResourcePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JasonF on 2016/12/5.
 */

public class HomeDataEyeUtils {
    private static final String TAG = "HomeDataEyeUtils";
    private ArrayList<DCResourcePair> mPairs = new ArrayList<>();
    private HashMap<Integer, ArrayList<DCResourcePair>> mPairsMap = new HashMap<>();
    private Context mContext;

    public HomeDataEyeUtils(Context context) {
        mContext = context;
    }

    //统计首页资源位的曝光量
    public void resourcesPositionExposure(int position) {
        Log.d(TAG, "resourcesPositionExposure: position : " + position);
        ListResult<Navigation> indexData = DataUtils.getInstance(mContext).getIndexData();
        List<Navigation> data = indexData.getData();
        if (position > data.size() || position == 1) {
            return;
        }
        if (position > 0) {
            position = position - 1;
        }
        if (indexData != null && data != null) {
            ArrayList<DCResourcePair> resourcePairs = mPairsMap.get(position);
            if (resourcePairs != null && resourcePairs.size() > 0) {
                Log.d(TAG, "resourcesPositionExposure: " + position + " resourcePairs" + resourcePairs.size() +
                        " mPairsMap : " + mPairsMap.size());
                DCResourceLocation.onBatchShow(resourcePairs);
                return;
            } else {
                for (int i = 0; i < data.size(); i++) {
                    Navigation navigation = data.get(position);
                    List<Layout> layout = navigation.getLayout();
                    mPairs.clear();
                    for (int j = 0; j < layout.size(); j++) {
                        String resourcesPositionID = navigation.getTitle() + "-" + layout.get(j).getLocation();
                        DCResourcePair pair = DCResourcePair.newBuilder().setResourceLocationId(resourcesPositionID).build();
                        mPairs.add(pair);
                    }
                    mPairsMap.put(position, mPairs);
                    DCResourceLocation.onBatchShow(mPairs);
                }
            }
        }
    }

    public void release() {
        if (mPairs != null) {
            mPairs.clear();
            mPairs = null;
        }
        if (mPairsMap != null) {
            mPairsMap.clear();
            mPairsMap = null;
        }
    }
}
