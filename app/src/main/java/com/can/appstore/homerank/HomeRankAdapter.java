package com.can.appstore.homerank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.search.widget.YIBaseCompatFocusAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by yibh on 2016/10/20 17:11 .
 */

public class HomeRankAdapter extends YIBaseCompatFocusAdapter {
    private List mList;
    private Context mContext;
    public List<View> mViewList = new ArrayList<>();

    public HomeRankAdapter(List datas, Context context) {
        super(datas);
        this.mList = datas;
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_list_item, parent, false);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                ToastUtils.showMessage(parent.getContext(), ((AppInfo) mList.get(position)).getName());
            }
        });
        return new RankAppItemViewHolder(view);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RankAppItemViewHolder) {
            mViewList.add(((RankAppItemViewHolder) holder).mView);
            AppInfo mApp = (AppInfo) mList.get(position);
            ((RankAppItemViewHolder) holder).mAppName.setText(mApp.getName());
            ImageLoader.getInstance().load(MyApp.mContext, ((RankAppItemViewHolder) holder).mAppIcon, mApp.getIcon());
            if ((position + 1) % 2 == 0) {
                ((RankAppItemViewHolder) holder).mView.setBackgroundColor(mContext.getResources().getColor(R.color.h_rank_transulcent));
            }
        }
    }

    class RankAppItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAppIcon;
        private TextView mAppName;
        private View mView;

        public RankAppItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppIcon = (ImageView) itemView.findViewById(R.id.icon_view);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
        }
    }

}
