package com.sickoorange.superflashlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

/**
 * Created by SickoOrange
 * on 2017/3/7
 * Email: heylbly@gmail.com
 */

public class MarshMallowCameraHandler {
    private Context mContext;
    private CameraManager mCameraManagement;
    private String cameraId;
    private static MarshMallowCameraHandler mCameraHandler;

    public static MarshMallowCameraHandler getInstance(Context context) {
        if (mCameraHandler == null) {
            mCameraHandler=new MarshMallowCameraHandler(context);
            return mCameraHandler;
        }
        return mCameraHandler;
    }

    private MarshMallowCameraHandler(Context context) {
        mContext=context;
        setupCamera();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupCamera() {
        mCameraManagement = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = mCameraManagement.getCameraIdList();
            cameraId = cameraIdList[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void openFlashLed() {
        try {
            mCameraManagement.setTorchMode(cameraId,true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void closeFlashLed() {
        try {
            mCameraManagement.setTorchMode(cameraId,false);
            // TODO: 2017/3/8 利用EventsBus 通知Main Thread 更新ImageButton的UI
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
