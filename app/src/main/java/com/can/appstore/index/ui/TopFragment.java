package com.can.appstore.index.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;

/**
 * Created by liuhao on 2016/10/21.
 */

public class TopFragment extends BaseFragment  {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";
    private View viewAll;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
        }
        viewAll = inflater.from(container.getContext()).inflate(R.layout.item_layout, null);
        return viewAll;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static TopFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        TopFragment fragment = new TopFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View getLastView() {
        return null;
    }
}
