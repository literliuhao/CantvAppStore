package com.can.appstore.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.can.appstore.R;
import com.can.appstore.index.adapter.GridAdapter;
import com.can.appstore.index.interfaces.IAddFocusListener;

/**
 * Created by liuhao on 2016/10/21.
 */

public class ManagerFragment extends BaseFragment {
    private final int[] NAMES = {R.string.index_manager_text1, R.string.index_manager_text2, R.string.index_manager_text3, R.string.index_manager_text4, R.string.index_manager_text5, R.string.index_manager_text6, R.string.index_manager_text7, R.string.index_manager_text8};
    private final int[] ICONS = {R.drawable.index_manager_icon1, R.drawable.index_manager_icon2, R.drawable.index_manager_icon3, R.drawable.index_manager_icon4, R.drawable.index_manager_icon5, R.drawable.index_manager_icon6, R.drawable.index_manager_icon7, R.drawable.index_manager_icon8};
    private final int[] COLORS = {R.drawable.index_item1_shape, R.drawable.index_item2_shape, R.drawable.index_item3_shape, R.drawable.index_item4_shape, R.drawable.index_item5_shape, R.drawable.index_item6_shape, R.drawable.index_item7_shape, R.drawable.index_item8_shape};
    private GridView gridView;
    private GridAdapter gridAdapter;
    private IAddFocusListener mFocusListener;

    public ManagerFragment(IAddFocusListener focusListener) {
        mFocusListener = focusListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(inflater.getContext()).inflate(R.layout.index_manage_grid, container, false);
        gridView = (GridView) view.findViewById(R.id.manage_grid);
        gridView.setFocusable(false);
        gridAdapter = new GridAdapter(inflater.getContext());
        gridAdapter.setNames(NAMES);
        gridAdapter.setIcons(ICONS);
        gridAdapter.setColors(COLORS);
        gridAdapter.setFocusListener(new IAddFocusListener() {
            @Override
            public void addFocusListener(View v, boolean hasFocus) {
                mFocusListener.addFocusListener(v, hasFocus);
            }
        });

        gridAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ManagerFragment", view.getId() + "");
                switch (view.getId()) {
                    //一键加速
                    case 0:
                        break;
                    //更新管理
                    case 1:
                        getActivity().startActivity(new Intent("com.can.appstore.ACTION.ACTIVITY_UPDATE"));
                        break;
                    //文件管理
                    case 2:
                        break;
                    //电视助手
                    case 3:
                        break;
                    //网络测速
                    case 4:
                        break;
                    //卸载管理
                    case 5:
                        break;
                    //安装包管理
                    case 6:
                        getActivity().startActivity(new Intent("com.can.appstore.ACTION.ACTIVITY_INSTALL"));
                        break;
                    //下载管理
                    case 7:
                        break;
                    default:
                        break;
                }
            }
        });


        gridView.setAdapter(gridAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View getLastView() {
        return gridView.getChildAt(3);
    }


//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
//        Log.i("ManagerFragment", view.getId() + " " + postion);
//    }

}
