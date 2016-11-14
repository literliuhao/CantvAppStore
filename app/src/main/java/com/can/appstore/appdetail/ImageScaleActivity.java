package com.can.appstore.appdetail;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.can.appstore.R;
import com.can.appstore.appdetail.adapter.ImageScaleAdapter;
import com.can.appstore.appdetail.custom.AlphaPageTransformer;
import com.can.appstore.appdetail.custom.PointView;
import com.can.appstore.appdetail.custom.ScaleInTransformer;
import com.can.appstore.base.BaseActivity;

import java.util.List;

/**
 * Created by JasonF on 2016/10/20.
 */

public class ImageScaleActivity extends BaseActivity {

    private static final String TAG = "ImageScaleActivity";
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private ImageScaleAdapter mScaleAdapter;
    private int mCurSelectPositon;
    private int mBeforePosition;
    private List<String> mImageUrls;
    private PointView mPointView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_scale);
        getData();
        initView();
    }

    public void getData() {
        Intent intent = getIntent();
        mCurSelectPositon = intent.getIntExtra("currentIndex", 0);
        mImageUrls = (List<String>) intent.getSerializableExtra("imageUrl");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_point);
        mPointView = (PointView) findViewById(R.id.point_view);
        mPointView.setPointCount(mImageUrls.size());
        mPointView.setSelectPosition(mCurSelectPositon);

        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.dimen_210px));
        mViewPager.setOffscreenPageLimit(3);
        mScaleAdapter = new ImageScaleAdapter(ImageScaleActivity.this, mImageUrls);
        mViewPager.setAdapter(mScaleAdapter);
        mViewPager.setPageTransformer(true, new ScaleInTransformer(0.91f, new AlphaPageTransformer(0.3f)));
        mViewPager.setCurrentItem(mScaleAdapter.getInstantiatePosition(mCurSelectPositon), true);
        setPointSelector(mCurSelectPositon, true);
        mBeforePosition = mCurSelectPositon;
        addListener();
    }

    private void addListener() {
        //noinspection deprecation
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int realPosition = mScaleAdapter.getRealPosition(mViewPager.getCurrentItem());
                setPointSelector(realPosition, true);
                setPointSelector(mBeforePosition, false);
                Log.d(TAG, "mCurSelectPositon : " + mCurSelectPositon + "  mBeforePosition  : " + mBeforePosition + "  position : " + position);
                mBeforePosition = realPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setPointSelector(int selectPosition, boolean isSelect) {
        if (isSelect) {
            mPointView.setSelectPosition(selectPosition);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown : " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
