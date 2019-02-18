package com.megvii.videobuffer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.megvii.videobuffer.utils.CameraInterface;
import com.megvii.videobuffer.utils.CameraMatrix;
import com.megvii.videobuffer.utils.DirectDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraGLsurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener, GLSurfaceView.Renderer {
    private static final String TAG = "CameraGLsurfaceView";
    private int mTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private DirectDrawer mDirectDrawer;
    private CameraMatrix mCameraMatrix;
    private final float[] mMvpMatrix = new float[16];

    public CameraGLsurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
//        Matrix.setIdentityM(mMvpMatrix, 0);
//        Matrix.rotateM(mMvpMatrix,0,270,0,0,1);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable: ");
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG, "onSurfaceCreated: ");
        mTextureId = createTextureId();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
//        mDirectDrawer = new DirectDrawer(mTextureId);
        //此处使用了CameraMatrix，抛弃了DirectDrawer，因为DirectDrawer没有进行旋转操作，导致预览图像倒立，
        // 具体原理需要openGL知识
        mCameraMatrix = new CameraMatrix(mTextureId);
        CameraInterface.getInstance().doOpenCamera(null);
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: ");
        GLES20.glViewport(0, 0, width, height);
        if (!CameraInterface.getInstance().isPreviewing()){
            CameraInterface.getInstance().doStartPreview(mSurfaceTexture, 1.33f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d(TAG, "onDrawFrame: ");
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
//        mDirectDrawer.draw(mtx);
        mCameraMatrix.draw(mtx);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }
    
    private int createTextureId() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
}
