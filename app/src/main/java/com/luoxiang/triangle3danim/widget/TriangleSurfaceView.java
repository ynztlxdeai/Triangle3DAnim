package com.luoxiang.triangle3danim.widget;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * projectName: 	    Triangle3DAnim
 * packageName:	        com.luoxiang.triangle3danim
 * className:	        TriangleSurfaceView
 * author:	            Luoxiang
 * time:	            2016/12/16	11:36
 * desc:	            用于显示的surfaceview
 *
 * svnVersion:	        $Rev
 * upDateAuthor:	    Vincent
 * upDate:	            2016/12/16
 * upDateDesc:	        TODO
 */
public class TriangleSurfaceView
        extends GLSurfaceView {
    //三角形一次旋转的角度
    final float ANGLE_SPAN = 0.375f;
    RotateThread mRotateThread;
    //渲染器引用
    SceneRenderer mSceneRenderer;


    public TriangleSurfaceView(Context context) {
        this(context , null);
    }

    public TriangleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setEGLContextClientVersion(2);
        mSceneRenderer = new SceneRenderer();
        //设置渲染器
        setRenderer(mSceneRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    /**
     * 渲染器
     */
    private class SceneRenderer implements GLSurfaceView.Renderer{
        Triangle mTriangle;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置背景颜色
            GLES20.glClearColor( 0 , 0 , 0 , 1.0f);
            //创建Triangle对象
            mTriangle = new Triangle(TriangleSurfaceView.this);
            //深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mRotateThread = new RotateThread();
            mRotateThread.start();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗
            GLES20.glViewport(0 , 0 , width , height);
            //计算屏幕的宽高比例
            float ratio = (float) width / height;
            //设置透视投影
            Matrix.frustumM(Triangle.mProjMatrix , 0 , -ratio , ratio , -1 , 1 , 1 , 10);
            //设置摄像机
            Matrix.setLookAtM(Triangle.mVMatrix , 0 , 0 , 0 , 3 , 0.0f , 0.0f , 0.0f , 0.0f , 1.0f , 0.0f );
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            /**
             * 清除深度和颜色缓存
             */
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //绘制三角形
            mTriangle.drawSelf();
        }
    }

    private class RotateThread extends Thread{
        public boolean mFlag = true;

        @Override
        public void run() {
            super.run();
            while (mFlag){
                /**
                 * 改变角度 以达到旋转
                 */
                mSceneRenderer.mTriangle.mXAngle = mSceneRenderer.mTriangle.mXAngle + ANGLE_SPAN;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
