package com.sickoorange.superflashlight;

import android.content.Context;
import android.os.Build;

import org.greenrobot.eventbus.EventBus;

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
    private MyRunnable runnable;
    private boolean isThreadRun;


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
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;
    }

    void openFlashLed() {
        if (isUponMarshMallow) {
            marshMallowCameraHandler.openFlashLed();
        } else {
            generalCameraHandler.openFlashLed();
        }
        //通知Main UI 更新ImageButton
        EventBus.getDefault().post(new MessageEvent.stateChanged(true));
    }

    public void releaseCamera() {
        if (isUponMarshMallow) {
            //MarshMallow 不需要释放资源
            return;
        } else {
            generalCameraHandler.releaseCamera();
        }

    }

    public void closeFlashLed() {
        if (isUponMarshMallow) {
            marshMallowCameraHandler.closeFlashLed();
        } else {
            generalCameraHandler.closeFlashLed();
        }
        EventBus.getDefault().post(new MessageEvent.stateChanged(false));

    }


    public void startStroboScope(int progress) {

        if (!isThreadRun) {
            runnable = MyRunnable.getInstance(marshMallowCameraHandler, generalCameraHandler);
            new Thread(runnable).start();
            isThreadRun=true;
        }
        runnable.setMarshMallow(isUponMarshMallow);
        runnable.setStroboFrequency(progress);
        runnable.shouldStopStroboScope(false);
        EventBus.getDefault().post(new MessageEvent.storboScopeChanged(true));

    }


    public void stopStroboScope() {
        if (runnable != null) {
            runnable.shouldStopStroboScope(true);
            EventBus.getDefault().post(new MessageEvent.storboScopeChanged(false));
        }

    }
}
