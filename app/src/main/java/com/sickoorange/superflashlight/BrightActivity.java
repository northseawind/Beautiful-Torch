package com.sickoorange.superflashlight;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class BrightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_bright);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        initGoogleAd();
     //   initGoogleAd_2();
    }

    private void initGoogleAd() {
        System.out.println("init google ad");
        AdView mAdView2 = (AdView) findViewById(R.id.adView_2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest);
    }

  /*   private void initGoogleAd_2() {
        AdView mAdView3 = (AdView) findViewById(R.id.adView_3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView3.loadAd(adRequest);
    }*/

    private void setStatusBar() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toggleBrightness(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toggleBrightness(false);
    }

    private void toggleBrightness(boolean increase) {
        final WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = (increase ? 1 : 0);
        getWindow().setAttributes(layout);
    }

}
