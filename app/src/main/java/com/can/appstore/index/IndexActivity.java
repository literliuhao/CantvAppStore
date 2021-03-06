package com.can.appstore.index;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.Ad;
import com.can.appstore.entity.AdReportParam;
import com.can.appstore.entity.ClassicResult;
import com.can.appstore.entity.CommonAdParam;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;
import com.can.appstore.entity.TvInfoModel;
import com.can.appstore.homerank.HomeRankFragment;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.index.adapter.IndexPagerAdapter;
import com.can.appstore.index.entity.FragmentEnum;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.interfaces.IOnPagerListener;
import com.can.appstore.index.model.ActionUtils;
import com.can.appstore.index.model.DataUtils;
import com.can.appstore.index.model.HomeDataEyeUtils;
import com.can.appstore.index.model.ShareData;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FixedScroller;
import com.can.appstore.index.ui.FragmentBody;
import com.can.appstore.index.ui.LiteText;
import com.can.appstore.index.ui.ManagerFragment;
import com.can.appstore.index.ui.TitleBar;
import com.can.appstore.message.MessageActivity;
import com.can.appstore.message.manager.MessageDBManager;
import com.can.appstore.myapps.ui.MyAppsFragment;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.update.AutoUpdate;
import com.can.appstore.update.UpdateManagerActivity;
import com.can.appstore.update.model.UpdateApkModel;
import com.can.appstore.upgrade.service.UpgradeService;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.can.appstore.widgets.CanDialog;
import com.dataeye.sdk.api.app.DCAgent;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.NetworkUtils;
import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.PromptUtils;
import cn.can.tvlib.ui.ToastUtils;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import retrofit2.Response;

