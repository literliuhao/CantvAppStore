package com.can.appstore.appdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;
import com.can.appstore.appdetail.adapter.IntroducGridAdapter;
import com.can.appstore.appdetail.adapter.RecommedGridAdapter;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by JasonF on 2016/10/13.
 */
@SuppressWarnings("deprecation")
public class AppDetailActivity extends BaseActivity implements AppDetailContract.View, View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "AppDetailActivity";
    private static final int TO_MOVE_RIGHT = 0;
    private static final int TO_MOVE_LEFT = 1;
    private static final int MESSAGE_TYPE_DOWNLAOD = 2;
    private static final int MESSAGE_TYPE_UPDATE = 3;
    private static final int RECOMMEND_LINE_COUNT = 4;
    private View mFocusedListChild;
    private AppDetailActivity.ListFocusMoveRunnable mListFocusMoveRunnable;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mScaleUtil;
    private AppDetailPresenter mAppDetailPresenter;
    private GlideRoundCornerImageView mImageViewIcon;
    private TextView mAppName;
    private TextView mAppSize;
    private TextView mAppUodateDate;
    private TextView mAppDownloadCount;
    private TextView mAppFreeStroage;
    private CanRecyclerView mRecommendGrid;
    private CanRecyclerView mIntroducGrid;
    private CanRecyclerViewDivider mRecommendGridDivider;
    private CanRecyclerViewDivider mIntroduceGridDivider;
    private TextProgressBar mButtonDownload;
    private TextProgressBar mButtonUpdate;
    private TextView mBtIntroduction;
    private Button mBtRecommend;
    private View mIvTabLine;
    private TextView mTvAppIntroduc;
    private TextView mTvAddFuntion;
    private TextView mTvDeveloper;
    private RelativeLayout mRelativeLayuotOperatingEquipment;
    private List<String> mControlType = new ArrayList<>();
    private RecommedGridAdapter mRecommedGridAdapter;
    private IntroducGridAdapter mIntroducGridAdapter;
    private ViewFlipper mViewFlipper;
    private CanRecyclerView.CanLinearLayoutManager mIntroducLayoutManager;
    private LinearLayout mLayoutIntroduceText;
    private LinearLayout mLayoutAppDetail;
    private boolean isTabLineMoveToRecommend = false;//线是否移动到推荐
    private boolean isSwitchAnimatComplete = true;//底部动画是否切换完成
    private AppInfo mAppinfo;
    private Rect mFocusRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        initView();
        initFocusUtil();
        mAppDetailPresenter = new AppDetailPresenter(this, AppDetailActivity.this, getIntent());
        mAppDetailPresenter.startLoad();
    }

    @Override
    protected void onResume() {
        mAppDetailPresenter.addBroadcastReceiverListener();
        super.onResume();
    }

    public void initFocusUtil() {
        mFocusMoveUtil = new FocusMoveUtil(AppDetailActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);
        mScaleUtil = new FocusScaleUtil();
        mScaleUtil.setFocusScale(1.0f);
        measureFocusActiveRegion();
        mFocusMoveUtil.hideFocus();
        mListFocusMoveRunnable = new AppDetailActivity.ListFocusMoveRunnable();
    }

    private void measureFocusActiveRegion() {
        mFocusRegion = new Rect();
        mIntroducGrid.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mIntroducGrid.getLocationInWindow(location);
                mFocusRegion.set(location[0] + mIntroducGrid.getPaddingLeft(), 0,
                        location[0] + mIntroducGrid.getMeasuredWidth() - mIntroducGrid.getPaddingRight(),
                        getWindowManager().getDefaultDisplay().getHeight());
            }
        });
    }

    private void initView() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        mImageViewIcon = (GlideRoundCornerImageView) findViewById(R.id.iv_icon);
        mAppName = (TextView) findViewById(R.id.tv_app_name);
        mAppSize = (TextView) findViewById(R.id.tv_app_size);
        mAppUodateDate = (TextView) findViewById(R.id.tv_update_date);
        mAppDownloadCount = (TextView) findViewById(R.id.tv_download_count);
        mAppFreeStroage = (TextView) findViewById(R.id.tv_free_stroage);
        mBtIntroduction = (Button) findViewById(R.id.bt_Introduction);
        mBtRecommend = (Button) findViewById(R.id.bt_recommend);
        mTvAppIntroduc = (TextView) findViewById(R.id.tv_app_introduc);
        mTvAddFuntion = (TextView) findViewById(R.id.tv_add_function);
        mTvDeveloper = (TextView) findViewById(R.id.tv_app_developer);
        mIvTabLine = findViewById(R.id.iv_tab_line);
        mButtonDownload = (TextProgressBar) findViewById(R.id.bt_download);
        mButtonUpdate = (TextProgressBar) findViewById(R.id.bt_update);
        mRelativeLayuotOperatingEquipment = (RelativeLayout) findViewById(R.id.rl_operating_equipment);
        mLayoutIntroduceText = (LinearLayout) findViewById(R.id.ll_introduce_text);
        mLayoutAppDetail = (LinearLayout) findViewById(R.id.ll_app_detail);
        mRecommendGrid = (CanRecyclerView) findViewById(R.id.crlv_recommed_grid);
        mIntroducGrid = (CanRecyclerView) findViewById(R.id.crlv_introduce_grid);
        mButtonDownload.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_36px));
        mButtonUpdate.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_36px));
        mButtonDownload.requestFocus();
        mButtonDownload.setFocusable(true);
        setGridLayoutManager();
        addButtonListener();
    }

    private void setGridLayoutManager() {
        mRecommendGrid.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(AppDetailActivity.this, 4, CanRecyclerView.CanGridLayoutManager.VERTICAL, false));
        mIntroducLayoutManager = new CanRecyclerView.CanLinearLayoutManager(AppDetailActivity.this, CanRecyclerView.CanLinearLayoutManager.HORIZONTAL, false);
        mIntroducGrid.setLayoutManager(mIntroducLayoutManager);
    }

    private void addButtonListener() {
        mButtonDownload.setOnFocusChangeListener(this);
        mButtonUpdate.setOnFocusChangeListener(this);
        mBtIntroduction.setOnFocusChangeListener(this);
        mBtRecommend.setOnFocusChangeListener(this);
        mButtonDownload.setOnClickListener(this);
        mLayoutIntroduceText.setOnFocusChangeListener(this);
        mButtonUpdate.setOnClickListener(this);
        mBtIntroduction.setOnClickListener(this);
        mBtRecommend.setOnClickListener(this);
        mLayoutIntroduceText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_download:
                mAppDetailPresenter.clickStartDownload(false);
                break;
            case R.id.bt_update:
                mAppDetailPresenter.clickStartDownload(true);
                break;
            case R.id.ll_introduce_text:
                mAppDetailPresenter.showIntroduceDialog();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.d(TAG, "onFocusChange : " + hasFocus + " view : " + view + " isTabLineMoveToRec : " + isTabLineMoveToRecommend);
        switch (view.getId()) {
            case R.id.bt_download:
                if (hasFocus) {
                    setTabLine(View.VISIBLE, getResources().getColor(R.color.tabline_show_color));
                    mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download_focus));
                } else {
                    if (ApkUtils.isAvailable(AppDetailActivity.this, mAppDetailPresenter.getCurAppPackageName())) {//应用已经安装
                        mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_run));
                    } else {
                        mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download));
                    }
                }
                break;
            case R.id.bt_update:
                if (hasFocus) {
                    setTabLine(View.VISIBLE, getResources().getColor(R.color.tabline_show_color));
                    mButtonUpdate.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download_focus));
                } else {
                    mButtonUpdate.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download));
                }
                break;
            case R.id.bt_Introduction:
                if (hasFocus) {
                    if (isTabLineMoveToRecommend) {
                        startMoveAnmi(TO_MOVE_LEFT);
                    }
                    setTabLine(View.GONE, Color.TRANSPARENT);
                }
                break;
            case R.id.bt_recommend:
                if (hasFocus) {
                    if (!isTabLineMoveToRecommend) {
                        startMoveAnmi(TO_MOVE_RIGHT);
                    }
                    setTabLine(View.GONE, Color.TRANSPARENT);
                }
                break;
            case R.id.ll_introduce_text:
                if (hasFocus) {
                    mFocusMoveUtil.setFocusActiveRegion(mFocusRegion.left - getResources().getDimensionPixelSize(R.dimen.dimen_20px),
                            mFocusRegion.top, mFocusRegion.right, mFocusRegion.bottom);
                }
                break;
        }
        buttonFocusSetting(hasFocus, view);
    }

    public void setTabLine(int isShow, int color) {
        mIvTabLine.setVisibility(isShow);
        mIvTabLine.setBackgroundColor(color);
    }

    public void buttonFocusSetting(boolean hasFocus, View view) {
        mFocusedListChild = view;
        if (hasFocus) {
            mListFocusMoveRunnable.run();
        } else {
            mScaleUtil.scaleToNormal();
        }
    }

    public void requestFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown : " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!isSwitchAnimatComplete) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!isSwitchAnimatComplete) {
                    return true;
                } else if (mLayoutIntroduceText.isFocused()) {
                    startMoveAnmi(TO_MOVE_RIGHT);
                    recommendGridPositionRequestFocus(500, 0);
                    mScaleUtil.scaleToNormal();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mButtonUpdate.isFocused() || mButtonDownload.isFocused() || mBtIntroduction.isFocused() || mBtRecommend.isFocused()) {
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mBtRecommend.isFocused() && mAppinfo.getRecommend().size() == 0 || mAppinfo.getRecommend() == null) {
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startTabLineAnimation(int moveDirection) {
        mIvTabLine.clearAnimation();
        TranslateAnimation translateAnimation = null;
        if (moveDirection == TO_MOVE_RIGHT) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.25f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            isTabLineMoveToRecommend = true;
        } else if (moveDirection == TO_MOVE_LEFT) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.25f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            isTabLineMoveToRecommend = false;
        }
        translateAnimation.setDuration(100);
        translateAnimation.setFillAfter(true);
        mIvTabLine.startAnimation(translateAnimation);
    }

    public void startMoveLayout(int moveDirection) {
        isSwitchAnimatComplete = false;
        if (moveDirection == TO_MOVE_RIGHT) {
            mViewFlipper.setInAnimation(AppDetailActivity.this, R.anim.push_right_in);
            mViewFlipper.setOutAnimation(AppDetailActivity.this, R.anim.push_left_out);
            mViewFlipper.showNext();
        } else if (moveDirection == TO_MOVE_LEFT) {
            mViewFlipper.setInAnimation(AppDetailActivity.this, R.anim.push_left_in);
            mViewFlipper.setOutAnimation(AppDetailActivity.this, R.anim.push_right_out);
            mViewFlipper.showPrevious();
        }
        mViewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isSwitchAnimatComplete = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void startMoveAnmi(int moveDirection) {
        startMoveLayout(moveDirection);
        startTabLineAnimation(moveDirection);
    }

    private void recommendGridPositionRequestFocus(final int hideFocusTime, final int position) {
        mRecommendGrid.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mRecommendGrid.getChildAt(position);
                if (childAt != null) {
                    mFocusMoveUtil.hideFocusForShowDelay(hideFocusTime);
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                } else {
                    requestFocus(mBtRecommend);
                }
            }
        }, 50);
    }

    @Override
    public void loadDataFail() {
        finish();
    }

    @Override
    public void setPresenter(AppDetailContract.Presenter presenter) {
    }

    @Override
    protected void onHomeKeyDown() {
        mAppDetailPresenter.dismissInsufficientStorageSpaceDialog();
        mAppDetailPresenter.dismissIntroduceDialog();
        finish();
    }

    private void setData() {
        mImageViewIcon.load(mAppinfo.getIcon(), R.drawable.shap_detail_icon_bg, R.mipmap.cibn_icon, R.mipmap.icon_loading_fail, true);
        mAppName.setText(String.format(getResources().getString(R.string.detail_app_name), mAppinfo.getName(), mAppinfo.getVersionName()));
        mAppSize.setText(String.format(getResources().getString(R.string.detail_app_size), mAppinfo.getSizeStr()));
        mAppUodateDate.setText(String.format(getResources().getString(R.string.detail_app_update_date), mAppinfo.getUpdateTime()));
        mAppDownloadCount.setText(String.format(getResources().getString(R.string.detail_app_downlaod_count), mAppinfo.getDownloadCount()));
        mAppFreeStroage.setText(String.format(getResources().getString(R.string.detail_app_free_stroage), StringUtils.formatFileSize(SystemUtil.getSDCardAvailableSpace(), false)));
        mTvAppIntroduc.setText(getResources().getString(R.string.app_introduce) + mAppinfo.getAbout());
        mTvDeveloper.setText(String.format(getResources().getString(R.string.detail_developer), mAppinfo.getDeveloper()));
        String updateLog = mAppinfo.getUpdateLog();
        if (!TextUtils.isEmpty(updateLog)) {
            updateLog = mAppinfo.getUpdateLog().replaceAll("\\r\\n", "  ");
            mTvAddFuntion.setText(getResources().getString(R.string.add_funtion) + updateLog);
        } else {
            mTvAddFuntion.setVisibility(View.INVISIBLE);
        }
        setOperaPic(mAppinfo.getControls());
    }

    public void setOperaPic(List<String> type) {
        if (mRelativeLayuotOperatingEquipment.getChildCount() != 1) {
            mRelativeLayuotOperatingEquipment.removeViewsInLayout(1, mControlType.size());
        }
        int width = getResources().getDimensionPixelSize(R.dimen.dimen_60px);
        int leftMargin = getResources().getDimensionPixelSize(R.dimen.dimen_12px);
        mControlType = type;
        for (int i = 0; i < type.size(); i++) {
            View childAt = mRelativeLayuotOperatingEquipment.getChildAt(i);
            RelativeLayout.LayoutParams controllerTypePic = new RelativeLayout.LayoutParams(width, width);
            final ImageView conTypePic = new ImageView(this);
            if (i == 0) {
                controllerTypePic.addRule(RelativeLayout.RIGHT_OF, R.id.tv_operating_equipment);
            } else {
                controllerTypePic.addRule(RelativeLayout.RIGHT_OF, childAt.getId());
            }
            controllerTypePic.leftMargin = leftMargin;
            controllerTypePic.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            conTypePic.setId(i + 1);
            conTypePic.setLayoutParams(controllerTypePic);
            conTypePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mRelativeLayuotOperatingEquipment.addView(conTypePic, controllerTypePic);
            ImageLoader.getInstance().load(AppDetailActivity.this, conTypePic, type.get(i), new GlideLoadTask.SuccessCallback() {
                @Override
                public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    conTypePic.setImageDrawable(resource);
                    return true;
                }
            }, null);
        }
    }

    @Override
    public void refreshDownloadButtonStatus(int status, float progress) {
        sendProgressMessage(status, MESSAGE_TYPE_DOWNLAOD, progress);
    }

    @Override
    public void refreshUpdateButtonStatus(int status, float progress) {
        sendProgressMessage(status, MESSAGE_TYPE_UPDATE, progress);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_TYPE_DOWNLAOD) {
                float progress = msg.arg1;
                String text = (String) msg.obj;
                refreshButtonProgress(MESSAGE_TYPE_DOWNLAOD, text, progress);
            } else if (msg.what == MESSAGE_TYPE_UPDATE) {
                float progress = msg.arg1;
                String text = (String) msg.obj;
                refreshButtonProgress(MESSAGE_TYPE_UPDATE, text, progress);
            }
            super.handleMessage(msg);
        }
    };

    private void sendProgressMessage(int status, int what, float progress) {
        Message message = mHandler.obtainMessage();
        message.what = what;
        message.arg1 = (int) progress;
        if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_RUN && what == MESSAGE_TYPE_DOWNLAOD) {
            message.obj = getResources().getString(R.string.detail_app_run);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_INSTALLING) {
            message.obj = getResources().getString(R.string.detail_app_installing);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_PAUSE) {
            message.obj = getResources().getString(R.string.detail_app_click_continue);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_PREPARE) {
            message.obj = getResources().getString(R.string.detail_app_download);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_WAIT) {
            message.obj = getResources().getString(R.string.detail_app_download_wait);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_DOWNLAODING) {
            message.obj = getResources().getString(R.string.detail_app_click_pause);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_RESTART) {
            message.obj = getResources().getString(R.string.downlaod_restart);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_UPDATE) {
            message.obj = getResources().getString(R.string.detail_app_update);
        }
        mHandler.sendMessage(message);
    }

    public void refreshButtonProgress(int refreshButtonProgress, String buttonText, float progress) {
        if (mButtonDownload != null && refreshButtonProgress == MESSAGE_TYPE_DOWNLAOD) {
            if (!buttonText.equals(getResources().getString(R.string.detail_app_run)) && !mButtonDownload.isFocused()) {
                mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download));
            }
            mButtonDownload.setProgress((int) progress);
            mButtonDownload.setText(buttonText);
            mHandler.removeMessages(MESSAGE_TYPE_DOWNLAOD);
        } else if (mButtonUpdate != null && refreshButtonProgress == MESSAGE_TYPE_UPDATE) {
            mButtonUpdate.setProgress((int) progress);
            mButtonUpdate.setText(buttonText);
            mHandler.removeMessages(MESSAGE_TYPE_UPDATE);
        }
    }

    @Override
    public void refreshUpdateButton(boolean isShow) {
        if (mButtonUpdate != null) {
            if (isShow) {
                mButtonUpdate.setVisibility(View.VISIBLE);
                mButtonDownload.requestFocus();
            } else {
                mButtonUpdate.setVisibility(View.INVISIBLE);
            }
        }
        setFocusMoveView(isShow);
    }

    private void setFocusMoveView(boolean isShow) {
        if (isShow) {
            mButtonDownload.setNextFocusRightId(mButtonUpdate.getId());
            mBtIntroduction.setNextFocusLeftId(mButtonUpdate.getId());
            mButtonUpdate.setNextFocusRightId(mBtIntroduction.getId());
        } else {
            mButtonDownload.setNextFocusRightId(mBtIntroduction.getId());
            mBtIntroduction.setNextFocusLeftId(mButtonDownload.getId());
        }
        mBtIntroduction.setNextFocusRightId(mBtRecommend.getId());
        mBtRecommend.setNextFocusLeftId(mBtIntroduction.getId());
    }

    @Override
    public void loadAppInfoOnSuccess(AppInfo appInfo) {
        mFocusMoveUtil.hideFocusForShowDelay(500);
        mLayoutAppDetail.setVisibility(View.VISIBLE);
        mAppinfo = appInfo;
        setData();
        setIntroduceAdapter();
        setRecommendAdapter();
    }

    private void setIntroduceAdapter() {
        if (mIntroducGridAdapter == null) {
            mIntroducGridAdapter = new IntroducGridAdapter(AppDetailActivity.this, mAppinfo.getThumbs());
            addIntroduceGridListener();
            addIntroduceSetting();
        } else {
            mIntroducGridAdapter.notifyDataSetChanged();
        }
    }

    private void addIntroduceSetting() {
        mIntroduceGridDivider = new CanRecyclerViewDivider(getResources().getDimensionPixelSize(R.dimen.dimen_32px));
        mIntroducGrid.addItemDecoration(mIntroduceGridDivider);
        mIntroducGrid.setHasFixedSize(true);
        mIntroducGrid.setItemAnimator(new DefaultItemAnimator());
        mIntroducGrid.setAdapter(mIntroducGridAdapter);
    }

    private void addIntroduceGridListener() {
        mIntroducGridAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.setFocusRes(AppDetailActivity.this, R.mipmap.image_focus);
                    setTabLine(View.VISIBLE, getResources().getColor(R.color.tabline_show_color));
                    mFocusedListChild = view;
                    boolean isLastChild = position == mIntroducGridAdapter.getItemCount() - 1;
                    mFocusMoveUtil.setFocusActiveRegion(mFocusRegion.left, mFocusRegion.top,
                            mFocusRegion.right - (isLastChild ? 0 : getResources().getDimensionPixelSize(R.dimen.dimen_32px)),
                            mFocusRegion.bottom);
                    mListFocusMoveRunnable.run();
                } else {
                    mFocusMoveUtil.setFocusRes(AppDetailActivity.this, R.mipmap.btn_focus);
                }
            }
        });

        mIntroducGridAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (position == mAppinfo.getThumbs().size() - 1) {
                        startMoveAnmi(TO_MOVE_RIGHT);
                        recommendGridPositionRequestFocus(500, 0);
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mLayoutIntroduceText.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN && position == 0) {
                    return true;
                }
                return false;
            }
        });

        mIntroducGridAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                mAppDetailPresenter.enterImageScaleActivity(position);
            }
        });

        mIntroducGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == CanRecyclerView.SCROLL_STATE_SETTLING) {
                    setButtonFocusable(false);
                } else if (newState == CanRecyclerView.SCROLL_STATE_IDLE) {
                    setButtonFocusable(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mListFocusMoveRunnable.run();
            }
        });
    }

    public void setButtonFocusable(boolean Focusable) {
        mBtIntroduction.setFocusable(Focusable);
        mBtRecommend.setFocusable(Focusable);
        mButtonDownload.setFocusable(Focusable);
        mButtonUpdate.setFocusable(Focusable);
    }

    private void setRecommendAdapter() {
        if (mAppinfo.getRecommend().size() == 0 || mAppinfo.getRecommend() == null) {
            mBtRecommend.setNextFocusDownId(mBtIntroduction.getId());
            return;
        }
        if (mRecommedGridAdapter == null) {
            mRecommedGridAdapter = new RecommedGridAdapter(AppDetailActivity.this, mAppinfo.getRecommend());
            addRecommendGridListener();
            addRecommendSetting();
        } else {
            mRecommedGridAdapter.notifyDataSetChanged();
        }
    }

    private void addRecommendGridListener() {
        mRecommedGridAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                Log.d(TAG, "mRecommedGridAdapter onItemFocusChanged: " + view + "   position : " + position);
                if (hasFocus) {
                    setTabLine(View.VISIBLE, getResources().getColor(R.color.tabline_show_color));
                    mFocusedListChild = view;
                    mListFocusMoveRunnable.run();
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item_focus);
                } else {
                    mScaleUtil.scaleToNormal();
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item);
                }
            }
        });
        mRecommedGridAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                if (mRecommendGrid.isShown()) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (position % RECOMMEND_LINE_COUNT == 0) {
                            startMoveAnmi(TO_MOVE_LEFT);
                            introduceGridRequestFocus(500, mAppinfo.getThumbs().size() - 1);
                            mScaleUtil.scaleToNormal(v);
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
                        int size = mAppinfo.getRecommend().size();
                        if (size - 1 == position) {
                            return true;
                        } else if (position % RECOMMEND_LINE_COUNT == 3) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        mRecommedGridAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                String appId = mAppinfo.getRecommend().get(position).getId();
                mAppDetailPresenter.mAppId = appId;
                mAppDetailPresenter.startLoad();
            }
        });
        mRecommendGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mListFocusMoveRunnable.run();
            }
        });
    }

    private void introduceGridRequestFocus(int hideTime, final int position) {
        mFocusMoveUtil.hideFocusForShowDelay(hideTime);
        mIntroducGrid.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mIntroducLayoutManager.findViewByPosition(position);
                if (childAt != null) {
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                }
            }
        }, 50);
    }

    private void addRecommendSetting() {
        mRecommendGridDivider = new CanRecyclerViewDivider(0, getResources().getDimensionPixelSize(R.dimen.dimen_24px), getResources().getDimensionPixelSize(R.dimen.dimen_32px));
        mRecommendGrid.addItemDecoration(mRecommendGridDivider);
        mRecommendGrid.setHasFixedSize(true);
        mRecommendGrid.setItemAnimator(new DefaultItemAnimator());
        mRecommendGrid.setAdapter(mRecommedGridAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtil.release();
        mAppDetailPresenter.release();
        mIvTabLine.clearAnimation();
        mHandler.removeMessages(MESSAGE_TYPE_UPDATE);
        mHandler.removeMessages(MESSAGE_TYPE_DOWNLAOD);
    }

    private class ListFocusMoveRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
            }
        }
    }

    /**
     * 打开应用详情页面
     *
     * @param context 上下文
     * @param appID   应用id
     * @param topicID
     */
    public static void actionStart(Context context, String appID, String topicID) {
        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra(AppDetailPresenter.ARGUMENT_APPID, appID);
        intent.putExtra(AppDetailPresenter.ARGUMENT_TOPICID, topicID);
        context.startActivity(intent);
    }

    /**
     * 打开应用详情页面
     *
     * @param context
     * @param appID   应用id
     */
    public static void actionStart(Context context, String appID) {
        actionStart(context, appID, null);
    }
}