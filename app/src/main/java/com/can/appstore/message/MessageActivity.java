package com.can.appstore.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.can.appstore.ActionConstants;
import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.active.ActiveActivity;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.message.adapter.MessageAdapter;
import com.can.appstore.message.db.entity.MessageInfo;
import com.can.appstore.message.manager.MessageManager;
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
    private MessageManager messageManager;
    private FocusMoveUtil focusMoveUtil;
    private LinearLayoutManager llManager;
    private Handler mHandler;
    private Runnable mFocusMoveRunnable;
    private Button btnTag, btnClear;
    private TextView empty, itemPos, itemTotal;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private View mFocusedView;
    private boolean focusViewMoveEnable = true;
    private boolean deleteLastItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        initFocusView();
        initData();
    }

    private void initView() {
        btnTag = (Button) findViewById(R.id.btn_tag);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnTag.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnClear.setOnFocusChangeListener(this);
        itemPos = (TextView) findViewById(R.id.tv_item_pos);
        itemTotal = (TextView) findViewById(R.id.tv_item_total);
        empty = (TextView) findViewById(R.id.tv_empty_msg);
        showLoadingDialog(getResources().getDimensionPixelSize(R.dimen.px132));   //显示偏移的Loading
    }

    private void initFocusView() {
        focusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.mipmap.btn_focus);
    }

    private void initData() {
        messageManager = new MessageManager(this);
        //查询数据库消息数据
        msgList = messageManager.queryMsgData();
        hideLoadingDialog();
        if (msgList != null && !msgList.isEmpty()) {
            mHandler = new Handler();
            mFocusMoveRunnable = new Runnable() {
                @Override
                public void run() {
                    View focusedView = MessageActivity.this.mFocusedView;
                    if (focusedView == null || !focusedView.isFocused()) {
                        return;
                    }
                    focusMoveUtil.startMoveFocus(focusedView);
                }
            };
            btnTag.setOnFocusChangeListener(this);
            initAdapter();
            initRecyclerView();
            refreshTotalText(msgList.size());
        } else {
            btnTag.post(new Runnable() {
                @Override
                public void run() {
                    btnTag.requestFocus();
                    focusMoveUtil.setFocusView(btnTag);
                }
            });
            empty.setVisibility(View.VISIBLE);
        }
    }


    private void initAdapter() {
        mAdapter = new MessageAdapter(msgList);
        mAdapter.setFocusListener(this);
        mAdapter.setOnMsgFocusLayoutClickListener(new MessageAdapter.OnMsgFocusLayoutClickListener() {
            @Override
            public void onMsgFocusLayoutClick(int position) {
                boolean isNetConnected = NetworkUtils.isNetworkConnected(MessageActivity.this);
                MessageInfo msg = msgList.get(position);
                if (msg == null) {
                    return;
                }
                if (!isNetConnected && !msg.getAction().equals(ActionConstants.ACTION_NOTHIN)) {
                    showToast(R.string.connect_net_fail);
                    return;
                }
                switch (msg.getAction()) {
                    case ActionConstants.ACTION_NOTHIN:
                        refreshRecyclerItem(msg, position);
                        break;
                    case ActionConstants.ACTION_APP_DETAIL:
                        String appDetailActionData = msg.getActionData();
                        AppDetailActivity.actionStart(MessageActivity.this, appDetailActionData);
                        refreshRecyclerItem(msg, position);
                        break;
                    case ActionConstants.ACTION_TOPIC_DETAIL:
                        String topicActionData = msg.getActionData();
                        SpecialDetailActivity.actionStart(MessageActivity.this, topicActionData);
                        refreshRecyclerItem(msg, position);
                        break;
                    case ActionConstants.ACTION_ACTIVITY_DETAIL:
                        String activityActionData = msg.getActionData();
                        ActiveActivity.actionStart(MessageActivity.this, activityActionData);
                        refreshRecyclerItem(msg, position);
                        break;
                }
            }
        });
        mAdapter.setOnMsgFocusLayoutFocusChangeListener(new MessageAdapter.OnMsgFocusLayoutFocusChangeListener() {
            @Override
            public void onMsgFocusLayoutFocusChange(View view, int position) {
                if (view.hasFocus()) {
                    refreshPosText(++position);
                }
            }
        });
        mAdapter.setOnItemRemoveListener(new MessageAdapter.OnItemRemoveListener() {
            @Override
            public void onRemoveItem(int position , String msgId) {
                messageManager.deleteMsg(msgId);
                int msgCount = msgList.size();
                if (msgCount == 0) {
                    itemTotal.setVisibility(View.INVISIBLE);
                    itemPos.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    btnClear.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    focusMoveUtil.setFocusView(btnTag);  // 直接设置焦点，无焦点移动动画
                    return;
                }
                refreshTotalText(msgCount);
                focusMoveUtil.hideFocusForShowDelay(500);
                if (position > msgCount - 1) {
                    focusMsgItem(position - 1);
                    return;
                }
                int first = llManager.findFirstVisibleItemPosition();
                int last = llManager.findLastVisibleItemPosition();
                if (first != 0 && last == msgCount) {
                    focusMsgItem(position - 1);
                    return;
                }
                deleteLastItem = position == msgCount + 1;
                final int pos = deleteLastItem ? position - 1 : position;
                focusMsgItem(pos);
                focusViewMoveEnable = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusMsgItemInRunnable(pos);
                    }
                }, 400);
            }
        });
    }

    /**
     * 更新条目
     */
    private void refreshRecyclerItem(MessageInfo msg, int position) {
        if (msg.getStatus()) {
            messageManager.updateStatus(msg.getMsgId());
            msg.setStatus(false);
            msgList.set(position, msg);
            mAdapter.setMsgList(msgList);
            mAdapter.notifyItemChanged(position);
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_msg);
        mRecyclerView.setVisibility(View.VISIBLE);
        itemPos.setVisibility(View.VISIBLE);
        itemTotal.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);
        mRecyclerView.requestFocus();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                //开始焦点在第一个item上
                mRecyclerView.getChildAt(0).requestFocus();
                focusMoveUtil.setFocusView(mRecyclerView.getChildAt(0));
                focusMoveUtil.hideFocusForShowDelay(500);
            }
        });
        llManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    focusViewMoveEnable = true;
                    mHandler.postDelayed(mFocusMoveRunnable, 50);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mHandler.removeCallbacks(mFocusMoveRunnable);
                if (dy == 0) {
                    if (deleteLastItem) {
                        focusViewMoveEnable = true;
                    }
                    mHandler.postDelayed(mFocusMoveRunnable, 500);
                }
            }
        });
    }

    private void focusMsgItemInRunnable(final int focusPos) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View childView = mRecyclerView.getChildAt(focusPos - llManager.findFirstVisibleItemPosition());
                if (childView != null) {
                    mFocusedView = childView.findViewById(R.id.item_ll_focus_msg);
                    mFocusedView.requestFocus();
                    mHandler.removeCallbacks(mFocusMoveRunnable);
                    mHandler.postDelayed(mFocusMoveRunnable, 50);
                }
                focusViewMoveEnable = true;
            }
        });
    }

    private void focusMsgItem(int focusPos) {
        View childView = mRecyclerView.getChildAt(focusPos - llManager.findFirstVisibleItemPosition());
        if (childView != null) {
            mFocusedView = childView.findViewById(R.id.item_ll_focus_msg);
            mFocusedView.requestFocus();
            mHandler.removeCallbacks(mFocusMoveRunnable);
            mHandler.postDelayed(mFocusMoveRunnable, 50);
        }
    }


    private void changeListStatus() {
        for (MessageInfo msg: msgList) {
            msg.setStatus(false);
        }
    }

    /**
     * 刷新右上角焦点框所在条目上的位置信息
     */
    private void refreshPosText(int pos) {
        itemPos.setText(String.valueOf(pos));
    }

    /**
     * 刷新右上角总共Item的条数信息
     */
    private void refreshTotalText(int total) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/");
        stringBuilder.append(total);
        stringBuilder.append("行");
        itemTotal.setText(stringBuilder);
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
    protected void onHomeKeyDown() {
        finish();
        super.onHomeKeyDown();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tag:
                if (!messageManager.existUnreadMsg() || msgList == null || msgList.isEmpty() ) {
                    return;
                }
                messageManager.updateAllMsgStatus();
                changeListStatus();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_clear:
                if (msgList == null || msgList.isEmpty()) {
                    return;
                }
                messageManager.clearAllMsg();
                mRecyclerView.setVisibility(View.GONE);
                itemPos.setVisibility(View.GONE);
                itemTotal.setVisibility(View.GONE);
                btnClear.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                msgList.clear();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (focusViewMoveEnable) {
            if (hasFocus) {
                mFocusedView = v;
                if (mFocusedView == btnTag || mFocusedView == btnClear) {
                    refreshPosText(0);
                }
                mHandler.removeCallbacks(mFocusMoveRunnable);
                mHandler.postDelayed(mFocusMoveRunnable, 50);
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

    @Override
    protected void onDestroy() {
        if (focusMoveUtil != null) {
            focusMoveUtil.release();
            focusMoveUtil = null;
        }
        if (messageManager != null) {
            messageManager = null;
        }
        if (msgList != null) {
            msgList.clear();
        }
        if (mAdapter != null) {
            mAdapter.setFocusListener(null);
        }
        super.onDestroy();
    }
}
