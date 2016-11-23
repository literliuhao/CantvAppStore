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
import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.message.adapter.MessageAdapter;
import com.can.appstore.message.db.entity.MessageInfo;
import com.can.appstore.message.manager.GreenDaoManager;
import com.can.appstore.message.manager.MessageManager;
import com.can.appstore.search.ToastUtil;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.utils.NetworkUtils;

/**
 * 消息主页面
 * Created by HEKANG on 2016/10/18.
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final static String TAG = "MessageActivity";
    private List<MessageInfo> msgList;
    private GreenDaoManager dbManager;
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
    private boolean isBtnTagClick = false;
    private boolean deleteLastItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        showLoadingDialog();
        initData();
        initFocusView();
    }

    private void initView() {
        btnTag = (Button) findViewById(R.id.btn_tag);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnTag.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnTag.setOnFocusChangeListener(this);
        btnClear.setOnFocusChangeListener(this);
        itemPos = (TextView) findViewById(R.id.tv_item_pos);
        itemTotal = (TextView) findViewById(R.id.tv_item_total);
        empty = (TextView) findViewById(R.id.tv_empty_msg);
    }

    private void initFocusView() {
        focusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.mipmap.btn_focus);
    }

    private void initData() {
        mHandler = new Handler();
        dbManager = new GreenDaoManager();
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
        msgList = queryData();
        hideLoadingDialog();
        if (msgList != null && !msgList.isEmpty()) {
            initAdapter();
            initRecyclerView();
            refreshTotalText(msgList.size());
        } else {
            itemPos.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    private List<MessageInfo> queryData() {
        return dbManager.queryMsg(System.currentTimeMillis() / 1000);
    }

    private void initAdapter() {
        mAdapter = new MessageAdapter(msgList);
        mAdapter.setFocusListener(this);
        mAdapter.setOnllMsgClickListener(new MessageAdapter.OnMsgFocusLayoutClickListener() {
            @Override
            public void onllMsgClick(View view, int position) {
                boolean isNetConnected = NetworkUtils.isNetworkConnected(MessageActivity.this);
                MessageInfo msg = msgList.get(position);
                if (!isNetConnected && msg.getAction() != ActionConstants.ACTION_NOTHIN){
                    ToastUtil.toastShort("网络链接失败");
                    return;
                }
                switch (msg.getAction()) {
                    case ActionConstants.ACTION_NOTHIN:
                        refreshRecyclerItem(msg , position);
                        break;
                    case ActionConstants.ACTION_APP_DETAIL:
                           ToastUtil.toastShort("跳转到应用详情页");
                           refreshRecyclerItem(msg , position);
                        break;
                    case ActionConstants.ACTION_TOPIC_DETAIL:
                            ToastUtil.toastShort("跳转到专题详情页");
                            refreshRecyclerItem(msg , position);
                        break;
                    case ActionConstants.ACTION_ACTIVITY_DETAIL:
                            refreshRecyclerItem(msg , position);
                            ToastUtil.toastShort("跳转到活动详情页");
                        break;
                }
            }
        });
        mAdapter.setOnllMsgFocusChangeListener(new MessageAdapter.OnMsgFocusLayoutFocusChangeListener() {
            @Override
            public void onllMsgFocusChange(View view, int position) {
                if (view.hasFocus()) {
                    refreshPosText(++position);
                }
            }
        });
        mAdapter.setOnItemRemoveListener(new MessageAdapter.OnItemRemoveListener() {
            @Override
            public void onRemoveItem(int position) {
                focusMoveUtil.hideFocusForShowDelay(650);
                int msgCount = msgList.size();
                refreshTotalText(msgCount);
                if (msgCount == 0) {
                    itemTotal.setVisibility(View.INVISIBLE);
                    itemPos.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    return;
                }
                if (position > msgCount - 1) {
                    focusMsgItem(position - 1);
                    return;
                }
                /*int first = llManager.findFirstVisibleItemPosition();
                int last = llManager.findLastVisibleItemPosition();
                if (first != 0 && last == msgCount) {
                    focusMsgItem(position - 1);
                    return;
                }*/
                deleteLastItem = position == msgCount - 1;
                final int posi = deleteLastItem ? position - 1 : position;
                focusMsgItem(posi);
                focusViewMoveEnable = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusMsgItemInRunnable(posi);
                    }
                }, 500);
            }
        });
    }

    private void refreshRecyclerItem( MessageInfo msg , int position){
        if (msg.getStatus()){
                    MessageManager.updateStatus(msg.getMsgId());
                    msg.setStatus(false);
                    msgList.set(position, msg);
                    mAdapter.setMsgList(msgList);
                    mAdapter.notifyItemChanged(position);
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.requestFocus();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.getChildAt(0).requestFocus();
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
                    mHandler.postDelayed(mFocusMoveRunnable, 400);
                }
            }
        });
    }

    private void focusMsgItemInRunnable(final int focusPosi) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View childView = mRecyclerView.getChildAt(focusPosi - llManager.findFirstVisibleItemPosition());
                if (childView != null) {
                    mFocusedView = childView.findViewById(R.id.ll_focus_msg);
                    mFocusedView.requestFocus();
                    mHandler.removeCallbacks(mFocusMoveRunnable);
                    mHandler.postDelayed(mFocusMoveRunnable, 50);
                }
                focusViewMoveEnable = true;
            }
        });
    }

    private void focusMsgItem(int focusPosi) {
        View childView = mRecyclerView.getChildAt(focusPosi - llManager.findFirstVisibleItemPosition());
        if (childView != null) {
            mFocusedView = childView.findViewById(R.id.ll_focus_msg);
            mFocusedView.requestFocus();
            mHandler.removeCallbacks(mFocusMoveRunnable);
            mHandler.postDelayed(mFocusMoveRunnable, 50);
        }
    }

    private void changeListStatus() {
        int size = msgList.size();
        for (int i = 0; i < size; i++) {
            MessageInfo msg = msgList.get(i);
            msg.setStatus(false);
            msgList.set(i, msg);
        }
    }

    private void refreshPosText(int pos) {
        itemPos.setText(String.valueOf(pos));
    }

    private void refreshTotalText(int total) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/");
        stringBuilder.append(total);
        stringBuilder.append("行");
        itemTotal.setText(stringBuilder);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onHomeKeyListener() {
        finish();
        super.onHomeKeyListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tag:
                if (msgList == null || msgList.isEmpty()) {
                    return;
                } else if (isBtnTagClick) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        dbManager.updateAllMsgStatus();
                    }
                }.start();
                changeListStatus();
                mAdapter.notifyDataSetChanged();
                isBtnTagClick = true;
                break;
            case R.id.btn_clear:
                if (msgList == null || msgList.isEmpty()) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        dbManager.clear();
                    }
                }.start();
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
                /*if (mFocusedView.getId() == R.id.iv_btn_delete){
                    focusMoveUtil.setFocusRes(MessageActivity.this , R.mipmap.btn_circle_focus);
                }else{
                    focusMoveUtil.setFocusRes(MessageActivity.this , R.mipmap.btn_focus);
                }*/
                if (mFocusedView == btnTag || mFocusedView == btnClear) {
                    refreshPosText(0);
                }
                mHandler.removeCallbacks(mFocusMoveRunnable);
                mHandler.postDelayed(mFocusMoveRunnable, 50);
            }
        }
    }

    @Override
    protected void onStop() {
        if (dbManager != null) {
            dbManager = null;
        }
        if (msgList != null) {
            msgList.clear();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (focusMoveUtil != null) {
            focusMoveUtil.release();
            focusMoveUtil = null;
        }
        if (mAdapter != null) {
            mAdapter.setFocusListener(null);
        }
        super.onDestroy();
    }
}
