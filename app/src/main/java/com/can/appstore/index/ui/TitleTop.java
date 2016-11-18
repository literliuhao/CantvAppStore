package com.can.appstore.index.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.IAddFocusListener;

/**
 * Created by liuhao on 2016/11/2.
 */

public class TitleTop extends Fragment implements View.OnFocusChangeListener,View.OnClickListener {
    private RelativeLayout rlSearch;
    private RelativeLayout rlMessage;
    private IAddFocusListener mFocusListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.index_top,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rlSearch = (RelativeLayout) view.findViewById(R.id.rl_search);
        rlSearch.setOnClickListener(this);
        rlSearch.setOnFocusChangeListener(this);
        rlMessage = (RelativeLayout) view.findViewById(R.id.rl_message);
        rlMessage.setOnClickListener(this);
        rlMessage.setOnFocusChangeListener(this);
    }

    public void initTop(IAddFocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.i("TitleTop", "addFocusListener...." + view.getId());
        mFocusListener.addFocusListener(view, hasFocus);
    }

    @Override
    public void onClick(View view) {
        Log.i("TitleTop", "view...." + view.getId());
        switch (view.getId()){
            case R.id.rl_search:
                Log.i("TitleTop","Search....");
                break;
            case R.id.rl_message:
                Log.i("TitleTop","Message....");
                break;
        }
    }
}
