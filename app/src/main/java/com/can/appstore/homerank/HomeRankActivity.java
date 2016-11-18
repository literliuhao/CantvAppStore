package com.can.appstore.homerank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.can.appstore.R;
import com.can.appstore.index.interfaces.IAddFocusListener;

public class HomeRankActivity extends AppCompatActivity implements IAddFocusListener {

    private FrameLayout mFrameLayout;
    private static final String TAG = HomeRankActivity.class.getSimpleName();

    public static void startAc(Context context) {
        Intent intent = new Intent(context, HomeRankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_rank);
        mFrameLayout = (FrameLayout) findViewById(R.id.rank_framelayout);

        if (null == savedInstanceState) {
            HomeRankFragment homeRankFragment = new HomeRankFragment(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rank_framelayout, homeRankFragment, TAG)
                    .commit();
        }

    }

    @Override
    public void addFocusListener(View v, boolean hasFocus) {

    }
}
