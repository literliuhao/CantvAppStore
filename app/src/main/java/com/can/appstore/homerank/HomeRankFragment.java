package com.can.appstore.homerank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.can.appstore.R;
import com.can.appstore.homerank.bean.RankBean;
import com.can.appstore.search.ToastUtil;

import java.util.List;

public class HomeRankFragment extends Fragment implements HomeRankContract.View {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeRankPresenter mHomeRankPresenter;
    private View mView;
//    private RecyclerView mRecyclerView;
    private RankAdapter mRankAdapter;
    private HorizontalScrollView mScrollView;
    private LinearLayout mLinearLayout;


    public HomeRankFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeRankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeRankFragment newInstance(String param1, String param2) {
        HomeRankFragment fragment = new HomeRankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.homerank_recycleview);
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
//        mRankAdapter = new RankAdapter(list);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i=0;i<list.size();i++){
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.homerank_item, null);
            RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.list_view);
            RankBean.DataBean mData = (RankBean.DataBean) list.get(i);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            HomeRankAdapter homeRankAdapter = new HomeRankAdapter(mData.getData());
            recyclerView.setAdapter(homeRankAdapter);
//            mLinearLayout.setLayoutParams(layoutParams);
//            view.setLeft(100);
            mLinearLayout.addView(view);
        }
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        mRecyclerView.setAdapter(mRankAdapter);
    }
}
