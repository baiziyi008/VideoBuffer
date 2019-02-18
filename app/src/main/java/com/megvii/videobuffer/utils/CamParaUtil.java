package com.megvii.videobuffer.utils;

import android.hardware.Camera;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mfx on 2018/7/14.
 */

public class CamParaUtil {
    private static final String TAG = "CamParaUtil";
    private static CamParaUtil INSTANCE = null;
    private CameraSizeComparator mSizeComparator = new CameraSizeComparator();

    private CamParaUtil() {
    }

    public static CamParaUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CamParaUtil();
        }
        return INSTANCE;
    }

    public Camera.Size getProPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, mSizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if (s.width > minWidth && equalRate(s, th)) {
                Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
        }
        i++;
        if (i == list.size()) {
            i = 0;
        }
        return list.get(0);
    }

    public Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, mSizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.width >= minWidth) && equalRate(s, th)){
                Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }


    public boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public class CameraSizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            if (o1.width == o2.width) {
                return 0;

            } else if (o1.width > o2.width) {
                return 1;
            } else if (o1.width < o2.width) {
                return -1;
            }
            return 0;
        }
    }
    /**打印支持的previewSizes
     * @param params
     */
    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
        }

    }

    /**打印支持的pictureSizes
     * @param params
     */
    public  void printSupportPictureSize(Camera.Parameters params){
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
