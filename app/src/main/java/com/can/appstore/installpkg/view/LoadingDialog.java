package com.can.appstore.installpkg.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;


public class LoadingDialog {

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        ImageView img = (ImageView) view.findViewById(R.id.iv_loading_image);
        TextView tipText = (TextView) view.findViewById(R.id.tv_loading_text);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_app);
        img.startAnimation(animation);

        tipText.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.LoadingProgressDialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }
}
