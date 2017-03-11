package com.sickoorange.superflashlight;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_content)
    RelativeLayout main_content;

    @BindView(R.id.more)
    ImageButton more;


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
    private TopRightMenu mTopRightMenu;


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
         initGoogleAd();
        setStatusBar();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            String[] PermissionString = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, PermissionString, REQUEST_CAMERA_PERMISSION);
        }
        flashStatus = false;
        main_content.setBackground(new BitmapDrawable(getResources(),BitmapCompressTools.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_1, 720, 1280)));
       // main_content.setBackgroundResource(R.drawable.bg_1);
        
        //初始化可移动的CardView
       /* more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

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


    }


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

    /*    if (cameraHandler != null) {

            if (!flashStatus) {
                cameraHandler.openFlashLed();
            }
        }*/

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

    @OnClick(R.id.more)
    void more(){
        mTopRightMenu = new TopRightMenu(MainActivity.this);

        //添加菜单项
        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(R.drawable.ic_stars_black_36px, getApplicationContext().getString(R.string.rate_us)));
         //menuItems.add(new MenuItem(R.drawable.ic_shopping_cart_black_36px, getApplicationContext().getString(R.string.purchase_us)));
         // menuItems.add(new MenuItem(R.drawable.ic_loyalty_black_36px, getApplicationContext().getString(R.string.magic_skin)));
        menuItems.add(new MenuItem(R.drawable.ic_share_black_36px, getApplicationContext().getString(R.string.invite)));
       // menuItems.add(new MenuItem(R.drawable.ic_shopping_cart_black_36px, getApplicationContext().getString(R.string.purchase_us)));
        menuItems.add(new MenuItem(R.drawable.ic_account_circle_black_36px, getApplicationContext().getString(R.string.setting)));
        mTopRightMenu
                .setHeight(720)     //默认高度480
                .setWidth(720)      //默认宽度wrap_content
                .showIcon(true)     //显示菜单图标，默认为true
                .dimBackground(true)        //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                        switch(position){
                            case 0:
                                final Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                } catch (ActivityNotFoundException ignored) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getStoreUrl())));
                                }
                                break;
                            case 1:
                                final Intent intent = new Intent();
                                final String text = String.format(getString(R.string.share_text), getString(R.string.app_name), getStoreUrl());
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                intent.putExtra(Intent.EXTRA_TEXT, text);
                                intent.setType("text/plain");
                                startActivity(Intent.createChooser(intent, getString(R.string.invite_via)));
                                break;
                         /*   case 2:
                                final Uri uri2 = Uri.parse("market://details?id=" + "com.sickoorange.prettylightpro");
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, uri2));
                                } catch (ActivityNotFoundException ignored) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getStoreUrl())));
                                }
                                break;*/

                            case 2:
                                startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
                                break;
                        }
                    }
                })
                .showAsDropDown(more, -225, 0);  //带偏移量
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
            dialog.setTitle(getApplication().getString(R.string.error));
            dialog.setMessage(getApplication().getString(R.string.error_info));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getApplication().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
    private String getStoreUrl() {
        return "https://play.google.com/store/apps/details?id=" + getPackageName();
    }

}
