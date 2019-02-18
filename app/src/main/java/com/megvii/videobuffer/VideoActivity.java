package com.megvii.videobuffer;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.megvii.videobuffer.utils.CameraInterface;
import com.megvii.videobuffer.utils.DisplayUtil;

public class VideoActivity extends AppCompatActivity implements Camera.PreviewCallback, CameraInterface.CamOpenOverCallback {
    private static final String TAG = "VideoActivity";
    private CameraGLsurfaceView mGLsurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
    }

    private void init() {
        mGLsurfaceView = findViewById(R.id.glsurfaceview);
        ViewGroup.LayoutParams layoutParams = mGLsurfaceView.getLayoutParams();
        Point screenMetrics = DisplayUtil.getScreenMetrics(this);
        float radioCamera = 640.0f / 480.0f;
        layoutParams.width = screenMetrics.x;
        layoutParams.height = (int) (screenMetrics.x * radioCamera);
        mGLsurfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLsurfaceView.bringToFront();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLsurfaceView.onPause();
    }

    @Override
    public void cameraHasOpened() {
//        CameraInterface.getInstance().doStartPreview(mGLsurfaceView.getSurfaceTexture(), 1.33f);
    }
}
