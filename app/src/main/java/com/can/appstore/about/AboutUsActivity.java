package com.can.appstore.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;

import cn.can.tvlib.utils.PackageUtil;

/**
 * Created by zhangbingyuan on 2016/12/19.
 * <p>
 * 关于我们
 */

public class AboutUsActivity extends BaseActivity {

    private TextView mVerionNameTv;
    private TextView mEmailTv;
    private TextView mPhoneTv;

    private String mCustomServiceEmail = "kefu@cantv.cn";
    private String mCustomServicePhone = "400-0809880";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mVerionNameTv = (TextView) findViewById(R.id.tv_version_name);
        mEmailTv = (TextView) findViewById(R.id.tv_email);
        mPhoneTv = (TextView) findViewById(R.id.tv_phone);

        String version = PackageUtil.getMyVersionName(this);
        mVerionNameTv.setText(String.format(getString(R.string.current_app_version_template), version));

        mEmailTv.setText(String.format(getString(R.string.email_template), mCustomServiceEmail));
        mPhoneTv.setText(String.format(getString(R.string.phone_template), mCustomServicePhone));
    }


    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, AboutUsActivity.class));
    }
}
