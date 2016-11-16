package com.can.appstore.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.can.appstore.R;
import com.can.appstore.message.manager.MessageManager;

/**
 * 主页
 * Created by HEKANG on 2016/10/18.
 */
public class MessageHomeActivity extends Activity implements View.OnClickListener {

    private ImageView   btnMsg ;
    private ImageView   dotMsg ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_home);
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        initData();
        super.onRestart();
    }

    private void initView(){
        dotMsg = (ImageView) findViewById(R.id.iv_dot_msg);
        btnMsg = (ImageView)findViewById(R.id.btn_iv_msg);
        btnMsg.setOnClickListener(this);
    }

    private void initData(){
        if (!MessageManager.existUnreadMsg()){
            dotMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch ( view.getId()){
            case  R.id.btn_iv_msg :
                Intent intent = new Intent(this , MessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
