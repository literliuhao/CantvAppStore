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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import retrofit2.Response;

import static com.can.appstore.index.ui.FragmentEnum.INDEX;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity implements IAddFocusListener, View.OnClickListener, View.OnKeyListener {
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
    private ShareData shareData;
    private final int TOP_INDEX = 1;
    private final int DURATIONLARGE = 300;
    private final int DURATIONSMALL = 300;
    private final float SCALE = 1.1f;
    private final int OFFSCREENPAGELIMIT = 5;
    private final int PAGERCURRENTITEM = 0;
    //滚动中
    private final int SCROLLING = 2;
    //滚动完成
    private final int SCROLLED = 0;
    private int scrollStatus;
    private final int FIND_FOCUS = 0X000001;
    private int currentPage;
    private CanCall<ListResult<Navigation>> mNavigationCall;
    private int oldIndex;
    private int count = 1;

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
        refreshUpdate();
        refreshMsg();
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
            public void onFocusChange(View view, boolean hasFocus) {
                addFocusListener(view, hasFocus, INDEX);
            }
        });
        rlSearch.setOnClickListener(this);

        rlMessage = (RelativeLayout) this.findViewById(R.id.rl_message);
        rlMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                addFocusListener(view, hasFocus, INDEX);
            }
        });
        rlMessage.setOnClickListener(this);

        imageRed = (ImageView) this.findViewById(R.id.iv_mssage_red);

        mTitleBar = (TitleBar) findViewById(R.id.id_indicator);
        mTitleBar.initTitle(this);
        mViewPager = (ViewPager) findViewById(R.id.id_custom_pager);

        textUpdate = (TextView) findViewById(R.id.tv_update_number);
        initUpdateListener();
        initMsgListener();
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
        mFocusUtils.hideFocus();
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
            public void onExtraPageSelected(int position) {
                currentPage = position;
                mFocusUtils.showFocus();
            }

            @Override
            public void onExtraPageScrollStateChanged(int state, View view) {
                scrollStatus = state;
                switch (currentPage){
                    case 0:
                        textUpdate.setVisibility(View.VISIBLE);
                        break;
                    default:
                        textUpdate.setVisibility(View.GONE);
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
                        }else{
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
                        }else{
                            mFocusUtils.setFocusView(view, SCALE);
                            mFocusUtils.startMoveFocus(view, SCALE);
                        }
                    }
                    mFocusUtils.showFocus();
                }
            }
        });

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(OFFSCREENPAGELIMIT);
        mViewPager.setCurrentItem(PAGERCURRENTITEM);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.px165));
        mViewPager.setOnKeyListener(this);

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(mViewPager.getContext(), sInterpolator);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTitleBar.setViewPager(mViewPager, PAGERCURRENTITEM);
        mHandler.sendEmptyMessageDelayed(FIND_FOCUS, 200);

        //开始获取第三方屏蔽列表
        shareData.getInstance().execute();

    }
    private void initUpdateListener() {
        MessageManager.setCallMsgDataUpdate(new MessageManager.CallMsgDataUpdate() {
            @Override
            public void onUpdate() {
                imageRed.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshUpdate() {
        if (count > 0) {
            //伪代码 可更新不等于0时显示
            textUpdate.setText(count + getResources().getString(R.string.index_app_update));
            textUpdate.setVisibility(View.VISIBLE);
        } else {
            textUpdate.setVisibility(View.GONE);
        }
    }

    private void initMsgListener() {
        MessageManager.setCallMsgDataUpdate(new MessageManager.CallMsgDataUpdate() {
            @Override
            public void onUpdate() {
                Log.i("IndexActivity", "有新的消息数据了");
                imageRed.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshMsg() {
        if (MessageManager.existUnreadMsg()) {
            textUpdate.setVisibility(View.VISIBLE);
        } else {
            textUpdate.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_FOCUS:
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
//            count++;
//            refreshUpdate();
//            if (scrollStatus != SCROLLED) {
//                return;
//            }
//            if (oldView.getId() == v.getId()) {
//            Log.i("IndexActivity", "mViewPager.isSelected() " + mViewPager.isSelected());
            switch (sourceEnum) {
                case INDEX:
                    v.bringToFront();
                    mFocusUtils.startMoveFocus(v, SCALE);
                    mFocusScaleUtils.scaleToLarge(v);
                    break;
                case TITLE:
                    if (v instanceof LiteText) {
                        v.callOnClick();
                    } else {
                        v.bringToFront();
                        mFocusScaleUtils.scaleToLarge(v);
                    }
                    mFocusUtils.startMoveFocus(v, SCALE);
                    break;
                case RANK:
                    v.bringToFront();
                    mFocusUtils.startMoveFocus(v);
                    break;
                case NORMAL:
                case MYAPP:
                case MANAGE:
                    v.bringToFront();
                    mFocusUtils.startMoveFocus(v, SCALE);
                    mFocusScaleUtils.scaleToLarge(v);
                    break;

            }
//            } else if (oldView.getId() > v.getId()) {
//                Log.i("IndexActivity", "else if oldView > newView");
//            } else if (oldView.getId() < v.getId()) {
//                Log.i("IndexActivity", "else  oldView < newView");
//            }
        } else {
//            oldView = v.geti;
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

    public static void actionStart(Context context, String topicId) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        Log.i("IndexActivity", "scrollStatus " + scrollStatus);
        return false;
    }
}
