package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.can.appstore.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by JasonF on 2016/10/20.
 */

public class ImageScaleAdapter extends PagerAdapter {
    private Context mContext;
    private LinkedList<ImageView> mRecycledViews = new LinkedList<ImageView>();
    private List<String> mUrlList = new ArrayList<String>();
    int[] imgRes = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
    private int pageCount;


    public ImageScaleAdapter(Context context, List<String> urlList) {
        super();
        this.mContext = context;
        this.mUrlList = urlList;
        //TODO 正常是mUrlList的size
        pageCount = imgRes.length;
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
        ImageView imageView = null;
        if (mRecycledViews != null && mRecycledViews.size() > 0) {
            imageView = mRecycledViews.getFirst();
            mRecycledViews.removeFirst();
        } else {
            imageView = new ImageView(mContext);
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(imgRes[index]);
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
