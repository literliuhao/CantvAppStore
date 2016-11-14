package com.can.appstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.can.appstore.active.ActiveActivity;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.special_detail.SpecialDetailActivity;
import com.can.appstore.widgets.CanDialog;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActiveActivity.class));
//                SearchActivity.startAc(MainActivity.this);
//                final CanDialog canDialog = new CanDialog(MainActivity.this).setmIvDialogTitle(R.mipmap.ic_launcher)
//                .setmTvDialogTitle("QQ音乐").setmBtnDialogPositive("安装").setmBtnDialogNegative("卸载");
//                canDialog.setOnCanBtnClickListener(new CanDialog.OnCanBtnClickListener() {
//                    @Override
//                    public void onClickPositive() {
//                        ToastUtil.toastShort("点击了 Positive 按钮");
//                    }
//
//                    @Override
//                    public void onClickNegative() {
//                        ToastUtil.toastShort("点击了 Negative 按钮");
//                    }
//                }).show();
//
//                CanDialog.OnCanBtnClickListener onCanBtnClickListener = new CanDialog.OnCanBtnClickListener() {
//                    @Override
//                    public void onClickPositive() {
//                        ToastUtil.toastShort("点击了 Positive 按钮");
//                    }
//
//                    @Override
//                    public void onClickNegative() {
//                        ToastUtil.toastShort("点击了 Negative 按钮");
//                    }
//                };
//                canDialog.showDialogForInstallAPP(R.mipmap.ic_launcher,"QQ音乐","安装","删除",onCanBtnClickListener);
//                canDialog.showDialogForUninstallAPP(R.mipmap.ic_launcher,"QQ音乐","确定卸载这个应用吗？","确定","取消",
//                        onCanBtnClickListener);
//                canDialog.showDialogForUpdateSetting("更新设置","自动更新","已开启","应用更新开启时，自动开始下载应用","开启","关闭",
//                        onCanBtnClickListener);
//                canDialog.show();
            }
        });

        findViewById(R.id.bt_specail_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    SpecialDetailActivity.startAc(MainActivity.this);
            }
        });
    }
}
