package com.can.appstore.index.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.bumptech.glide.Glide;
import com.can.appstore.R;
import com.can.appstore.entity.Layout;
import com.can.appstore.entity.Navigation;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.index.entity.FragmentEnum;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.model.ActionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.system.DisplayUtil;
import cn.can.tvlib.ui.view.GlideRoundCornerImageView;

/**
 * Created by liuhao on 2016/10/17.
 */
public class FragmentBody extends BaseFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private IOnPagerKeyListener mPagerKeyListener;
    private IAddFocusListener mFocusListener;
    private Navigation mNavigation;
    private FrameLayout frameLayout;
    private View lastView = null;
    private Float SEVICER_HEIGHT = 1080f;
    private Boolean isOnKeyListener = true;
    public List<View> firstColumnViews = new ArrayList<>();

    public FragmentBody() {
    }

    public void setFocusListener(IAddFocusListener focusListener) {
        mFocusListener = focusListener;
    }

    public void setPagerKeyListener(IOnPagerKeyListener pagerKeyListener) {
        mPagerKeyListener = pagerKeyListener;
    }

    public void setPageData(Navigation navigation) {
        mNavigation = navigation;
    }

    public void markOnKeyListener(Boolean bool){
        isOnKeyListener = bool;
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
    private View drawView(final Context context, final Navigation mNavigation) {
        if (null == mNavigation) return null;
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

        frameLayout.setId(Integer.parseInt(mNavigation .getId()));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setFocusable(false);
        frameLayout.setLayoutParams(params);

        FrameLayout imageFrame;
        for (int j = 0; j < mNavigation.getLayout().size(); j++) {
            final Layout childBean = mNavigation.getLayout().get(j);
            final GlideRoundCornerImageView myImageView = new GlideRoundCornerImageView(getActivity());
            myImageView.setScaleType(MyImageView.ScaleType.FIT_XY);
            myImageView.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.px8));
//            myImageView.setImageURI(childBean.getIcon());
            imageFrame = new FrameLayout(context);
            imageFrame.setId(Integer.parseInt(childBean.getId()));
            imageFrame.setBackground(getResources().getDrawable(R.drawable.index_recommend));
            imageFrame.setFocusable(true);
            imageFrame.setOnFocusChangeListener(FragmentBody.this);
            imageFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //StartActivity
                        ActionUtils.getInstance().convertAction(context, mNavigation.getTitle() + "-" + childBean.getLocation(), childBean.getAction(), childBean.getActionData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (childBean.getX() == 0 && isOnKeyListener) {
                imageFrame.setOnKeyListener(this);
            }

            if (ActionUtils.getInstance().checkURL(childBean.getIcon())) {
                myImageView.load(childBean.getIcon(), 0, R.mipmap.icon_load_default, R.mipmap.icon_loading_fail, true);
            } else {
                Glide.with(context).load(ActionUtils.getInstance().getResourceId(childBean.getIcon())).into(myImageView);
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageFrame.setLeft(childBean.getX());
            imageFrame.setTop(childBean.getY());
            layoutParams.leftMargin = (childBean.getX());
            layoutParams.topMargin = childBean.getY();
            layoutParams.width = childBean.getWidth();
            layoutParams.height = childBean.getHeight();

            if(layoutParams.leftMargin == 0){
                firstColumnViews.add(imageFrame);
            }

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
        if (null == converNavigation) return null;
        for (int i = 0; i < converNavigation.getLayout().size(); i++) {
            Layout layoutBean = converNavigation.getLayout().get(i);
            layoutBean.setX((int) ((converNavigation.getBaseWidth() * layoutBean.getX() * scale) + (converNavigation.getLineSpace() * scale) * layoutBean.getX()));
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
            }
        }
    }

    public static FragmentBody newInstance(IndexActivity indexActivity, Navigation navigation) {
        FragmentBody fragmentBody = new FragmentBody();
        fragmentBody.setFocusListener(indexActivity);
        fragmentBody.setPagerKeyListener(indexActivity);
        fragmentBody.setPageData(navigation);
        return fragmentBody;
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
        float serviceH = SEVICER_HEIGHT;
        return currentH / serviceH;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNavigation = null;
    }

    /**
     * 回调给首页当前焦点，做统一处理
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mFocusListener.addFocusListener(v, hasFocus, FragmentEnum.NORMAL);
    }

    @Override
    public View getLastView() {
        return lastView;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mPagerKeyListener.onKeyEvent(view, keyCode, keyEvent);
        }
        return false;
    }
}
