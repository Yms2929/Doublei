package com.example.doublei.Etc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.doublei.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#074E72"))
                .withLogo(R.drawable.ic_eye)
                .withHeaderText("")
                .withFooterText("Copyright 2017")
                .withBeforeLogoText("")
                .withAfterLogoText("더블아이");

        //Set Text Color
        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);

        //Set tto view
        View view = config.create();

        //Set view to content view
        setContentView(view);
    }
}