package com.sickoorange.superflashlight;

import android.content.Context;
import android.hardware.Camera;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by SickoOrange
 * on 2017/3/7
 * Email: heylbly@gmail.com
 */

public class GeneralCameraHandler {
    private Context mContext;
    private static GeneralCameraHandler mCameraHandler;
    private Camera camera;
    private Camera.Parameters parameters;

    public static GeneralCameraHandler getInstance(Context context) {
        if (mCameraHandler == null) {
            mCameraHandler = new GeneralCameraHandler(context);
            return mCameraHandler;
        }
        return mCameraHandler;
    }

    private GeneralCameraHandler(Context context) {
        mContext = context;
        setupCamera();

    }

    private void setupCamera() {
        //在Android M上需要获取手动获取权限
        //否则会报错 camera service cant connect
        camera = Camera.open();
        parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    public void openFlashLed() {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        if (camera != null) {
            camera.setParameters(parameters);
            camera.startPreview();
        }


    }

    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void closeFlashLed() {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    public void startStroboScope(int progress) {
    }
}
