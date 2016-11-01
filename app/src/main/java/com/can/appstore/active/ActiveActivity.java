package com.can.appstore.active;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.can.appstore.R;

/**
 * Created by Atangs on 2016/11/1.
 */

public class ActiveActivity extends Activity {
    private WebView mactiveWebview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        initUI();
    }

    private void initUI() {
        mactiveWebview = (WebView) findViewById(R.id.active_webview);
    }
}
