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
import com.can.appstore.index.entity.FragmentEnum;
import com.can.appstore.index.interfaces.IAddFocusListener;
import com.can.appstore.index.interfaces.IOnPagerKeyListener;
import com.can.appstore.index.ui.GridManager;

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
    private boolean hasAddFocus;

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
        GridManager gridManager;
        if (convertView == null) {
            if (position < 4) {
                view = inflater.inflate(R.layout.manage_grid_item, null);
            } else if (position == 4) {
                view = inflater.inflate(R.layout.manage_grid_large_item, null);
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
        gridManager = (GridManager) viewGroup;
        if (gridManager.isMeasure) {
            return view;
        }
        mView[position] = view;
        return view;
    }

    public void refreshUI(int position, int number) {
        if (null == mView) return;
        View v = mView[position];
        if (v == null) {
            return;
        }
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

    public void setFocusAll() {
        if(hasAddFocus){
            return;
        }
        for (int i = 0; i < mView.length; i++) {
            if (null != mView[i]) {
                hasAddFocus = true;
                mView[i].setId(i);
                mView[i].setClickable(true);
                mView[i].setFocusable(true);
                mView[i].setOnFocusChangeListener(this);
                mView[i].setOnClickListener(this);
                switch (i) {
                    case 0:
                    case 5:
                        mView[i].setOnKeyListener(this);
                        break;
                }
            } else {
                break;
            }
        }
    }

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
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mPagerKeyListener.onKeyEvent(view, keyCode, keyEvent);
        }
        return false;
    }


}
