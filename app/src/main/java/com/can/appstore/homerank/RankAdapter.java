package com.can.appstore.homerank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.homerank.bean.AppInfo;
import com.can.appstore.homerank.bean.RankBean;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by yibh on 2016/10/17 11:35 .
 */

public class RankAdapter extends CanRecyclerViewAdapter {

    private List mList;

    public RankAdapter(List datas) {
        super(datas);
        this.mList = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homerank_category_item, parent, false);
        return new HomeRankViewHolder(view);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeRankViewHolder) {
            RankBean.DataBean mData = (RankBean.DataBean) mList.get(position);
            ((HomeRankViewHolder) holder).mCateTitleView.setText(mData.getName());
            List<AppInfo> appList = mData.getData();
            ((HomeRankViewHolder) holder).mAppName1.setText(appList.get(0).getName());
            ((HomeRankViewHolder) holder).mAppName2.setText(appList.get(1).getName());
            ((HomeRankViewHolder) holder).mAppName3.setText(appList.get(2).getName());
//            ((HomeRankViewHolder) holder).mAppName4.setText(appList.get(3).getName());
//            ((HomeRankViewHolder) holder).mAppName5.setText(appList.get(4).getName());
        }
    }

    class HomeRankViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRLView1;
        private TextView mCateTitleView;    //分类标题
        private TextView mCateMoreView;    //分类更多
        private ImageView mAppIcon1;
        private ImageView mAppIcon2;
        private ImageView mAppIcon3;
        private ImageView mAppIcon4;
        private ImageView mAppIcon5;
        private TextView mAppName1;
        private TextView mAppName2;
        private TextView mAppName3;
        private TextView mAppName4;
        private TextView mAppName5;

        public HomeRankViewHolder(View itemView) {
            super(itemView);
            mRLView1= (RelativeLayout) itemView.findViewById(R.id.rlview1);
            mRLView1.requestFocus();
            mCateTitleView = (TextView) itemView.findViewById(R.id.rank_category_title_view);
            mCateMoreView = (TextView) itemView.findViewById(R.id.rank_category_more_view);
            mAppIcon1 = (ImageView) itemView.findViewById(R.id.icon_view1);
            mAppIcon2 = (ImageView) itemView.findViewById(R.id.icon_view2);
            mAppIcon3 = (ImageView) itemView.findViewById(R.id.icon_view3);
            mAppIcon4 = (ImageView) itemView.findViewById(R.id.icon_view4);
            mAppIcon5 = (ImageView) itemView.findViewById(R.id.icon_view5);
            mAppName1 = (TextView) itemView.findViewById(R.id.app_name_view1);
            mAppName2 = (TextView) itemView.findViewById(R.id.app_name_view2);
            mAppName3 = (TextView) itemView.findViewById(R.id.app_name_view3);
            mAppName4 = (TextView) itemView.findViewById(R.id.app_name_view4);
            mAppName5 = (TextView) itemView.findViewById(R.id.app_name_view5);
        }
    }

}
