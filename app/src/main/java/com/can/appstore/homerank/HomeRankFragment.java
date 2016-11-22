package com.can.appstore.homerank;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.applist.AppListActivity;
import com.can.appstore.entity.Ranking;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.ui.BaseFragment;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.ToastUtils;

public class HomeRankFragment extends BaseFragment implements HomeRankContract.View {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeRankPresenter mHomeRankPresenter;
    private View mView;
    private HorizontalScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private IAddFocusListener mFocusListener;

    public HomeRankFragment(IAddFocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeRankFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeRankFragment newInstance(String param1, String param2) {
//        HomeRankFragment fragment = new HomeRankFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeRankPresenter = new HomeRankPresenter(this);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_rank, container, false);
        Log.w("onCreateView", "onCreateView");
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.w("onViewCreated", "onViewCreated");
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.homeran_scrollview);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_par_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.w("onActivityCreated", "onActivityCreated");
        mHomeRankPresenter.loadingData();
    }

    @Override
    public void startLoading() {
        ToastUtil.toastLong("开始加载数据...");
    }

    @Override
    public void getData(List list) {
        //保存显示的分类布局,为了得到最后那个分类,把右边距去除
        ArrayList<View> views = new ArrayList<>();
        RankItemOnFocusChangelistener rankItemOnFocusChangelistener = new RankItemOnFocusChangelistener();
        for (int i = 0; i < list.size(); i++) {
            //若只有分类标题没有具体的条目,就不显示
            final Ranking mData = (Ranking) list.get(i);
            if (null != mData.getData() && mData.getData().size() > 0) {
                LinearLayout childView = (LinearLayout) mLinearLayout.getChildAt(i);
                childView.setVisibility(View.VISIBLE);
                //分类标签
                //appList
                RecyclerView recyclerView = (RecyclerView) childView.findViewById(R.id.list_view);
                //"更多" 布局
                View ll_more_view = childView.findViewById(R.id.load_more_veiw);
                //设置布局背景
//                setBG(recyclerView, ll_more_view, i);
                setBG(childView, ll_more_view, i);

                TextView categoryMore = (TextView) childView.findViewById(R.id.more_textview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                HomeRankAdapter homeRankAdapter = new HomeRankAdapter(mData.getData(), getActivity());
                //焦点变化的监听
                ll_more_view.setOnFocusChangeListener(rankItemOnFocusChangelistener);
                homeRankAdapter.setMyOnFocusChangeListener(rankItemOnFocusChangelistener);

                recyclerView.setAdapter(homeRankAdapter);

                categoryMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showMessage(getActivity(), mData.getName());
                    }
                });
                ll_more_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppListActivity.actionStart(getActivity(), AppListActivity.PAGE_TYPE_RANKING, "", mData.getId());
                    }
                });

                views.add(childView);
            }
        }
        final LinearLayout view = (LinearLayout) views.get(views.size() - 1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(490, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = 0;
        view.setLayoutParams(layoutParams);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //得到最后一项分类的第一条数据
                RecyclerView recyclerView = (RecyclerView) view.getChildAt(0);
                mLastView = recyclerView.getChildAt(0);
            }
        }, 1500);
    }

    /**
     * 设置每列排行的背景色
     *
     * @param recyview
     * @param moreView
     * @param position
     */
    private void setBG(View recyview, View moreView, int position) {
        int recy_defaultColor = R.drawable.shape_homerank_item_bg1;
        int moreView_defaultColor = R.drawable.homerank_bottom_bg1;

        switch (position) {
            case 0:
                break;
            case 1:
                recy_defaultColor = R.drawable.shape_homerank_item_bg2;
                moreView_defaultColor = R.drawable.homerank_bottom_bg2;
                break;
            case 2:
                recy_defaultColor = R.drawable.shape_homerank_item_bg3;
                moreView_defaultColor = R.drawable.homerank_bottom_bg3;
                break;
            case 3:
                recy_defaultColor = R.drawable.shape_homerank_item_bg4;
                moreView_defaultColor = R.drawable.homerank_bottom_bg4;
                break;
            case 4:
                recy_defaultColor = R.drawable.shape_homerank_item_bg5;
                moreView_defaultColor = R.drawable.homerank_bottom_bg5;
                break;
        }
        recyview.setBackgroundResource(recy_defaultColor);
        moreView.setBackgroundResource(moreView_defaultColor);
    }


    private class RankItemOnFocusChangelistener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            view.setSelected(hasFocus);
            mFocusListener.addFocusListener(view, hasFocus);
        }
    }

    private View mLastView;

    @Override
    public View getLastView() {
        return mLastView;
    }


}
