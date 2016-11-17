package com.can.appstore.index.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.can.appstore.index.entity.ChildBean;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.interfaces.IAddFocusListener;

import cn.can.tvlib.utils.DisplayUtil;

/**
 * Created by liuhao on 2016/10/17.
 */
public class FragmentBody extends BaseFragment implements View.OnFocusChangeListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private LayoutBean mLayoutBean;
    private IAddFocusListener mFocusListener;
    private int bodeColor = 0x782A2B2B;
    private View lastView = null;
    private int baseW = 270;
    private int baseH = 180;
    private int lineSpace = 8;

    public FragmentBody(IAddFocusListener focusListener, LayoutBean layoutBean) {
        mFocusListener = focusListener;
        mLayoutBean = layoutBean;
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
        return drawView(inflater.getContext(), this.converPosition(mLayoutBean, measureZoom(inflater.getContext())));
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
     * @param mLayoutBean
     * @return
     */
    private View drawView(Context context, LayoutBean mLayoutBean) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setFocusable(false);
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(scrollParams);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setFocusable(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);

        FrameLayout.LayoutParams layoutParams;
        for (int j = 0; j < mLayoutBean.getPages().size(); j++) {
            final ChildBean childBean = mLayoutBean.getPages().get(j);
            MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setId(j);
//            int[] rect = scaleXY(childBean, scaleX);
            myImageView.setImageURI(childBean.getIcon());
//            myImageView.setColour(bodeColor);
//            myImageView.setBorder(2);
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
            layoutParams.leftMargin = (childBean.getX() + 10);
            layoutParams.topMargin = childBean.getY();
            layoutParams.width = childBean.getWidth();
            layoutParams.height = childBean.getHeight();
            myImageView.setLayoutParams(layoutParams);
            markLastView(myImageView);
            frameLayout.addView(myImageView);
        }
        horizontalScrollView.addView(frameLayout);
//        horizontalScrollView.setId(container.getId());

        return horizontalScrollView;
    }

    private LayoutBean converPosition(LayoutBean mLayoutBean, float scale) {
        LayoutBean converBean = mLayoutBean;
        for (int i = 0; i < converBean.getPages().size(); i++) {
            ChildBean childBean = mLayoutBean.getPages().get(i);
            childBean.setX((int) ((baseW * childBean.getX() * scale) + (lineSpace * scale) * childBean.getX()));
            Log.i("FragmentBody", "childBean X " + childBean.getX());
            childBean.setY((int) ((baseH * childBean.getY() * scale) + (lineSpace * scale) * childBean.getY()));
            childBean.setWidth((int) (((baseW * childBean.getWidth()) * scale) + (((childBean.getWidth() - 1) * lineSpace) * scale)));
            childBean.setHeight((int) (((baseH * childBean.getHeight()) * scale) + (((childBean.getHeight() - 1) * lineSpace) * scale)));
        } return converBean;
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
     * 根据分辨率计算当前缩放
     *
     * @param childBean
     * @param percentage
     * @return
     */
    private int[] scaleXY(ChildBean childBean, float percentage) {
        int x = (int) (childBean.getX());
        int y = (int) (childBean.getY());
        int width = (int) (childBean.getWidth());
        int height = (int) (childBean.getHeight());
        return new int[]{x, y, width, height};
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
