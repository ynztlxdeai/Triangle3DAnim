package com.luoxiang.triangle3danim.widget;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.luoxiang.triangle3danim.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * projectName: 	    Triangle3DAnim
 * packageName:	        com.luoxiang.triangle3danim.widget
 * className:	        Triangle
 * author:	            Luoxiang
 * time:	            2016/12/16	11:40
 * desc:	            绘制三角形的类
 *
 * svnVersion:	        $Rev
 * upDateAuthor:	    Vincent
 * upDate:	            2016/12/16
 * upDateDesc:	        TODO
 */

public class Triangle {
    //4X4的投影矩阵
    public static float[] mProjMatrix = new float[16];
    //摄像机位置朝向的参数矩阵
    public static float[] mVMatrix    = new float[16];
    //总变换矩阵
    public static float[] mMVPMatrix;
    //自定义渲染管线着色器程序ID
    int mProgram;
    //总变换矩阵引用
    int muMVPMatrixHandle;
    //顶点位置属性引用
    int maPositionHandle;
    //顶点颜色属性引用
    int maColorHandle;
    //顶点着色器代码
    String mVertexShader;
    //片元着色器代码
    String mFragmentShader;
    //具体物体的3D变换矩阵,包括旋转 平移 缩放
    static float[] mMMatrix = new float[16];
    //顶点坐标数据缓冲
    FloatBuffer mVertexBuffer;
    //顶点着色数据缓冲
    FloatBuffer mColorBuffer;
    //顶点数量
    int mVCount;
    //绕X轴旋转角度
    float mXAngle;

    public Triangle(TriangleSurfaceView triangleSurfaceView){
        //初始化顶点数据
        initVertexData();
        //初始化着色器
        initShader(triangleSurfaceView);
    }

    /**
     * 初始化着色器
     * @param triangleSurfaceView 显示用
     */
    private void initShader(TriangleSurfaceView triangleSurfaceView) {
        //加载顶点着色器
        mVertexShader = ShaderUtil.loadFromAeertsFile("vertex.sh" , triangleSurfaceView.getResources());
        //加载片元着色器
        mFragmentShader = ShaderUtil.loadFromAeertsFile("frag.sh" , triangleSurfaceView.getResources());
        //创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader , mFragmentShader);
        //顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram , "aPosition");
        //顶点颜色属性引用
        maColorHandle = GLES20.glGetAttribLocation(mProgram , "aColor");
        //总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram , "uMVPMatrix");
    }

    /**
     * 初始化顶点数据
     */
    private void initVertexData() {
        //设置顶点的数量
        mVCount = 3;
        //单位长度
        final float UNIT_SIZE = 0.2f;
        //顶点坐标数组
        float vertices[] = new float[]{
                -4*UNIT_SIZE , 0 , 0 ,
                0 , -4*UNIT_SIZE , 0 ,
                4*UNIT_SIZE , 0 , 0
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        //字节顺序为本地操作系统顺序
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        //写入数据
        mVertexBuffer.put(vertices);
        //设置起始位置
        mVertexBuffer.position(0);

        //颜色数组
        float colors[] = new float[]{
                1 , 1 , 1 , 0 ,
                0 , 0 , 1 , 0 ,
                0 , 1 , 0 , 0

        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        //写入数据
        mColorBuffer.put(colors);
        //设置起始位置
        mColorBuffer.position(0);
    }

    /**
     * 绘制自己
     */
    public void drawSelf(){
        //给着色器ID指定着色器程序
        GLES20.glUseProgram(mProgram);
        //初始化变换矩阵
        Matrix.setRotateM(mMMatrix , 0 , 0 , 0 , 1 , 0);
        //设置沿z轴正方向位移
        Matrix.translateM(mMMatrix , 0 , 0 , 0 , 1);
        //设置绕X轴旋转
        Matrix.rotateM(mMMatrix , 0 , mXAngle , 1 , 0 , 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle , 1 , false , Triangle.getFinalMatrix(mMMatrix) , 0);
        //把顶点位置传送进渲染管线
        GLES20.glVertexAttribPointer(maPositionHandle , 3 , GLES20.GL_FLOAT , false , 3 * 4 , mVertexBuffer);
        //把颜色数据传送进渲染管线
        GLES20.glVertexAttribPointer(maColorHandle , 4 , GLES20.GL_FLOAT , false , 4 * 4 , mColorBuffer);
        //启用顶点位置数据和颜色数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);

        //开始绘制,绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES , 0 , mVCount);

    }

    /**
     * 产生最终变换矩阵的方法
     * @param spec
     * @return
     */
    public static float[] getFinalMatrix(float[] spec){
        //初始化总变换矩阵
        mMVPMatrix = new float[16];
        /**
         * Multiplies two 4x4 matrices together and stores the result in a third 4x4
         * matrix. In matrix notation: result = lhs x rhs. Due to the way
         * matrix multiplication works, the result matrix will have the same
         * effect as first multiplying by the rhs matrix, then multiplying by
         * the lhs matrix. This is the opposite of what you might expect.
         * <p>
         * The same float array may be passed for result, lhs, and/or rhs. However,
         * the result element values are undefined if the result elements overlap
         * either the lhs or rhs elements.
         *
         * @param result The float array that holds the result.
         * @param resultOffset The offset into the result array where the result is
         *        stored.
         * @param lhs The float array that holds the left-hand-side matrix.
         * @param lhsOffset The offset into the lhs array where the lhs is stored
         * @param rhs The float array that holds the right-hand-side matrix.
         * @param rhsOffset The offset into the rhs array where the rhs is stored.
         *
         * @throws IllegalArgumentException if result, lhs, or rhs are null, or if
         * resultOffset + 16 > result.length or lhsOffset + 16 > lhs.length or
         * rhsOffset + 16 > rhs.length.
         */
        Matrix.multiplyMM(mMVPMatrix , 0 , mVMatrix , 0 , spec , 0);
        Matrix.multiplyMM(mMVPMatrix , 0 , mProjMatrix , 0 , mMVPMatrix , 0);
        return mMVPMatrix;
    }
}
