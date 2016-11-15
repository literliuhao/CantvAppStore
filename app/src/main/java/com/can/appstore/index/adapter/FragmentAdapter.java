package com.can.appstore.index.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FragmentAdapter extends PagerAdapter {
    private List<Fragment> mFragmentLists;
    private int photoSize;
    private Fragment[] views;
    private Context mContext;


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    public FragmentAdapter(Context context, List<Fragment> fragmentLists) {
        this.mContext = context;
        this.mFragmentLists = fragmentLists;
    }

//    public void addListenerPosition(IPositionListener listener) {
//        this.listener = listener;
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragmentView = mFragmentLists.get(position);
//        views[position] = fragmentView;
        return fragmentView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragmentLists.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(views[position]);
//        if (views[position] != null) {
//            views[position] = null;
//        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}