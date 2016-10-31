package com.can.appstore.index;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.entity.PageBean;
import com.can.appstore.index.interfaces.ICallBack;
import com.can.appstore.index.model.JsonFormat;
import com.can.appstore.index.ui.FragmentBody;
import com.can.appstore.index.ui.ManagerFragment;
import com.can.appstore.index.ui.TitleBar;
import com.can.appstore.myapps.ui.MyAppsFragment;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity implements ICallBack, ViewPager.OnPageChangeListener {
    private List<Fragment> mFragmentLists;
    private PageBean mPageBeans;
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;

    private TitleBar mTitleBar;
    private FocusMoveUtil mFocusUtils;
    private FocusScaleUtil mFocusScaleUtils;
    private View lastView = null;

    //滚动中
    private final int SCROLLING = 2;
    //滚动停止
    private final int SCROLLED = 0;

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
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        mTitleBar = (TitleBar) findViewById(R.id.id_indicator);
        mTitleBar.initTitle(this);
    }

    /**
     * 首页数据初始化
     */
    private void initData() {
        mFragmentLists = new ArrayList<>();
        mPageBeans = JsonFormat.parseJson("");
        if (null == mPageBeans) return;
        //根据服务器配置文件生成不同样式加入Fragment列表中
        for (int i = 0; i < mPageBeans.getPageLists().size(); i++) {
            FragmentBody fragment = new FragmentBody(mPageBeans.getPageLists().get(i), this);
            mFragmentLists.add(fragment);

        }
        //排行、管理、我的应用不受服务器后台配置，因此手动干预位置
        ManagerFragment topFragment = new ManagerFragment(this);
        mFragmentLists.add(1, topFragment);
        ManagerFragment managerFragment = new ManagerFragment(this);
        mFragmentLists.add(4, managerFragment);
        MyAppsFragment myAppsFragment = new MyAppsFragment(this);
        mFragmentLists.add(5, myAppsFragment);

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
        mDatas.add(1, "排行");
        mDatas.add(4, "管理");
        mDatas.add(5, "我的应用");
        //设置导航栏Title
        mTitleBar.setTabItemTitles(mDatas);
    }

    /**
     * 首页焦点初始化，并且在IndexActivity做统一处理
     */
    private void initFocus() {
        mFocusUtils = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtils = new FocusScaleUtil(300, 300, 1.05f, null, null);
    }

    /**
     * mViewPager、mTitleBar 数据绑定与页面绑定
     */
    private void bindData() {
        bindTtile(mPageBeans);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragmentLists.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentLists.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(this);
        mTitleBar.setViewPager(mViewPager, 0);


    }

    /**
     * 自定义onSuccess接口，首页所有Fragment实现后都可以在此做统一焦点处理
     *
     * @param v        焦点动画对象
     * @param hasFocus 是否获得焦点
     */
    @Override
    public void onSuccess(View v, boolean hasFocus) {
        if (hasFocus) {
            mFocusUtils.startMoveFocus(v);
            if (v == null) return;
            Log.i("onSuccess", v.getId() + "");
            if (v instanceof TextView) {
                v.callOnClick();
            } else {
                mFocusScaleUtils.scaleToLarge(v);
            }
        } else {
            mFocusScaleUtils.scaleToNormal();
        }
        //此方法不能交换View层结构
//        mFocusUtils.showFocus();
//        widgetTvViewBring.bringChildToFront((ViewGroup) getWindow().getDecorView(), v);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.i("IndexActivity", "onPageScrolled " + position + "");
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("IndexActivity", "onPageSelected " + position + "");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("IndexActivity", "onPageScrollStateChanged " + state + "");
        //ViewPager切换时候焦点框先消失，滚动完成后设置位置并显示
        if (state == SCROLLING) {
            if (!(getCurrentFocus() instanceof TextView)) {
                mFocusUtils.hideFocus();
            }
        } else if (state == SCROLLED) {
            mFocusUtils.startMoveFocus(getCurrentFocus());
            mFocusUtils.showFocus(200);
        }
    }
}
