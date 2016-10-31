package com.can.appstore.update;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

/**
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerAdapter extends RecyclerView.Adapter<UpdateManagerAdapter.UpdateViewHolder> {

    private List<AppInfoBean> mDatas;
    private LayoutInflater mInflater;
    private Context mContext;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public UpdateManagerAdapter(Context context, List<AppInfoBean> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        this.mContext = context;
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UpdateViewHolder holder = new UpdateViewHolder(mInflater.inflate(
                R.layout.ac_updatemanager_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final UpdateViewHolder holder, final int position) {
        holder.appName.setText(mDatas.get(position).getAppName());
        holder.appSize.setText(mDatas.get(position).getAppSize());
        holder.appVersioncode.setText(mDatas.get(position).getVersionName());
        //Glide.with(MyApp.mContext).load(mDatas.get(position).getIcon()).into(holder.appIcon);
        holder.appIcon.setImageDrawable(mDatas.get(position).getIcon());
        holder.updatedIcon.setVisibility(mDatas.get(position).getInstall() ? View.VISIBLE : View.INVISIBLE);
        holder.downloading.setVisibility(mDatas.get(position).getIsInstalling() ? (mDatas.get(position).getInstall() ? View.INVISIBLE : View.VISIBLE) : View.INVISIBLE);
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appVersioncode;
        TextView appSize;
        ProgressBar progressbar;
        ImageView updatedIcon;
        TextView downloading;

        public UpdateViewHolder(View view) {
            super(view);
            appName = (TextView) view.findViewById(R.id.tv_updateapp_name);
            appSize = (TextView) view.findViewById(R.id.tv_updateapp_size);
            appVersioncode = (TextView) view.findViewById(R.id.tv_updateapp_versioncode);
            appIcon = (ImageView) view.findViewById(R.id.iv_updateapp_icon);
            progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
            updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
            downloading = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
        }
    }
}
