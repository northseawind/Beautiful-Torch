package com.sickoorange.superflashlight;


/**
 * Created by SickoOrange
 * on 2017/3/8
 * Email: heylbly@gmail.com
 */

public class MyRunnable implements Runnable {

    private  MarshMallowCameraHandler handler1;
    private   GeneralCameraHandler handler2;
    private boolean isMarshMallow;
    private int stroboFrequency;
    private static MyRunnable mRunnable;
    private boolean shouldStopStroboScope=true;

    public MyRunnable(MarshMallowCameraHandler handler1, GeneralCameraHandler handler2){

        this.handler1 = handler1;
        this.handler2 = handler2;
    }

    public static MyRunnable getInstance( MarshMallowCameraHandler handler1, GeneralCameraHandler handler2) {
        if (mRunnable == null) {
            mRunnable =new MyRunnable(handler1,handler2);
        }
        return mRunnable;
    }
    @Override
    public void run() {
        while (true) {
            System.out.println("shouldStopStroboScope:"+shouldStopStroboScope);
            if (shouldStopStroboScope) {
                continue;
            }
            if (isMarshMallow) {

                   try {
                        handler1.openFlashLed();
                        Thread.sleep(stroboFrequency);
                        handler1.closeFlashLed();
                        Thread.sleep(stroboFrequency);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

            } else {

                try {
                    handler2.openFlashLed();
                    Thread.sleep(stroboFrequency);
                    handler2.closeFlashLed();
                    Thread.sleep(stroboFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void setStroboFrequency(int stroboFrequency) {
        this.stroboFrequency = stroboFrequency;
    }

    public void setMarshMallow(boolean marshMallow) {
        isMarshMallow = marshMallow;
    }


    public void shouldStopStroboScope(boolean shouldStop) {
        shouldStopStroboScope = shouldStop;
    }
}
