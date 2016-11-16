package com.can.appstore.specialdetail;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import java.util.List;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;
import cn.can.tvlib.ui.focus.CanRecyclerViewFocusHelper;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by atang on 2016/10/24.
 * 专题详情适配器
 */

public class SpecialDetailAdapter extends CanRecyclerViewAdapter<AppInfo>{
    private Context mContext;

    private List<AppInfo> mAppInfos;

    private CanRecyclerViewFocusHelper mCanFocusHelper;

    public SpecialDetailAdapter(List<AppInfo> appInfos, Context context) {
        super(appInfos);
        this.mContext = context;
        this.mAppInfos = appInfos;
    }

    @Override
    protected MyViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.special_detail_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void bindContentData(AppInfo appInfo, RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        ImageLoader.getInstance().load(mContext, viewHolder.mAppImgView, appInfo.getIcon());
        ImageLoader.getInstance().buildTask(viewHolder.mAppImgView, appInfo.getIcon()).bitmapTransformation(new
                GlideRoundTransform(mContext,12)).build().start(mContext);
        viewHolder.mAppNameView.setText(appInfo.getName());
        viewHolder.mAppSize.setText(appInfo.getSizeStr());
        viewHolder.mAppDownloadNum.setText(appInfo.getDownloadCount());
    }

    /**
     * 为 adapter 提供 viewHolder
     */
    private class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mAppImgView;
        TextView mAppNameView;
        TextView mAppSize;
        TextView mAppDownloadNum;

        MyViewHolder(View itemView) {
            super(itemView);
            mAppImgView = (ImageView) itemView.findViewById(R.id.special_detail_item_appicon);
            mAppNameView = (TextView) itemView.findViewById(R.id.special_detail_item_appname);
            mAppSize = (TextView) itemView.findViewById(R.id.special_detail_item_appsize);
            mAppDownloadNum = (TextView) itemView.findViewById(R.id.special_detail_item_appdownloadnum);
        }
    }
}
