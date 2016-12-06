package com.can.appstore.message.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.message.db.entity.MessageInfo;

import java.util.List;

/**
 * Created by HEKANG on 2016/11/14.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<MessageInfo> msgList;
    private OnMsgFocusLayoutClickListener msgFocusLayoutClickListener;
    private OnMsgFocusLayoutFocusChangeListener msgFocusLayoutFocusChangeListener;
    private View.OnFocusChangeListener mFocusListener;
    private OnItemRemoveListener mRemoveListener;
    private View mFocusedDeleteBtn;
    private LayoutInflater mLayoutInflater;
    private final Handler mHandler = new Handler();
    private final Runnable showMsgDelete = new Runnable() {
        @Override
        public void run() {
            if (mFocusedDeleteBtn != null) {
                mFocusedDeleteBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    public interface OnItemRemoveListener {
        void onRemoveItem(int position , String msgId);
    }

    public void setOnItemRemoveListener(OnItemRemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    public interface OnMsgFocusLayoutClickListener {
        void onMsgFocusLayoutClick(int position);
    }

    public void setOnMsgFocusLayoutClickListener(OnMsgFocusLayoutClickListener msgFocusLayoutClickListener) {
        this.msgFocusLayoutClickListener = msgFocusLayoutClickListener;
    }

    public interface OnMsgFocusLayoutFocusChangeListener {
        void onMsgFocusLayoutFocusChange(View view, int position);
    }

    public void setOnMsgFocusLayoutFocusChangeListener(OnMsgFocusLayoutFocusChangeListener msgFocusLayoutFocusChangeListener) {
        this.msgFocusLayoutFocusChangeListener = msgFocusLayoutFocusChangeListener;
    }

    public void setFocusListener(View.OnFocusChangeListener focusListener) {
        this.mFocusListener = focusListener;
    }

    public MessageAdapter(List<MessageInfo> msgList) {
        this.msgList = msgList;
    }

    public void setMsgList(List<MessageInfo> msgList) {
        this.msgList = msgList;
    }

    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_msg_recyclerview, parent, false);
        MessageAdapter.MyViewHolder holder = new MessageAdapter.MyViewHolder(view, msgFocusLayoutClickListener, msgFocusLayoutFocusChangeListener);
        view.setOnFocusChangeListener(mFocusListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.MyViewHolder holder, final int position) {
        final MessageInfo messageInfo = msgList.get(position);
        if (messageInfo.getStatus()) {
            holder.greenDot.setVisibility(View.VISIBLE);
            holder.msgTitle.getPaint().setFakeBoldText(true);
        } else {
            holder.greenDot.setVisibility(View.INVISIBLE);
            holder.msgTitle.getPaint().setFakeBoldText(false);
        }
        holder.msgTitle.setText(messageInfo.getMsgTitle());
        holder.msgDate.setText(messageInfo.getMsgDate());
        holder.msgDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {
        private final OnMsgFocusLayoutClickListener mFocusClickListener;
        private final OnMsgFocusLayoutFocusChangeListener mFocusChangeListener;
        final TextView msgTitle, msgDate;
        final ImageView greenDot, msgDelete;
        final LinearLayout msgFocusLayout;

        public MyViewHolder(View view, OnMsgFocusLayoutClickListener msgClickListener, OnMsgFocusLayoutFocusChangeListener msgFocusChangeListener) {
            super(view);
            this.mFocusClickListener = msgClickListener;
            this.mFocusChangeListener = msgFocusChangeListener;
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
                case R.id.item_iv_btn_delete:
                    String msgId = msgList.get(getLayoutPosition()).getMsgId();
                    msgList.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    if (mRemoveListener != null) {
                        mRemoveListener.onRemoveItem(getLayoutPosition() , msgId);
                    }
                    break;
                case R.id.item_ll_focus_msg:
                    if (mFocusClickListener != null) {
                        mFocusClickListener.onMsgFocusLayoutClick(getLayoutPosition());
                    }
                    break;
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int adapterPosition = getAdapterPosition();
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && adapterPosition == 0) {
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mFocusListener != null) {
                mFocusListener.onFocusChange(v, hasFocus);
            }
            if (mFocusChangeListener != null) {
                mFocusChangeListener.onMsgFocusLayoutFocusChange(v, getLayoutPosition());
            }
            if (msgDelete.hasFocus() || msgFocusLayout.hasFocus()) {
                msgFocusLayout.setSelected(true);
                msgTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);  //获取焦点是跑马灯格式
                mFocusedDeleteBtn = msgDelete;
                mHandler.removeCallbacks(showMsgDelete);
                mHandler.postDelayed(showMsgDelete, 200);
            } else {
                msgTitle.setEllipsize(TextUtils.TruncateAt.END);  //焦点移除为省略号样式
                msgDelete.setVisibility(View.INVISIBLE);
                msgFocusLayout.setSelected(false);
            }
        }
    }
}
