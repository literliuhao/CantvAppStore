package com.can.appstore.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.adapter.HotRecommendAdapter;
import com.can.appstore.search.adapter.KeyboardAdapter;
import com.can.appstore.search.adapter.SearchAppListAdapter;
import com.can.appstore.search.widget.YIGridLayoutManager;

import java.util.Arrays;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;

import static android.support.v7.widget.GridLayoutManager.VERTICAL;

public class SearchActivity extends AppCompatActivity implements SearchContract.View, View.OnClickListener {

    private TextView mSearch_con_view;
    private RecyclerView mKeyboard_recy;
    private TextView mContent_cl_view;
    private TextView mContent_del_view;
    private KeyboardAdapter mKeyboardAdapter;
    private SearchPresenter mSearchPresenter;
    private static final int START_SEARCH = 1;

    private final int TAG_SHOW_TOP_BOTTOM = 0;   //默认展示
    private final int TAG_S_NULLAPP_G_TOP_APPLIST = 1;    //没有搜到内容
    private final int TAG_S_TOP_APPLIST_G_BOTTOM = 2;    //搜到内容


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_SEARCH:
                    mSearchPresenter.getSearchList(mSearch_con_view.getText().toString().trim());
                    break;
            }
        }
    };
    private TextView mleft_top;
    private TextView mright_top;
    private RecyclerView mSearAppList_recycle;
    private LinearLayout mBottom_re_ll;
    private RecyclerView mBottom_re_recycle;
    private static final int KEYBOARD_SPANCOUNT = 6;
    private static final int SEARCH_APP_SPANCOUNT = 4;
    private SearchAppListAdapter mAppListAdapter;
    private View mSearch_null;
    private HotRecommendAdapter mHotRecommendAdapter;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private MyFocusRunnable myFocusRunnable;
    private View mFocusedListChild;
    private ScaleFocusChangeListener mScaleFocusChangeListener;
    private RelativeLayout mTopView;


    public static void startAc(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
    }

    private void initView() {
        mSearchPresenter = new SearchPresenter(this);
        //用于缩放操作
        mScaleFocusChangeListener = new ScaleFocusChangeListener();
        List<String> mKeyList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
                , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        //左侧布局
        mSearch_con_view = (TextView) findViewById(R.id.show_se_con_view);
        mKeyboard_recy = (RecyclerView) findViewById(R.id.keyboard_recycleview);
        mContent_cl_view = (TextView) findViewById(R.id.con_clear_view);
        mContent_del_view = (TextView) findViewById(R.id.con_del_view);

        //右侧布局
        mTopView = (RelativeLayout) findViewById(R.id.top_view);
        mleft_top = (TextView) findViewById(R.id.left_top_view);
        mright_top = (TextView) findViewById(R.id.right_top_view);
        mSearAppList_recycle = (RecyclerView) findViewById(R.id.applist_recycle);
        mBottom_re_ll = (LinearLayout) findViewById(R.id.ll_recommed);
        mBottom_re_recycle = (RecyclerView) findViewById(R.id.recommend_app_recycle);
        //没有搜索到数据时显示
        mSearch_null = findViewById(R.id.search_null);

        mKeyboard_recy.setLayoutManager(new GridLayoutManager(SearchActivity.this, KEYBOARD_SPANCOUNT, VERTICAL, false));
        mKeyboardAdapter = new KeyboardAdapter(mKeyList);
        mKeyboard_recy.setAdapter(mKeyboardAdapter);

        mKeyboardAdapter.setOnItemClickListener(new KeyboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, String content) {
                mSearch_con_view.setText(mSearch_con_view.getText() + content);
            }
        });

        //发搜索Handler
        final SearchRunner searchRunner = new SearchRunner();
        mSearch_con_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeMessages(START_SEARCH);
                mHandler.removeCallbacks(searchRunner);
                if (s.length() > 0) {
                    mHandler.postDelayed(searchRunner, 2000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContent_del_view.setOnClickListener(this);
        mContent_cl_view.setOnClickListener(this);

        //焦点缩放相关
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.search_btn_focus1);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mContent_cl_view.setOnFocusChangeListener(mScaleFocusChangeListener);
        mContent_del_view.setOnFocusChangeListener(mScaleFocusChangeListener);
        mKeyboardAdapter.setMyOnFocusChangeListener(mScaleFocusChangeListener);

    }


    /**
     * 得到"大家都在搜"的数据
     */
    private void initData() {
        mSearAppList_recycle.setLayoutManager(new YIGridLayoutManager(this, SEARCH_APP_SPANCOUNT, VERTICAL, false));
        mBottom_re_recycle.setLayoutManager(new GridLayoutManager(this, SEARCH_APP_SPANCOUNT, VERTICAL, false));
        mSearchPresenter.getDefaultList();  //获取"大家都在搜"的数据
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.con_clear_view:
                clearContent();
                break;
            case R.id.con_del_view:
                delContent();
                break;
        }
    }

    public class SearchRunner implements Runnable {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(START_SEARCH);
        }
    }

    @Override
    public void startSearch() {
        ToastUtil.toastShort("开始搜索...");
    }

    /**
     * 进行删除首字母
     */
    @Override
    public void delContent() {
        CharSequence con = mSearch_con_view.getText();
        if (con.length() > 0) {
            mSearch_con_view.setText(con.toString().substring(0, con.length() - 1));
        }
        if (!(mSearch_con_view.getText().length() > 0)) {
            resetDefaultList();
        }
    }

    /**
     * 清空首字母
     */
    @Override
    public void clearContent() {
        mSearch_con_view.setText("");
        resetDefaultList();
    }

    @Override
    public void getDefaultList(List defaultList, List hotList) {
        //"大家都在搜"数据
        mAppListAdapter = new SearchAppListAdapter(defaultList);
        mSearAppList_recycle.setAdapter(mAppListAdapter);
        mAppListAdapter.setOnInitialsListener(new SearchAppListAdapter.OnInitialsListener() {
            @Override
            public void onInitials(String con) {
                getInitials(con);
            }
        });
        //"热门推荐"数据
        mHotRecommendAdapter = new HotRecommendAdapter(hotList);
        mBottom_re_recycle.setAdapter(mHotRecommendAdapter);
        mAppListAdapter.setMyOnFocusChangeListener(mScaleFocusChangeListener);
        mHotRecommendAdapter.setMyOnFocusChangeListener(mScaleFocusChangeListener);
    }

    /**
     * 获取到搜索
     *
     * @param list
     */
    @Override
    public void getAppList(List list) {
        mleft_top.setText(R.string.search_left_top_prompt2);
        if (list.size() > 0) {
            showGoneView(TAG_S_TOP_APPLIST_G_BOTTOM);
            mAppListAdapter.setDataList(list);
        } else {
            showGoneView(TAG_S_NULLAPP_G_TOP_APPLIST);
        }
    }

    /**
     * 设置首字母
     *
     * @param con
     */
    @Override
    public void getInitials(String con) {
        mSearch_con_view.setText(con);
    }

    /**
     * 在清空搜索时,右侧重置为默认数据
     */
    public void resetDefaultList() {
        mleft_top.setText(getResources().getText(R.string.search_left_top_prompt1));
        mAppListAdapter.setDefaultApplist();
        showGoneView(TAG_SHOW_TOP_BOTTOM);
    }

    /**
     * 执行焦点变化后的具体操作
     */
    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
                //1f,1f不就行缩放
                mFocusScaleUtil.scaleToLarge(mFocusedListChild,1f,1f);
            }
        }
    }

    /**
     * 焦点变化监听
     */
    private class ScaleFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mFocusedListChild = view;
                view.postDelayed(myFocusRunnable, 50);
            } else {
                mFocusScaleUtil.scaleToNormal();
            }
        }
    }

    /**
     * 统一控制布局的显示/隐藏
     *
     * @param tag
     */
    private void showGoneView(int tag) {
        //默认状态
        if (tag == TAG_SHOW_TOP_BOTTOM) {
            mTopView.setVisibility(View.VISIBLE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.VISIBLE);   //搜索结果对应的布局
            mBottom_re_ll.setVisibility(View.VISIBLE);  //热门推荐
            mSearch_null.setVisibility(View.GONE);  //没有到结果对应的布局
        }

        //没有搜到内容
        if (tag == TAG_S_NULLAPP_G_TOP_APPLIST) {
            mTopView.setVisibility(View.GONE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.GONE);   //搜索结果对应的布局
            mSearch_null.setVisibility(View.VISIBLE);
            mBottom_re_ll.setVisibility(View.VISIBLE);  //热门推荐
        }

        //搜到内容
        if (tag == TAG_S_TOP_APPLIST_G_BOTTOM) {
            mTopView.setVisibility(View.VISIBLE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.VISIBLE);   //搜索结果对应的布局
            mSearch_null.setVisibility(View.GONE);
            mBottom_re_ll.setVisibility(View.GONE);  //热门推荐
        }

    }

}
