package com.can.appstore.index.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.can.appstore.index.entity.ChildBean;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.interfaces.ICallBack;

/**
 * Created by liuhao on 2016/10/17.
 */
public class FragmentBody extends Fragment implements View.OnFocusChangeListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private LayoutBean mLayoutBean;
    private ICallBack mICallBack;
    private int bodeColor = 0x782A2B2B;

    public FragmentBody(LayoutBean layoutBean, ICallBack iCallBack) {
        mLayoutBean = layoutBean;
        mICallBack = iCallBack;
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
        return drawView(container, measureZoom());
    }

    /**
     * 根据服务器配置文件动态生成界面
     *
     * @param container
     * @param scaleX    当前缩放比例
     */
    private HorizontalScrollView drawView(ViewGroup container, float scaleX) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(container.getContext());
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(scrollParams);
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);
        FrameLayout.LayoutParams layoutParams;
        for (int j = 0; j < mLayoutBean.getPages().size(); j++) {
            final ChildBean childBean = mLayoutBean.getPages().get(j);
            MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setId(j);
            myImageView.setImageURI(childBean.getBg());
            myImageView.setColour(bodeColor);
            myImageView.setBorder(2);
            myImageView.setFocusable(true);
            myImageView.setScaleType(MyImageView.ScaleType.CENTER_CROP);
            myImageView.setBackgroundColor(bodeColor);
            myImageView.setOnFocusChangeListener(FragmentBody.this);
            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("FragmentBody", String.valueOf(childBean.getId()));
                    Log.i("FragmentBody", String.valueOf(childBean.getBg()));
                }
            });
            int[] rect = scaleXY(childBean, scaleX);
            layoutParams = new FrameLayout.LayoutParams(rect[2], rect[3]);
            layoutParams.setMargins(rect[0], rect[1], 0, 0);
            myImageView.setLayoutParams(layoutParams);
            frameLayout.addView(myImageView);
        }
        horizontalScrollView.addView(frameLayout);

        return horizontalScrollView;
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
    private float measureZoom() {
        //得到当前分辨率
        float currentH = 720f;
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
        mICallBack.onSuccess(v, hasFocus);
    }

}
