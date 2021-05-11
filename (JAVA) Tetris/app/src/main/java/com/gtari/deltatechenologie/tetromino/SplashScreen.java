package com.gtari.deltatechenologie.tetromino;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;


public class SplashScreen extends AppCompatActivity {
    private ProgressBar mProgress;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full Screen View
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.splash_screen_layout);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this,GameMenuActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public void finish() {
        super.finish();
        Runtime.getRuntime().gc();
    }

}
