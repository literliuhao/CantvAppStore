package com.can.appstore.appdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.can.appstore.R;
import com.can.appstore.appdetail.adapter.ImageScaleAdapter;
import com.can.appstore.appdetail.custom.AlphaPageTransformer;
import com.can.appstore.appdetail.custom.PointView;
import com.can.appstore.appdetail.custom.ScaleInTransformer;
import com.can.appstore.base.BaseActivity;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;

/**
 * Created by JasonF on 2016/10/20.
 */

public class ImageScaleActivity extends BaseActivity {
    private static final String TAG = "ImageScaleActivity";
    private static final int KEYCODE_EFFECT_INTERVAL_UNLIMIT = 0;
    private static final int KEYCODE_EFFECT_INTERVAL_NORMAL = 150;
    public static final String CURRENT_INDEX = "currentIndex";
    public static final String IMAGE_URL = "imageUrl";
    private ViewPager mViewPager;
    private ImageScaleAdapter mScaleAdapter;
    private int mCurSelectPositon;
    private int mBeforePosition;
    private List<String> mImageUrls;
    private PointView mPointView;
    private long mLastKeyCodeTimePoint;
    private int keyCodeEffectInterval = KEYCODE_EFFECT_INTERVAL_NORMAL;

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
        mCurSelectPositon = intent.getIntExtra(CURRENT_INDEX, 0);
        mImageUrls = (List<String>) intent.getSerializableExtra(IMAGE_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPointView = (PointView) findViewById(R.id.point_view);
        mPointView.setPointCount(mImageUrls.size());
        mPointView.setSelectPosition(mCurSelectPositon);

        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.px210));
        Utils.controlViewPagerSpeed(ImageScaleActivity.this, mViewPager, 400);
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
                Log.d(TAG, "onPageScrollStateChanged: state : " + state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    ImageLoader.getInstance().resumeAllTask(ImageScaleActivity.this);
                } else {
                    ImageLoader.getInstance().pauseAllTask(ImageScaleActivity.this);
                }
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
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCodeEffectInterval != KEYCODE_EFFECT_INTERVAL_UNLIMIT) {
            long time = System.currentTimeMillis();
            if (mLastKeyCodeTimePoint == 0) {
                mLastKeyCodeTimePoint = System.currentTimeMillis();
                return super.dispatchKeyEvent(event);
            } else if (time - mLastKeyCodeTimePoint < keyCodeEffectInterval) {
                return true;
            } else {
                mLastKeyCodeTimePoint = System.currentTimeMillis();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageUrls != null) {
            mImageUrls.clear();
            mImageUrls = null;
        }
        mScaleAdapter.release();
    }
}
