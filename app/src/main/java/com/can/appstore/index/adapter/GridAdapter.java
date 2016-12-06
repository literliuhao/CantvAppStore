package com.can.appstore.index.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.ui.FragmentEnum;

/**
 * Created by liuhao on 2016/11/15.
 */

public class GridAdapter extends BaseAdapter implements View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener {
    private Context mContext;
    private int[] mNames;
    private int[] mIcons;
    private int[] mColors;
    private IAddFocusListener mFocusListener;
    private View.OnClickListener mClickListener;
    private IOnPagerKeyListener mPagerKeyListener;
    private View[] mView;

    public GridAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mNames.length;
    }

    @Override
    public Object getItem(int i) {
        return mNames[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (convertView == null) {
            if (position < 4) {
                view = inflater.inflate(R.layout.manage_grid_item, null);
            } else {
                view = inflater.inflate(R.layout.manage_grid_mini_item, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.tv_manage_name);
            textView.setText(mNames[position]);

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_manage_icon);
            imageView.setImageResource(mIcons[position]);

            ImageView imageColor = (ImageView) view.findViewById(R.id.iv_manage_color);
            imageColor.setImageResource(mColors[position]);
        } else {
            view = convertView;
        }
        view.setId(position);
        view.setClickable(true);
        view.setFocusable(true);
        view.setOnFocusChangeListener(this);
        view.setOnClickListener(this);
        switch (position) {
            case 0:
            case 4:
                view.setOnKeyListener(this);
                break;
        }
        mView[position] = view;
        return view;
    }

    public void refreshUI(int position, int number) {
        if (null == mView) return;
        View v = mView[position];
        ImageView imageSize = (ImageView) v.findViewById(R.id.iv_manage_size);
        TextView textSize = (TextView) v.findViewById(R.id.tv_manage_text);
        if (number > 0) {
            imageSize.setVisibility(View.VISIBLE);
            textSize.setVisibility(View.VISIBLE);
            textSize.setText(String.valueOf(number));
        } else {
            imageSize.setVisibility(View.GONE);
            textSize.setVisibility(View.GONE);
        }
    }

//    public void refreshDown(int number) {
//        if (null == mUpdateView) return;
//        ImageView imageSize = (ImageView) mUpdateView.findViewById(R.id.iv_manage_size);
//        TextView textSize = (TextView) mUpdateView.findViewById(R.id.tv_manage_text);
//        if (number > 0) {
//            imageSize.setVisibility(View.VISIBLE);
//            textSize.setVisibility(View.VISIBLE);
//            textSize.setText(String.valueOf(number));
//        } else {
//            imageSize.setVisibility(View.GONE);
//            textSize.setVisibility(View.GONE);
//        }
//    }

    public void setFocusListener(IAddFocusListener focusListener) {
        this.mFocusListener = focusListener;
    }

    public void setKeyListener(IOnPagerKeyListener onPagerKeyListener) {
        this.mPagerKeyListener = onPagerKeyListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setNames(int[] names) {
        this.mNames = names;
        this.mView = new View[mNames.length];
    }

    public void setIcons(int[] icons) {
        this.mIcons = icons;
    }

    public void setColors(int[] colors) {
        this.mColors = colors;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        mFocusListener.addFocusListener(view, b, FragmentEnum.MANAGE);
    }

    @Override
    public void onClick(View view) {
        mClickListener.onClick(view);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == 21) {
                mPagerKeyListener.onKeyEvent(view, keyCode, keyEvent);
            }
        }
        return false;
    }
}
