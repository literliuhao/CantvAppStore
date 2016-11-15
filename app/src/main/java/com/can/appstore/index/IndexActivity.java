package com.can.appstore.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.index.adapter.IndexPagerAdapter;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.entity.PageBean;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerListener;
import com.can.appstore.index.model.JsonFormat;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FragmentBody;
import com.can.appstore.index.ui.ManagerFragment;
import com.can.appstore.index.ui.ManagerFragmentTest;
import com.can.appstore.index.ui.TitleBar;
import com.can.appstore.myapps.ui.MyAppsFragment;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity implements IAddFocusListener,View.OnClickListener{
    private List<BaseFragment> mFragmentLists;
    private PageBean mPageBeans;
    private IndexPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private RelativeLayout rlSearch;
    private RelativeLayout rlMessage;
    private FocusMoveUtil mFocusUtils;
    private FocusScaleUtil mFocusScaleUtils;
    private View lastView = null;
    private final int TOP_INDEX = 1;
    private final int DURATIONLARGE = 300;
    private final int DURATIONSMALL = 300;
    private final float SCALE = 1.05f;
    private final int OFFSCREENPAGELIMIT = 3;
    private final int PAGERCURRENTITEM = 0;
    //滚动中
    private final int SCROLLING = 2;
    //滚动完成
    private final int SCROLLED = 0;
    //存储焦点
//    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
        initView();
        initData();
        initFocus();
        bindData();
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
    private void initData() {
        mFragmentLists = new ArrayList<>();
        mPageBeans = JsonFormat.parseJson("");
        if (null == mPageBeans) return;
        //根据服务器配置文件生成不同样式加入Fragment列表中
        FragmentBody fragment;
        for (int i = 0; i < mPageBeans.getPageLists().size(); i++) {
            fragment = new FragmentBody(this, mPageBeans.getPageLists().get(i));
//            fragment.setTargetFragment(fragment, i);
            mFragmentLists.add(fragment);
        }
        //排行、管理、我的应用、不受服务器后台配置，因此手动干预位置
        ManagerFragmentTest topFragment = new ManagerFragmentTest(this);
        if (mFragmentLists.size() > 0) {
            mFragmentLists.add(TOP_INDEX, topFragment);
        } else {
            mFragmentLists.add(topFragment);
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
    private void bindTtile(PageBean mPage) {
        List<String> mDatas = new ArrayList<>();
        for (int i = 0; i < mPage.getPageLists().size(); i++) {
            LayoutBean layoutBean = mPage.getPageLists().get(i);
            mDatas.add(layoutBean.getTitle());
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
        mFocusScaleUtils = new FocusScaleUtil(DURATIONLARGE, DURATIONSMALL, SCALE, null, null);
    }

    /**
     * mViewPager、mTitleBar 数据绑定与页面绑定
     */
    private void bindData() {
        bindTtile(mPageBeans);
        mAdapter = new IndexPagerAdapter(this.getSupportFragmentManager(), mViewPager, mFragmentLists);
        mAdapter.setOnExtraPageChangeListener(new IOnPagerListener() {
            @Override
            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onExtraPageSelected(int position, View view) {
//                view.requestFocus();
//                mFocusUtils.startMoveFocus(view);
//                mFocusUtils.showFocus(200);
            }

            @Override
            public void onExtraPageScrollStateChanged(int state, View view) {
                if (state == SCROLLING) {
                    if (!(IndexActivity.this.getCurrentFocus() instanceof TextView)) {
                        mFocusUtils.hideFocus();
                    }
                } else if (state == SCROLLED) {
                    if (null == view) {
                        mFocusUtils.startMoveFocus(IndexActivity.this.getCurrentFocus());
                        mFocusUtils.showFocus(200);
                        return;
                    }
                    if (!(IndexActivity.this.getCurrentFocus() instanceof TextView)) {
                        view.requestFocus();
                        mFocusUtils.startMoveFocus(view);
                        mFocusUtils.showFocus(200);
                    }
                }
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(OFFSCREENPAGELIMIT);
        mViewPager.setCurrentItem(PAGERCURRENTITEM);
        mTitleBar.setViewPager(mViewPager, PAGERCURRENTITEM);
    }

    /**
     * 自定义onFocusChange接口，首页所有Fragment实现后都可以在此做统一焦点处理
     *
     * @param v        焦点动画对象
     * @param hasFocus 是否获得焦点
     */
    @Override
    public void addFocusListener(View v, boolean hasFocus) {
        if (hasFocus) {
            mFocusUtils.startMoveFocus(v);
            if (v == null) return;
            Log.i("addFocusListener", v.getId() + "");
//            Log.i("addFocusListener", getCurrentFocus().getId() + "");
            if (v instanceof TextView) {
                v.callOnClick();
            } else {
                mFocusScaleUtils.scaleToLarge(v);
            }
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
        switch (view.getId()){
            case R.id.rl_search:
                Log.i("IndexActivity", "onClick...." + view.getId());
                break;
            case R.id.rl_message:
                Log.i("IndexActivity", "onClick...." + view.getId());
                break;
        }
    }
}
