package com.can.appstore.index;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;
import com.can.appstore.homerank.HomeRankFragment;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.index.adapter.IndexPagerAdapter;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerListener;
import com.can.appstore.index.model.DataUtils;
import com.can.appstore.index.model.ShareData;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FixedScroller;
import com.can.appstore.index.ui.FragmentBody;
import com.can.appstore.index.ui.FragmentEnum;
import com.can.appstore.index.ui.LiteText;
import com.can.appstore.index.ui.ManagerFragment;
import com.can.appstore.index.ui.TitleBar;
import com.can.appstore.message.MessageActivity;
import com.can.appstore.message.manager.MessageManager;
import com.can.appstore.myapps.ui.MyAppsFragment;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.update.AutoUpdate;
import com.can.appstore.update.model.UpdateApkModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PromptUtils;
import retrofit2.Response;

import static com.can.appstore.index.ui.FragmentEnum.INDEX;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity implements IAddFocusListener, View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {
    private List<BaseFragment> mFragmentLists;
    private IndexPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private RelativeLayout rlSearch;
    private RelativeLayout rlMessage;
    private ImageView imageRed;
    private TextView textUpdate;
    private FocusMoveUtil mFocusUtils;
    private FocusScaleUtil mFocusScaleUtils;
    private final int TOP_INDEX = 1;
    private final int DURATION_LARGE = 300;
    private final int DURATION_SMALL = 300;
    private final float SCALE = 1.1f;
    private final int SCREEN_PAGE_LIMIT = 5;
    private final int PAGER_CURRENT = 0;
    private final int DELAYED = 200;
    //滚动中
    private final int SCROLLING = 2;
    //滚动完成
    private final int SCROLLED = 0;
    private int scrollStatus;
    private final int INIT_FOCUS = 0X000001;
    private int currentPage;
    private CanCall<ListResult<Navigation>> mNavigationCall;
    private int updateNum;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
        initView();
        initFocus();
        getNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        EventBus.getDefault().register(mContext);
        //导航
        mTitleBar = (TitleBar) findViewById(R.id.id_indicator);
        mTitleBar.initTitle(this);
        mViewPager = (ViewPager) findViewById(R.id.id_custom_pager);
        //搜索
        rlSearch = (RelativeLayout) this.findViewById(R.id.rl_search);
        rlSearch.setOnFocusChangeListener(this);
        rlSearch.setOnClickListener(this);
        //消息
        rlMessage = (RelativeLayout) this.findViewById(R.id.rl_message);
        rlMessage.setOnFocusChangeListener(this);
        rlMessage.setOnClickListener(this);
        //消息提示
        imageRed = (ImageView) this.findViewById(R.id.iv_mssage_red);
        //更新
        textUpdate = (TextView) findViewById(R.id.tv_update_number);
    }

    /**
     * 首页焦点初始化，并且在IndexActivity做统一处理
     */
    private void initFocus() {
        mFocusUtils = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusUtils.hideFocus();
        mFocusScaleUtils = new FocusScaleUtil(DURATION_LARGE, DURATION_SMALL, SCALE, null, null);
    }

    public void getNavigation() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            mNavigationCall = HttpManager.getApiService().getNavigations();
            mNavigationCall.enqueue(new CanCallback<ListResult<Navigation>>() {
                @Override
                public void onResponse(CanCall<ListResult<Navigation>> call, Response<ListResult<Navigation>> response) throws Exception {
                    ProxyCache(response);
                }

                @Override
                public void onFailure(CanCall<ListResult<Navigation>> call, CanErrorWrapper errorWrapper) {
                    Log.i("DataUtils", errorWrapper.getReason() + " || " + errorWrapper.getThrowable());
                    ProxyCache(null);
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
            } else {
                listResult = new Gson().fromJson(DataUtils.getInstance(mContext).getCache(), new TypeToken<ListResult<Navigation>>() {
                }.getType());
            }
            DataUtils.getInstance(mContext).setIndexData(listResult);
            parseData(listResult);
        } catch (Exception e) {
            PromptUtils.toast(mContext, getResources().getString(R.string.index_data_error));
            DataUtils.getInstance(mContext).clearData();
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
        if (null == navigationListResult.getData()) return;
        //根据服务器配置文件生成不同样式加入Fragment列表中
        FragmentBody fragment;
        for (int i = 0; i < navigationListResult.getData().size(); i++) {
            fragment = new FragmentBody(this, navigationListResult.getData().get(i));
            mFragmentLists.add(fragment);
        }

        //排行、管理、我的应用、不受服务器后台配置，因此手动干预位置
        HomeRankFragment homeRankFragment = new HomeRankFragment(this);
        if (mFragmentLists.size() > 0) {
            mFragmentLists.add(TOP_INDEX, homeRankFragment);
        } else {
            mFragmentLists.add(homeRankFragment);
        }
        ManagerFragment managerFragment = new ManagerFragment(this);
        mFragmentLists.add(managerFragment);

        MyAppsFragment myAppsFragment = new MyAppsFragment(this);
        mFragmentLists.add(myAppsFragment);
    }

    /**
     * 首页与TitleBar的数据绑定
     *
     * @param mPage 导航栏数据
     */
    private void bindTtile(ListResult<Navigation> mPage) {
        List<String> mDatas = new ArrayList<>();
        for (int i = 0; i < mPage.getData().size(); i++) {
            Navigation navigation = mPage.getData().get(i);
            mDatas.add(navigation.getTitle());
        }
        //排行、管理、我的应用不受服务器后台配置，因此手动干预位置
        if (mDatas.size() > 0) {
            mDatas.add(TOP_INDEX, getResources().getString(R.string.index_top));
        } else {
            mDatas.add(getResources().getString(R.string.index_top));
        }
        mDatas.add(getResources().getString(R.string.index_manager));
        mDatas.add(getResources().getString(R.string.index_myapp));
        //设置导航栏Title
        mTitleBar.setTabItemTitles(mDatas);
    }

    /**
     * mViewPager、mTitleBar 数据绑定与页面绑定
     */
    private void bindData(ListResult<Navigation> listResult) {
        bindTtile(listResult);
        mAdapter = new IndexPagerAdapter(this.getSupportFragmentManager(), mViewPager, mFragmentLists);
        mAdapter.setOnExtraPageChangeListener(new IOnPagerListener() {
            @Override
            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onExtraPageSelected(int position) {
                currentPage = position;
                mFocusUtils.showFocus();
            }

            @Override
            public void onExtraPageScrollStateChanged(int state, View view) {
                scrollStatus = state;
                switch (currentPage) {
                    case 0:
                        refreshUpdate(updateNum);
                        break;
                    default:
                        textUpdate.setVisibility(View.GONE);
                        break;
                }
                if (state == SCROLLING) {
                    if (!(IndexActivity.this.getCurrentFocus() instanceof LiteText)) {
                        mFocusUtils.hideFocus();
                    }
                } else if (state == SCROLLED) {
                    if (null == view) {
                        view = IndexActivity.this.getCurrentFocus();
                        if (!(view instanceof LiteText) && currentPage == TOP_INDEX) {
                            mFocusUtils.setFocusView(view);
                            mFocusUtils.startMoveFocus(view);
                        } else {
                            mFocusUtils.setFocusView(view, SCALE);
                            mFocusUtils.startMoveFocus(view, SCALE);
                        }
                        mFocusUtils.showFocus();
                        return;
                    }
                    if (!(IndexActivity.this.getCurrentFocus() instanceof LiteText)) {
                        view.requestFocus();
                        if (currentPage == TOP_INDEX) {
                            mFocusUtils.setFocusView(view);
                            mFocusUtils.startMoveFocus(view);
                        } else {
                            mFocusUtils.setFocusView(view, SCALE);
                            mFocusUtils.startMoveFocus(view, SCALE);
                        }
                    }
                    mFocusUtils.showFocus();
                }
            }
        });

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(SCREEN_PAGE_LIMIT);
        mViewPager.setCurrentItem(PAGER_CURRENT);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.px165));
        mViewPager.setOnKeyListener(this);
        mTitleBar.setViewPager(mViewPager, PAGER_CURRENT);
        mHandler.sendEmptyMessageDelayed(INIT_FOCUS, DELAYED);
        fixedScroll();
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
        initUpdateListener();
        initMsgListener();
    }

    private void initUpdateListener() {
        AutoUpdate.getInstance().autoUpdate(IndexActivity.this);
    }

    private void refreshUpdate(int number) {
        if (number > 0) {
            textUpdate.setText(number + getResources().getString(R.string.index_app_update));
            textUpdate.setVisibility(View.VISIBLE);
        } else {
            textUpdate.setVisibility(View.GONE);
        }
    }

    private void initMsgListener() {
        MessageManager.setCallMsgDataUpdate(new MessageManager.CallMsgDataUpdate() {
            @Override
            public void onUpdate() {
                imageRed.setVisibility(View.VISIBLE);
            }
        });
        MessageManager.requestMsg(mContext);
    }

    private void refreshMsg() {
        if (MessageManager.existUnreadMsg(this)) {
            imageRed.setVisibility(View.VISIBLE);
        } else {
            imageRed.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_FOCUS:
                    View first = mTitleBar.getFirstView();
                    mFocusUtils.setFocusView(first, SCALE);
                    first.requestFocus();
                    mFocusUtils.showFocus(100);
                    rlSearch.setFocusable(true);
                    rlMessage.setFocusable(true);
                    break;
            }
        }
    };

    /**
     * 自定义onFocusChange接口，首页所有Fragment实现后都可以在此做统一焦点处理
     *
     * @param v        焦点动画对象
     * @param hasFocus 是否获得焦点
     */
    @Override
    public void addFocusListener(View v, boolean hasFocus, FragmentEnum sourceEnum) {
        if (hasFocus) {
            if (null == v) return;
            switch (sourceEnum) {
                case INDEX:
                    v.bringToFront();
                    mFocusUtils.setFocusRes(mContext, R.drawable.btn_circle_focus);
                    mFocusUtils.startMoveFocus(v);
//                    mFocusScaleUtils.scaleToLarge(v);
                    break;
                case TITLE:
                    if (v instanceof LiteText) {
                        v.callOnClick();
                    } else {
                        v.bringToFront();
                        mFocusScaleUtils.scaleToLarge(v);
                    }
                    mFocusUtils.setFocusRes(mContext, R.drawable.btn_focus);
                    mFocusUtils.startMoveFocus(v, SCALE);
                    break;
                case RANK:
                    v.bringToFront();
                    mFocusUtils.setFocusRes(mContext, R.drawable.btn_focus);
                    mFocusUtils.startMoveFocus(v);
                    break;
                case NORMAL:
                case MYAPP:
                case MANAGE:
                    v.bringToFront();
                    mFocusUtils.setFocusRes(mContext, R.drawable.btn_focus);
                    mFocusUtils.startMoveFocus(v, SCALE);
                    mFocusScaleUtils.scaleToLarge(v);
                    break;

            }
        } else {
            mFocusScaleUtils.scaleToNormal();
        }
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.i("IndexActivity", "scrollStatus " + scrollStatus);
//        if (scrollStatus == SCROLLED) {
//            return super.onKeyUp(keyCode, event);
//        } else {
//            return true;
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                SearchActivity.startAc(this);
                break;
            case R.id.rl_message:
                MessageActivity.actionStart(this);
                break;
        }
    }

    /**
     * 使用eventbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateApkModel model) {
        updateNum = model.getNumber();
        refreshUpdate(updateNum);
    }

    public static void actionStart(Context context, String topicId) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        Log.i("IndexActivity", "scrollStatus " + scrollStatus);
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        addFocusListener(view, hasFocus, INDEX);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }
}
