package com.can.appstore.appdetail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.can.appstore.R;
import com.can.appstore.appdetail.adapter.ImageScaleAdapter;
import com.can.appstore.appdetail.custom.AlphaPageTransformer;
import com.can.appstore.appdetail.custom.ScaleInTransformer;

/**
 * Created by JasonF on 2016/10/20.
 */

public class ImageScaleActivity extends Activity {

    private static final String TAG = "ImageScaleActivity";
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private ImageScaleAdapter mScaleAdapter;
    private int mCurSelectPositon;
    private int mBeforePosition;
    private View mRootView;
    private BroadcastReceiver mHomeReceivcer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_scale);
        getData();
        initView();
    }

    public void getData() {//获取数据
        Intent intent = getIntent();
        mCurSelectPositon = intent.getIntExtra("currentIndex", 0);
        Log.d(TAG, "mCurSelectPositon pic : " + mCurSelectPositon);
    }

    @Override
    protected void onResume() {
        registHomeBoradCast();
        super.onResume();
    }

    private void registHomeBoradCast() {
        if (mHomeReceivcer == null) {
            mHomeReceivcer = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                        if (mRootView != null) {
                            mRootView.setVisibility(View.INVISIBLE);
                        }
                        finish();
                        return;
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(mHomeReceivcer, filter);
        }
    }

    private void initView() {
        mRootView = findViewById(R.id.ll_view);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_point);
        createPoint();

        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.dimen_210px));
        mViewPager.setOffscreenPageLimit(3);
        mScaleAdapter = new ImageScaleAdapter(ImageScaleActivity.this, null);
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
                Log.d(TAG, "currState : " + state);
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
        Log.d(TAG, "setPointSelector selectPosition: " + selectPosition + "isSelect : " + isSelect);
        ImageView img = (ImageView) mLinearLayout.getChildAt(selectPosition);
        img.clearAnimation();
        img.setSelected(isSelect);
    }

    private void createPoint() {
        int dotSize = getResources().getDimensionPixelSize(R.dimen.dimen_16px);
        for (int i = 0; i < 5; i++) {
            ImageView imagePont = new ImageView(ImageScaleActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            imagePont.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagePont.setBackgroundResource(R.drawable.selector_point_foucs);
            imagePont.setSelected(false);
            if (i != 0) {
                params.setMarginStart(dotSize);
            }
            imagePont.setLayoutParams(params);
            mLinearLayout.addView(imagePont);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown : " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        if (mHomeReceivcer != null) {
            unregisterReceiver(mHomeReceivcer);
            mHomeReceivcer = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
