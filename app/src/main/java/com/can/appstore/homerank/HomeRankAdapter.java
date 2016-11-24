package com.can.appstore.homerank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;

import static com.can.appstore.R.drawable.home_rank1;
import static com.can.appstore.R.drawable.home_rank2;
import static com.can.appstore.R.drawable.home_rank3;

/**
 * Created by yibh on 2016/10/20 17:11 .
 */

public class HomeRankAdapter extends RecyclerView.Adapter<HomeRankAdapter.RankAppItemViewHolder> {
    private List mList;
    private Context mContext;
    public List<View> mViewList = new ArrayList<>();

    public HomeRankAdapter(List datas, Context context) {
        this.mList = datas;
        mContext = context;
    }

    @Override
    public RankAppItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_list_item, parent, false);
        return new RankAppItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankAppItemViewHolder holder, int position) {
        mViewList.add(holder.mView);
        final AppInfo mApp = (AppInfo) mList.get(position);
        holder.mAppName.setText(mApp.getName());
        ImageLoader.getInstance().load(MyApp.mContext, holder.mAppIcon, mApp.getIcon());
        if ((position + 1) % 2 == 0) {
            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.h_rank_transulcent));
        }
        //每列第一条比较高
        if (position == 0) {
            holder.mView.setLayoutParams(new LinearLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.px490)
                    , (int) mContext.getResources().getDimension(R.dimen.px136)));
        }
        //焦点变化监听
        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (null != mOnFocusChangeListener) {
                    mOnFocusChangeListener.onFocusChange(view, b);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDetailActivity.actionStart(mContext,mApp.getId());
            }
        });
        setAppRImg(holder.mAppRImg, holder.mAPPRText, position);
        Log.w("ranAdapter", holder.mAppRImg.getWidth() + "~ ~" + holder.mAppRImg.getHeight());

    }

    /**
     * 设置右侧标记图片及图片大小,文字等
     *
     * @param imageView
     * @param position
     */
    private void setAppRImg(ImageView imageView, TextView textView, int position) {
        if (position >= 3) {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(position + 1 + "");
            return;
        } else {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        int defaultImg = R.drawable.home_rank1;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int w_dimen = R.dimen.px56;
        int h_dimen = R.dimen.px78;
        switch (position + 1) {
            case 1:
                defaultImg = home_rank1;
                w_dimen = R.dimen.px56;
                h_dimen = R.dimen.px78;
                break;
            case 2:
                w_dimen = R.dimen.px42;
                h_dimen = R.dimen.px60;
                defaultImg = home_rank2;
                break;
            case 3:
                w_dimen = R.dimen.px42;
                h_dimen = R.dimen.px60;
                defaultImg = home_rank3;
                break;
            case 4:
                w_dimen = R.dimen.px26;
                h_dimen = R.dimen.px26;
//                defaultImg = home_rank4;
                break;
            case 5:
                w_dimen = R.dimen.px26;
                h_dimen = R.dimen.px26;
//                defaultImg = home_rank5;
                break;
            default:
                break;
        }
        layoutParams.width = (int) mContext.getResources().getDimension(w_dimen);
        layoutParams.height = (int) mContext.getResources().getDimension(h_dimen);
        imageView.setImageResource(defaultImg);
        imageView.setLayoutParams(layoutParams);
    }


    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    class RankAppItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAppIcon;
        private TextView mAppName;
        private View mView;
        private ImageView mAppRImg;
        private TextView mAPPRText;

        public RankAppItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppIcon = (ImageView) itemView.findViewById(R.id.icon_view);
            mAppRImg = (ImageView) itemView.findViewById(R.id.rank_r_img);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
            mAPPRText = (TextView) itemView.findViewById(R.id.rank_r_text);
        }
    }

    private View.OnFocusChangeListener mOnFocusChangeListener;

    public void setMyOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

}
