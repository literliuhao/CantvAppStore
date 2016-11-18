package com.can.appstore.index.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.can.appstore.entity.Layout;
import com.can.appstore.entity.Navigation;
import com.can.appstore.index.interfaces.IAddFocusListener;

import cn.can.tvlib.utils.DisplayUtil;

/**
 * Created by liuhao on 2016/10/17.
 */
public class FragmentBody extends BaseFragment implements View.OnFocusChangeListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private Navigation mNavigation;
    private IAddFocusListener mFocusListener;
    private int bodeColor = 0x782A2B2B;
    private View lastView = null;

    public FragmentBody(IAddFocusListener focusListener, Navigation navigation) {
        mFocusListener = focusListener;
        mNavigation = navigation;
    }

    /**
     * CreateView 再次之前可通过Bundle传递需要的参数
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
            Log.i("onCreateView", mTitle);
        }
        return drawView(inflater.getContext(), this.converPosition(mNavigation, measureZoom(inflater.getContext())));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 根据服务器配置文件动态生成界面
     *
     * @param context
     * @param mNavigation
     * @return
     */
    private View drawView(Context context, Navigation mNavigation) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setFocusable(false);
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(scrollParams);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setFocusable(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);

        FrameLayout.LayoutParams layoutParams;
        for (int j = 0; j < mNavigation.getLayout().size(); j++) {
            final Layout childBean = mNavigation.getLayout().get(j);
            MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setId(j);
            myImageView.setImageURI(childBean.getIcon());
            myImageView.setColour(bodeColor);
            myImageView.setBorder(2);
            myImageView.setFocusable(true);
            myImageView.setScaleType(MyImageView.ScaleType.CENTER_CROP);
//            myImageView.setBackground(getResources().getDrawable(R.drawable.index_recommend, null));
            myImageView.setOnFocusChangeListener(FragmentBody.this);
            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("FragmentBody", String.valueOf(childBean.getId()));
                    Log.i("FragmentBody", String.valueOf(childBean.getIcon()));
                }
            });

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myImageView.setLeft(childBean.getX());
            myImageView.setTop(childBean.getY());
            layoutParams.leftMargin = (childBean.getX());
            layoutParams.topMargin = childBean.getY();
            layoutParams.width = childBean.getWidth();
            layoutParams.height = childBean.getHeight();
            myImageView.setLayoutParams(layoutParams);
            markLastView(myImageView);
            frameLayout.addView(myImageView);
        }
        horizontalScrollView.addView(frameLayout);
        return horizontalScrollView;
    }

    private Navigation converPosition(Navigation mNavigation, float scale) {
        Navigation converNavigation = mNavigation;
        for (int i = 0; i < converNavigation.getLayout().size(); i++) {
            Layout layoutBean = mNavigation.getLayout().get(i);
            layoutBean.setX((int) ((converNavigation.getBaseWidth() * layoutBean.getX() * scale) + (converNavigation.getLineSpace() * scale) * layoutBean.getX()));
            Log.i("FragmentBody", "childBean X " + layoutBean.getX());
            layoutBean.setY((int) ((converNavigation.getBaseHeight() * layoutBean.getY() * scale) + (converNavigation.getLineSpace() * scale) * layoutBean.getY()));
            layoutBean.setWidth((int) (((converNavigation.getBaseWidth() * layoutBean.getWidth()) * scale) + (((layoutBean.getWidth() - 1) * converNavigation.getLineSpace()) * scale)));
            layoutBean.setHeight((int) (((converNavigation.getBaseHeight() * layoutBean.getHeight()) * scale) + (((layoutBean.getHeight() - 1) * converNavigation.getLineSpace()) * scale)));
        } return converNavigation;
    }

    private void markLastView(MyImageView mView) {
        if (null == lastView) {
            lastView = mView;
        } else {
            if (mView.getTop() <= lastView.getTop() && mView.getLeft() >= lastView.getLeft()) {
                lastView = mView;
                Log.i("FragmentBody", "return lastView " + lastView.getId());
            }
        }
    }

    /**
     * 已知服务器和当前设备分辨率计算基数
     *
     * @return
     */
    private float measureZoom(Context context) {
        //得到当前分辨率
        float currentH = DisplayUtil.getScreenHeight(context);
        //已知后端配置为1080p
        float serviceH = 1080f;
        return currentH / serviceH;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 回调给首页当前焦点，做统一处理
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mFocusListener.addFocusListener(v, hasFocus);
    }

    @Override
    public View getLastView() {
        return lastView;
    }
}
