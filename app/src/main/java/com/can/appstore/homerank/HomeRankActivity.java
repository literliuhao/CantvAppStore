package com.can.appstore.homerank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.can.appstore.R;
import com.can.appstore.search.SearchActivity;

public class HomeRankActivity extends AppCompatActivity {

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
            HomeRankFragment homeRankFragment = HomeRankFragment.newInstance("", "");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rank_framelayout, homeRankFragment, TAG)
                    .commit();
        }

    }
}
