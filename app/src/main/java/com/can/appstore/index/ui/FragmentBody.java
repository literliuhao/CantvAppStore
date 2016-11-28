package com.can.appstore.index.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;
import com.can.appstore.entity.Layout;
import com.can.appstore.entity.Navigation;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.model.ActionUtils;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;
import cn.can.tvlib.utils.DisplayUtil;

import static cn.can.tvlib.imageloader.GlideLoadTask.SuccessCallback;

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
    private FrameLayout frameLayout;

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
    private View drawView(final Context context, Navigation mNavigation) {
        FrameLayout mainLayout = new FrameLayout(context);
        ViewGroup.LayoutParams mainParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainLayout.setLayoutParams(mainParams);
        mainLayout.setPadding((int) getResources().getDimension(R.dimen.px160), (int) getResources().getDimension(R.dimen.px30), (int) getResources().getDimension(R.dimen.px100), (int) getResources().getDimension(R.dimen.px0));
        mainLayout.setClipToPadding(false);
        mainLayout.setClipChildren(false);
        mainLayout.setFocusable(false);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setClipToPadding(false);
        horizontalScrollView.setClipChildren(false);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setFocusable(false);
        frameLayout = new FrameLayout(context);
        frameLayout.setId(Integer.parseInt(mNavigation.getId()));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setFocusable(false);
        frameLayout.setLayoutParams(params);

        FrameLayout imageFrame;
        for (int j = 0; j < mNavigation.getLayout().size(); j++) {
            final Layout childBean = mNavigation.getLayout().get(j);
            final MyImageView myImageView = new MyImageView(getActivity());
            myImageView.setScaleType(MyImageView.ScaleType.CENTER);
            myImageView.setImageURI(childBean.getIcon());
            imageFrame = new FrameLayout(context);
            imageFrame.setId(j);
            imageFrame.setBackground(getResources().getDrawable(R.drawable.index_recommend));
            imageFrame.setFocusable(true);
            imageFrame.setOnFocusChangeListener(FragmentBody.this);
            imageFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //StartActivity
                        ActionUtils.convertAction(context, childBean.getAction(), childBean.getActionData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ImageLoader.getInstance().buildTask(myImageView, childBean.getIcon()).bitmapTransformation(new GlideRoundTransform(context, getResources().getDimension(R.dimen.px8))).size(childBean.getWidth(), childBean.getHeight()).placeholder(R.mipmap.icon_load_default).errorHolder(R.mipmap.icon_loading_fail).successCallback(new SuccessCallback() {
                @Override
                public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    myImageView.setImageDrawable(resource);
                    return true;
                }
            }).build().start(context);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageFrame.setLeft(childBean.getX());
            imageFrame.setTop(childBean.getY());
            layoutParams.leftMargin = (childBean.getX());
            layoutParams.topMargin = childBean.getY();
            layoutParams.width = childBean.getWidth();
            layoutParams.height = childBean.getHeight();
            imageFrame.setLayoutParams(layoutParams);
            markLastView(imageFrame);
            imageFrame.addView(myImageView);
            frameLayout.addView(imageFrame);
        }
        horizontalScrollView.addView(frameLayout);
        mainLayout.addView(horizontalScrollView);
        return mainLayout;
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
        }
        return converNavigation;
    }

    private void markLastView(FrameLayout mView) {
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
    public void registerFocus() {
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            frameLayout.getChildAt(i).setFocusable(true);
        }
    }

    @Override
    public void removeFocus() {
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            frameLayout.getChildAt(i).setFocusable(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        Log.i("FragmentBody", v.getId() + "");
//        v.bringToFront();
        mFocusListener.addFocusListener(v, hasFocus, FragmentEnum.NORMAL);
    }

    @Override
    public View getLastView() {
        return lastView;
    }
}
