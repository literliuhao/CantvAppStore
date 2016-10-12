package com.can.appstore.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.adapter.KeyboardAdapter;

import java.util.Arrays;
import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class SearchActivity extends AppCompatActivity {

    private TextView mSearch_con_view;
    private RecyclerView mKeyboard_recy;
    private TextView mContent_cl_view;
    private TextView mContent_del_view;
    private KeyboardAdapter mKeyboardAdapter;


    public static void startAc(Context context){
        Intent intent = new Intent(context,SearchActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }


    private void initView() {
        List<String> mKeyList = Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N"
                ,"O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","0");
        mSearch_con_view = (TextView) findViewById(R.id.show_se_con_view);
        mKeyboard_recy = (RecyclerView) findViewById(R.id.keyboard_recycleview);
        mContent_cl_view = (TextView) findViewById(R.id.con_clear_view);
        mContent_del_view = (TextView) findViewById(R.id.con_del_view);

        mKeyboard_recy.setLayoutManager(new GridLayoutManager(SearchActivity.this,6,GridLayoutManager.VERTICAL,false));
        mKeyboardAdapter = new KeyboardAdapter(mKeyList);
        mKeyboard_recy.setAdapter(mKeyboardAdapter);
    }
}
