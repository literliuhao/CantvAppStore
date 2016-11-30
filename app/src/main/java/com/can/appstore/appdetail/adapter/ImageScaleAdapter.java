package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;


/**
 * Created by JasonF on 2016/10/20.
 */

public class ImageScaleAdapter extends PagerAdapter {
    private Context mContext;
    private LinkedList<ImageView> mRecycledViews = new LinkedList<>();
    private List<String> mUrlList = new ArrayList<>();
    private int pageCount;
    private int mRoundSize;


    public ImageScaleAdapter(Context context, List<String> urlList) {
        super();
        this.mContext = context;
        this.mUrlList = urlList;
        mRoundSize = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_8px);
        pageCount = mUrlList.size();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = getRealPosition(position);
        ImageView imageView;
        if (mRecycledViews != null && mRecycledViews.size() > 0) {
            imageView = mRecycledViews.getFirst();
            mRecycledViews.removeFirst();
        } else {
            imageView = new ImageView(mContext);
        }
        imageView.setBackgroundResource(R.drawable.bg_item);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        final ImageView finalImageView = imageView;
        ImageLoader.getInstance()
                .buildTask(imageView, mUrlList.get(index))
                .bitmapTransformation(new GlideRoundTransform(mContext, mRoundSize))
                .placeholder(R.mipmap.icon_load_default)
                .errorHolder(R.mipmap.icon_loading_fail)
                .successCallback(new GlideLoadTask.SuccessCallback() {
                    @Override
                    public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        finalImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        finalImageView.setImageDrawable(resource);
                        return true;
                    }
                })
                .build()
                .start(mContext);
        container.addView(imageView);
        return imageView;
    }

    public int getRealPosition(int adapterPosition) {
        return adapterPosition % pageCount;
    }

    public int getInstantiatePosition(int preferPosition) {
        int position = Integer.MAX_VALUE >> 1;
        return position - position % pageCount + preferPosition;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        if (object != null) {
            mRecycledViews.addLast((ImageView) object);
        }
    }
}
