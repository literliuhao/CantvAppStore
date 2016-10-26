package com.can.appstore.special_detail.bean;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.ApkScanSDUtils;
import cn.can.tvlib.utils.StringUtils;

/**
 * Created by atang on 2016/10/24.
 */

public class AppDetailUtils {
    public static List<AppDetail> getAppData() {
        List<AppDetail> appDetails = new ArrayList<AppDetail>();
            AppDetail app1 = new AppDetail("1","McLaughlin","http://img2.imgtn.bdimg.com/it/u=1092350862,3528911334&fm=21&gp=0.jpg");
            AppDetail app2 = new AppDetail("2","McLaughlin1","http://img2.imgtn.bdimg.com/it/u=4107053065,2431804237&fm=21&gp=0.jpg");
            AppDetail app3 = new AppDetail("3","McLaughlin2","http://img2.imgtn.bdimg.com/it/u=2629746232,1040656054&fm=21&gp=0.jpg");
            AppDetail app4 = new AppDetail("4","McLaughlin3","http://img2.imgtn.bdimg.com/it/u=524740059,3140668033&fm=21&gp=0.jpg");
            AppDetail app5 = new AppDetail("5","McLaughlin4","http://img2.imgtn.bdimg.com/it/u=294622120,1627056179&fm=21&gp=0.jpg");
            AppDetail app6 = new AppDetail("6","McLaughlin5","http://img2.imgtn.bdimg.com/it/u=934791135,1873253725&fm=21&gp=0.jpg");
            AppDetail app7 = new AppDetail("7","McLaughlin6","http://img2.imgtn.bdimg.com/it/u=636773050,3957542872&fm=21&gp=0.jpg");
            AppDetail app8 = new AppDetail("8","McLaughlin7","http://img2.imgtn.bdimg.com/it/u=2919838172,3455802268&fm=21&gp=0.jpg");

            appDetails.add(app1);
            appDetails.add(app2);
            appDetails.add(app3);
            appDetails.add(app4);
            appDetails.add(app5);
            appDetails.add(app6);
            appDetails.add(app7);
            appDetails.add(app8);
        return appDetails;
    }

    public void getLocalApp(Context context, File file) {
        ApkScanSDUtils apkScanSDUtils = new ApkScanSDUtils(context);
        apkScanSDUtils.FindAllAPKFile(file);
        apkScanSDUtils.getMyFiles();
    }

}