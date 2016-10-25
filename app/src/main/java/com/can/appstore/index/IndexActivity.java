package com.can.appstore.index;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.can.appstore.R;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.entity.PageBean;
import com.can.appstore.index.focus.FocusScaleUtils;
import com.can.appstore.index.focus.FocusUtils;
import com.can.appstore.index.interfaces.ICallBack;
import com.can.appstore.index.model.JsonFormat;
import com.can.appstore.index.ui.CustormFragment;
import com.can.appstore.index.ui.ViewPagerIndicator;
import com.can.appstore.index.ui.VpSimpleFragment;
import com.can.appstore.index.ui.WidgetTvViewBring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhao on 2016/10/15.
 */
public class IndexActivity extends FragmentActivity {
    private List<Fragment> mTabContents = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    //    private List<String> mDatas = Arrays.asList("推荐", "排行", "应用", "游戏", "教育", "管理", "我的应用");
    private List<String> mDatas = new ArrayList<String>();

    private ViewPagerIndicator mIndicator;
    private FocusUtils mFocusUtils;
    private FocusScaleUtils mFocusScaleUtils;
    private View moveView = null;
    private WidgetTvViewBring widgetTvViewBring;
    private View lastView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vp_indicator);
        PageBean mPageBean = JsonFormat.parseJson("");
        if (null != mPageBean) {
            initView();
            initDatas(mPageBean);
            //设置Tab上的标题
            mIndicator.setTabItemTitles(mDatas);
            mIndicator.setOnPageChangeListener(new ViewPagerIndicator.PageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.i("onPageSelected", position + "");

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1 || position == 4) {
                        CustormFragment fragment = (CustormFragment) mTabContents.get(position);
//                        View view = fragment.setOnFocus();
//                        mFocusUtils.startMoveFocus(view, null, true, 1f, 1f, 0f, 0f);
                    } else {
                        VpSimpleFragment fragment = (VpSimpleFragment) mTabContents.get(position);
//                        View view = fragment.setOnFocus();
//                        mFocusUtils.startMoveFocus(view, null, true, 1f, 1f, 0f, 0f);
                    }
//                    mFocusUtils.showFocus();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    Log.i("onPageScrollStateChanged", state + "");
                    if (state == 2) {
                        mFocusUtils.hideFocus();
                    } else if (state == 0) {
                        View view = IndexActivity.this.getCurrentFocus();
//                        if(view instanceof MyImageView){
                        mFocusUtils.startMoveFocus(view, null, true, 1f, 1f, 0f, 0f);
                        mFocusUtils.showFocus();
//                        }
                    }
                }
            });
            mViewPager.setAdapter(mAdapter);
            //设置关联的ViewPager
            mIndicator.setViewPager(mViewPager, 0);
            mFocusUtils = new FocusUtils(this, getWindow().getDecorView(), R.drawable.image_focus);
            mFocusScaleUtils = new FocusScaleUtils(300, 300, 1.05f, null, null);
            widgetTvViewBring = new WidgetTvViewBring((ViewGroup) this.getWindow().getDecorView());
        }
    }

    private void initDatas(PageBean mPageBean) {
        for (int i = 0; i < mPageBean.getPageLists().size(); i++) {
            LayoutBean layoutBean = mPageBean.getPageLists().get(i);
            mDatas.add(layoutBean.getTitle());
        }
        for (int i = 0; i < mPageBean.getPageLists().size(); i++) {
            VpSimpleFragment fragment = new VpSimpleFragment(mPageBean.getPageLists().get(i), new ICallBack() {
                @Override
                public void onSuccess(View view, boolean hasFocus) {
                    Log.i("onSuccess", view.getParent() + " " + view.getId() + "");
                    //此方法不能交换View层结构
                    mFocusUtils.showFocus();
                    mFocusUtils.startMoveFocus(view, null, true, 1f, 1f, 0f, 0f);
                    mFocusScaleUtils.scaleToLargeWH(view, 1.1F, 1.1f);
                    widgetTvViewBring.bringChildToFront((ViewGroup) getWindow().getDecorView(), view);
                }
            });
            if (i == 1) {
                CustormFragment custormFragment = CustormFragment.newInstance("custorm");
                mTabContents.add(custormFragment);
            } else if (i == 4) {
                CustormFragment custormFragment = CustormFragment.newInstance("custorm");
                mTabContents.add(custormFragment);
            } else {
                mTabContents.add(fragment);
            }
//            Bundle bundle = new Bundle();
//            bundle.putString("title", mDatas.get(i));
//            fragment.setArguments(bundle);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
    }


}
