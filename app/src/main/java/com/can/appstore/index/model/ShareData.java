package com.can.appstore.index.model;

import android.util.Log;

import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by liuhao on 2016/11/21.
 */

public class ShareData {

    private ShareData() {
    }

    private static ShareData shareData;

    public static ShareData getInstance() {
        if (null == shareData) {
            shareData = new ShareData();
        }
        return shareData;
    }

    private CanCall<ListResult<String>> mHiddenApps;
    private ListResult<String> hiddenList = null;

    public void execute() {
        mHiddenApps = HttpManager.getApiService().getHiddenApps();
        mHiddenApps.enqueue(new CanCallback<ListResult<String>>() {
            @Override
            public void onResponse(CanCall<ListResult<String>> call, Response<ListResult<String>> response) throws Exception {
                hiddenList = response.body();
            }

            @Override
            public void onFailure(CanCall<ListResult<String>> call, CanErrorWrapper errorWrapper) {
                Log.i("DataUtils", errorWrapper.getReason() + " || " + errorWrapper.getThrowable());
            }
        });
    }

    public List<String> getHiddenApps(List<String> list) {
        if (list == null) {
            list = new ArrayList<String>();
            execute();
        } else {
            list.clear();
        }
        list = hiddenList.getData();
//        list.add("com.cantv.media");
        return list;
    }

}
