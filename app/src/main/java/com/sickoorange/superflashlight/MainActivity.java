package com.sickoorange.superflashlight;

import android.*;
import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.Permission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.switch_button)
    ImageButton switch_button;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    private boolean flashStatus;
    private CameraManager mCameraManage;
    private String mCameraId;
    private MediaPlayer player;
    private CameraHandler cameraHandler;

    public static final int REQUEST_CAMERA_PERMISSION = 0;
    private static final int MAX_STROBO_DELAY = 2000;
    private static final int MIN_STROBO_DELAY = 30;
    private int cameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        isFlashAvailable();

        initGoogleAd();

        flashStatus = false;


        seekBar.setMax(MAX_STROBO_DELAY - MIN_STROBO_DELAY);
        seekBar.setProgress(seekBar.getMax() / 2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //// TODO: 2017/3/8
                final int frequency = seekBar.getMax() - progress + MIN_STROBO_DELAY;
                /*if (mCameraImpl != null)
                    mCameraImpl.setStroboFrequency(frequency);*/

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            setupCameraHandler();
        } else {
            String[] PermissionString = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, PermissionString, REQUEST_CAMERA_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("请求权限成功");
                setupCameraHandler();
            }else {
                //// TODO: 2017/3/8 请求权限失败 需要处理的逻辑
            }
        }

    }

    private void setupCameraHandler() {
        if (cameraHandler == null) {
            cameraHandler = new CameraHandler(MainActivity.this);
        }
    }

    //每次进入应用，默认是打开手电筒的
    @Override
    protected void onResume() {
        super.onResume();
        cameraHandler.openFlashLed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraHandler.releaseCamera();
    }

    private void initGoogleAd() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @OnClick(R.id.switch_button)
    void switch_button() {
        if (flashStatus) {
            // turnOff();
            playSound();
            flashStatus = false;
            switch_button.setImageResource(R.drawable.off);
        } else {
            // turnOn();
            playSound();
            flashStatus = true;
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
                    finish();
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }


  /*  private void turnOff() {
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
    }*/

}
