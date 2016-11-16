package com.can.appstore.index.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.can.appstore.R;
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
    private FrameLayout frameLayout;

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
        return drawView(inflater.getContext(), measureZoom(inflater.getContext()));
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
     * @param scaleX 当前缩放比例
     */
    private View drawView(Context context, float scaleX) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setFocusable(false);
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(scrollParams);
        frameLayout = new FrameLayout(context);
        frameLayout.setFocusable(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);

        FrameLayout.LayoutParams layoutParams;
        for (int j = 0; j < mLayoutBean.getPages().size(); j++) {
            final ChildBean childBean = mLayoutBean.getPages().get(j);
            MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setId(j);
            int[] rect = scaleXY(childBean, scaleX);
            myImageView.setImageURI(childBean.getBg());
//            myImageView.setColour(bodeColor);
//            myImageView.setBorder(2);
            myImageView.setFocusable(true);
            myImageView.setScaleType(MyImageView.ScaleType.CENTER_CROP);
            myImageView.setBackground(getResources().getDrawable(R.drawable.index_recommend, null));
            myImageView.setOnFocusChangeListener(FragmentBody.this);
            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("FragmentBody", String.valueOf(childBean.getId()));
                    Log.i("FragmentBody", String.valueOf(childBean.getBg()));
                }
            });

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myImageView.setLeft(rect[0]);
            myImageView.setTop(rect[1]);
            layoutParams.leftMargin = rect[0];
            layoutParams.topMargin = rect[1];
            layoutParams.width = rect[2];
            layoutParams.height = rect[3];
            myImageView.setLayoutParams(layoutParams);
            markLastView(myImageView);
            frameLayout.addView(myImageView);
        }
        horizontalScrollView.addView(frameLayout);
//        horizontalScrollView.setId(container.getId());

        return horizontalScrollView;
    }

    private void markLastView(MyImageView mView) {
        if (null == lastView) {
            lastView = mView;
        } else {
            if (mView.getTop() <= lastView.getTop() && mView.getLeft() >= lastView.getLeft()) {
                lastView = frameLayout.getChildAt(frameLayout.getChildCount() - 1);
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
        int x = (int) (childBean.getX() * percentage);
        int y = (int) (childBean.getY() * percentage);
        int width = (int) (childBean.getWidth() * percentage);
        int height = (int) (childBean.getHeight() * percentage);
        return new int[]{x, y, width, height};
    }

    /**
     * 已知服务器和当前设备分辨率计算基数
     *
     * @return
     */
    private float measureZoom(Context context) {
        //得到当前分辨率
        float currentH = DisplayUtil.getScreenWidth(context);
        //已知后端配置为1080p
        float serviceH = 1920f;
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
