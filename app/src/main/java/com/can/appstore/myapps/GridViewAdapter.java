package com.can.appstore.myapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

/**
 * Created by wei on 2016/10/20.
 */

public class GridViewAdapter   extends BaseAdapter {


    LayoutInflater mInflater;
    List<AppInfo> mDatas;

    int[] mItemColors = { R.color.square_blue, R.color.square_brown, R.color.square_green, R.color.square_orange,
            R.color.square_purple, R.color.square_red };




    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    OnItemClickListener mClickListener;

    void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public GridViewAdapter(Context context, List<AppInfo> datas) {
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.allapps_grid_item, parent, false);
            holder.tv = (TextView) convertView.findViewById(R.id.id_tv);
            holder.img = (ImageView) convertView.findViewById(R.id.id_appicon);
            holder.itemBg = (LinearLayout) convertView.findViewById(R.id.id_item_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(mDatas.get(position).appName);
        holder.img.setBackground(mDatas.get(position).appIcon);
        holder.itemBg.setBackgroundResource(mItemColors[position % 6]);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView tv;
        ImageView img;
        LinearLayout itemBg;
    }


    public abstract static class OnFocusChangeListener {
        public boolean onFocusMoveOutside(int currFocus, int direction){
            return false;
        }
        public abstract void onItemFocusChanged(View view, int position, boolean hasFocus, Object dataType);
    }

    final public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.mFocusChangeListener = listener;
    }

    private OnFocusChangeListener mFocusChangeListener;

}
