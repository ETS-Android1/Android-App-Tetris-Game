package com.gtari.deltatechenologie.tetromino;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class GameMenuActivity extends AppCompatActivity {
    private MediaPlayer btSound;
    private MediaPlayer backgrounfMusic;
    private Button sound;
    private Button music;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Full Screen View
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.game_menu_layout);

        //Set Aniamtion
        Animation animPlay = AnimationUtils.loadAnimation(this,R.anim.start_anim_bt);
        Button startGame =(Button) findViewById(R.id.idStartGame);
        startGame.startAnimation(animPlay);

        // Set Sound Effect
        btSound= MediaPlayer.create(this,R.raw.pause_option_sound);
        btSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));

        sound =(Button) findViewById(R.id.idSoundBt);
        if(Float.parseFloat(getSoundVolumeDB())==0f) sound.setBackgroundResource(R.drawable.sound_off_bt);
        else sound.setBackgroundResource(R.drawable.sound_bt);

        //Background Music
        backgrounfMusic = MediaPlayer.create(this,R.raw.bck_house_party);
        backgrounfMusic.setLooping(true);
        backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
        //backgrounfMusic.setVolume(0.2f,0.2f);
        backgrounfMusic.start();

        music =(Button) findViewById(R.id.idMusicBt);
        if(Float.parseFloat(getMusicVolumeDB())==0f) music.setBackgroundResource(R.drawable.music_bt);
        else music.setBackgroundResource(R.drawable.music_on_bt);


        // Reintialise InterstitialAd Full screen Ad
        MobileAds.initialize(this, "ca-app-pub-4168864559615120~7655846864");
        adRequest = new AdRequest.Builder().build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4168864559615120/7372227099");
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
        mInterstitialAd.loadAd(adRequest);
            }
        });

        ImageView launchLogoImage=(ImageView)findViewById(R.id.idImageLogo);
        Animation animImageLogo = AnimationUtils.loadAnimation(this,R.anim.launch_tetris_logo_anim);
        launchLogoImage.startAnimation(animImageLogo);

    }

    public void buPlay(View view) {
        btSound.start();
        Intent intent=new Intent(GameMenuActivity.this,LoadingGame.class);
        startActivity(intent);
        GameMenuActivity.this.finish();
    }

    public void buQuitGame(View view) {
        btSound.start();
        Exit();
    }

    public void buSoundOnOff(View view) {
        //SoundAndMusicVolume soundAndMusicVolume=new SoundAndMusicVolume();
        Button sound =(Button) findViewById(R.id.idSoundBt);

        if(Float.parseFloat(getSoundVolumeDB())==0f){
              setSoundVolumeDB("1");
        sound.setBackgroundResource(R.drawable.sound_bt);
        }
        else {
            setSoundVolumeDB("0");
            sound.setBackgroundResource(R.drawable.sound_off_bt);
        }
        btSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        btSound.start();
    }

    public void buMusicOnOff(View view) {
        //SoundAndMusicVolume soundAndMusicVolume=new SoundAndMusicVolume();
        Button sound =(Button) findViewById(R.id.idMusicBt);

        if(Float.parseFloat(getMusicVolumeDB())==0f){
            setMusicVolumeDB("1");
            sound.setBackgroundResource(R.drawable.music_on_bt);
        }
        else {
            setMusicVolumeDB("0");
            sound.setBackgroundResource(R.drawable.music_bt);
        }

        backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
       // btSound.start();

    }

    public void buRate(View view) {
        btSound.start();
        Uri marketUri = Uri.parse("market://details?id="+getPackageAppName());
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(marketIntent);
    }

    public void buShare(View view) {
        btSound.start();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Tetromino Game ROYALE is awesome game. Recall your childhood! I liked it too. " +
                "Believe me this is awesome game! Download it for FREE on Play Store from here: \n" +
                "https://play.google.com/store/apps/details?id="+getPackageAppName());
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    public void buRecod(View view) {
        btSound.start();
        ShowHighScore();
    }


    // Dialog Game Over
    private void Exit(){

        //Launch Full Screen Ad
        mInterstitialAd.loadAd(adRequest);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mInterstitialAd.show();
            }
        },400); // Millisecond 1000 = 1 sec


        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.exit_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        // Exit Game
        dialog.findViewById(R.id.idExit).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                btSound.start();
                finish();
            }

        });

        //Stay in The Game Menu
        dialog.findViewById(R.id.idCancelExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btSound.start();
                dialog.cancel();
            }
        });
    }

    // High Score Dialg
    private void ShowHighScore(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.show_high_score);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView highScore=(TextView) dialog.findViewById(R.id.idShowHighScoreText);
        highScore.setText(getScoreDB());
        dialog.show();


        //Stay in The Game Menu
        dialog.findViewById(R.id.idOkHighScore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btSound.start();
                dialog.cancel();
            }
        });
    }


    // Load Score
    private String getScoreDB(){
        SharedPreferences perfers=getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("High_Score")) {
            return "0";
        }
        return perfers.getString("High_Score","0");
    }


    @Override
    protected void onDestroy() {
        backgrounfMusic.stop();
        backgrounfMusic.release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        backgrounfMusic.pause();
        super.onPause();
    }


    @Override
    public void finish() {
        super.finish();
        Runtime.getRuntime().gc();
    }

/*    @Override
    public void finish() {
        backgrounfMusic.stop();
        backgrounfMusic.release();
        super.finish();
    }
*/
    @Override
    protected void onResume() {
        backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
        if(Float.parseFloat(getMusicVolumeDB())==0f) music.setBackgroundResource(R.drawable.music_bt);
        else music.setBackgroundResource(R.drawable.music_on_bt);
        backgrounfMusic.start();

        btSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        if(Float.parseFloat(getSoundVolumeDB())==0f) sound.setBackgroundResource(R.drawable.sound_off_bt);
        else sound.setBackgroundResource(R.drawable.sound_bt);

        super.onResume();
    }


    // Back Button Event
    @Override
    public void onBackPressed() {
        Exit();
    }


    // Set and Get From DataBase

    //set Sound Volume in XML file
    private void setSoundVolumeDB(String volume){
        // /Save Date For Next Time
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("MuteSound",volume);
        editor.apply();
    }

    // Load Sound Volume
    public native String getPackageAppName();
    private String getSoundVolumeDB(){
        SharedPreferences perfers=getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("MuteSound")) {
            return "1";
        }
        return perfers.getString("MuteSound","1");
    }
    static {
        System.loadLibrary("native-lib");
    }

    //set Music Volume in XML file
    private void setMusicVolumeDB(String volume){
        // /Save Date For Next Time
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("MuteMusic",volume);
        editor.apply();
    }

    // Load Music Volume
    private String getMusicVolumeDB(){
        SharedPreferences perfers=getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("MuteMusic")) {
            return "1";
        }
        return perfers.getString("MuteMusic","1");
    }

    private void privacyPolcyDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Privacy Policy");

        WebView wv = new WebView(this);
        wv.loadUrl("file:///android_asset/privacy_policy.html");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void buPivacyPolicy(View view) {
        privacyPolcyDialog();
    }
}
