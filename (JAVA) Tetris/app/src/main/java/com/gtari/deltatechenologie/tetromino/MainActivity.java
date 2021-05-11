package com.gtari.deltatechenologie.tetromino;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    GameSurface gameSurface;
    Context mContext=this;

    private boolean isPlaying=false;
    private Button buttonPauseResum;
    private Button buttonRoulette;
    //AdMob attributes
    private RewardedVideoAd mRewardedVideoAd;
    private boolean isRewardedVideoComplited=false;
    private int idGift=0;
    private Animation anim;
    private Button playVideo;
    private AdView mAdView;
    //Sounds Attributes
    private InterstitialAd mInterstitialAd;
    private MediaPlayer btRotateMusic;
    private MediaPlayer btCheckCards;
    private MediaPlayer btRotate;
    private MediaPlayer btUpSpeed;
   // private MediaPlayer btLeftRight;
    private MediaPlayer MoreCardsSound;
    private MediaPlayer backgrounfMusic;
    private MediaPlayer jokerDialogSound;
    private MediaPlayer yahooStarSound;
    private MediaPlayer pauseOptionSound;
    private MediaPlayer magicDialogSound;
    private AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.activity_main);

         buttonPauseResum = findViewById(R.id.idPauseResume);
         buttonRoulette= findViewById(R.id.idRoulette);

        MobileAds.initialize(this, "ca-app-pub-4168864559615120~7655846864");
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loadRewardedVideoAd();

        // Reintialise InterstitialAd Full screen Ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4168864559615120/1755818779");
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(adRequest);
            }
        });


        anim = AnimationUtils.loadAnimation(this,R.anim.video_play_anim);
        playVideo =(Button) findViewById(R.id.idStar);


        //Play Music
        btRotateMusic = MediaPlayer.create(this,R.raw.button_b);
        btCheckCards = MediaPlayer.create(this,R.raw.check_card);
        btUpSpeed= MediaPlayer.create(this,R.raw.down_fast);
       // btLeftRight= MediaPlayer.create(this,R.raw.left_right_click);
        MoreCardsSound=MediaPlayer.create(this,R.raw.more_cards_sound);
        jokerDialogSound=MediaPlayer.create(this,R.raw.launch_joker_dialog);
        yahooStarSound=MediaPlayer.create(this,R.raw.yahoo);
        pauseOptionSound=MediaPlayer.create(this,R.raw.pause_option_sound);
        magicDialogSound=MediaPlayer.create(this,R.raw.magic_dialog_launch);
        //Load Last Sound State
        pauseOptionSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        btRotateMusic.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        btCheckCards.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        btUpSpeed.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
        //btLeftRight.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));


