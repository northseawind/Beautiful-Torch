package com.sickoorange.superflashlight;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;



import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_content)
    RelativeLayout main_content;

  /*  @BindView(R.id.bg_view)
    ImageView bg_view;*/

    @BindView(R.id.switch_button)
    ImageButton switch_button;
    @BindView(R.id.stroboscope_button)
    ImageButton stroboscope_button;
    @BindView(R.id.screen_bright_button)
    ImageButton screen_bright_button;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    private boolean flashStatus;
    private MediaPlayer player;
    private CameraHandler cameraHandler;


    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CAMERA_PERMISSION = 0;
    private static final int MAX_STROBO_DELAY = 2000;
    private static final int MIN_STROBO_DELAY = 30;
    private int cameraPermission;
    private boolean isStorboScopeModeOn;


    // This method will be called when a stateChanged MessageEvent is posted
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateChanged(MessageEvent.stateChanged event) {
        flashStatus = event.getStatus();
        if (flashStatus) {
            switch_button.setImageResource(R.mipmap.ic_sentiment_very_satisfied_white_48dp);
        } else {
            switch_button.setImageResource(R.mipmap.ic_power_settings_new_white_48dp);
        }

    }

    // This method will be called when a cameraError MessageEvent is posted
    @Subscribe
    public void onCameraError(MessageEvent.cameraError event) {
        Toast.makeText(getApplicationContext(), "Error\n" + event.getErrorMessage(), Toast.LENGTH_LONG).show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStorboScopeChanged(MessageEvent.storboScopeChanged event) {
        isStorboScopeModeOn = event.getStatus();
        System.out.println("闪光灯标志位:" + isStorboScopeModeOn);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
     // Glide.with(this).load(R.drawable.bg_1).fitCenter().dontAnimate().into(bg_view);
        isFlashAvailable();
        // initGoogleAd();

          setStatusBar();
        flashStatus = false;
        main_content.setBackground(new BitmapDrawable(BitmapCompressTools.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_1, 1080, 1920)));
       // main_content.setBackgroundResource(R.drawable.bg_1);
        seekBar.setVisibility(View.INVISIBLE);
        seekBar.setMax(MAX_STROBO_DELAY - MIN_STROBO_DELAY);
        seekBar.setProgress(seekBar.getMax() / 2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int frequency = seekBar.getMax() - progress + MIN_STROBO_DELAY;
                if (cameraHandler != null) {
                    cameraHandler.startStroboScope(frequency);
                }

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

    private void setStatusBar() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

    }

 /*   @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
*/

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

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
            } else {
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
        if (!flashStatus) {
            cameraHandler.openFlashLed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregister EventBus
        EventBus.getDefault().unregister(this);
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
            turnOff();
            cameraHandler.stopStroboScope();
            seekBar.setVisibility(View.INVISIBLE);
            stroboscope_button.setImageResource(R.mipmap.ic_lightbulb_outline_white_48dp);
            playSound();
            flashStatus = false;

        } else {
            turnOn();
            playSound();
            flashStatus = true;

        }
    }

    @OnClick(R.id.stroboscope_button)
    void stroboscope_button() {
        if (isStorboScopeModeOn) {
            System.out.println("关闭闪光灯模式");
            Toast.makeText(getApplicationContext(), R.string.stroboscopeOff, Toast.LENGTH_SHORT).show();
            cameraHandler.stopStroboScope();
            seekBar.setVisibility(View.INVISIBLE);
            switch_button.setImageResource(R.mipmap.ic_power_settings_new_white_48dp);
            stroboscope_button.setImageResource(R.mipmap.ic_lightbulb_outline_white_48dp);

        } else {
            System.out.println("开启闪光灯模式");
            Toast.makeText(getApplicationContext(), R.string.stroboscopeOn, Toast.LENGTH_SHORT).show();
            final int frequency = seekBar.getMax() - seekBar.getProgress() + MIN_STROBO_DELAY;
            cameraHandler.startStroboScope(frequency);
            seekBar.setVisibility(View.VISIBLE);
            switch_button.setImageResource(R.mipmap.ic_sentiment_very_satisfied_white_48dp);

            stroboscope_button.setImageResource(R.mipmap.ic_lightbulb_outline_white_48dp_selected);
        }
    }

    @OnClick(R.id.screen_bright_button)
    void screen_bright_button() {
        Intent intent = new Intent(MainActivity.this, BrightActivity.class);
        startActivity(intent);
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


    private void turnOff() {
        cameraHandler.closeFlashLed();
    }


    private void turnOn() {
        cameraHandler.openFlashLed();
    }

}
