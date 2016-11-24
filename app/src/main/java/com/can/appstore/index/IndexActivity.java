package com.can.appstore.index;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

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
import com.can.appstore.index.model.ShareData;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FragmentBody;
import com.can.appstore.index.ui.LiteText;
import com.can.appstore.index.ui.ManagerFragment;
import com.can.appstore.index.ui.TitleBar;
import com.can.appstore.message.MessageActivity;
import com.can.appstore.myapps.ui.MyAppsFragment;
import com.can.appstore.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import retrofit2.Response;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity implements IAddFocusListener, View.OnClickListener, View.OnFocusChangeListener {
    private List<BaseFragment> mFragmentLists;
    private IndexPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private RelativeLayout rlSearch;
    private RelativeLayout rlMessage;
    private FocusMoveUtil mFocusUtils;
    private FocusScaleUtil mFocusScaleUtils;
    private ShareData shareData;
    private final int TOP_INDEX = 1;
    private final int DURATIONLARGE = 300;
    private final int DURATIONSMALL = 300;
    private final float SCALE = 1.1f;
    private final int OFFSCREENPAGELIMIT = 3;
    private final int PAGERCURRENTITEM = 0;
    //滚动中
    private final int SCROLLING = 2;
    //滚动完成
    private final int SCROLLED = 0;
    private final int FIND_FOCUS = 0X000001;
    private CanCall<ListResult<Navigation>> mNavigationCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
        initView();
        initFocus();
        getNavigation();
    }

    /**
     * 当前Activity样式及载入的布局
     */
    private void setStyle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index);
    }

    public void getNavigation() {
        mNavigationCall = HttpManager.getApiService().getNavigations();
        mNavigationCall.enqueue(new CanCallback<ListResult<Navigation>>() {
            @Override
            public void onResponse(CanCall<ListResult<Navigation>> call, Response<ListResult<Navigation>> response) throws Exception {
                ListResult<Navigation> info = response.body();
                if (null != info.getData()) {
                    initData(info);
                    bindData(info);
                }
            }

            @Override
            public void onFailure(CanCall<ListResult<Navigation>> call, CanErrorWrapper errorWrapper) {
                Log.i("DataUtils", errorWrapper.getReason() + " || " + errorWrapper.getThrowable());
            }
        });
    }

    /**
     * 首页初始化所有View
     */
    private void initView() {
        rlSearch = (RelativeLayout) this.findViewById(R.id.rl_search);
        rlSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                addFocusListener(view, b);
            }
        });
        rlSearch.setOnClickListener(this);

        rlMessage = (RelativeLayout) this.findViewById(R.id.rl_message);
        rlMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                addFocusListener(view, b);
            }
        });
        rlMessage.setOnClickListener(this);

        mTitleBar = (TitleBar) findViewById(R.id.id_indicator);
        mTitleBar.initTitle(this);
        mViewPager = (ViewPager) findViewById(R.id.id_vp);

    }

    /**
     * 首页数据初始化
     */
    private void initData(ListResult<Navigation> navigationListResult) {
        mFragmentLists = new ArrayList<>();
//        mPageBeans = JsonFormat.parseJson("");
        if (null == navigationListResult.getData()) return;
        //根据服务器配置文件生成不同样式加入Fragment列表中
        FragmentBody fragment;
        for (int i = 0; i < navigationListResult.getData().size(); i++) {
            fragment = new FragmentBody(this, navigationListResult.getData().get(i));
            mFragmentLists.add(fragment);
        }

        //排行、管理、我的应用、不受服务器后台配置，因此手动干预位置
//        ManagerFragmentTest topFragment = new ManagerFragmentTest(this);
//        if (mFragmentLists.size() > 0) {
//            mFragmentLists.add(TOP_INDEX, topFragment);
//        } else {
//            mFragmentLists.add(topFragment);
//        }

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
     * 首页焦点初始化，并且在IndexActivity做统一处理
     */
    private void initFocus() {
        mFocusUtils = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
//        mFocusUtils.hideFocus();
        mFocusScaleUtils = new FocusScaleUtil(DURATIONLARGE, DURATIONSMALL, SCALE, null, null);
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
            public void onExtraPageSelected(int position, View view) {
            }

            @Override
            public void onExtraPageScrollStateChanged(int state, View view) {
                if (state == SCROLLING) {
                    if (!(IndexActivity.this.getCurrentFocus() instanceof LiteText)) {
                        mFocusUtils.hideFocus();
                    }
                } else if (state == SCROLLED) {
                    if (null == view) {
                        mFocusUtils.setFocusView(IndexActivity.this.getCurrentFocus(), SCALE);
                        mFocusUtils.startMoveFocus(IndexActivity.this.getCurrentFocus(), SCALE);
                        mFocusUtils.showFocus();
                        return;
                    }
                    if (!(IndexActivity.this.getCurrentFocus() instanceof LiteText)) {
                        view.requestFocus();
                        mFocusUtils.setFocusView(view, SCALE);
                        mFocusUtils.startMoveFocus(view, SCALE);
                        mFocusUtils.showFocus();
                    }
                }
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(OFFSCREENPAGELIMIT);
        mViewPager.setCurrentItem(PAGERCURRENTITEM);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.px165));
//        mViewPager.setOnFocusChangeListener(this);
        mTitleBar.setViewPager(mViewPager, PAGERCURRENTITEM);
        mHandler.sendEmptyMessageAtTime(FIND_FOCUS, 1000);

        //开始获取第三方屏蔽列表
        shareData.getInstance().execute();

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_FOCUS:
//                    View first = mTitleBar.getFirstView();
//                    mFocusUtils.setFocusView(first, SCALE);
//                    mFocusUtils.showFocus();
//                    Log.i("IndexActivity","first " + first.toString());
//                    first.requestFocus();
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
    public void addFocusListener(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v == null) return;
            Log.i("IndexActivity", v.getId() + "");
            if (v instanceof LiteText) {
                v.callOnClick();
            } else {
                v.bringToFront();
                mFocusScaleUtils.scaleToLarge(v);
            }
            mFocusUtils.startMoveFocus(v, SCALE);
        } else {
            mFocusScaleUtils.scaleToNormal();
        }
    }

    @Nullable
    @Override
    public View getCurrentFocus() {
        Log.i("IndexActivity", "getCurrentFocus ");
        return super.getCurrentFocus();
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
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Log.i("IndexActivity", "view...." + view.getId());
    }

    public static void actionStart(Context context, String topicId) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
    }
}
