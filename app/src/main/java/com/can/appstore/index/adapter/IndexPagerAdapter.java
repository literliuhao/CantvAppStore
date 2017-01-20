package com.can.appstore.index.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.index.interfaces.IOnPagerListener;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.index.ui.FragmentBody;

import java.util.List;

/**
 * Created by liuhao on 2016/11/2.
 */

public class IndexPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<BaseFragment> mFragmentList;
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    // 当前page索引
    private int currentPageIndex = 0;
    // ViewPager切换页面时的额外功能添加接口
    private IOnPagerListener onExtraPageChangeListener;
    // 记录页面跳转前的焦点位置
    private View markView;

    public IndexPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager, List<BaseFragment> fragmentList) {
        this.mFragmentManager = fragmentManager;
        this.mViewPager = viewPager;
        this.mFragmentList = fragmentList;
        this.mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mFragmentList.get(position).getView());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * 会在进程的主线程中，用异步的方式来执行。
     * 如果想要立即执行这个等待中的操作，就要调用这个方法
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡
        Log.i("IndexPagerAdapter", "position " + position);
        BaseFragment fragment = mFragmentList.get(position);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            if(ft != null) {
                ft.commitAllowingStateLoss();
                mFragmentManager.executePendingTransactions();
            }
        }

        if (null == fragment.getView().getParent()) {
            container.addView(fragment.getView()); // 为viewpager增加布局
        }
        if(position == 0 && fragment instanceof FragmentBody){
            for(View view : ((FragmentBody)fragment).firstColumnViews){
                view.setNextFocusLeftId(view.getId());
            }
        }
        return fragment.getView();
    }

    /**
     * 当前page索引（切换之前）
     *
     * @return
     */
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int index) {
        currentPageIndex = index;
    }

    /**
     * 设置页面切换额外功能监听器
     *
     * @param onExtraPageChangeListener
     */
    public void setOnExtraPageChangeListener(IOnPagerListener onExtraPageChangeListener) {
        this.onExtraPageChangeListener = onExtraPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
            onExtraPageChangeListener.onExtraPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("IndexPagerAdapter", "i " + position);
        mFragmentList.get(getCurrentPageIndex()).onPause();
        if (mFragmentList.get(position).isAdded()) {
            mFragmentList.get(position).onResume();
        }

        BaseFragment newFragment = mFragmentList.get(position);
        markView = null;
        if (getCurrentPageIndex() > position) {
            markView = newFragment.getLastView();
        }
        setCurrentPageIndex(position);
        if (null != onExtraPageChangeListener) {
            onExtraPageChangeListener.onExtraPageSelected(position);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (null != onExtraPageChangeListener) {
            onExtraPageChangeListener.onExtraPageScrollStateChanged(state, markView);
        }
    }

}
