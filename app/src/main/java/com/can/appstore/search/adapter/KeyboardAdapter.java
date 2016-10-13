package com.can.appstore.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;
import com.can.appstore.search.YAroundTextView;

import java.util.List;

/**
 * Created by yibh on 2016/10/11 19:51 .
 */

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.KeyboardViewHolder> {

    private List<String> mKeyList;
    private OnItemClickListener mOnItemClickListener;

    public KeyboardAdapter(List<String> keyList) {
        mKeyList = keyList;
    }

    @Override
    public KeyboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_search_left_key_item, parent, false);
        return new KeyboardViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(KeyboardViewHolder holder, int position) {
        holder.mTextView.setText(mKeyList.get(position));
//        holder.mTextView.setAroundColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return mKeyList.size();
    }

    public class KeyboardViewHolder extends RecyclerView.ViewHolder {
        YAroundTextView mTextView;

        public KeyboardViewHolder(View itemView) {
            super(itemView);
            mTextView = (YAroundTextView) itemView.findViewById(R.id.key_item_view);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClick(getLayoutPosition(), mKeyList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int index, String content);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
