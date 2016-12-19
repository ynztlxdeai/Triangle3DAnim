package com.luoxiang.triangle3danim;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.luoxiang.triangle3danim.widget.TriangleSurfaceView;

public class MainActivity
        extends AppCompatActivity
{

    private TriangleSurfaceView mTriangleSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mTriangleSurfaceView = new TriangleSurfaceView(this);
        /**
         * 设置获取焦点
         * 设置接收触摸事件
         */
        mTriangleSurfaceView.requestFocus();
        mTriangleSurfaceView.setFocusableInTouchMode(true);
        setContentView(mTriangleSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTriangleSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTriangleSurfaceView.onPause();
    }
}
