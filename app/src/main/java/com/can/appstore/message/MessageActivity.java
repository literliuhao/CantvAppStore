package com.can.appstore.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.ActionConstants;
import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.active.ActiveActivity;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.db.entity.MessageInfo;
import com.can.appstore.message.adapter.MessageAdapter;
import com.can.appstore.message.manager.MessageDBManager;
import com.can.appstore.specialdetail.SpecialDetailActivity;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.utils.NetworkUtils;

/**
 * 消息主页面
 * Created by HEKANG on 2016/10/18.
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private List<MessageInfo> msgList;

    private Button mBtnTag, mBtnClear;
    private TextView mTvEmpty, mTvLineNum;
    private RecyclerView mRecyclerView;
    private View mFocusedView;
    private ImageView mMsgDeleteBtnView;
    private LinearLayoutManager mllManager;

    private MessageAdapter mAdapter;
    private MessageDBManager mMessageDBManager;
    private FocusMoveUtil mFocusMoveUtil;
    private Handler mHandler;
    private Runnable mFocusMoveRunnable;
    private Runnable mShowMsgDeleteRunnable;

    private boolean mFocusViewMoveEnable = true;
    private boolean mDeleteLastItem;
    private int mCurrentLine;
    private int mTotalLine;
    private StringBuilder crowNumber;

    private final int HIDE_FOCUS_SHOW_DELAY_TIME = 500;
    private final int SHOW_MSG_DELETE_BTN_DELAY_TIME = 200;
    private final int ENTER_PAG_FOCUS_SHOW_DELAY_TIME = 500;
    private final int REV_STATED_CHANGED_DELAY_TIME = 50;
    private final int REV_SCROLLED_FOCUS_TIME = 500;
    private final int FOCUS_CHANGE_FOCUS_DELAY_TIME = 50;
    private final int MSG_ITEM_FOCUS_MOVE_DELAY_TIME = 50;
    private final int REV_MSG_ITEM_FOCUS_MOVE_DELAY_TIME = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        initData();
    }

    private void initView() {
        mBtnTag = (Button) findViewById(R.id.btn_tag);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnTag.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnClear.setOnFocusChangeListener(this);
        mTvLineNum = (TextView) findViewById(R.id.tv_msg_list_line);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty_msg);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.mipmap.btn_focus);
    }


    private void initData() {
        showLoadingDialog(getResources().getDimensionPixelSize(R.dimen.px132));
        mMessageDBManager = new MessageDBManager(this);
        msgList = mMessageDBManager.queryMsgList();
        List<MessageInfo> list = msgList;
        hideLoadingDialog();
        if (list != null && !list.isEmpty()) {
            mTotalLine = list.size();
            mHandler = new Handler();
            mFocusMoveRunnable = new Runnable() {
                @Override
                public void run() {
                    View focusedView = MessageActivity.this.mFocusedView;
                    if (focusedView == null || !focusedView.isFocused()) {
                        return;
                    }
                    mFocusMoveUtil.startMoveFocus(focusedView);
                }
            };
            mShowMsgDeleteRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mMsgDeleteBtnView != null) {
                        mMsgDeleteBtnView.setVisibility(View.VISIBLE);
                    }
                }
            };
            mBtnTag.setOnFocusChangeListener(this);
            initAdapter();
            initRecyclerView();
        } else {
            mBtnTag.post(new Runnable() {
                @Override
                public void run() {
                    mBtnTag.requestFocus();
                    mFocusMoveUtil.setFocusView(mBtnTag);
                }
            });
            mTvEmpty.setVisibility(View.VISIBLE);
        }
    }


    private void initAdapter() {
        mAdapter = new MessageAdapter(msgList);
        mAdapter.setFocusListener(this);
        mAdapter.setOnMsgItemClickListener(new MessageAdapter.OnMsgItemClickListener() {
            @Override
            public void onMsgClick(int position) {
                MessageInfo msg = msgList.get(position);
                switch (msg.getAction().trim()) {
                    case ActionConstants.ACTION_NOTHIN:
                        refreshMsgItemView(msg, position);
                        break;
                    case ActionConstants.ACTION_APP_DETAIL:
                        String appDetailActionData = msg.getActionData().trim();
                        if (NetworkUtils.isNetworkConnected(MessageActivity.this) && !TextUtils.isEmpty(appDetailActionData)) {
                            AppDetailActivity.actionStart(MessageActivity.this, appDetailActionData);
                            refreshMsgItemView(msg, position);
                        } else {
                            showToast(R.string.connect_net_fail);
                        }
                        break;
                    case ActionConstants.ACTION_TOPIC_DETAIL:
                        String topicActionData = msg.getActionData().trim();
                        if (NetworkUtils.isNetworkConnected(MessageActivity.this) && !TextUtils.isEmpty(topicActionData)) {
                            SpecialDetailActivity.actionStart(MessageActivity.this, topicActionData);
                            refreshMsgItemView(msg, position);
                        } else {
                            showToast(R.string.connect_net_fail);
                        }
                        break;
                    case ActionConstants.ACTION_ACTIVITY_DETAIL:
                        String activityActionData = msg.getActionData().trim();
                        if (NetworkUtils.isNetworkConnected(MessageActivity.this) && !TextUtils.isEmpty(activityActionData)) {
                            ActiveActivity.actionStart(MessageActivity.this, activityActionData);
                            refreshMsgItemView(msg, position);
                        } else {
                            showToast(R.string.connect_net_fail);
                        }
                        break;
                }
            }

            @Override
            public void onDeleteBtnClick(int position) {
                long _id = msgList.remove(position).get_id();
                mAdapter.notifyItemRemoved(position);
                mMessageDBManager.deleteMsg(_id);
                int msgCount = msgList.size();
                if (msgCount == 0) {
                    mTvLineNum.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mBtnClear.setVisibility(View.INVISIBLE);
                    mTvEmpty.setVisibility(View.VISIBLE);
                    mFocusMoveUtil.setFocusView(mBtnTag);
                    return;
                }
                mTotalLine = msgCount;
                mFocusMoveUtil.hideFocusForShowDelay(HIDE_FOCUS_SHOW_DELAY_TIME);
                if (position > msgCount - 1) {
                    focusMsgItem(position - 1);
                    return;
                }
                int first = mllManager.findFirstVisibleItemPosition();
                int last = mllManager.findLastVisibleItemPosition();
                if (first != 0 && last == msgCount) {
                    focusMsgItem(position - 1);
                    return;
                }
                mDeleteLastItem = position == msgCount + 1;
                final int pos = mDeleteLastItem ? position - 1 : position;
                focusMsgItem(pos);
                mFocusViewMoveEnable = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusMsgItemInRunnable(pos);
                    }
                }, 400);
            }
        });
        mAdapter.setOnMsgItemFocusChangeListener(new MessageAdapter.OnMsgItemFocusChangeListener() {
            @Override
            public void onMsgFocusChange(View msgView, ImageView deleteView, TextView msgTitleView, int position) {
                if (msgView.hasFocus()) {
                    mCurrentLine = ++position;
                    refreshRowNumber();
                }
                if (msgView.hasFocus() || deleteView.hasFocus()) {
                    mMsgDeleteBtnView = deleteView;
                    msgView.setSelected(true);
                    msgTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    mHandler.removeCallbacks(mShowMsgDeleteRunnable);
                    mHandler.postDelayed(mShowMsgDeleteRunnable, SHOW_MSG_DELETE_BTN_DELAY_TIME);
                } else {
                    msgView.setSelected(false);
                    msgTitleView.setEllipsize(TextUtils.TruncateAt.END);
                    deleteView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onDeleteBtnFocusChange(View deleteView, View msgView, TextView msgTitleView, int position) {
                if (!deleteView.hasFocus()) {
                    deleteView.setVisibility(View.INVISIBLE);
                    msgView.setSelected(false);
                    msgTitleView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
    }

    /**
     * 更新条目
     */
    private void refreshMsgItemView(MessageInfo msg, int position) {
        if (!msg.getStatus()) {
            msg.setStatus(true);
            msgList.set(position, msg);
            mAdapter.notifyItemChanged(position);
            mMessageDBManager.setMsgRead(msg.get_id());
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_msg);
        mRecyclerView.setVisibility(View.VISIBLE);
        mTvLineNum.setVisibility(View.VISIBLE);
        mBtnClear.setVisibility(View.VISIBLE);
        mRecyclerView.requestFocus();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.getChildAt(0).requestFocus();
                mFocusMoveUtil.setFocusView(mRecyclerView.getChildAt(0));
                mFocusMoveUtil.hideFocusForShowDelay(ENTER_PAG_FOCUS_SHOW_DELAY_TIME);
            }
        });
        mllManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mllManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFocusViewMoveEnable = true;
                    mHandler.postDelayed(mFocusMoveRunnable, REV_STATED_CHANGED_DELAY_TIME);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mHandler.removeCallbacks(mFocusMoveRunnable);
                if (dy == 0) {
                    if (mDeleteLastItem) {
                        mFocusViewMoveEnable = true;
                    }
                    mHandler.postDelayed(mFocusMoveRunnable, REV_SCROLLED_FOCUS_TIME);
                }
            }
        });
    }

    private void focusMsgItemInRunnable(final int focusPos) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View childView = mRecyclerView.getChildAt(focusPos - mllManager.findFirstVisibleItemPosition());
                if (childView != null) {
                    mFocusedView = childView.findViewById(R.id.item_ll_focus_msg);
                    mFocusedView.requestFocus();
                    mHandler.removeCallbacks(mFocusMoveRunnable);
                    mHandler.postDelayed(mFocusMoveRunnable, REV_MSG_ITEM_FOCUS_MOVE_DELAY_TIME);
                }
                mFocusViewMoveEnable = true;
            }
        });
    }

    private void focusMsgItem(int focusPos) {
        View childView = mRecyclerView.getChildAt(focusPos - mllManager.findFirstVisibleItemPosition());
        if (childView != null) {
            mFocusedView = childView.findViewById(R.id.item_ll_focus_msg);
            mFocusedView.requestFocus();
            mHandler.removeCallbacks(mFocusMoveRunnable);
            mHandler.postDelayed(mFocusMoveRunnable, MSG_ITEM_FOCUS_MOVE_DELAY_TIME);
        }
    }

    private final void changeListStatus() {
        for (int i = 0; i < mTotalLine; i++) {
            MessageInfo msg = msgList.get(i);
            if (!msg.getStatus()) {
                msg.setStatus(true);
                msgList.set(i, msg);
            }
        }
    }

    private void refreshRowNumber() {
        crowNumber = new StringBuilder();
        if (crowNumber != null) {
            crowNumber.setLength(0);
        }
        crowNumber.append(mCurrentLine);
        crowNumber.append(this.getResources().getString(R.string.backslashes));
        crowNumber.append(mTotalLine);
        crowNumber.append(this.getResources().getString(R.string.line));
        SpannableStringBuilder spannable = new SpannableStringBuilder(crowNumber);
        int currentLineTextLength = String.valueOf(mCurrentLine).length();
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentLineTextLength, Spannable
                .SPAN_EXCLUSIVE_INCLUSIVE);
        mTvLineNum.setText(spannable);
    }

    /**
     * 启动消息页面
     * （外部调用）
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        super.showToast(resId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tag:
                if (msgList == null || msgList.isEmpty()) {
                    return;
                }
                changeListStatus();
                mMessageDBManager.setAllMsgRead(msgList);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_clear:
                mRecyclerView.setVisibility(View.INVISIBLE);
                mTvLineNum.setVisibility(View.INVISIBLE);
                mBtnClear.setVisibility(View.INVISIBLE);
                mTvEmpty.setVisibility(View.VISIBLE);
                msgList.clear();
                mMessageDBManager.clearAllMsg();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mFocusViewMoveEnable) {
            if (hasFocus) {
                mFocusedView = v;
                mHandler.removeCallbacks(mFocusMoveRunnable);
                mHandler.postDelayed(mFocusMoveRunnable, FOCUS_CHANGE_FOCUS_DELAY_TIME);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DCPage.onEntry(AppConstants.NEWS_LIST);
        DCEvent.onEvent(AppConstants.NEWS_LIST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.NEWS_LIST);
        DCEvent.onEventDuration(AppConstants.NEWS_LIST, mDuration);
    }

    /*    @Override
    protected void onHomeKeyDown() {
        finish();
        super.onHomeKeyDown();
    }*/

    @Override
    protected void onDestroy() {
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
            mFocusMoveUtil = null;
        }
        if (mMessageDBManager != null) {
            mMessageDBManager = null;
        }
        if (msgList != null) {
            msgList.clear();
            msgList = null;
        }
        if (mAdapter != null) {
            mAdapter.setOnMsgItemClickListener(null);
            mAdapter.setOnMsgItemFocusChangeListener(null);
            mAdapter.setFocusListener(null);
            mAdapter = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mFocusMoveRunnable);
            mHandler.removeCallbacks(mShowMsgDeleteRunnable);
            mHandler = null;
        }
        super.onDestroy();
    }
}
