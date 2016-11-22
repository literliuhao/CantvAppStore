package com.can.appstore.appdetail.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.can.appstore.R;

/**
 * Created by JasonF on 2016/11/3.
 */

public class CustomDialog extends Dialog {
    private static final String TAG = "CustomDialog";

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private LinearLayout mLinearLyoutContent;
        private ScrollView mScrollView;
        private TextView mTvAppUpdatelog;
        private TextView mTvAppAbout;
        private String mUpdatelogText;
        private String mAboutText;
        private LinearLayout mLinearLyoutOneBg;
        private Drawable mDrawable;

        public Builder(Context context) {
            this.mContext = context;
        }

        public void setUpdatelogText(String updatelogText) {
            this.mUpdatelogText = updatelogText;
        }

        public void setAboutText(String aboutText) {
            this.mAboutText = aboutText;
        }

        public void setBulrBg(Drawable drawable) {
            this.mDrawable = drawable;
        }

        public CustomDialog create() {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CustomDialog customDialog = new CustomDialog(mContext, R.style.Iintroduce_Dialog_Transparent);
            View view = layoutInflater.inflate(R.layout.dialog_introduce, null);
            customDialog.setContentView(view);

            mLinearLyoutOneBg = (LinearLayout) view.findViewById(R.id.ll_one_bg);
            mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            mLinearLyoutContent = (LinearLayout) view.findViewById(R.id.ll_content);
            mTvAppUpdatelog = (TextView) view.findViewById(R.id.tv_app_updatelog);
            mTvAppAbout = (TextView) view.findViewById(R.id.tv_app_about);
            mScrollView.setVerticalScrollBarEnabled(false);
            //            mLinearLyoutOneBg.setBackground(mDrawable);
            mLinearLyoutOneBg.setBackgroundResource(R.color.introduce_one_bg);
            mTvAppUpdatelog.setText(mContext.getResources().getString(R.string.update_introduce) + mUpdatelogText);
            mTvAppAbout.setText(mAboutText);
            calculateShowScrollBar();
            return customDialog;
        }

        private void calculateShowScrollBar() {
            mLinearLyoutContent.measure(0, 0);
            mScrollView.measure(0, 0);
            int contentHeight = mLinearLyoutContent.getMeasuredHeight();
            Log.d(TAG, "calculateShowScrollBar: mLinearLyoutContent : " + contentHeight + "  mScrollView : " + mScrollView.getMeasuredHeight());
            int srollViewHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_500px);
            if (contentHeight > srollViewHeight) {
                mScrollView.setVerticalScrollBarEnabled(true);
            }
        }
    }
}
