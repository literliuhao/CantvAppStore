package com.can.appstore.index.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.ICallBack;

/**
 * Created by liuhao on 2016/10/21.
 */

public class ManagerFragment extends Fragment implements View.OnFocusChangeListener {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private View viewAll;
//    private FocusMoveUtil mFocusUtils;
//    private FocusScaleUtil mFocusScaleUtils;
    private ICallBack mICallBack;
    private LayoutInflater mInflater;
    private RelativeLayout mrl_1;
    private RelativeLayout mrl_2;
    private RelativeLayout mrl_3;
    private RelativeLayout mrl_4;
    private RelativeLayout mrl_5;
    private RelativeLayout mrl_6;
    private RelativeLayout mrl_7;
    private RelativeLayout mrl_8;

    public ManagerFragment(ICallBack iCallBack) {
        mICallBack = iCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mInflater = inflater;
        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
        }
        viewAll = inflater.from(container.getContext()).inflate(R.layout.index_manager, null);
        return viewAll;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

    }

    /**
     * 管理页面：view初始化
     */
    private void initViews() {
        mrl_1 = (RelativeLayout) getView().findViewById(R.id.rl_item1);
        mrl_2 = (RelativeLayout) getView().findViewById(R.id.rl_item2);
        mrl_3 = (RelativeLayout) getView().findViewById(R.id.rl_item3);
        mrl_4 = (RelativeLayout) getView().findViewById(R.id.rl_item4);
        mrl_5 = (RelativeLayout) getView().findViewById(R.id.rl_item5);
        mrl_6 = (RelativeLayout) getView().findViewById(R.id.rl_item6);
        mrl_7 = (RelativeLayout) getView().findViewById(R.id.rl_item7);
        mrl_8 = (RelativeLayout) getView().findViewById(R.id.rl_item8);
        mrl_1.setFocusable(true);
        mrl_2.setFocusable(true);
        mrl_3.setFocusable(true);
        mrl_4.setFocusable(true);
        mrl_5.setFocusable(true);
        mrl_6.setFocusable(true);
        mrl_7.setFocusable(true);
        mrl_8.setFocusable(true);
        mrl_1.setOnFocusChangeListener(this);
        mrl_2.setOnFocusChangeListener(this);
        mrl_3.setOnFocusChangeListener(this);
        mrl_4.setOnFocusChangeListener(this);
        mrl_5.setOnFocusChangeListener(this);
        mrl_6.setOnFocusChangeListener(this);
        mrl_7.setOnFocusChangeListener(this);
        mrl_8.setOnFocusChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initFocus() {
//        mFocusUtils = new FocusMoveUtil(mInflater.getContext(), getView(), R.drawable.image_focus);
//        mFocusScaleUtils = new FocusScaleUtil(300, 300, 1.05f, null, null);
    }

    /**
     * 所有焦点移动操作都给IndexActivity
     * 因此删除许多逻辑代码
     *
     * @param view
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        mICallBack.onSuccess(view, hasFocus);
    }
}
