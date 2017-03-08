package com.sickoorange.superflashlight;

import android.content.Context;
import android.os.Build;

/**
 * Created by SickoOrange
 * on 2017/3/8
 * Email: heylbly@gmail.com
 */

class CameraHandler {
    private Context mContext;
    private MarshMallowCameraHandler marshMallowCameraHandler;
    private GeneralCameraHandler generalCameraHandler;

    private boolean isUponMarshMallow;


    CameraHandler(Context context) {
        mContext = context;
        isUponMarshMallow = deriveCurrentSDKVersion();
        if (isUponMarshMallow) {
            marshMallowCameraHandler = MarshMallowCameraHandler.getInstance(context);
        } else {
            generalCameraHandler = GeneralCameraHandler.getInstance(context);
        }
    }

    private boolean deriveCurrentSDKVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    void openFlashLed() {
        if (isUponMarshMallow) {
            marshMallowCameraHandler.openFlashLed();
        } else {
            generalCameraHandler.openFlashLed();
        }
    }

    public void releaseCamera() {
        if (isUponMarshMallow) {
            //MarshMallow 不需要释放资源
            return;
        } else {
            generalCameraHandler.releaseCamera();
        }
    }
}
