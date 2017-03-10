package com.sickoorange.superflashlight;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.header_layout)
    LinearLayout headerView;

    @BindView(R.id.header_layout_setting)
    TextView header_layout_setting;

    @BindView(R.id.image_bg)
    ImageView imageView;

    @BindView(R.id.about_email)
    TextView mEmailTV;

    private boolean isload = false;
    private Resources mRes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setStatusBar();
        mRes=getResources();
        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!isload) {
            Glide.with(this).load(R.drawable.header_image).diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop().into(imageView);
            isload = true;
        }
    }

    private void initView() {
        setSupportActionBar(toolbar);

        header_layout_setting = (TextView) findViewById(R.id.header_layout_setting);
        header_layout_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/10
            }
        });
        setupTitle();
        setupEmail();
    }

    private void setupEmail() {
        final String email = mRes.getString(R.string.email);
        final String appName = mRes.getString(R.string.app_name);
        final String href = "<a href=\"mailto:" + email + "?subject=" + appName + "\">" + email + "</a>";
        mEmailTV.setText(Html.fromHtml(href));
        mEmailTV.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    private void setupTitle() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getHeight() / 2) {
                    collapsingToolbarLayout.setTitle(getApplication().getString(R.string.welcome));

                    // TODO: 2017/3/10 背景视差动画
                    headerView.setVisibility(View.INVISIBLE);
                } else {
                    //collapsingToolbarLayout.setTitle("nothing ");
                    headerView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
    }

    @OnClick(R.id.about_invite)
    public void inviteFriend(){

        final Intent intent = new Intent();
        final String text = String.format(getString(R.string.share_text), getString(R.string.app_name), getStoreUrl());
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.invite_via)));

    }

    @OnClick(R.id.about_rate_us)
    public void rateUsClicked() {
        final Uri uri = Uri.parse("market://details?id=" + getPackageName());
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ignored) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getStoreUrl())));
        }
    }

    @OnClick(R.id.about_pauchase_us)
    void pauchaseUsClick(){
        // TODO: 2017/3/10 pauchase us via play store
    }

    private String getStoreUrl() {
        return "https://play.google.com/store/apps/details?id=" + getPackageName();
    }

}
