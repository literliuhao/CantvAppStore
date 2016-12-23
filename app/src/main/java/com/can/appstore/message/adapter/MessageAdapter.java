package com.can.appstore.message.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.db.entity.MessageInfo;

import java.util.List;

import cn.can.tvlib.utils.DateUtil;

/**
 * Created by HEKANG on 2016/11/14.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<MessageInfo> msgList;
    private LayoutInflater mLayoutInflater;

    private View.OnFocusChangeListener mFocusListener;
    private OnMsgItemClickListener mOnMsgItemClickListener;
    private OnMsgItemFocusChangeListener mOnMsgItemFocusChangeListener;

    public interface OnMsgItemClickListener {
        void onMsgClick(int position);

        void onDeleteBtnClick(int position);
    }

    public void setOnMsgItemClickListener(OnMsgItemClickListener onMsgItemClickListener) {
        this.mOnMsgItemClickListener = onMsgItemClickListener;
    }

    public interface OnMsgItemFocusChangeListener {
        void onMsgFocusChange(View msgView, ImageView deleteView, TextView msgTitleView, int position);

        void onDeleteBtnFocusChange(View deleteView, View msgView, TextView msgTitleView, int position);
    }

    public void setOnMsgItemFocusChangeListener(OnMsgItemFocusChangeListener msgItemFocusChangeListener) {
        this.mOnMsgItemFocusChangeListener = msgItemFocusChangeListener;
    }

    public void setFocusListener(View.OnFocusChangeListener focusListener) {
        this.mFocusListener = focusListener;
    }

    public MessageAdapter(List<MessageInfo> msgList) {
        this.msgList = msgList;
    }

    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_msg_recyclerview, parent, false);
        view.setOnFocusChangeListener(mFocusListener);
        return new MessageAdapter.MyViewHolder(view, mOnMsgItemClickListener, mOnMsgItemFocusChangeListener);
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.MyViewHolder holder, final int position) {
        final MessageInfo messageInfo = msgList.get(position);
        if (messageInfo.getStatus()) {
            holder.greenDot.setVisibility(View.INVISIBLE);
            holder.msgTitle.getPaint().setFakeBoldText(false);
        } else {
            holder.greenDot.setVisibility(View.VISIBLE);
            holder.msgTitle.getPaint().setFakeBoldText(true);
        }
        holder.msgTitle.setText(messageInfo.getTitle());
        holder.msgDate.setText(DateUtil.format(Long.valueOf(messageInfo.getDate())*1000 ,DateUtil.FORMAT_YEAR_MONTH_DAY));
        holder.msgDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {
        private final OnMsgItemClickListener mMsgItemClickListener;
        private final OnMsgItemFocusChangeListener mMsgItemFocusChangeListener;
        final TextView msgTitle, msgDate;
        final ImageView greenDot, msgDelete;
        final LinearLayout msgFocusLayout;

        public MyViewHolder(View view, OnMsgItemClickListener msgItemClickListener, OnMsgItemFocusChangeListener mOnMsgItemFocusChangeListener) {
            super(view);
            this.mMsgItemClickListener = msgItemClickListener;
            this.mMsgItemFocusChangeListener = mOnMsgItemFocusChangeListener;
            greenDot = (ImageView) view.findViewById(R.id.item_green_dot);
            msgTitle = (TextView) view.findViewById(R.id.item_tv_msg_title);
            msgDate = (TextView) view.findViewById(R.id.item_tv_msg_date);
            msgDelete = (ImageView) view.findViewById(R.id.item_iv_btn_delete);
            msgFocusLayout = (LinearLayout) view.findViewById(R.id.item_ll_focus_msg);
            msgDelete.setOnClickListener(this);
            msgDelete.setOnKeyListener(this);
            msgDelete.setOnFocusChangeListener(this);
            msgFocusLayout.setOnClickListener(this);
            msgFocusLayout.setOnFocusChangeListener(this);
            msgFocusLayout.setOnKeyListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_ll_focus_msg:
                    if (mMsgItemClickListener != null) {
                        mMsgItemClickListener.onMsgClick(getLayoutPosition());
                    }
                    break;
                case R.id.item_iv_btn_delete:
                    if (mMsgItemClickListener != null) {
                        mMsgItemClickListener.onDeleteBtnClick(getLayoutPosition());
                    }
                    break;
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return getAdapterPosition() == 0 && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mFocusListener != null) {
                mFocusListener.onFocusChange(v, hasFocus);
            }
            switch (v.getId()) {
                case R.id.item_ll_focus_msg:
                    if (mMsgItemFocusChangeListener != null) {
                        mMsgItemFocusChangeListener.onMsgFocusChange(v, msgDelete, msgTitle, getLayoutPosition());
                    }
                    break;
                case R.id.item_iv_btn_delete:
                    if (mMsgItemFocusChangeListener != null) {
                        mMsgItemFocusChangeListener.onDeleteBtnFocusChange(v, msgFocusLayout, msgTitle, getLayoutPosition());
                    }
                    break;
            }
        }
    }
}
