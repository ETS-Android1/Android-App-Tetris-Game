package com.gtari.deltatechenologie.tetromino;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;


public class LoadingGame extends AppCompatActivity {

    private ProgressBar mProgress;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full Screen View
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.loading_game_layout);

        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
                LoadingGame.this.finish();
            }
        }).start();
    }


    private void doWork() {
        for (int progress=0; progress<100; progress+=2) {
            try {
                Thread.sleep(35);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startApp() {
        Intent mainIntent = new Intent(LoadingGame.this,MainActivity.class);
        LoadingGame.this.startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
        Runtime.getRuntime().gc();
    }

}
