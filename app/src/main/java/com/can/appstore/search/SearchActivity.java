package com.can.appstore.search;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.adapter.KeyboardAdapter;

import java.util.Arrays;
import java.util.List;

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
                    mSearchPresenter.getSearchList();
                    break;
            }
        }
    };


    public static void startAc(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }


    private void initView() {
        mSearchPresenter = new SearchPresenter(this);
        List<String> mKeyList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
                , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        mSearch_con_view = (TextView) findViewById(R.id.show_se_con_view);
        mKeyboard_recy = (RecyclerView) findViewById(R.id.keyboard_recycleview);
        mContent_cl_view = (TextView) findViewById(R.id.con_clear_view);
        mContent_del_view = (TextView) findViewById(R.id.con_del_view);

        mKeyboard_recy.setLayoutManager(new GridLayoutManager(SearchActivity.this, 6, GridLayoutManager.VERTICAL, false));
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
    }

    @Override
    public void clearContent() {
        mSearch_con_view.setText("");
    }
}