// Background music
        //Play Music
        backgrounfMusic = MediaPlayer.create(this,R.raw.bck_house_party);
        backgrounfMusic.setLooping(true);
        backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
        //backgrounfMusic.setVolume(0.2f,0.2f);
        backgrounfMusic.start();

        gameSurface = findViewById(R.id.my_gameSurface);
        gameSurface.CleanGame();

        // Start the Runnable immediately
        handler.post(runnable);

        adaptScreenViewButton();

        // Intialize reward video button
        buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);

        //Remove the handler
        //handler.removeCallbacks(runnable);
    }



    private void adaptScreenViewButton(){
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float density = metrics.density;

        LinearLayout ltPauseOption =(LinearLayout)findViewById(R.id.idLayoutPauseMenu);
        RelativeLayout.LayoutParams layoutParams0 = (RelativeLayout.LayoutParams)ltPauseOption.getLayoutParams();
        //layoutParams0.height = dpToPx(40);
        //layoutParams0.width = dpToPx(40);
        layoutParams0.setMargins(Math.round(5.27f*width/100) ,0 , 0, Math.round(1.23f*(height-74*density)/100));
        ltPauseOption.setLayoutParams(layoutParams0);

        Space YOUR_Space =(Space)findViewById(R.id.idSpacePauseMenu);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams)YOUR_Space.getLayoutParams();
        //layoutParams0.height = dpToPx(40);
        layoutParams2.width = Math.round(1.94f*width/100);
        YOUR_Space.setLayoutParams(layoutParams2);

        Button btMenu = (Button) findViewById(R.id.idMenu);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)btMenu.getLayoutParams();
        layoutParams.height = Math.round(7.06f*(height-74*density)/100);
        layoutParams.width = Math.round(7.06f*(height-74*density)/100); // 40dp
        //layoutParams.setMargins(0, dpToPx(100), 0, 0);
        btMenu.setLayoutParams(layoutParams);

        Button btPauseResume = (Button) findViewById(R.id.idPauseResume);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams)btPauseResume.getLayoutParams();
        layoutParams1.height =Math.round(7.06f*(height-74*density)/100);
        layoutParams1.width = Math.round(7.06f*(height-74*density)/100);
        btPauseResume.setLayoutParams(layoutParams1);

                   // Star button and Layout //
        LinearLayout ltStar =(LinearLayout)findViewById(R.id.idLtStar);
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams)ltStar.getLayoutParams();
        //layoutParams0.height = dpToPx(40);
        //layoutParams0.width = dpToPx(40);
        layoutParams4.setMargins(Math.round(79f*width/100) ,Math.round(55f*(height-74*density)/100) , 0, 0);
        ltStar.setLayoutParams(layoutParams4);


        Button btMagicStar = (Button) findViewById(R.id.idStar);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams)btMagicStar.getLayoutParams();
        layoutParams3.height = Math.round(8.83f*(height-74*density)/100);
        layoutParams3.width = Math.round(15.27f*width/100);
        btMagicStar.setLayoutParams(layoutParams3);


                // Game button //
        Button btRotate = (Button) findViewById(R.id.idRotate);
        RelativeLayout.LayoutParams layoutParams5 = (RelativeLayout.LayoutParams)btRotate.getLayoutParams();
        layoutParams5.height = Math.round(10.6f*(height-74*density)/100);
        layoutParams5.width = Math.round(10.6f*(height-74*density)/100);
        layoutParams5.setMargins(Math.round(3.33f*width/100) ,0 , Math.round(2.77f*width/100), 0);
        btRotate.setLayoutParams(layoutParams5);

        Button btSpeedUp = (Button) findViewById(R.id.idSpeedUp);
        RelativeLayout.LayoutParams layoutParams6 = (RelativeLayout.LayoutParams)btSpeedUp.getLayoutParams();
        layoutParams6.height = Math.round(10.6f*(height-74*density)/100);
        layoutParams6.width = Math.round(10.6f*(height-74*density)/100);
        layoutParams6.setMargins(Math.round(6.66f*width/100) ,0 , 0, 0);
        btSpeedUp.setLayoutParams(layoutParams6);

        Button btLeft = (Button) findViewById(R.id.idLeft);
        RelativeLayout.LayoutParams layoutParams7 = (RelativeLayout.LayoutParams)btLeft.getLayoutParams();
        layoutParams7.height = Math.round(10.6f*(height-74*density)/100);
        layoutParams7.width = Math.round(10.6f*(height-74*density)/100);
        layoutParams7.setMargins(0 ,0 , Math.round(8.33f*width/100), 0);
        btLeft.setLayoutParams(layoutParams7);

        Button btRight = (Button) findViewById(R.id.idRight);
        RelativeLayout.LayoutParams layoutParams8 = (RelativeLayout.LayoutParams)btRight.getLayoutParams();
        layoutParams8.height = Math.round(10.6f*(height-74*density)/100);
        layoutParams8.width = Math.round(10.6f*(height-74*density)/100);
        layoutParams8.setMargins(0 ,0 , 0, Math.round(5.47f*(height-74*density)/100));
        btRight.setLayoutParams(layoutParams8);

        Button btRoulette = (Button) findViewById(R.id.idRoulette);
        RelativeLayout.LayoutParams layoutParams9 = (RelativeLayout.LayoutParams)btRoulette.getLayoutParams();
        layoutParams9.height = Math.round(10.6f*(height-74*density)/100);
        layoutParams9.width = Math.round(10.6f*(height-74*density)/100);
        btRoulette.setTextSize(TypedValue.COMPLEX_UNIT_SP, spToPx(1.94f));
        btRoulette.setLayoutParams(layoutParams9);

    }
    private int dpToPx(int dp) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float density = metrics.density;
        float density1 = metrics.scaledDensity;
        double px;
        double a=-0.0034375, b=0.0075;
        px=dp*(a*height+b*width);

        return Math.round((float) px * density);//*height/100 *
    }

    private int spToPx(float sp) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
     //    int height = metrics.heightPixels;
     //   float scaledDensity = metrics.scaledDensity;
     //    double px;
     //     double a=-0.01107141, b=0.0210714;
     //   px=sp*(a*height+b*width);
        return Math.round(sp* width/100);// Math.round((float) px * scaledDensity);//*height/100 *
    }


    // Create the Handler
    private Handler handler = new Handler();
    private int countTime=50;
    private boolean isStartCounting=false;

    // Define the code block to be executed
    private  boolean isAnimRun=false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
           // Detect Game Over Sate
            if(gameSurface.GameOver()) {
                gameSurface.isGameOver=false;
                //Remove the handler
                  handler.removeCallbacks(runnable);

                if(gameSurface.Score> Integer.parseInt(getScoreDB())){
                    NewRecord(String.valueOf(gameSurface.Score));
                    setScoreDB(String.valueOf(gameSurface.Score));
                }
                else
                    GameOverDialog();

                return;
            }

            // Animation
            if(gameSurface.getMagicHelpSate()) {
                if(!isAnimRun) {
                    playVideo.startAnimation(anim);
                    isAnimRun = true;
                }
            }
            else {
               if(isAnimRun) {
                   playVideo.clearAnimation();
                   isAnimRun=false;
               }
            }

            if(gameSurface.getIsShowRewardVideoBt()) {
                buttonRoulette.setBackgroundResource(R.drawable.bt_joker_rewarded_vd);
                gameSurface.setIsShowRewardVideoBt(false);
            }

            // Repeat every 1 seconds
            handler.postDelayed(runnable, 1000);
        }
    };


    private Handler hndlrCountTime = new Handler();
    private Runnable rnnble = new Runnable() {
        @Override
        public void run() {
            if(isStartCounting)
                //Counter Ticks
                        countTime--;

            // Repeat every 1 seconds
            if(countTime<0)hndlrCountTime.removeCallbacks(rnnble);
            else hndlrCountTime.postDelayed(rnnble, 1000);
        }
    };

    @Override
    protected void onResume() {
        gameSurface.resume();
        gameSurface.isPlaying=true;
        isPlaying=true;
        handler.post(runnable);
        buttonPauseResum.setBackgroundResource(R.drawable.wood_bt_pause);
        if(!isPlaying) isPlaying = true;
        mRewardedVideoAd.resume(this);
        backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
        backgrounfMusic.start();
        super.onResume();
    }


    @Override
    protected void onPause() {
        gameSurface.pause();
        handler.removeCallbacks(runnable);
        mRewardedVideoAd.pause(this);
        backgrounfMusic.pause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        handler.removeCallbacks(runnable);
        backgrounfMusic.stop();
        backgrounfMusic.release();
        super.onDestroy();
    }


    @Override
    public void finish() {
        mRewardedVideoAd.destroy(this);
        gameSurface.pause();
        handler.removeCallbacks(runnable);
        gameSurface.finish();
        super.finish();
        Runtime.getRuntime().gc();
    }


    public void buRotate(View view) {
        if(!isPlaying)return;
        gameSurface.rotate=true;//setRotation(true);
        btRotateMusic.start();}

    public void buTranslateRight(View view) {
        if(!isPlaying)return;
        gameSurface.dx=-1;//setTranslation(-1);
        //btLeftRight.start();
        }

    public void buTranslateLeft(View view) {
        if(!isPlaying)return;
        gameSurface.dx=1;//setTranslation(1);
        //btLeftRight.start();
         }

    public void buSpeedUp(View view) {
        if(!isPlaying)return;
        gameSurface.setSpeed(0);
        btUpSpeed.start();
    }


    public void buPauseStart(View view) {
        pauseOptionSound.start();
        isPlaying = !isPlaying;
        if(isPlaying){
            gameSurface.resume();
            buttonPauseResum.setBackgroundResource(R.drawable.wood_bt_pause);
        }
        else {
            gameSurface.pause();
            buttonPauseResum.setBackgroundResource(R.drawable.wood_bt_resum);
        }
    }

    public void buRoulette(View view) {
        if(gameSurface.Roulette()==-1){
            gameSurface.pause();
            if(countTime<0||!isStartCounting) RewardedVideoAdDialog(R.layout.joker_cards_dialog, R.id.idLaunchVideo, R.id.idClose, 1);
                else CountTimeJokerDialog();

            jokerDialogSound.start();
            return;
        }

        if(gameSurface.getNbreOfCards()<10) buttonRoulette.setTextSize(TypedValue.COMPLEX_UNIT_SP, spToPx(1.94f));
        buttonRoulette.setText(" "+String.valueOf(gameSurface.Roulette()));
        btCheckCards.start();

    }

    public void buMagicHelp(View view) {
             gameSurface.pause();

        if(gameSurface.getMagicHelpSate()) {
                 RewardedVideoAdDialog(R.layout.magic_help_layout, R.id.idLaunchVideoMagic, R.id.idCloseMagic, 2);
                 magicDialogSound.start();
             }else {

                 if(countTime<0||!isStartCounting) RewardedVideoAdDialog(R.layout.joker_cards_dialog, R.id.idLaunchVideo, R.id.idClose, 1);
                 else CountTimeJokerDialog();
                 jokerDialogSound.start();
             }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4168864559615120/5411567720",
                adRequest);
    }

    //Get Connectivity State
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // Dilago Cancel App
    private void QuitGameDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.cancel_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        gameSurface.pause();
        dialog.show();

        //cancel and Continue
        dialog.findViewById(R.id.idCancelQuitGame).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameSurface.CleanGame();
                buttonRoulette.setText(" 3");
                buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);
                gameSurface.isGameOver=false;
                Intent menuIntent = new Intent(MainActivity.this,GameMenuActivity.class);
                MainActivity.this.finish();
                startActivity(menuIntent);

                //Launch Full Screen Ad
                mInterstitialAd.loadAd(adRequest);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInterstitialAd.show();
                    }
                },100); // Millisecond 1000 = 1 sec

            }

        });


        // Quit Game and back to Menu
        dialog.findViewById(R.id.idQuitGame).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                onResume();
            }

        });
    }



    // Option Menu Dialog
    private void OptionMenuDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.option_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        final Button sound =dialog.findViewById(R.id.idOptionSound);
        final Button music =(Button) dialog.findViewById(R.id.idOptionMusic);
        gameSurface.pause();

        //set Music State Image
        if(Float.parseFloat(getMusicVolumeDB())==0f) music.setBackgroundResource(R.drawable.music_bt);
        else music.setBackgroundResource(R.drawable.music_on_bt);

        //set Sound State Image
        if(Float.parseFloat(getSoundVolumeDB())==0f) sound.setBackgroundResource(R.drawable.sound_off_bt);
        else sound.setBackgroundResource(R.drawable.sound_bt);

        dialog.show();

        //cancel and Continue
        dialog.findViewById(R.id.idOkOptionMenu).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
               // gameSurface.resume();
                onResume();
            }

        });

        // Music Button Event
        dialog.findViewById(R.id.idOptionMusic).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if(Float.parseFloat(getMusicVolumeDB())==0f){
                    setMusicVolumeDB("1");
                    music.setBackgroundResource(R.drawable.music_on_bt);
                }
                else {
                    setMusicVolumeDB("0");
                    music.setBackgroundResource(R.drawable.music_bt);
                }

                backgrounfMusic.setVolume(Float.parseFloat(getMusicVolumeDB()),Float.parseFloat(getMusicVolumeDB()));
                pauseOptionSound.start();
            }

        });

        // Sound Button Event
        dialog.findViewById(R.id.idOptionSound).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(Float.parseFloat(getSoundVolumeDB())==0f){
                    setSoundVolumeDB("1");
                    sound.setBackgroundResource(R.drawable.sound_bt);
                }
                else {
                    setSoundVolumeDB("0");
                    sound.setBackgroundResource(R.drawable.sound_off_bt);
                }

                pauseOptionSound.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                btRotateMusic.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                btCheckCards.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                btUpSpeed.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));
                //btLeftRight.setVolume(Float.parseFloat(getSoundVolumeDB()),Float.parseFloat(getSoundVolumeDB()));

                pauseOptionSound.start();
            }
        });

        dialog.findViewById(R.id.idOptionQuit).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                QuitGameDialog();
            }

        });

        //Restart game Event
        dialog.findViewById(R.id.idOptionRestart).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameSurface.RestartNewGame();
                buttonRoulette.setText(" 3");
                buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);
                gameSurface.isGameOver=false;
                isStartCounting=false;
                handler.post(runnable);
                onResume();
            }

        });

    }


    // Dilago Game Over
    private void GameOverDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.game_over_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        final MediaPlayer gameOverSound=MediaPlayer.create(this,R.raw.game_over);
        gameOverSound.start();
        dialog.show();
       // Quit Game and back to Menu
        dialog.findViewById(R.id.idBack).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameOverSound.stop();
                gameOverSound.release();
                Intent menuIntent = new Intent(MainActivity.this,GameMenuActivity.class);
                MainActivity.this.finish();
                startActivity(menuIntent);

            }

        });

        //New Game
        dialog.findViewById(R.id.idPlayAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameOverSound.stop();
                gameOverSound.release();
                gameSurface.RestartNewGame();
                buttonRoulette.setText(" 3");
                buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);
                gameSurface.isGameOver=false;
                isStartCounting=false;
                onResume();
            }
        });
    }

    // Dilago New High Score
    private void NewRecord(String _record){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.new_record_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView highScore=(TextView) dialog.findViewById(R.id.idNewRecod);
        highScore.setText(_record);
        final MediaPlayer fireworksSound=MediaPlayer.create(mContext,R.raw.fireworks_sound);
        fireworksSound.start();
        dialog.show();
        // Quit Game and back to Menu
        dialog.findViewById(R.id.idBack0).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                fireworksSound.stop();
                fireworksSound.release();
                Intent menuIntent = new Intent(MainActivity.this,GameMenuActivity.class);
                MainActivity.this.finish();
                startActivity(menuIntent);

            }

        });

        //New Game
        dialog.findViewById(R.id.idPlayAgain0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                fireworksSound.stop();
                fireworksSound.release();
                gameSurface.RestartNewGame();
                buttonRoulette.setText(" 3");
                buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);
                // pb detected here
                gameSurface.isGameOver=false;
                isStartCounting=false;
                onResume();
            }
        });
    }



    // Counting Time for next pack of joker cards
    private void CountTimeJokerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.count_time_joker_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        final MediaPlayer LaunchSoundDialog=MediaPlayer.create(this,R.raw.launch_joker_dialog);
        final TextView countingTimeMsg=(TextView) dialog.findViewById(R.id.idTimeCounter);
        LaunchSoundDialog.start();
        dialog.show();

        // Counter
        final Handler h=new Handler();
        final Runnable r;
        h.post(r=new Runnable()
        {
            public void run() {
                if(countTime<0){
                    dialog.cancel();
                    RewardedVideoAdDialog(R.layout.joker_cards_dialog, R.id.idLaunchVideo, R.id.idClose, 1);
                    return;
                }
                if(countTime>9)
                    countingTimeMsg.setText("00:00:"+String.valueOf(countTime));
                else
                    countingTimeMsg.setText("00:00:0"+String.valueOf(countTime));


                h.postDelayed(this, 500);

            }
        });


        // Cancel Counter Dialog
        dialog.findViewById(R.id.idBuJokerTimeCounter).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();
                h.removeCallbacks(r);
                LaunchSoundDialog.stop();
                LaunchSoundDialog.release();
                onResume();
            }

        });
    }




    // Dilago Cancel App
    private void RewardedVideoAdDialog(int idDialogAlert,int idVideoLunch, int idClose,  final int id_Gift){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(idDialogAlert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        //Dialog Alert
        dialog.findViewById(idVideoLunch).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog.cancel();

                        if(isConnected()){
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                            idGift=id_Gift;
                        }else {
                            loadRewardedVideoAd();
                            Toast.makeText(getApplicationContext(), "    Please try again.\nVideo Not Loaded Yet.", Toast.LENGTH_LONG).show();
                            onResume();
                        }

                        }else {
                            // Launch alert
                            ConnectionStateAlert();
                           }

            }

        });

        dialog.findViewById(idClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                onResume();
            }

        });

    }

    // Connection state alert
    private void ConnectionStateAlert(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.connection_state_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        dialog.findViewById(R.id.idOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                onResume();
            }

        });
    }



    // Back Button Event
    @Override
    public void onBackPressed() {
        QuitGameDialog();
    }

        @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Load the next rewarded video ad.
        loadRewardedVideoAd();
        if(isRewardedVideoComplited) {

            switch (idGift){
                case 1:   MoreCardsSound.start();
                          gameSurface.addNbreOfCards();
                          buttonRoulette.setText(" "+String.valueOf(gameSurface.Roulette()));
                          buttonRoulette.setBackgroundResource(R.drawable.wood_bt_square);

                    if(gameSurface.getNbreOfCards()>9) buttonRoulette.setTextSize(TypedValue.COMPLEX_UNIT_SP, spToPx(1.38f));
                    else buttonRoulette.setTextSize(TypedValue.COMPLEX_UNIT_SP, spToPx(1.94f));

                          isStartCounting=true;
                          countTime=50;
                          hndlrCountTime.post(rnnble);
                    break;
                case 2: yahooStarSound.start();
                        gameSurface.MagicHelp();
                default:break;
            }
            isRewardedVideoComplited=false;
        }else{
            switch (idGift){
                case 1:    Toast.makeText(this, "    Please try again.\nYou need to COMPLETE this Video to get more Joker Cards!", Toast.LENGTH_LONG).show();
                    break;
                case 2:    Toast.makeText(this, "    Please try again.\nYou need to COMPLETE this Video to get Magic Help!", Toast.LENGTH_LONG).show();
                default:break;
            }
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {
        isRewardedVideoComplited=true;
        switch (idGift){
            case 1:
               // gameSurface.addNbreOfCards();
                break;
            case 2:
                gameSurface.NbreOfMagicHelp();
                break;
        }
    }

    public void buOptionMenu(View view) {
        pauseOptionSound.start();
        OptionMenuDialog();
    }

    //set Score in XML file
    private void setScoreDB(String score){
        // /Save Date For Next Time
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("High_Score",score);
        editor.apply();
    }

    // Load Score
    private String getScoreDB(){
        SharedPreferences perfers=getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("High_Score")) {
            return "0";
        }
        return perfers.getString("High_Score","0");
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
    private String getSoundVolumeDB(){
        SharedPreferences perfers=getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        if(!perfers.contains("MuteSound")) {
            return "1";
        }
        return perfers.getString("MuteSound","1");
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

}