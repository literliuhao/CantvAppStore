package com.can.appstore.index.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.can.appstore.index.entity.ChildBean;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.entity.PageBean;
import com.can.appstore.index.interfaces.ICallBack;

/**
 * Created by liuhao on 2016/10/17.
 */
public class VpSimpleFragment extends Fragment implements View.OnFocusChangeListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private PageBean mPageBean;
    private LayoutBean mLayoutBean;
    private ICallBack mICallBack;
    private View view;

    public VpSimpleFragment(LayoutBean layoutBean, ICallBack iCallBack) {
        mLayoutBean = layoutBean;
        mICallBack = iCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
            Log.i("onCreateView", mTitle);
        }
//		inflater = LayoutInflater.from(mContext).finflate(R.layout.item_layout, null, false);

//		Button button = new Button(getActivity());
//		button.setText(mTitle);
//		button.setGravity(Gravity.CENTER);

        //计算缩放
        float percentage = measureZoom();

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(container.getContext());
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(scrollParams);
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);
        FrameLayout.LayoutParams layoutParams;
        for (int j = 0; j < mLayoutBean.getPages().size(); j++) {
            ChildBean childBean = mLayoutBean.getPages().get(j);
            MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setId(j);
            myImageView.setImageURI(childBean.getBg());
            myImageView.setColour(0x782A2B2B);
            myImageView.setBorder(2);
            myImageView.setFocusable(true);
            myImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            myImageView.setBackgroundColor(0x782A2B2B);
            myImageView.setOnFocusChangeListener(VpSimpleFragment.this);
            int[] rect = scaleXY(childBean, percentage);
            layoutParams = new FrameLayout.LayoutParams(rect[2], rect[3]);
            layoutParams.setMargins(rect[0], rect[1], 0, 0);
            myImageView.setLayoutParams(layoutParams);
            if (j == 0) {
                view = myImageView;
            }
            frameLayout.addView(myImageView);
        }

        horizontalScrollView.addView(frameLayout);
        return horizontalScrollView;
    }

    private int[] scaleXY(ChildBean childBean, float percentage) {
        int x = (int) (childBean.getX() * percentage);
        int y = (int) (childBean.getY() * percentage);
        int width = (int) (childBean.getWidth() * percentage);
        int height = (int) (childBean.getHeight() * percentage);
        return new int[]{x, y, width, height};
    }

    private float measureZoom() {
        return 720f / 720f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mICallBack.onSuccess(v,hasFocus);
        }
    }

}
