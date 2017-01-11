//package com.can.appstore;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.can.appstore.active.ActiveActivity;
//import com.can.appstore.appdetail.AppDetailActivity;
//import com.can.appstore.applist.AppListActivity;
//import com.can.appstore.download.DownloadLeadAcitivity;
//import com.can.appstore.message.MessageActivity;
//import com.can.appstore.specialdetail.SpecialDetailActivity;
//import com.can.appstore.uninstallmanager.UninstallManagerActivity;
//import com.can.appstore.upgrade.InstallApkListener;
//import com.can.appstore.upgrade.UpgradeUtil;
//import com.can.appstore.upgrade.activity.UpgradeInfoActivity;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.can.tvlib.ui.focus.FocusMoveUtil;
//import cn.can.tvlib.ui.focus.FocusScaleUtil;
//import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
//import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
//
///**
// * Created by syl on 2016/11/3.
// * 应用入口  item点击事件中添加自己的跳转
// */
//
//public class PortalActivity extends Activity {
//    private static final String TAG = "PortalActivity";
//
//    private RecyclerView mRecyclerView;
//    private HomeAdapter adapter;
//    private FocusMoveUtil mFocusMoveUtil;
//    private FocusScaleUtil mFocusScaleUtil;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_test);
//        initView();
//    }
//
//    private void initView() {
//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_test);
//        List list = new ArrayList();
//        for (int i = 0; i < 1000; i++) {
//            // TODO: 2016/11/4  添加跳转页面名称
//            if (i == 0) {
//                list.add("应用列表页面");
//            } else if (i == 1) {
//                list.add("下载列表和专题列表");
//            } else if (i == 2) {
//                list.add("应用详情1");
//            } else if (i == 3) {
//                list.add("应用详情2");
//            } else if (i == 4) {
//                list.add("卸载管理");
//            } else if (i == 5) {
//                list.add("应用详情3");
//            } else if (i == 6) {
//                list.add("消息中心");
//            } else if (i == 7) {
//                list.add("首页");
//            } else if (i == 8) {
//                list.add("排行榜列表");
//            } else if (i == 9) {
//                list.add("专题详情页");
//            } else if (i == 10) {
//                list.add("活动详情页");
//            } else if (i == 11) {
//                list.add("install");
//            } else {
//                list.add(i + "");
//            }
//        }
//
//        //设置布局管理器
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        //设置adapter
//        adapter = new HomeAdapter(list);
//        mRecyclerView.setAdapter(adapter);
//        //        mRecyclerView.setAdapter(new HomeAdapter());
//        //添加分割线
//        mRecyclerView.addItemDecoration(new CanRecyclerViewDivider(100));
//
//
//        adapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(View view, int position, Object data) {
//                Log.d("", "mRecyclerView" + position);
//                // TODO: 2016/11/4  添加跳转页面点击事件
//                if (position == 0) {
//                    Intent intent = new Intent(PortalActivity.this, UpgradeInfoActivity.class);
//                    startActivity(intent);
//                } else if (position == 1) {
//                    Intent intent = new Intent(PortalActivity.this, DownloadLeadAcitivity.class);
//                    startActivity(intent);
//                } else if (position == 2) {
//                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
//                    intent.putExtra("appID", "1");
//                    startActivity(intent);
//                } else if (position == 3) {
//                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
//                    intent.putExtra("appID", "2");
//                    startActivity(intent);
//                } else if (position == 4) {
//                    Intent intent = new Intent(PortalActivity.this, UninstallManagerActivity.class);
//                    startActivity(intent);
//                } else if (position == 5) {
//                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
//                    intent.putExtra("appID", "3");
//                    startActivity(intent);
//                } else if (position == 6) {
//                    Intent intent = new Intent(PortalActivity.this, MessageActivity.class);
//                    startActivity(intent);
//                } else if (position == 7) {
//                    Intent intent = new Intent(PortalActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else if (position == 8) {
//                    Intent intent = new Intent(PortalActivity.this, AppListActivity.class);
//                    intent.putExtra(AppListActivity.ENTRY_KEY_SRC_TYPE, AppListActivity.PAGE_TYPE_RANKING);
//                    intent.putExtra(AppListActivity.ENTRY_KEY_TOPIC_ID, "15");
//                    intent.putExtra(AppListActivity.ENTRY_KEY_TYPE_ID, "");
//                    PortalActivity.this.startActivity(intent);
//                } else if (position == 9) {
//                    SpecialDetailActivity.actionStart(PortalActivity.this, "");
//                } else if (position == 10) {
//                    ActiveActivity.actionStart(PortalActivity.this, "");
//                } else if (position == 11) {
//                    initService();
//                }
//            }
//        });
//
//
//        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android
//                .R.id.content), R.mipmap.btn_focus);
//        mFocusScaleUtil = new FocusScaleUtil();
//
//        mRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("", "mRecyclerView" + "===postDelayed");
//                mRecyclerView.getChildAt(0).requestFocus();
//            }
//        }, 50);
//
//        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d("", "mRecyclerView==setOnFocusChangeListener=" + hasFocus);
//            }
//        });
//
//        adapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
//            @Override
//            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
//                Log.d("", "mRecyclerView" + position);
//                if (hasFocus) {
//                    mFocusMoveUtil.startMoveFocus(view, 1.0f);
//                    mFocusScaleUtil.scaleToLarge(view, 1.0f, 1.0f);
//                } else {
//                    mFocusScaleUtil.scaleToNormal(view);
//                }
//            }
//        });
//    }
//
//    private void initService() {
//        String path1 = Environment.getExternalStorageDirectory() + File.separator
//                +"install";
//        String path = Environment.getExternalStorageDirectory() + File.separator
//                +"install/service.apk";
//        if (!UpgradeUtil.isFileExist(path1)) {
//            UpgradeUtil.creatDir(path1);
//        }
//        UpgradeUtil.delAllDateFile(path1);
//        //写入apk到sd卡
//        boolean a = copyApkFromAssets(this, "service.apk", path);
//        Log.d(TAG, "initService: "+a);
//        //启动activity
//        Intent intent = new Intent("com.zby.uphelp.MService");
//        startService(intent);
//        //安装apk
//        UpgradeUtil.installApk(this,path,0, new InstallApkListener() {
//            @Override
//            public void onInstallSuccess() {
//                Log.d(TAG, "onInstallSuccess: ");
//            }
//
//            @Override
//            public void onInstallFail(String reason) {
//                Log.d(TAG, "onInstallFail: "+reason);
//            }
//        });
//
//    }
//
//
//    public boolean copyApkFromAssets(Context context, String fileName, String path) {
//        boolean copyIsFinish = false;
//        try {
//            Log.d(TAG, "initService: path="+path);
//
//            InputStream is = context.getAssets().open(fileName);
//            File file = new File(path);
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            byte[] temp = new byte[1024];
//            int i = 0;
//            while ((i = is.read(temp)) > 0) {
//                fos.write(temp, 0, i);
//            }
//            fos.close();
//            is.close();
//            copyIsFinish = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return copyIsFinish;
//    }
//
//
//
//    //        class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
//    //
//    //            @Override
//    //            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//    //                View view = LayoutInflater.from(PortalActivity.this).inflate(R.layout.layout_item,parent, false);
//    //                view.setFocusable(true);
//    //                MyViewHolder holder = new MyViewHolder(view);
//    //                Log.d("","ssssssssssssss");
//    //                return holder;
//    //            }
//    //
//    //            @Override
//    //            public void onBindViewHolder(MyViewHolder holder, int position) {
//    //                holder.tv.setText(position+"");
//    //            }
//    //
//    //            @Override
//    //            public int getItemCount() {
//    //                return 1000;
//    //            }
//    //
//    //            class MyViewHolder extends RecyclerView.ViewHolder {
//    //
//    //                TextView tv;
//    //
//    //                public MyViewHolder(View view) {
//    //                    super(view);
//    //                    tv = (TextView) view.findViewById(R.id.tv_test);
//    //                }
//    //            }
//    //        }
//
//
//    class HomeAdapter extends CanRecyclerViewAdapter {
//
//        public HomeAdapter(List datas) {
//            super(datas);
//        }
//
//        @Override
//        protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(PortalActivity.this).inflate(R.layout.layout_item, parent, false);
//            view.setFocusable(true);
//            MyViewHolder holder = new MyViewHolder(view);
//            return holder;
//        }
//
//        @Override
//        protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
//            String s = (String) mDatas;
//            MyViewHolder ho = (MyViewHolder) holder;
//            ho.tv.setText(s);
//        }
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tv;
//
//        public MyViewHolder(View view) {
//            super(view);
//            tv = (TextView) view.findViewById(R.id.tv_test);
//        }
//    }
//}
