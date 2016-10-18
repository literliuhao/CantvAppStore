package com.can.appstore.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.adapter.HotRecommendAdapter;
import com.can.appstore.search.adapter.KeyboardAdapter;
import com.can.appstore.search.adapter.SearchAppListAdapter;

import java.util.Arrays;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;

public class SearchActivity extends AppCompatActivity implements SearchContract.View, View.OnClickListener {

    private TextView mSearch_con_view;
    private RecyclerView mKeyboard_recy;
    private TextView mContent_cl_view;
    private TextView mContent_del_view;
    private KeyboardAdapter mKeyboardAdapter;
    private SearchPresenter mSearchPresenter;
    private static final int START_SEARCH = 1;
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
    private CanRecyclerView mSearAppList_recycle;
    private LinearLayout mBottom_re_ll;
    private CanRecyclerView mBottom_re_recycle;
    private static final int KEYBOARD_SPANCOUNT = 6;
    private static final int SEARCH_APP_SPANCOUNT = 3;
    private SearchAppListAdapter mAppListAdapter;
    private TextView mSearch_null;
    private HotRecommendAdapter mHotRecommendAdapter;


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
        List<String> mKeyList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
                , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        //左侧布局
        mSearch_con_view = (TextView) findViewById(R.id.show_se_con_view);
        mKeyboard_recy = (RecyclerView) findViewById(R.id.keyboard_recycleview);
        mContent_cl_view = (TextView) findViewById(R.id.con_clear_view);
        mContent_del_view = (TextView) findViewById(R.id.con_del_view);
        //右侧布局
        mleft_top = (TextView) findViewById(R.id.left_top_view);
        mright_top = (TextView) findViewById(R.id.right_top_view);
        mSearAppList_recycle = (CanRecyclerView) findViewById(R.id.applist_recycle);
        mBottom_re_ll = (LinearLayout) findViewById(R.id.ll_recommed);
        mBottom_re_recycle = (CanRecyclerView) findViewById(R.id.recommend_app_recycle);
        //没有搜索到数据时显示
        mSearch_null = (TextView) findViewById(R.id.search_null);

        mKeyboard_recy.setLayoutManager(new GridLayoutManager(SearchActivity.this, KEYBOARD_SPANCOUNT, GridLayoutManager.VERTICAL, false));
        mKeyboardAdapter = new KeyboardAdapter(mKeyList);
        mKeyboard_recy.setAdapter(mKeyboardAdapter);
        mKeyboardAdapter.setOnItemClickListener(new KeyboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, String content) {
                mSearch_con_view.setText(mSearch_con_view.getText() + content);
            }
        });

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

    }

    /**
     * 得到"大家都在搜"的数据
     */
    private void initData() {
        mSearAppList_recycle.setLayoutManager(new CanRecyclerView.LayoutManager(this, SEARCH_APP_SPANCOUNT, GridLayoutManager.VERTICAL, false));
        mBottom_re_recycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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

    }

    @Override
    public void getAppList(List list) {
        mleft_top.setText(R.string.search_left_top_prompt2);
        if (list.size() > 0) {
            if (mSearAppList_recycle.getVisibility() == View.GONE) {
                mSearAppList_recycle.setVisibility(View.VISIBLE);
            }
            mSearch_null.setVisibility(View.GONE);
            mBottom_re_ll.setVisibility(View.GONE);
            mAppListAdapter.setDataList(list);
        } else {
            mSearAppList_recycle.setVisibility(View.GONE);
            mSearch_null.setVisibility(View.VISIBLE);
            mBottom_re_ll.setVisibility(View.VISIBLE);
        }
    }

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
        mBottom_re_ll.setVisibility(View.VISIBLE);
    }

}
