package com.coderstory.FTool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.R;

public class SplashActivity extends Activity {

    private static final int SHOW_TIME_MIN = 1600;
    private Splashhandler splashhandler;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && splashhandler != null) {
            handler.removeCallbacks(splashhandler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null && splashhandler != null) {
            handler.postDelayed(splashhandler, SHOW_TIME_MIN);
        }
    }

    private void init() {
        TextView version = findViewById(R.id.version_name);
        version.setText(BuildConfig.VERSION_NAME);
        showSplash();
    }


    public void showSplash() {
        handler = new Handler();
        splashhandler = new Splashhandler();

    }

    class Splashhandler implements Runnable {
        public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
