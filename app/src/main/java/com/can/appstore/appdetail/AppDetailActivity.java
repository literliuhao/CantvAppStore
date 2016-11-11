package com.can.appstore.appdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.can.appstore.R;
import com.can.appstore.appdetail.adapter.IntroducGridAdapter;
import com.can.appstore.appdetail.adapter.RecommedGridAdapter;
import com.can.appstore.appdetail.custom.CustomDialog;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by JasonF on 2016/10/13.
 */
public class AppDetailActivity extends Activity implements AppDetailContract.View, View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "AppDetailActivity";
    private static final int TO_MOVE_RIGHT = 0;
    private static final int TO_MOVE_LEFT = 1;
    private static final int MESSAGE_TYPE_DOWNLAOD = 2;
    private static final int MESSAGE_TYPE_UPDATE = 3;
    private View mFocusedListChild;
    private AppDetailActivity.ListFocusMoveRunnable mListFocusMoveRunnable;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mScaleUtil;
    private boolean focusSearchFailed;
    private AppDetailPresenter mAppDetailPresenter;
    private ImageView mImageViewIcon;
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
    private ImageView mIvTabLine;
    private TextView mTvAppIntroduc;
    private TextView mTvAddFuntion;
    private RelativeLayout mRelativeLayuotOperatingEquipment;
    private List<String> mIntroduceInfoList = new ArrayList<String>();
    private RecommedGridAdapter mRecommedGridAdapter;
    private IntroducGridAdapter mIntroducGridAdapter;
    private boolean isTabLineMoveToRecommend = false;//线是否移动到推荐
    private boolean isRecommendGridFirstRow = false;//焦点是否在推荐列表的第一行
    private boolean isSwitchAnimatComplete = true;//底部动画是否切换完成
    private ViewFlipper mViewFlipper;
    private CanRecyclerView.CanLinearLayoutManager mIntroducLayoutManager;
    private LinearLayout mLayoutIntroduceText;
    private CustomDialog mCustomDialog;
    private AppInfo mAppinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_detail);
        initView();
        mFocusMoveUtil = new FocusMoveUtil(AppDetailActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);
        mScaleUtil = new FocusScaleUtil();
        mListFocusMoveRunnable = new AppDetailActivity.ListFocusMoveRunnable();
        mAppDetailPresenter = new AppDetailPresenter(this, AppDetailActivity.this, getIntent());
        mAppDetailPresenter.startLoad();
    }

    @Override
    protected void onResume() {
        mAppDetailPresenter.addBroadcastReceiverListener();
        super.onResume();
    }

    private void initView() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        mImageViewIcon = (ImageView) findViewById(R.id.iv_icon);
        mAppName = (TextView) findViewById(R.id.tv_app_name);
        mAppSize = (TextView) findViewById(R.id.tv_app_size);
        mAppUodateDate = (TextView) findViewById(R.id.tv_update_date);
        mAppDownloadCount = (TextView) findViewById(R.id.tv_download_count);
        mAppFreeStroage = (TextView) findViewById(R.id.tv_free_stroage);
        mBtIntroduction = (Button) findViewById(R.id.bt_Introduction);
        mBtRecommend = (Button) findViewById(R.id.bt_recommend);
        mTvAppIntroduc = (TextView) findViewById(R.id.tv_app_introduc);
        mTvAddFuntion = (TextView) findViewById(R.id.tv_add_function);
        mIvTabLine = (ImageView) findViewById(R.id.iv_tab_line);
        mButtonDownload = (TextProgressBar) findViewById(R.id.bt_download);
        mButtonUpdate = (TextProgressBar) findViewById(R.id.bt_update);
        mRelativeLayuotOperatingEquipment = (RelativeLayout) findViewById(R.id.rl_operating_equipment);
        mLayoutIntroduceText = (LinearLayout) findViewById(R.id.ll_introduce_text);
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
        mRecommendGrid.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(AppDetailActivity.this, 4, CanRecyclerView.CanGridLayoutManager.VERTICAL, false), new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = true;
            }
        });
        mIntroducLayoutManager = new CanRecyclerView.CanLinearLayoutManager(AppDetailActivity.this, CanRecyclerView.CanLinearLayoutManager.HORIZONTAL, false);
        mIntroducLayoutManager.setOnFocusSearchFailCallback(new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = true;
            }
        });
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
        Log.d(TAG, "onClick view : " + view);
        switch (view.getId()) {
            case R.id.bt_download:
                mAppDetailPresenter.clickStartDownload(false);
                break;
            case R.id.bt_update:
                mAppDetailPresenter.clickStartDownload(true);
                break;
            case R.id.bt_Introduction:
                break;
            case R.id.bt_recommend:
                break;
            case R.id.ll_introduce_text:
                CustomDialog.Builder builder = new CustomDialog.Builder(AppDetailActivity.this);
                builder.setUpdatelogText(mAppinfo.getUpdateLog());
                builder.setAboutText(mAppinfo.getAbout());
                //                Bitmap shots = AppUtils.getScreenShots(AppDetailActivity.this);
                //                Canvas canvas = new Canvas(shots);
                //                canvas.drawARGB(0xD2, 23, 25, 29);
                //                Drawable drawable = AppUtils.blurBitmap(shots, AppDetailActivity.this);
                //                builder.setBulrBg(drawable);
                //                Bitmap shots = AppUtils.getScreenShots(AppDetailActivity.this);
                //                Drawable drawable = AppUtils.blurBitmap(shots, AppDetailActivity.this);
                //                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                //                Bitmap bitmap = bitmapDrawable.getBitmap();
                //                Canvas canvas = new Canvas(bitmap);
                //                canvas.drawARGB(0xD2, 23, 25, 29);
                //                builder.setBulrBg(drawable);
                mCustomDialog = builder.create();
                mCustomDialog.show();
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.d(TAG, "onFocusChange : " + hasFocus + " view : " + view + " isTabLineMoveToRec : " + isTabLineMoveToRecommend);
        switch (view.getId()) {
            case R.id.bt_download:
                if (hasFocus) {
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
                    mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download_focus));
                } else {
                    mButtonDownload.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_app_detail_download));
                }
                break;
            case R.id.bt_Introduction:
                if (hasFocus) {
                    if (isTabLineMoveToRecommend) {
                        changeLayouToLeft();
                        startTabLineAnimation(TO_MOVE_LEFT);
                    }
                }
                break;
            case R.id.bt_recommend:
                if (hasFocus) {
                    if (!isTabLineMoveToRecommend) {
                        changeLayouToRight();
                        startTabLineAnimation(TO_MOVE_RIGHT);
                    }
                }
                break;
            case R.id.ll_introduce_text:
                break;
        }
        focusSetting(hasFocus, view);
    }

    /**
     * 焦点框设置
     *
     * @param hasFocus
     * @param view
     */
    public void focusSetting(boolean hasFocus, View view) {
        mFocusedListChild = view;
        if (hasFocus) {
            mListFocusMoveRunnable.run();
        } else {
            mScaleUtil.scaleToNormal();
        }
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

    public void changeLayouToRight() {
        isSwitchAnimatComplete = false;
        mViewFlipper.setInAnimation(AppDetailActivity.this, R.anim.push_right_in);
        mViewFlipper.setOutAnimation(AppDetailActivity.this, R.anim.push_left_out);
        mViewFlipper.showNext();
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

    public void changeLayouToLeft() {
        isSwitchAnimatComplete = false;
        mViewFlipper.setInAnimation(AppDetailActivity.this, R.anim.push_left_in);
        mViewFlipper.setOutAnimation(AppDetailActivity.this, R.anim.push_right_out);
        mViewFlipper.showPrevious();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown : " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mButtonDownload.isFocused()) {
                    return true;
                } else if (mBtIntroduction.isFocused() && mButtonUpdate.getVisibility() == View.INVISIBLE) {
                    requestFocus(mButtonDownload);
                    return true;
                } else if (mBtIntroduction.isFocused() && mButtonUpdate.getVisibility() == View.VISIBLE) {
                    requestFocus(mButtonUpdate);
                    return true;
                } else if (mButtonUpdate.isFocused() && mButtonUpdate.getVisibility() == View.VISIBLE) {
                    requestFocus(mButtonDownload);
                    return true;
                } else if (mLayoutIntroduceText.isFocused()) {
                    return true;
                } else if (!isSwitchAnimatComplete) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mButtonDownload.isFocused() && mButtonUpdate.getVisibility() == View.VISIBLE) {
                    requestFocus(mButtonUpdate);
                    return true;
                } else if (!isSwitchAnimatComplete) {
                    return true;
                } else if (mLayoutIntroduceText.isFocused()) {
                    startTabLineAnimation(TO_MOVE_RIGHT);
                    changeLayouToRight();
                    recommendGridPositionRequestFocus(500, 0);
                    mScaleUtil.scaleToNormal(mLayoutIntroduceText);
                    return true;
                } else if (mBtIntroduction.isFocused()) {
                    mBtRecommend.requestFocus();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mLayoutIntroduceText.isFocused()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mButtonUpdate.isFocused() || mButtonDownload.isFocused() || mBtIntroduction.isFocused() || mBtRecommend.isFocused()) {
                    return true;
                } else if (mRecommendGrid.isShown() && isRecommendGridFirstRow) {
                    requestFocus(mButtonDownload);
                } else if (mIntroducGrid.isShown() && !mLayoutIntroduceText.isFocused()) {
                    requestFocus(mButtonDownload);
                }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 请求推荐列表的第一个位置焦点
     */
    private void recommendGridPositionRequestFocus(int hideFocusTime, final int position) {
        mFocusMoveUtil.hideFocusForShowDelay(hideFocusTime);
        mRecommendGrid.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mRecommendGrid.getChildAt(position);
                if (childAt != null) {
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                } else {
                    mBtRecommend.requestFocus();
                }
            }
        }, 50);
    }

    public void requestFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * 更新下载按钮的状态
     *
     * @param buttonText
     * @param progress
     */
    public void refreshDownloadButtonProgress(String buttonText, float progress) {
        if (mButtonDownload != null) {
            mButtonDownload.setProgress((int) progress);
            mButtonDownload.setText(buttonText);
        }
    }

    /**
     * 刷新更新按钮的状态
     *
     * @param buttonText
     * @param progress
     */
    public void refreshUpdateButtonProgress(String buttonText, float progress) {
        if (mButtonUpdate != null) {
            mButtonUpdate.setProgress((int) progress);
            mButtonUpdate.setText(buttonText);
        }
    }

    @Override
    public void showLoading() {
        mAppDetailPresenter.showLoading(getResources().getString(R.string.loading));
    }

    @Override
    public void hideLoading() {
        mAppDetailPresenter.hideLoading();
    }

    @Override
    public void loadDataFail() {
        mAppDetailPresenter.showToast(getResources().getString(R.string.load_data_faild));
        finish();
    }

    @Override
    public void onClickHomeKey() {
        dismissIntroduceDialog();
        finish();
    }

    @Override
    public void loadAppInfoOnSuccess(AppInfo appInfo) {
        mAppinfo = appInfo;
        setData();
        setIntroduceAdapter();
        setRecommendAdapter();
    }

    private void setData() {//TODO   修改设置数据
        int roundSize = getResources().getDimensionPixelSize(R.dimen.dimen_36px);
        ImageLoader.getInstance().buildTask(mImageViewIcon, mAppinfo.getIcon()).bitmapTransformation(new GlideRoundTransform(AppDetailActivity.this, roundSize)).build().start(AppDetailActivity.this);
        mAppName.setText(mAppinfo.getName());
        mAppSize.setText(String.format(getResources().getString(R.string.detail_app_size), mAppinfo.getSizeStr()));
        mAppUodateDate.setText(String.format(getResources().getString(R.string.detail_app_update_date), mAppinfo.getUpdateTime()));
        mAppDownloadCount.setText(String.format(getResources().getString(R.string.detail_app_downlaod_count), mAppinfo.getDownloadCount()));
        mAppFreeStroage.setText(String.format(getResources().getString(R.string.detail_app_free_stroage), StringUtils.formatFileSize(SystemUtil.getSDCardAvailableSpace(), false)));
        mTvAppIntroduc.setText(getResources().getString(R.string.app_introduce) + mAppinfo.getAbout());
        String updateLog = mAppinfo.getUpdateLog().replaceAll("\n", "");
        mTvAddFuntion.setText(getResources().getString(R.string.add_funtion) + updateLog);
        setOperaPic(mAppinfo.getControls());
    }

    /**
     * 设置操作类型的图标
     *
     * @param type
     */
    public void setOperaPic(List<String> type) {
        for (int i = 0; i < type.size(); i++) {
            View childAt = mRelativeLayuotOperatingEquipment.getChildAt(i);
            RelativeLayout.LayoutParams controllerTypePic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            ImageView conTypePic = new ImageView(this);
            if (i == 0) {
                controllerTypePic.addRule(RelativeLayout.RIGHT_OF, R.id.tv_operating_equipment);
            } else {
                controllerTypePic.addRule(RelativeLayout.RIGHT_OF, childAt.getId());
            }
            controllerTypePic.leftMargin = getResources().getDimensionPixelSize(R.dimen.dimen_24px);
            controllerTypePic.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            conTypePic.setId(i + 1);
            conTypePic.setLayoutParams(controllerTypePic);
            conTypePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mRelativeLayuotOperatingEquipment.addView(conTypePic, controllerTypePic);
            int selectOperationPic = mAppDetailPresenter.getOperationPic(type.get(i));
            conTypePic.setImageResource(selectOperationPic);
        }
    }

    @Override
    public void refreshDownloadButtonStatus(int status, float progress) {
        mHandler.removeMessages(MESSAGE_TYPE_UPDATE);
        Message message = mHandler.obtainMessage();
        message.what = MESSAGE_TYPE_DOWNLAOD;
        message.arg1 = (int) progress;
        if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_RUN) {
            message.obj = getResources().getString(R.string.detail_app_run);
            mButtonDownload.setBackgroundResource(R.drawable.layer_list_app_detail_run);
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
        }
        mHandler.sendMessage(message);
    }

    @Override
    public void refreshUpdateButtonStatus(int status, float progress) {
        mHandler.removeMessages(MESSAGE_TYPE_DOWNLAOD);
        Message message = mHandler.obtainMessage();
        message.what = MESSAGE_TYPE_UPDATE;
        message.arg1 = (int) progress;
        if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_INSTALLING) {
            message.obj = getResources().getString(R.string.detail_app_installing);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_PAUSE) {
            message.obj = getResources().getString(R.string.detail_app_click_continue);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_PREPARE) {
            message.obj = getResources().getString(R.string.detail_app_update);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_WAIT) {
            message.obj = getResources().getString(R.string.detail_app_download_wait);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_DOWNLAODING) {
            message.obj = getResources().getString(R.string.detail_app_click_pause);
        } else if (status == AppDetailPresenter.DOWNLOAD_BUTTON_STATUS_RESTART) {
            message.obj = getResources().getString(R.string.downlaod_restart);
        }
        mHandler.sendMessage(message);
    }

    @Override
    public void refreshUpdateButton(boolean isShow) {
        if (mButtonUpdate != null) {
            if (isShow) {
                mButtonUpdate.setVisibility(View.VISIBLE);
            } else {
                mButtonUpdate.setVisibility(View.INVISIBLE);
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_TYPE_DOWNLAOD) {
                float progress = msg.arg1;
                String text = (String) msg.obj;
                refreshDownloadButtonProgress(text, progress);
            } else if (msg.what == MESSAGE_TYPE_UPDATE) {
                float progress = msg.arg1;
                String text = (String) msg.obj;
                refreshUpdateButtonProgress(text, progress);
            }
            super.handleMessage(msg);
        }
    };

    private void setIntroduceAdapter() {
        mIntroduceInfoList.clear();
        for (int i = 0; i < 5; i++) {
            mIntroduceInfoList.add("1");
        }
        if (mIntroducGridAdapter == null) {
            mIntroducGridAdapter = new IntroducGridAdapter(AppDetailActivity.this, mIntroduceInfoList);
            //                        mIntroducGridAdapter = new IntroducGridAdapter(AppDetailActivity.this, mAppinfo.getThumbs());
            addIntroduceGridListener();
            addIntroduceSetting();
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
                Log.d(TAG, "mIntroducGridAdapter onItemFocusChanged: " + view + "   position : " + position);
                if (hasFocus) {
                    mFocusedListChild = view;
                    mIntroducGrid.postDelayed(mListFocusMoveRunnable, 50);
                } else {
                    mScaleUtil.scaleToNormal();
                }
            }
        });

        mIntroducGridAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "mIntroducGridAdapter onItemKeyEvent keyCode : " + keyCode + "    position : " + position);
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (position == 4) {
                        startTabLineAnimation(TO_MOVE_RIGHT);
                        changeLayouToRight();
                        recommendGridPositionRequestFocus(500, 0);
                        mScaleUtil.scaleToNormal(v);
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
                if (isTabLineMoveToRecommend) {
                    int size = mAppinfo.getRecommend().size();
                    if (size < 4) {
                        recommendGridPositionRequestFocus(200, size - 1);
                    } else {
                        recommendGridPositionRequestFocus(0, 3);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mListFocusMoveRunnable.run();
            }
        });
    }

    private void setRecommendAdapter() {
        if (mRecommedGridAdapter == null) {
            mRecommedGridAdapter = new RecommedGridAdapter(AppDetailActivity.this, mAppinfo.getRecommend());
            addRecommendGridListener();
            addRecommendSetting();
        }
    }

    private void addRecommendGridListener() {
        mRecommedGridAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                Log.d(TAG, "mRecommedGridAdapter onItemFocusChanged: " + view + "   position : " + position);
                if (hasFocus) {
                    mFocusedListChild = view;
                    mRecommendGrid.postDelayed(mListFocusMoveRunnable, 50);
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
                Log.d(TAG, "mRecommedGridAdapter onItemKeyEvent keyCode : " + keyCode + "    position : " + position);
                if (mRecommendGrid.isShown()) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (position % 4 == 0) {
                            startTabLineAnimation(TO_MOVE_LEFT);
                            changeLayouToLeft();
                            introduceGridRequestFocus(500, 4);
                            mScaleUtil.scaleToNormal(v);
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
                        int size = mAppinfo.getRecommend().size();
                        if (size - 1 == position) {
                            return true;
                        } else if (position % 4 == 3) {
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (position == 0 || position == 1 || position == 2 || position == 3) {
                            isRecommendGridFirstRow = true;
                        } else {
                            isRecommendGridFirstRow = false;
                        }
                    }
                }
                return false;
            }
        });
        mRecommedGridAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                Log.d(TAG, "mRecommedGridAdapter onClick position: " + position);
                if (mRecommendGrid.isShown()) {
                    Intent intent = new Intent(AppDetailActivity.this, AppDetailActivity.class);
                    intent.putExtra("appID", mAppinfo.getRecommend().get(position).getId());
                    startActivity(intent);
                    finish();
                }
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
        mAppDetailPresenter.unRegiestr();
    }

    public void dismissIntroduceDialog() {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtil.release();
        mAppDetailPresenter.release();
        mIvTabLine.clearAnimation();
        dismissIntroduceDialog();
        mHandler.removeMessages(MESSAGE_TYPE_UPDATE);
        mHandler.removeMessages(MESSAGE_TYPE_DOWNLAOD);
        isRecommendGridFirstRow = false;
    }

    private class ListFocusMoveRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mScaleUtil.scaleToLarge(mFocusedListChild);
                if (focusSearchFailed) {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);
                } else {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f, 0);
                }
            }
        }
    }
}