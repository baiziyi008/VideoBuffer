package com.megvii.videobuffer;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.megvii.videobuffer.utils.CameraInterface;
import com.megvii.videobuffer.utils.DisplayUtil;

import java.util.concurrent.ArrayBlockingQueue;

public class VideoActivity extends AppCompatActivity implements Camera.PreviewCallback, CameraInterface.CamOpenOverCallback {
    private static final String TAG = "VideoActivity";
    private CameraGLsurfaceView mGLsurfaceView;

    private Button start, stop;
    private boolean isRecording = false;

    int width = 640;//1280;

    int height = 480;//720;

    int framerate = 30;

    int biterate = 8500*1000;
    private static int yuvqueuesize = 10;

    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);
    private AvcEncoder avcCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
    }

    private void init() {
        start = findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始录制
                isRecording = true;
            }
        });
        stop = findViewById(R.id.btn_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //结束录制
                isRecording = false;
                avcCodec.StopThread();
            }
        });
        mGLsurfaceView = findViewById(R.id.glsurfaceview);
        ViewGroup.LayoutParams layoutParams = mGLsurfaceView.getLayoutParams();
        Point screenMetrics = DisplayUtil.getScreenMetrics(this);
        float radioCamera = 640.0f / 480.0f;
        layoutParams.width = screenMetrics.x;
        layoutParams.height = (int) (screenMetrics.x * radioCamera);
        mGLsurfaceView.setLayoutParams(layoutParams);

        CameraInterface.getInstance().setPreviewCallback(this);
        CameraInterface.getInstance().setOpenOverCallback(this);
    }

    private int frameCount = 0;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isRecording) {
            Log.d(TAG, "onPreviewFrame: " + (++frameCount));
            putYUVData(data, data.length);
        }
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
        Log.d(TAG, "cameraHasOpened: ");
//        CameraInterface.getInstance().doStartPreview(mGLsurfaceView.getSurfaceTexture(), 1.33f);
        avcCodec = new AvcEncoder(width,height,framerate,biterate);
        avcCodec.StartEncoderThread();
    }

    private void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }
}