import static com.can.appstore.index.entity.FragmentEnum.INDEX;
import static com.can.appstore.index.entity.FragmentEnum.NORMAL;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends BaseActivity implements IAddFocusListener, View.OnClickListener, View.OnFocusChangeListener, IOnPagerKeyListener, IOnPagerListener, View.OnKeyListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "IndexActivity";
    private CanCall<ListResult<Navigation>> mNavigationCall;
    private CanCall<ClassicResult<List<Ad>>> mAdCall;
    private List<BaseFragment> mFragmentLists;
    private FocusScaleUtil mFocusScaleUtils;
    private IndexPagerAdapter mAdapter;
    private FocusMoveUtil mFocusUtils;
    private RelativeLayout rlMessage,mAdContentLayoutGroup;
    private TextView textTime;
    private ImageView imageAD;
    private LinearLayout adTimeLayout;
    private RelativeLayout rlSearch;
    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private ImageView imageRed;
    private TextView textUpdate;
    private ManagerFragment managerFragment;
    private final int DURATION_LARGE = 300;
    private final int DURATION_SMALL = 300;
    private final int INIT_FOCUS = 0X000001;
    private final int HIDE_FOCUS = 0X000002;
    private final int INIT_VIEW = 0x6;
    private final int MSG_SHOW_LOADING=0x3;
    private final int MSG_HIDE_LOADING=0x4;
    private final int SCREEN_PAGE_LIMIT = 5;
    private final int PAGER_CURRENT = 0;
    private final int TOP_INDEX = 1;
    private final int DELAYED = 200;
    private final int SCROLLING = 2;
    private final float SCALE = 1.1f;
    private final int SCROLLED = 0;
    private int mCurrentPage;
    private Context mContext;
    private CanDialog canDialog;
    private Boolean isIntercept = false;
    private MessageDBManager messageDBManager;
    private HomeDataEyeUtils mDataEyeUtils;
    private Boolean isShowAD = false;
    private String materialId = null;
    private int mDefaultTime = 5;
    private int mShowTime = 5;
    private int mClickCount = 0;
    private long mEnter = 0;
    private int mUpdateNum;
    private String mAdtfid;
    private Timer mTimer;
    private final int AD_INDEX = 0;

    private final int AD_SHOW_STEP = 1000;
    private final String AD_POSITION_ID = "adyyscqd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setStyle();
        initView();
        initFocus();
        getAD();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEnter = System.currentTimeMillis();
        DCAgent.resume(getApplicationContext());
        DCPage.onEntry(AppConstants.HOME_PAGE);
        DCEvent.onEvent(AppConstants.HOME_PAGE);
        refreshMsg();
    }

    /**
     * 当前Activity样式及载入的布局
     */
    private void setStyle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index);
    }

    /**
     * 首页初始化所有View
     */
    private void initView() {
        mContext = IndexActivity.this;
        //导航
        mTitleBar = (TitleBar) findViewById(R.id.id_indicator);
        mTitleBar.initTitle(this);
        mTitleBar.setOnItemKeyEvent(new TitleBar.OnItemKeyEvent() {
            @Override
            public boolean onKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT &&
                        position == mTitleBar.getChildCount() - 1) {
                    rlSearch.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.id_custom_pager);
        //广告
        textTime = (TextView) this.findViewById(R.id.tv_ad_time);
        imageAD = (ImageView) this.findViewById(R.id.iv_index_ad);
        adTimeLayout = (LinearLayout) findViewById(R.id.ll_ad_time);
        mAdContentLayoutGroup= (RelativeLayout) findViewById(R.id.rl_content_ad);
        //搜索
        rlSearch = (RelativeLayout) this.findViewById(R.id.rl_search);
        rlSearch.setOnFocusChangeListener(this);
        rlSearch.setOnClickListener(this);
        rlSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    View titleChildView = mTitleBar.getChildAt(mTitleBar.getChildCount() - 1);
                    if (titleChildView != null) {
                        titleChildView.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });
        //消息
        rlMessage = (RelativeLayout) this.findViewById(R.id.rl_message);
        rlMessage.setOnFocusChangeListener(this);
        rlMessage.setOnClickListener(this);
        //消息提示
        imageRed = (ImageView) this.findViewById(R.id.iv_mssage_red);
        //更新
        textUpdate = (TextView) findViewById(R.id.tv_update_number);
        textUpdate.setOnFocusChangeListener(this);
        textUpdate.setOnClickListener(this);
        messageDBManager = new MessageDBManager(this);
    }

    /**
     * 首页焦点初始化，并且在IndexActivity做统一处理
     */
    private void initFocus() {
        mFocusUtils = new FocusMoveUtil(this, R.drawable.btn_focus, getWindow().getDecorView(), true, true, 2);
        mFocusScaleUtils = new FocusScaleUtil(DURATION_LARGE, DURATION_SMALL, SCALE, null, null);
    }

    /**
     * 没网情况跳过广告 ok
     * 有网能取到数据，展示广告，倒计时默认为5秒，以后台为准，右上角显示倒计时； ok
     * 载入图片过程较长时提前加载默认图片； ok
     * 广告展示过程中可退出应用；ok
     * 广告无Action时，点击确定无效，但能退出应用；ok
     */
    private void getAD() {
        textTime.setText(String.valueOf(mShowTime));
        mTimer = new Timer();
        mTimer.schedule(task, AD_SHOW_STEP, AD_SHOW_STEP);

        CommonAdParam commonAdParam = new CommonAdParam();
        commonAdParam.setAdPositionId(AD_POSITION_ID);
        commonAdParam.setMac(NetworkUtils.getMac());
        commonAdParam.setVersionId(PackageUtil.getMyVersionName(mContext));
        mAdCall = HttpManager.getAdService().getCommonAd(commonAdParam.toMap());
        mAdCall.enqueue(new CanCallback<ClassicResult<List<Ad>>>() {
            @Override
            public void onResponse(CanCall<ClassicResult<List<Ad>>> call, Response<ClassicResult<List<Ad>>> response) throws Exception {
                if (null != response) {
                    ClassicResult<List<Ad>> listResult = response.body();
                    List<Ad> listAD = listResult.getData();
                    mAdtfid = listAD.get(AD_INDEX).getAdtfid();
                    List<Ad.Material> listMaterial = listAD.get(AD_INDEX).getMaterial();
                    final Ad.Material material = listMaterial.get(AD_INDEX);
                    materialId = material.getMaterialid();

                    isShowAD = true;
                    imageAD.setFocusable(true);
                    imageAD.requestFocus();
                    imageAD.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (keyEvent.ACTION_DOWN == keyEvent.getAction() && (keyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() || keyEvent.KEYCODE_DPAD_CENTER == keyEvent.getKeyCode())) {
                                mClickCount = 1;
                                isShowAD = false;
                                String action = material.getAction();
                                if (action.equals("") || null == action) return true;
                                JsonObject jsonObject = material.getActionParam();
                                JsonElement jsonElement = jsonObject.get("parameters");
                                try {
                                    JSONObject jsonParams = new JSONObject(new Gson().toJson(jsonElement));
                                    ActionUtils.getInstance().sendActionById(mContext, jsonParams.optString("appid"), jsonParams.optString("topicid"), jsonParams.optString("applist"), jsonParams.optString("activityid"), jsonParams.optString("topiclist"));
                                    mShowTime=0;
                                    mHandler.sendEmptyMessageDelayed(INIT_FOCUS, DELAYED);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }
                    });
                    ImageLoader.getInstance().buildTask(imageAD, material.getMaterialurl()).placeholder(R.drawable.app_store).successCallback(new GlideLoadTask.SuccessCallback() {

                        @Override
                        public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (imageAD.getVisibility() != View.VISIBLE) {
                                return true;
                            }
                            imageAD.setImageDrawable(resource);
                            return true;
                        }
                    }).failCallback(new GlideLoadTask.FailCallback() {
                        @Override
                        public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }
                    }).build().start(mContext);
                }
            }

            @Override
            public void onFailure(CanCall<ClassicResult<List<Ad>>> call, CanErrorWrapper errorWrapper) {
                Log.i(TAG, errorWrapper.getReason() + " || " + errorWrapper.getThrowable());
            }
        });
    }

    private String getUserId() {
        String userid = "";
        Cursor cursor = getContentResolver().query(Uri.parse("content://cn.cibntv.ott.custom.provider/user"), null, null, null, null);
        if (cursor == null) {
            return userid;
        }
        while (cursor.moveToNext()) {
            userid = cursor.getString(cursor.getColumnIndex("userid"));
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return userid;
    }

    public void reportAD() {
        AdReportParam adReportParam = new AdReportParam();
        adReportParam.setAdPositionId(AD_POSITION_ID);
        adReportParam.setAdtfId(mAdtfid);
        adReportParam.setUserId(getUserId());
        adReportParam.setMac(NetworkUtils.getMac());
        adReportParam.setModel(TvInfoModel.getInstance().getModelName());
        adReportParam.setChannel(TvInfoModel.getInstance().getChannelId() + "|");
        adReportParam.setVersionId(PackageUtil.getMyVersionName(mContext));
        adReportParam.setUserAction(mClickCount);
        adReportParam.setMaterialId(materialId);
        adReportParam.setDuration(mDefaultTime);
        adReportParam.setImpressions(1);

        CanCall<ClassicResult> reportCall = HttpManager.getAdService().report(adReportParam);
        reportCall.enqueue(new CanCallback<ClassicResult>() {
            @Override
            public void onResponse(CanCall<ClassicResult> call, Response<ClassicResult> response) throws Exception {
            }

            @Override
            public void onFailure(CanCall<ClassicResult> call, CanErrorWrapper errorWrapper) {
                Log.i("IndexActivity", "onFailure " + errorWrapper.getThrowable() + errorWrapper.getReason());
            }
        });

    }

    private boolean reqNavigationSuccess;

    public void getNavigation() {
        if (reqNavigationSuccess == false && NetworkUtils.isNetworkConnected(this)) {
            mHandler.sendEmptyMessage(MSG_SHOW_LOADING);
            mNavigationCall = HttpManager.getApiService().getNavigations();
            mNavigationCall.enqueue(new CanCallback<ListResult<Navigation>>() {
                @Override
                public void onResponse(CanCall<ListResult<Navigation>> call, Response<ListResult<Navigation>> response) throws Exception {
                    ProxyCache(response);
                    if(mHandler!=null){
                        mHandler.sendEmptyMessage(MSG_HIDE_LOADING);
                    }
                }

                @Override
                public void onFailure(CanCall<ListResult<Navigation>> call, CanErrorWrapper errorWrapper) {
                    String msg = errorWrapper.getReason();
                    Log.i(TAG, msg + " || " + errorWrapper.getThrowable());
                    if (msg != null && msg.contains("域名解析错误")) {
                        ToastUtils.showMessageLong(getContext(), "域名解析错误");
                    }
                    ProxyCache(null);
                    if(mHandler!=null){
                        mHandler.sendEmptyMessage(MSG_HIDE_LOADING);
                    }
                }
            });
        } else {
            ProxyCache(null);
        }
    }

    private void ProxyCache(Response<ListResult<Navigation>> response) {
        try {
            //JSON不完整或错误会出现异常
            ListResult<Navigation> listResult;
            if (null != response) {
                listResult = response.body();
                if(listResult.getData()==null){
                    listResult = new Gson().fromJson(DataUtils.getInstance(this).getCache(), new TypeToken<ListResult<Navigation>>() {
                    }.getType());
                }
            } else {
                listResult = new Gson().fromJson(DataUtils.getInstance(this).getCache(), new TypeToken<ListResult<Navigation>>() {
                }.getType());
            }
            DataUtils.getInstance(this).setIndexData(listResult);
            parseData(listResult);
            reqNavigationSuccess = true;
        } catch (Exception e) {
            PromptUtils.toast(this, getResources().getString(R.string.index_data_error));
            DataUtils.getInstance(this).clearData();
            stopTimer();
            e.printStackTrace();
        }
    }

    private void parseData(ListResult<Navigation> listResult) {
        if (null != listResult.getData()) {
            initData(listResult);
            bindData(listResult);
        }
    }

    /**
     * 首页数据初始化
     */
    private void initData(ListResult<Navigation> navigationListResult) {
        mFragmentLists = new ArrayList<>();
        List<Navigation> navigationList = navigationListResult.getData();
        if (null == navigationList) return;
        //根据服务器配置文件生成不同样式加入Fragment列表中
        FragmentBody fragment;
        Boolean rankVisibility = false;
        for (int i = 0; i < navigationList.size(); i++) {
            //因为后台没有及时添加更多字段，暂时以字符串判断。
            if (navigationList.get(i).getTitle().equals("排行")) {
                rankVisibility = true;
                continue;
            }
            fragment = FragmentBody.newInstance(this, navigationList.get(i));
            if (i == 0) {
                fragment.markOnKeyListener(false);
            }
            mFragmentLists.add(fragment);
        }

        //排行、管理、我的应用、不受服务器后台配置，因此手动干预位置
        if (rankVisibility) {
            HomeRankFragment homeRankFragment = new HomeRankFragment(this);
            if (mFragmentLists.size() > 0) {
                mFragmentLists.add(TOP_INDEX, homeRankFragment);
            } else {
                mFragmentLists.add(homeRankFragment);
            }
        }
        managerFragment = new ManagerFragment(this);
        mFragmentLists.add(managerFragment);

        MyAppsFragment myAppsFragment = new MyAppsFragment(this);
        mFragmentLists.add(myAppsFragment);
    }

    public void notifyAddManagerFragmentFocus(){
        if(managerFragment!=null){
            managerFragment.setAdapterFocus();
        }
    }

    /**
     * 首页与TitleBar的数据绑定
     *
     * @param mPage 导航栏数据
     */
    private void bindTitle(ListResult<Navigation> mPage) {
        List<String> mTitles = new ArrayList<>();
        boolean hasRank = false;
        String rankStr = getResources().getString(R.string.rank);
        for (int i = 0; i < mPage.getData().size(); i++) {
            Navigation navigation = mPage.getData().get(i);
            String title = navigation.getTitle();
            if (rankStr.equals(title)) {
                hasRank = true;
                continue;
            }
            mTitles.add(title);
        }
        //管理、我的应用不受服务器后台配置，因此手动干预位置
        mTitles.add(getResources().getString(R.string.index_manager));
        mTitles.add(getResources().getString(R.string.index_myapp));
        if (hasRank) {
            mTitles.add(1, rankStr);
        }
        mTitleBar.setTabItemTitles(mTitles);//设置导航栏Title
    }

    /**
     * mViewPager、mTitleBar 数据绑定与页面绑定
     */
    private void bindData(ListResult<Navigation> listResult) {
        bindTitle(listResult);
        mAdapter = new IndexPagerAdapter(this.getSupportFragmentManager(), mViewPager, mFragmentLists);
        mAdapter.setOnExtraPageChangeListener(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(SCREEN_PAGE_LIMIT);
        mViewPager.setCurrentItem(PAGER_CURRENT);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.px165));
        mViewPager.addOnPageChangeListener(this);
        mTitleBar.setViewPager(mViewPager, PAGER_CURRENT);
        fixedScroll();
        if (!isShowAD) {
            mHandler.sendEmptyMessageDelayed(INIT_FOCUS, DELAYED);
        }
        loadMore();
    }

    private void fixedScroll() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(mViewPager.getContext(), sInterpolator);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMore() {
        //开始获取第三方屏蔽列表
        ShareData.getInstance().execute();
        //更新接口监听
        initUpdateListener();
        //消息接口监听
        initMsgListener();
        //统计首页资源位曝光量
        mDataEyeUtils = new HomeDataEyeUtils(this.getApplicationContext());
        mDataEyeUtils.resourcesPositionExposure(0);
        //恢复下载任务。2016-11-29 11:47:23 xzl
        DownloadManager.getInstance(this).resumeAllTasks();
        //检测更新
        Beta.checkUpgrade(false, true);
        //检测自升级是否已成功
        checkVersion();
    }

    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }

    //------------注册首页监听---------------
    private void initUpdateListener() {
        EventBus.getDefault().register(this);
        AutoUpdate.getInstance().autoUpdate(IndexActivity.this);
    }

    private void refreshUpdate(int number) {
        if (number > 0) {
            textUpdate.setText(number + getResources().getString(R.string.index_app_update));
            if (mCurrentPage == 0) {
                textUpdate.setVisibility(View.VISIBLE);
            }
        } else {
            textUpdate.setVisibility(View.GONE);
        }
    }

    private void initMsgListener() {
        messageDBManager.setOnReceivedMsgListener(new MessageDBManager.OnReceivedMsgListener() {
            @Override
            public void onReceivedMsg() {
                imageRed.setVisibility(View.VISIBLE);
            }
        });
        messageDBManager.requestMsgData(this);
    }

    private void refreshMsg() {
        if (null == messageDBManager) return;
        if (messageDBManager.existUnreadMsg()) {
            imageRed.setVisibility(View.VISIBLE);
        } else {
            imageRed.setVisibility(View.INVISIBLE);
        }
    }
    //------------注册首页监听---------------END

    private View mFocusedView;
    private float mFocusedViewScale = 1f;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            if(mFocusUtils != null){
                if(mFocusedView != null){
                    mFocusUtils.setFocusView(mFocusedView, mFocusedViewScale);
                }
                if(reqNavigationSuccess){
                    mFocusUtils.showFocus();
                }
            }
        } else {
            if(mFocusUtils != null){
                mFocusUtils.hideFocus();
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_FOCUS:
                    if (managerFragment != null) {
                        managerFragment.setAdapterFocus();
                    }
                    if (imageAD != null) {
                        imageAD.setVisibility(View.GONE);
                        mAdContentLayoutGroup.removeAllViews();
                        adTimeLayout.removeAllViews();
                        adTimeLayout.setVisibility(View.GONE);
                        Drawable drawable=imageAD.getDrawable();
                        if(drawable!=null&&drawable instanceof BitmapDrawable){
                            BitmapDrawable bitmapDrawable=(BitmapDrawable) drawable;
                            if (bitmapDrawable != null) {
                                Bitmap bm = bitmapDrawable.getBitmap();
                                if (bm != null && bm.isRecycled()) {
                                    bm.recycle();
                                    bm = null;
                                }
                            }
                        }
                        imageAD = null;
                    }

                    rlSearch.setFocusable(true);
                    rlMessage.setFocusable(true);
                    View first = mTitleBar.getFirstView();
                    if (first != null) {
                        mFocusUtils.setFocusView(first, SCALE);
                        mFocusedView = first;
                        mFocusedViewScale = 1f;
                        first.requestFocus();
                        mFocusUtils.showFocus(100);
                    }
                    reportAD();
                    break;
                case HIDE_FOCUS:
                    mFocusUtils.hideFocus();
                    break;
                case MSG_SHOW_LOADING:
                    if (!reqNavigationSuccess) {
                        if(imageAD!=null&&View.VISIBLE==imageAD.getVisibility()){
                            return;
                        }
                        if(canDialog!=null&&canDialog.isShowing()){
                            return;
                        }
                        showLoadingDialog(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showExit();
                            }
                        });
                    }
                    break;
                case MSG_HIDE_LOADING:
                    hideLoadingDialog();
                    break;
                case INIT_VIEW:
                    getNavigation();
                    break;
            }
        }
    };

    private boolean mViewPageContainFocus;

    /**
     * 自定义onFocusChange接口，首页所有Fragment实现后都可以在此做统一焦点处理
     *
     * @param v        焦点动画对象
     * @param hasFocus 是否获得焦点
     */
    @Override
    public void addFocusListener(View v, boolean hasFocus, FragmentEnum sourceEnum) {
//        Log.i(TAG, "addFocusListener: v = " + v.getClass().getSimpleName() + "| id = " + v.getId() + ", hasFocus = " + hasFocus + ", enum = " + sourceEnum.name() + ", isIntercept = " + isIntercept);
        if (hasFocus) {
            if (isIntercept) {
                isIntercept = false;
                return;
            }
            mFocusedView = v;
            mFocusedViewScale = 1f;
            if (null == v) return;
            switch (sourceEnum) {
                case INDEX:
                    v.bringToFront();
                    mViewPageContainFocus = false;
                    mFocusUtils.setFocusRes(this, R.drawable.btn_circle_focus);
                    mFocusUtils.startMoveFocus(v);
                    break;
                case TITLE:
                    if (v instanceof LiteText) {
                        v.callOnClick();
                    } else {
                        v.bringToFront();
                        mFocusScaleUtils.scaleToLarge(v);
                    }
                    mViewPageContainFocus = false;
                    mFocusUtils.setFocusRes(this, R.drawable.btn_focus);
                    mFocusUtils.startMoveFocus(v);
                    break;
                case RANK:
                    mViewPageContainFocus = true;
                    v.bringToFront();
                    mFocusUtils.setFocusRes(this, R.drawable.btn_focus);
                    mFocusUtils.startMoveFocus(v);
                    break;
                case NORMAL:
                case MYAPP:
                case MANAGE:
                    mViewPageContainFocus = true;
                    v.bringToFront();
                    mFocusUtils.setFocusRes(this, R.drawable.btn_focus);
                    mFocusedViewScale = SCALE;
                    mFocusUtils.startMoveFocus(v, SCALE);
                    mFocusScaleUtils.scaleToLarge(v);
                    break;
            }
        } else {
            mFocusScaleUtils.scaleToNormal();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                SearchActivity.startAc(this);
                break;
            case R.id.rl_message:
                MessageActivity.actionStart(this);
                break;
            case R.id.tv_update_number:
                UpdateManagerActivity.actionStart(this);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateApkModel model) {
        mUpdateNum = model.getNumber();
        refreshUpdate(mUpdateNum);
    }

    public static void actionStart(Context context, String topicId) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view.getId() == textUpdate.getId()) {
            addFocusListener(view, hasFocus, NORMAL);
        } else {
            addFocusListener(view, hasFocus, INDEX);
        }
    }

    /**
     * 检测是否是刚升级的新版本,新版本弹出升级信息对话框
     */
    private void checkVersion() {
        SharedPreferences sp = getSharedPreferences(UpgradeService.UPGRADE_INFO, Activity.MODE_PRIVATE);
        String info = sp.getString(UpgradeService.UPGRADE_INFO, UpgradeService.NO_UPGRADE_INFO);
        if (UpgradeService.NO_UPGRADE_INFO.equals(info)) {
            Log.d("", "checkVersion: " + info);
        } else {
            try {
                UpgradeInfo upgradeInfo = new Gson().fromJson(info, UpgradeInfo.class);
                int localVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
                Log.d("", "LocalVersionCode=" + localVersion + ",UpGradeVersionCode=" + upgradeInfo.versionCode);
                if (localVersion == upgradeInfo.versionCode) {
                    UpgradeInFoDialog dialog = new UpgradeInFoDialog(IndexActivity.this, getResources().getString(R.string.last_version), upgradeInfo.versionName, upgradeInfo.newFeature, getResources().getString(R.string.ok), false);
                    dialog.show();
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (mShowTime == 0) {
                stopTimer();
                mHandler.sendEmptyMessage(INIT_FOCUS);
                mHandler.sendEmptyMessage(INIT_VIEW);
                isShowAD = false;
                return;
            }

            mShowTime--;
            runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    textTime.setText(String.valueOf(mShowTime));
                    if (adTimeLayout.getVisibility() != View.VISIBLE) {
                        adTimeLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };

    private void stopTimer() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        if (null != task) {
            task.cancel();
        }
    }

    @Override
    protected void onPause() {
        DCAgent.pause(this.getApplicationContext());
        DCPage.onExit(AppConstants.HOME_PAGE);//统计页面结束
        DCEvent.onEventDuration(AppConstants.HOME_PAGE, (System.currentTimeMillis() - mEnter) / 1000);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mAdCall!=null){
            mAdCall.cancel();
        }
        if(mNavigationCall!=null){
            mNavigationCall.cancel();
        }
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        if (canDialog != null) {
            canDialog.dismiss();
            canDialog.release();
            canDialog = null;
        }
        stopTimer();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (messageDBManager != null) {
            messageDBManager.removeOnReceivedMsgListener();
            messageDBManager = null;
        }
        if (mDataEyeUtils != null) {
            mDataEyeUtils.release();
            mDataEyeUtils = null;
        }
        if (mFocusUtils != null) {
            mFocusUtils.release();
            mFocusUtils = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExit();
    }

    private void showExit() {
        mFocusUtils.hideFocus();  //  隐藏焦点框
        if (null == canDialog) {
            canDialog = new CanDialog(this);
            canDialog.setTitleToBottom(getResources().getString(R.string.index_exit_titile), R.dimen.px28);
            canDialog.setMessageBackground(Color.TRANSPARENT);
            canDialog.setPositiveButton(getResources().getString(R.string.index_exit)).setNegativeButton(getResources().getString(R.string.index_cancel)).setOnCanBtnClickListener(new CanDialog.OnClickListener() {
                @Override
                public void onClickPositive() {
                    canDialog.dismiss();
                    IndexActivity.this.finish();
                }

                @Override
                public void onClickNegative() {
                    if(reqNavigationSuccess){
                        mFocusUtils.showFocus();
                    }
                    canDialog.dismiss();
                    mHandler.sendEmptyMessage(MSG_SHOW_LOADING);

                }
            });
            canDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(reqNavigationSuccess){
                        mFocusUtils.showFocus();
                    }
                    mHandler.sendEmptyMessage(MSG_SHOW_LOADING);
                }
            });
        }
        canDialog.show();
    }

    @Override
    public void onKeyEvent(View view, int i, KeyEvent keyEvent) {
        isIntercept = true;
        mHandler.sendEmptyMessage(HIDE_FOCUS);
    }

    @Override
    public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onExtraPageSelected(int position) {
        mCurrentPage = position;
//        mFocusUtils.showFocus();
        //统计首页资源位曝光量
        if (mDataEyeUtils != null) {
            mDataEyeUtils.resourcesPositionExposure(position);
        }
    }

    @Override
    public void onExtraPageScrollStateChanged(int state, View view) {
        switch (mCurrentPage) {
            case 0:
                refreshUpdate(mUpdateNum);
                break;
            default:
                textUpdate.setVisibility(View.GONE);
                break;
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (null == view) {
                return;
            }
            if (!(IndexActivity.this.getCurrentFocus() instanceof LiteText)) {
                view.requestFocus();
                if (mCurrentPage == TOP_INDEX) {
                    mFocusUtils.setFocusView(view);
                } else {
                    mFocusUtils.setFocusView(view, SCALE);
                }
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            if (mViewPageContainFocus) {
                mFocusUtils.hideFocus();
            } else {
                mViewPager.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
            mHandler.removeCallbacks(mShowFocusRunnable);
            mHandler.removeCallbacks(mFindFocusRunnable);
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
            mHandler.postDelayed(mFindFocusRunnable, 100);
        }
    }

    private Runnable mShowFocusRunnable = new Runnable() {
        @Override
        public void run() {
            mFocusUtils.showFocus();
        }
    };

    private Runnable mFindFocusRunnable = new Runnable() {
        @Override
        public void run() {
            mViewPager.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            if (!mViewPageContainFocus) {
                return;
            }
            View focus = mViewPager.findFocus();
            if (focus != null) {
                mFocusUtils.startMoveFocus(focus, mCurrentPage == 1 ? 1 : SCALE);
                mHandler.postDelayed(mShowFocusRunnable, 200);
            }
        }
    };

    @Override
    protected void onHomeKeyDown() {
       // super.onHomeKeyDown();
    }
}
