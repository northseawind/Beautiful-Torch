package com.sickoorange.superflashlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
  @BindView(R.id.switch_button)
    ImageButton switch_button;
    private boolean flashStatus;
    private CameraManager mCameraManage;
    private String mCameraId;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.

        ButterKnife.bind(this);
        isFlashAvailable();
        initGoogleAd();
        flashStatus = false;
        mCameraManage = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = mCameraManage.getCameraIdList();
            mCameraId = cameraIdList[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initGoogleAd() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @OnClick(R.id.switch_button)
    void switch_button(){
        if (flashStatus) {
            turnOff();
            playSound();
            flashStatus = false;
            switch_button.setImageResource(R.drawable.off);
        }else {
            turnOn();
            playSound();
            flashStatus=true;
            switch_button.setImageResource(R.drawable.on);
        }
    }


    private void playSound() {
        player = MediaPlayer.create(MainActivity.this, R.raw.flash_sound);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("play finish");
                mp.release();
            }
        });
        player.start();

    }

    private void isFlashAvailable() {
        boolean isAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isAvailable) {
            //doesn't support flash
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Error!");
            dialog.setMessage("This device doesn't support flash light!");
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                  //  System.exit(0);
                }
            });
            dialog.show();
        }
    }


    private void turnOff() {
        try {
            mCameraManage.setTorchMode(mCameraId,false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void turnOn() {
        try {
            mCameraManage.setTorchMode(mCameraId,true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
